package com.kangong.advstock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.advstock.parser.AdvStockParser;
import com.kangong.stock.model.StockCategoryLinkVO;
import com.kangong.stock.model.StockCategoryVO;
import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AdvStockService {

    private static final String NAVER_CATEGORY_TYPE = "NAVER";
    private static final int CATEGORY_NO_START = 90001;

    @Autowired
    private AdvStockParser advStockParser;

    @Autowired
    private AdvStockRepository advStockRepository;

    private List<StockVO> fetchKospiList() throws Exception {
        return advStockParser.fetchKospiStockList();
    }

    public void saveStockMaster() throws Exception {
        saveStockMasterInternal(fetchKospiList());
    }

    public void saveDailyPrice(String tradingDate) throws Exception {
        saveDailyPriceInternal(fetchKospiList(), tradingDate);
    }

    private List<StockVO> saveStockMasterInternal(List<StockVO> stockList) {
        log.info("KOSPI 종목 저장 시작: {}건", stockList.size());

        for (StockVO vo : stockList) {
            advStockRepository.restoreIfDeleted(vo.getStockId());
            advStockRepository.saveFromNaver(vo);
        }

        Set<String> kospiIds = stockList.stream()
                .map(StockVO::getStockId)
                .collect(Collectors.toSet());

        List<StockVO> extraStocks = fetchMissingInterestStocks(kospiIds);
        for (StockVO vo : extraStocks) {
            advStockRepository.restoreIfDeleted(vo.getStockId());
            advStockRepository.saveFromNaver(vo);
        }

        List<String> activeStockIds = new ArrayList<>(kospiIds);
        extraStocks.stream().map(StockVO::getStockId).forEach(activeStockIds::add);

        if (!activeStockIds.isEmpty()) {
            advStockRepository.markDeleted(activeStockIds);
        }

        log.info("ST_STOCK_MASTER 저장 완료 (KOSPI: {}건, 추가: {}건)", kospiIds.size(), extraStocks.size());
        return extraStocks;
    }

    private List<StockVO> fetchMissingInterestStocks(Set<String> kospiIds) {
        List<StockVO> result = new ArrayList<>();
        List<String> interestIds = advStockRepository.selectInterestStockIds();
        List<String> missingIds = interestIds.stream()
                .filter(id -> !kospiIds.contains(id))
                .collect(Collectors.toList());

        if (missingIds.isEmpty()) return result;

        log.info("KOSPI 미포함 관심종목 개별 조회: {}건 ({})", missingIds.size(), missingIds);
        for (String stockId : missingIds) {
            try {
                StockVO vo = advStockParser.fetchStockByCode(stockId);
                if (vo != null && vo.getStockId() != null) {
                    result.add(vo);
                }
            } catch (Exception e) {
                log.warn("개별 종목 조회 실패: {} - {}", stockId, e.getMessage());
            }
        }
        return result;
    }

    private void saveDailyPriceInternal(List<StockVO> stockList, String tradingDate) {
        log.info("Daily Price 저장 시작: {}건 (기준일: {})", stockList.size(), tradingDate);

        for (StockVO vo : stockList) {
            StockDailyPriceVO dailyVO = StockDailyPriceVO.builder()
                    .stockId(vo.getStockId())
                    .tradingDate(tradingDate)
                    .closingPrice(vo.getPrice())
                    .previousDayRate(vo.getPriceChange())
                    .fluctuationRate(vo.getFluctuationRate())
                    .volumn(vo.getVolumn())
                    .build();
            advStockRepository.saveDailyPrice(dailyVO);
        }

        log.info("ST_STOCK_DAILY_PRICE 저장 완료 (기준일: {})", tradingDate);
    }

    /**
     * ST_STOCK_CATEGORY + ST_STOCK_CATEGORY_LINK: 업종 분류 저장
     * (Naver API enrollStocks에는 업종정보 미포함 — 기존 sectorName이 있는 종목만 처리)
     */
    public void saveCategory() throws Exception {
        List<StockVO> masterList = advStockRepository.selectList(StockVO.builder().build());

        List<String> sectorNames = masterList.stream()
                .map(StockVO::getSectorName)
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (sectorNames.isEmpty()) {
            log.info("업종 정보가 있는 종목이 없습니다.");
            return;
        }

        Map<String, String> sectorNoMap = new HashMap<>();
        int categoryNo = CATEGORY_NO_START;
        for (String sectorName : sectorNames) {
            String no = String.valueOf(categoryNo++);
            sectorNoMap.put(sectorName, no);

            StockCategoryVO catVO = new StockCategoryVO();
            catVO.setCategoryType(NAVER_CATEGORY_TYPE);
            catVO.setCategoryNo(no);
            catVO.setCategoryName(sectorName);
            advStockRepository.saveCategory(catVO);
        }
        log.info("ST_STOCK_CATEGORY 저장 완료: {}건", sectorNames.size());

        for (StockVO vo : masterList) {
            String no = sectorNoMap.get(vo.getSectorName());
            if (no == null) continue;

            StockCategoryLinkVO linkVO = new StockCategoryLinkVO();
            linkVO.setCategoryNo(no);
            linkVO.setStockId(vo.getStockId());
            advStockRepository.saveCategoryLink(linkVO);
        }
        log.info("ST_STOCK_CATEGORY_LINK 저장 완료");
    }

    public void saveAll(String tradingDate) throws Exception {
        List<StockVO> stockList = fetchKospiList();
        List<StockVO> extraStocks = saveStockMasterInternal(stockList);

        List<StockVO> allStocks = new ArrayList<>(stockList);
        allStocks.addAll(extraStocks);

        saveDailyPriceInternal(allStocks, tradingDate);
        saveCategory();
    }

    public List<StockVO> getStockList(StockVO paramVO) {
        return advStockRepository.selectList(paramVO);
    }

}
