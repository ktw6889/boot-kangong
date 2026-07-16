package com.kangong.stock.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.common.service.CommonService;
import com.kangong.common.util.BatchOperationHandler;
import com.kangong.stock.model.StockCategoryVO;
import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockInterestParamVO;
import com.kangong.stock.model.StockInterestVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.model.StockValueScreenVO;
import com.kangong.stock.parser.StockCategoryParser;
import com.kangong.stock.parser.StockDailyPriceParser;
import com.kangong.stock.parser.StockFinancialParser;
import com.kangong.stock.parser.StockInfoParser;
import com.kangong.stock.parser.StockSeleniumParser;
import com.kangong.stock.parser.StockValueUpParser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockService extends CommonService implements StockOperations {

	@Autowired
	StockInfoParser stockInfoParser;

	@Autowired
	StockFinancialParser stockFinancialParser;

	@Autowired
	StockCategoryParser stockCategoryParser;

	@Autowired
	StockDailyPriceParser stockDailyPriceParser;

	@Autowired
	StockSeleniumParser stockSeleniumParser;

	@Autowired
	StockValueUpParser stockValueUpParser;

	@Override
	public void saveStock(String stockId) throws Exception{
		StockVO stockVO = getStockVO(stockId);
		if (stockVO == null) stockVO = StockVO.builder().stockId(stockId).build();
		stockVO = stockInfoParser.parseStockDetail(stockId, stockVO);
		try {
			stockVO = stockSeleniumParser.getStockDividendInfo(stockVO);
		} catch (Exception e) { /* Selenium 실패 시 무시 */ }
		mergeStock(stockInfoParser.insertZero(stockVO));
	}

	@Override
	public StockVO getStockVO(String stockId) throws Exception{
		StockVO paramVO = StockVO.builder().build();
		paramVO.setStockId(stockId);
		return (StockVO)sqlSession.selectOne("seckim.stock.select", paramVO);
	}

	@Override
	public List<StockVO> getStockList(StockVO stockVO) throws Exception {
		return sqlSession.selectList("seckim.stock.select", stockVO);
	}

	@Override
	public void saveAllStock() throws Exception {
		forEachStock(stock -> saveStock(stock.getStockId()), "saveAllStock");
	}

	public void mergeStock(StockVO stockVO) throws Exception {
		sqlSession.update("seckim.stock.save", stockVO);
	}

	public StockFinancialVO getStockFinancialVO(StockFinancialVO stockFinancialVO) throws Exception {
		return sqlSession.selectOne("seckim.stock.selectFinancial", stockFinancialVO);
	}

	public void saveFinancialList() throws Exception {
		forEachStock(stock -> saveStockFinancial(stock.getStockId()), "saveFinancialList");
	}

	public void saveStockFinancial(String stockId) throws Exception {
		try {
			StockVO stockVO = getStockVO(stockId);
			List<StockFinancialVO> stockFinancialVOList = stockFinancialParser.getStockFinancial(stockVO);
			for (StockFinancialVO stockFinancialVO : stockFinancialVOList) {
				mergeFinancial(stockFinancialVO, "seckim.stock.saveFinancail");
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public void saveFinancialList2() throws Exception {
		forEachStock(stock -> saveStockFinancial2(stock.getStockId()), "saveFinancialList2");
	}

	public void saveStockFinancial2(String stockId) throws Exception {
		try {
			StockVO stockVO = getStockVO(stockId);
			List<StockFinancialVO> stockFinancialVOList = stockFinancialParser.getStockFinancial2(stockVO);
			mergeFinancial(stockFinancialVOList.get(0), "seckim.stock.saveFinancail2");
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public void saveCashFlowList() throws Exception {
		forEachStock(stock -> saveStockCashFlow(stock.getStockId()), "saveCashFlowList");
	}

	public void saveValueUpList() throws Exception {
		try {
			java.util.Set<String> valueUpIds = stockValueUpParser.fetchValueUpStockIds();
			if (valueUpIds.isEmpty()) {
				log.warn("밸류업지수 구성종목 0개 — KRX API 파라미터 확인 필요");
				return;
			}
			// 전체 초기화 후 편입종목 Y 설정
			sqlSession.update("seckim.stock.resetValueUpYn");
			for (String stockId : valueUpIds) {
				StockVO vo = StockVO.builder().stockId(stockId).valueUpYn("Y").build();
				sqlSession.update("seckim.stock.saveValueUp", vo);
			}
			log.info("밸류업지수 편입종목 {}개 저장 완료", valueUpIds.size());
		} catch (Exception e) {
			log.warn("밸류업지수 저장 실패: {}", e.getMessage());
		}
	}

	public void saveStockCashFlow(String stockId) throws Exception {
		try {
			StockVO stockVO = getStockVO(stockId);
			List<StockFinancialVO> cashFlowList = stockFinancialParser.getStockCashFlow(stockVO);
			for (StockFinancialVO vo : cashFlowList) {
				if (vo.getOperatingCashFlow() != null && !vo.getOperatingCashFlow().isEmpty()) {
					sqlSession.update("seckim.stock.saveCashFlow", vo);
				}
			}
		} catch (Exception e) {
			log.warn("현금흐름 수집 실패 [{}]: {}", stockId, e.getMessage());
		}
	}

	private void mergeFinancial(StockFinancialVO stockFinancialVO, String sqlId) throws Exception {
		stockFinancialVO = stockFinancialParser.insertZero(stockFinancialVO);
		sqlSession.update(sqlId, stockFinancialVO);
	}

	public void saveStockCategoryList() throws Exception {
		Map<String, List<StockCategoryVO>> categoryMap = stockCategoryParser.getStockCategoryList();
		List<StockCategoryVO> stockCategoryList = categoryMap.get("categoryList");
		List<StockCategoryVO> stockCategoryLinkList = categoryMap.get("categoryLinkList");

		for (StockCategoryVO categoryVO : stockCategoryList) {
			saveStockCategory(categoryVO);
		}
		for (StockCategoryVO categoryLinkVO : stockCategoryLinkList) {
			saveStockCategoryLink(categoryLinkVO);
		}

		Map<String, List<StockCategoryVO>> themeMap = stockCategoryParser.getStockThemeList();
		List<StockCategoryVO> stockThemeList = themeMap.get("categoryList");
		List<StockCategoryVO> stockThemeLinkList = themeMap.get("categoryLinkList");

		for (StockCategoryVO categoryVO : stockThemeList) {
			saveStockCategory(categoryVO);
		}
		for (StockCategoryVO categoryLinkVO : stockThemeLinkList) {
			saveStockCategoryLink(categoryLinkVO);
		}
	}

	public void saveStockCategory(StockCategoryVO categoryVO) throws Exception {
		sqlSession.update("seckim.stock.saveStockCategory", categoryVO);
	}

	public void saveStockCategoryLink(StockCategoryVO categoryVO) throws Exception {
		sqlSession.update("seckim.stock.saveStockCategoryLink", categoryVO);
	}

	@Override
	public void saveDailyPrice(String stockId) throws Exception {
		StockVO stockVO = getStockVO(stockId);
		List<StockDailyPriceVO> stockDailyPriceVOList = stockDailyPriceParser.getStockDailyPriceList(stockVO);

		for (StockDailyPriceVO stockDailyPriceVO : stockDailyPriceVOList) {
			if(!"0".equals(stockDailyPriceVO.getTradingDate()) )
			 sqlSession.update("seckim.stock.saveStockDailyPrice", stockDailyPriceVO);
		}
	}

	@Override
	public void saveDailyPriceAll() throws Exception {
		forEachStock(stock -> saveDailyPrice(stock.getStockId()), "saveDailyPriceAll");
	}

	public void saveSeleniumStockFinancial(String stockId) throws Exception {
		try {
			StockVO stockVO = getStockVO(stockId);
			List<StockFinancialVO> stockFinancialVOList = stockSeleniumParser.getFinancialEnterpriseState(
					stockVO, this::getStockFinancialVOSafe);
			for (StockFinancialVO stockFinancialVO : stockFinancialVOList) {
				mergeFinancial(stockFinancialVO, "seckim.stock.saveFinancail");
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public void saveSeleniumStockFinancialAll() throws Exception {
		forEachStock(stock -> saveSeleniumStockFinancial(stock.getStockId()), "saveSeleniumStockFinancialAll");
	}

	public void saveSeleniumStockFinancialAnalysis(String stockId) throws Exception {
		try {
			StockVO stockVO = getStockVO(stockId);
			List<StockFinancialVO> stockFinancialVOList = stockSeleniumParser.getFinancialAnalysis(
					stockVO, this::getStockFinancialVOSafe);
			for (StockFinancialVO stockFinancialVO : stockFinancialVOList) {
				mergeFinancial(stockFinancialVO, "seckim.stock.saveFinancail2");
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private StockFinancialVO getStockFinancialVOSafe(StockFinancialVO vo) {
		try {
			return getStockFinancialVO(vo);
		} catch (Exception e) {
			log.error("재무정보 조회 실패: stockId={}, year={}", vo.getStockId(), vo.getYear(), e);
			return null;
		}
	}

	public void saveSeleniumStockFinancialAnalysisAll() throws Exception {
		forEachStock(stock -> saveSeleniumStockFinancialAnalysis(stock.getStockId()), "saveSeleniumStockFinancialAnalysisAll");
	}

	public List<StockInterestVO> getStockInterestList(StockInterestVO param) throws Exception {
		List<StockInterestVO> list = sqlSession.selectList("seckim.stock.selectStockInterest", param);
		if (list == null || list.isEmpty()) return list;

		List<String> stockIds = list.stream().map(StockInterestVO::getStockId).collect(java.util.stream.Collectors.toList());
		java.util.Map<String, Object> qParam = new java.util.HashMap<>();
		qParam.put("stockIds", stockIds);
		List<java.util.Map<String, Object>> prices = sqlSession.selectList("seckim.stock.selectPeriodPrices", qParam);

		// stockId -> sorted list of (tradingDate, closingPrice)
		java.util.Map<String, java.util.TreeMap<String, Long>> priceMap = new java.util.HashMap<>();
		for (java.util.Map<String, Object> row : prices) {
			String sid = (String) row.get("stockId");
			String date = (String) row.get("tradingDate");
			Object cp = row.get("closingPrice");
			long price = 0;
			if (cp instanceof Number) price = ((Number) cp).longValue();
			else if (cp instanceof String) { try { price = Long.parseLong((String) cp); } catch (Exception ignored) {} }
			priceMap.computeIfAbsent(sid, k -> new java.util.TreeMap<>()).put(date, price);
		}

		java.time.LocalDate today = java.time.LocalDate.now();
		for (StockInterestVO vo : list) {
			java.util.TreeMap<String, Long> tm = priceMap.get(vo.getStockId());
			if (tm == null || tm.isEmpty()) continue;
			long curPrice = vo.getPrice();
			vo.setMonth1Rate(calcRate(curPrice, tm, today.minusDays(30)));
			vo.setMonth3Rate(calcRate(curPrice, tm, today.minusDays(90)));
			vo.setMonth6Rate(calcRate(curPrice, tm, today.minusDays(180)));
			vo.setMonth12Rate(calcRate(curPrice, tm, today.minusDays(365)));
		}
		return list;
	}

	private Double calcRate(long curPrice, java.util.TreeMap<String, Long> tm, java.time.LocalDate targetDate) {
		String key = targetDate.toString();
		java.util.Map.Entry<String, Long> e = tm.floorEntry(key);
		if (e == null || e.getValue() == 0) return null;
		return Math.round((curPrice - e.getValue()) * 1000.0 / e.getValue()) / 10.0;
	}

	public List<StockInterestParamVO> getStockDivisions() throws Exception {
		return sqlSession.selectList("seckim.stock.selectStockDivisions");
	}

	public List<StockInterestVO> getStockInterestRaw(StockInterestVO param) throws Exception {
		return sqlSession.selectList("seckim.stock.selectStockInterestRaw", param);
	}

	public void saveStockInterest(StockInterestVO vo) throws Exception {
		sqlSession.update("seckim.stock.saveStockInterest", vo);
	}

	public void saveStockMasterMin(StockVO vo) {
		sqlSession.update("seckim.stock.saveSimpleIfAbsent", vo);
	}

	public void deleteStockInterest(StockInterestVO vo) throws Exception {
		sqlSession.delete("seckim.stock.deleteStockInterest", vo);
	}

	public void saveStockInterestParam(StockInterestParamVO vo) throws Exception {
		sqlSession.update("seckim.stock.saveStockInterestParam", vo);
	}

	public void deleteStockInterestParam(StockInterestParamVO vo) throws Exception {
		sqlSession.delete("seckim.stock.deleteStockInterestParam", vo);
	}

	public List<StockVO> searchStockByName(String keyword) {
		return sqlSession.selectList("seckim.stock.searchStockByName", keyword);
	}

	public List<StockEsgVO> getStockEsgList(StockEsgVO param) {
		return sqlSession.selectList("seckim.stock.selectStockEsgList", param);
	}

	public List<StockValueScreenVO> getValueScreenList(StockValueScreenVO param) {
		if (param.getFilterPerMax() == null) param.setFilterPerMax(10.0);
		if (param.getFilterPbrMax() == null) param.setFilterPbrMax(0.8);
		if (param.getFilterRoeMin() == null) param.setFilterRoeMin(8.0);
		if (param.getFilterDividendMin() == null) param.setFilterDividendMin(3.5);
		if (param.getFilterDebtMax() == null) param.setFilterDebtMax(100.0);
		if (param.getFilterPassCountMin() == null) param.setFilterPassCountMin(3);
		return sqlSession.selectList("seckim.stock.selectValueScreen", param);
	}

	private void forEachStock(BatchOperationHandler.ThrowingConsumer<StockVO> action, String operationName) throws Exception {
		List<StockVO> stockList = getStockList(StockVO.builder().build());
		for (StockVO stock : stockList) {
			try {
				action.accept(stock);
			} catch (Exception e) {
				log.info("{} 오류: {}", operationName, stock.getName());
			}
		}
	}
}
