package com.kangong.stock.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.advstock.parser.AdvStockParser;
import com.kangong.common.util.BatchOperationHandler;
import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockInfoParser;
import com.kangong.stock.parser.StockSeleniumParser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockMobileService implements StockOperations {

	private static final int SIMPLE_STOCK_LIST_MAX_PAGE = 99;
	private static final int DAILY_PRICE_PAGE_COUNT = 1;
	private static final int MIGRATION_PRICE_PAGE_COUNT = 28;

	@Autowired
	private StockFetcher stockFetcher;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockJsonConverter stockJsonConverter;

	@Autowired
	private StockMobileMarketIndexService stockMobileMarketIndexService;

	@Autowired
	private StockMobileFinancialService stockMobileFinancialService;

	@Autowired
	private StockSeleniumParser stockSeleniumParser;

	@Autowired
	private StockInfoParser stockInfoParser;

	@Autowired
	private AdvStockParser advStockParser;

	/**
	 * 당일 전체 주식 업데이트
	 */
	public void updateDailyStock() throws Exception {
		long totalStart = System.currentTimeMillis();

		// KOSPI 전체 종목 간략정보(종목코드,종목명,종가,시가총액) 99페이지 병렬 수집 → DB 배치 저장
		long start = System.currentTimeMillis();
		saveSimpleStockList();
		log.info("[TIME] saveSimpleStockList: {}ms", System.currentTimeMillis() - start);

		// KOSPI 미포함 관심종목(ETF 등) 개별 저장
		start = System.currentTimeMillis();
		saveMissingInterestStocks();
		log.info("[TIME] saveMissingInterestStocks: {}ms", System.currentTimeMillis() - start);

		// 전체 종목 상세정보 + Daily 시세 + 마켓인덱스 일괄 업데이트
		start = System.currentTimeMillis();
		updateDailyInfo();
		log.info("[TIME] updateDailyInfo: {}ms", System.currentTimeMillis() - start);

		// 업종PER 갱신
		start = System.currentTimeMillis();
		updateIndustryPerAll();
		log.info("[TIME] updateIndustryPerAll: {}ms", System.currentTimeMillis() - start);

		log.info("[TIME] updateDailyStock 전체: {}ms", System.currentTimeMillis() - totalStart);
	}

	private void saveMissingInterestStocks() {
		Set<String> masterIds = stockRepository.selectStockList(StockVO.builder().build())
				.stream().map(StockVO::getStockId).collect(Collectors.toSet());
		List<String> missingIds = stockRepository.selectInterestStockIds().stream()
				.filter(id -> !masterIds.contains(id))
				.collect(Collectors.toList());

		if (missingIds.isEmpty()) return;
		log.info("KOSPI 미포함 관심종목 개별 조회: {}건 ({})", missingIds.size(), missingIds);

		for (String stockId : missingIds) {
			try {
				StockVO vo = advStockParser.fetchStockByCode(stockId);
				if (vo != null && vo.getStockId() != null) {
					stockRepository.saveSimple(vo);
				}
			} catch (Exception e) {
				log.warn("관심종목 개별 저장 실패: {} - {}", stockId, e.getMessage());
			}
		}
	}

	/**
	 * 주식 전체 Simple 리스트 저장 (병렬 + 배치 INSERT)
	 */
	public void saveSimpleStockList() throws Exception {
		List<Integer> pages = IntStream.rangeClosed(1, SIMPLE_STOCK_LIST_MAX_PAGE).boxed().collect(Collectors.toList());
		BatchOperationHandler.executeBatchParallel(pages,
			this::saveSimpleStockListPage,
			stockFetcher::simpleStockListUrl,
			"saveSimpleStockList", log);
	}

	private void saveSimpleStockListPage(int pageNum) throws Exception {
		String strJson = stockFetcher.fetchSimpleStockList(pageNum);
		List<StockVO> simpleStockList = stockJsonConverter.convertSimpleStockList(strJson);
		if (!simpleStockList.isEmpty()) {
			stockRepository.saveSimpleBatch(simpleStockList);
		}
	}

	/**
	 * Daily Update
	 * 1. 당일 전체 주식 상세 저장
	 * 2. Daily 마감금액 저장
	 * 3. 마켓 index 저장 (콜금리, 채권, 금, 원자재, 원유)
	 */
	public void updateDailyInfo() throws Exception {
		List<StockVO> stockVOList = getStockList(StockVO.builder().build());
		long start = System.currentTimeMillis();

		// saveAllStock + saveDailyPriceAll 동시 실행 (서로 독립적)
		ExecutorService executor = Executors.newFixedThreadPool(2);
		try {
			CompletableFuture<Void> stockDetailFuture = CompletableFuture.runAsync(
				() -> saveAllStock(stockVOList), executor);
			CompletableFuture<Void> dailyPriceFuture = CompletableFuture.runAsync(
				() -> saveDailyPriceAll(stockVOList), executor);
			CompletableFuture.allOf(stockDetailFuture, dailyPriceFuture).join();
		} finally {
			executor.shutdown();
		}
		log.info("[TIME] saveAllStock + saveDailyPriceAll 병렬: {}ms", System.currentTimeMillis() - start);

		// 마켓인덱스(콜금리,채권,금,원자재,원유) 1회 API 호출 → DB 저장
		start = System.currentTimeMillis();
		stockMobileMarketIndexService.saveStockMarketIndex();
		log.info("[TIME] saveStockMarketIndex: {}ms", System.currentTimeMillis() - start);
	}

	/**
	 * 당일 전체 주식 상세 저장 (병렬)
	 */
	@Override
	public void saveAllStock() throws Exception {
		List<StockVO> stockVOList = getStockList(StockVO.builder().build());
		saveAllStock(stockVOList);
	}

	private static final int BATCH_INSERT_SIZE = 50;

	private void saveAllStock(List<StockVO> stockVOList) {
		List<StockVO> convertedList = Collections.synchronizedList(new ArrayList<>());
		BatchOperationHandler.executeBatchParallel(stockVOList,
			stockVO -> {
				StockVO converted = fetchAndConvertStock(stockVO.getStockId());
				if (converted != null) convertedList.add(converted);
			},
			stockVO -> stockFetcher.stockDetailUrl(stockVO.getStockId()),
			"saveAllStock(fetch)", log);

		for (int i = 0; i < convertedList.size(); i += BATCH_INSERT_SIZE) {
			List<StockVO> batch = convertedList.subList(i, Math.min(i + BATCH_INSERT_SIZE, convertedList.size()));
			stockRepository.saveStockBatch(batch);
		}
		log.info("[TIME] saveAllStock DB배치 완료: {}건", convertedList.size());
	}

	private StockVO fetchAndConvertStock(String stockId) throws Exception {
		String strUrlJson = stockFetcher.fetchStockDetail(stockId);
		if (!stockJsonConverter.checkValidJson(strUrlJson))
			return null;
		StockVO stockVO = StockVO.builder().stockId(stockId).build();
		return stockJsonConverter.convertStockDetail(stockVO, strUrlJson);
	}

	@Override
	public List<StockVO> getStockList(StockVO stockVO) throws Exception {
		return stockRepository.selectStockList(stockVO);
	}

	@Override
	public void saveStock(String stockId) throws Exception {
		StockVO converted = fetchAndConvertStock(stockId);
		if (converted != null) {
			stockRepository.saveStock(converted);
		}
	}

	@Override
	public StockVO getStockVO(String stockId) throws Exception {
		StockVO paramVO = StockVO.builder().build();
		paramVO.setStockId(stockId);
		return stockRepository.selectStock(paramVO);
	}

	/**
	 * 주가 Daily 정보 전체 저장 (Migration)
	 */
	public void saveDailyPriceAllMigration() throws Exception {
		List<StockVO> stockVOList = getStockList(StockVO.builder().build());
		BatchOperationHandler.executeBatchParallel(stockVOList,
			stockVO -> saveDailyPriceMigration(stockVO.getStockId()),
			stockVO -> stockFetcher.dailyPriceUrl(stockVO.getStockId(), 1) + " (Migration)",
			"saveDailyPriceAllMigration", log);
	}

	public void saveDailyPriceMigration(String stockId) throws Exception {
		for (int i = 1; i <= MIGRATION_PRICE_PAGE_COUNT; i++) {
			saveDailyPrice(stockId, i);
		}
	}

	/**
	 * 당일 PRICE값 전체 저장 (병렬 + 배치 INSERT)
	 */
	@Override
	public void saveDailyPriceAll() throws Exception {
		List<StockVO> stockVOList = getStockList(StockVO.builder().build());
		saveDailyPriceAll(stockVOList);
	}

	private void saveDailyPriceAll(List<StockVO> stockVOList) {
		List<StockDailyPriceVO> allPrices = Collections.synchronizedList(new ArrayList<>());
		BatchOperationHandler.executeBatchParallel(stockVOList,
			stockVO -> {
				for (int i = 1; i <= DAILY_PRICE_PAGE_COUNT; i++) {
					String strUrlJson = stockFetcher.fetchDailyPrice(stockVO.getStockId(), i);
					allPrices.addAll(stockJsonConverter.convertDailyPriceList(stockVO.getStockId(), strUrlJson));
				}
			},
			stockVO -> stockFetcher.dailyPriceUrl(stockVO.getStockId(), 1),
			"saveDailyPriceAll(fetch)", log);

		for (int i = 0; i < allPrices.size(); i += BATCH_INSERT_SIZE) {
			List<StockDailyPriceVO> batch = allPrices.subList(i, Math.min(i + BATCH_INSERT_SIZE, allPrices.size()));
			stockRepository.saveDailyPriceBatch(batch);
		}
		log.info("[TIME] saveDailyPriceAll DB배치 완료: {}건", allPrices.size());
	}

	@Override
	public void saveDailyPrice(String stockId) throws Exception {
		List<StockDailyPriceVO> allPrices = new ArrayList<>();
		for (int i = 1; i <= DAILY_PRICE_PAGE_COUNT; i++) {
			String strUrlJson = stockFetcher.fetchDailyPrice(stockId, i);
			allPrices.addAll(stockJsonConverter.convertDailyPriceList(stockId, strUrlJson));
		}
		stockRepository.saveDailyPriceBatch(allPrices);
	}

	/**
	 * 주가 Daily 정보 저장 (단건 페이지)
	 */
	public void saveDailyPrice(String stockId, int pageNum) throws Exception {
		String strUrlJson = stockFetcher.fetchDailyPrice(stockId, pageNum);
		List<StockDailyPriceVO> dailyPrices = stockJsonConverter.convertDailyPriceList(stockId, strUrlJson);
		stockRepository.saveDailyPriceBatch(dailyPrices);
	}

	public void updateIndustryPerAll() throws Exception {
		List<StockVO> stockVOList = getStockList(StockVO.builder().build());
		BatchOperationHandler.executeBatchParallel(stockVOList,
			stockVO -> {
				String industryPer = stockInfoParser.parseIndustryPer(stockVO.getStockId());
				if (industryPer != null && !industryPer.isEmpty()) {
					StockVO updateVO = StockVO.builder().stockId(stockVO.getStockId()).industryPer(industryPer).build();
					stockRepository.updateIndustryPer(updateVO);
				}
			},
			stockVO -> "industryPer:" + stockVO.getStockId(),
			"updateIndustryPerAll", log);
	}

	public void saveAllStockId() throws Exception {
		List<StockVO> stockVOList = stockInfoParser.getStockList();
		for (StockVO stockVO : stockVOList) {
			stockRepository.saveMasterForStockId(stockVO);
		}
	}

	public String getSeleniumJsonData(String httpUrl) throws Exception {
		ChromeDriver driver = stockSeleniumParser.getChromeDriver();
		driver.get(httpUrl);
		driver.manage().window().maximize();
		log.info("JSONText:{}", driver.getPageSource());
		return driver.getPageSource();
	}
}
