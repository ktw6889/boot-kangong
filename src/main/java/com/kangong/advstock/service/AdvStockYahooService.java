package com.kangong.advstock.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.advstock.model.YahooStockVO;
import com.kangong.advstock.parser.AdvStockYahooParser;
import com.kangong.stock.model.StockDailyPriceVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AdvStockYahooService {

    // BRK.B, BF.B 등 점/대시 포함 티커도 US 주식으로 인식
    private static final String US_TICKER_PATTERN = "[A-Za-z][A-Za-z0-9.\\-]*";

    private static boolean isUsTicker(String stockId) {
        return stockId != null && stockId.matches(US_TICKER_PATTERN);
    }

    @Autowired
    private AdvStockYahooParser advStockYahooParser;

    @Autowired
    private AdvStockRepository advStockRepository;

    public List<YahooStockVO> fetchStockList(String keyword) throws Exception {
        return advStockYahooParser.fetchStockList(keyword);
    }

    public void saveMaster(String keyword) throws Exception {
        List<YahooStockVO> stockList = advStockYahooParser.fetchStockList(keyword);
        log.info("Yahoo Finance 다운로드: {}건", stockList.size());

        for (YahooStockVO vo : stockList) {
            advStockRepository.saveFromYahoo(vo);
        }

        log.info("Yahoo Finance Master 저장 완료: {}건", stockList.size());
    }

    /**
     * 저장 시 단일 US 종목만 동기화 — USDKRW=X + 해당 티커를 1번 API 호출로 처리
     */
    public void syncSingleUsStock(String stockId) throws Exception {
        log.info("US 주식 단일 동기화: {}", stockId);

        // Yahoo Finance는 점(.) 대신 대시(-) 사용: BRK.B → BRK-B
        String yahooSymbol = stockId.replace(".", "-").toUpperCase();
        List<YahooStockVO> result = advStockYahooParser.fetchQuotesBySymbols(List.of("USDKRW=X", yahooSymbol));
        if (result.isEmpty()) {
            log.warn("{} — Yahoo Finance에서 결과 없음", stockId);
            return;
        }

        double usdKrwRate = result.stream()
                .filter(v -> "USDKRW=X".equals(v.getStockId()))
                .mapToDouble(v -> { try { return Double.parseDouble(v.getPrice()); } catch (Exception e) { return 0; } })
                .findFirst().orElse(0);

        if (usdKrwRate <= 0) {
            log.warn("환율 조회 실패 (USDKRW=X 없음) — USD 원화 변환 생략");
        } else {
            log.info("USD/KRW 환율: {}", usdKrwRate);
        }

        for (YahooStockVO vo : result) {
            // yahooSymbol(예: BRK-B)과 일치하는 레코드만 처리 — USDKRW=X/빈레코드 등 무조건 제외
            if (!yahooSymbol.equalsIgnoreCase(vo.getStockId())) continue;
            // 포트폴리오의 원본 ID(BRK.B)로 저장
            vo.setStockId(stockId.toUpperCase());
            if ("USD".equalsIgnoreCase(vo.getCurrency())) {
                if (usdKrwRate <= 0) {
                    log.warn("{} 저장 생략 — 환율 조회 실패로 USD 원가($) 그대로 저장 방지", vo.getStockId());
                    continue;
                }
                try {
                    double usdPrice = Double.parseDouble(vo.getPrice());
                    long krwPrice = Math.round(usdPrice * usdKrwRate);
                    vo.setPrice(String.valueOf(krwPrice));
                    log.info("{} USD→KRW: ${} × {} = {}원", vo.getStockId(), usdPrice, usdKrwRate, krwPrice);
                } catch (NumberFormatException e) {
                    log.warn("{} 저장 생략 — 가격 변환 실패: {}", vo.getStockId(), vo.getPrice());
                    continue;
                }
            }
            advStockRepository.saveFromYahoo(vo);
            log.info("{} 저장 완료 (PRICE={}원)", vo.getStockId(), vo.getPrice());
        }
    }

    /**
     * dailyUpdate 용 — 포트폴리오 전체 US 종목 일괄 동기화 (USDKRW=X 포함 1번 호출)
     */
    public void syncInterestUsStocks() throws Exception {
        List<String> allInterestIds = advStockRepository.selectInterestStockIds();
        List<String> usSymbols = allInterestIds.stream()
                .filter(AdvStockYahooService::isUsTicker)
                .collect(Collectors.toList());

        if (usSymbols.isEmpty()) {
            log.info("포트폴리오에 US 주식 없음 — 동기화 생략");
            return;
        }

        log.info("US 주식 가격 동기화 시작: {}건 {}", usSymbols.size(), usSymbols);

        // yahooId → portfolioId 매핑 (BRK.B → BRK-B 로 보내고, 결과는 BRK.B 로 복원)
        Map<String, String> yahooToPortfolio = new HashMap<>();
        List<String> querySymbols = new java.util.ArrayList<>();
        querySymbols.add("USDKRW=X");
        for (String pid : usSymbols) {
            String yahooId = pid.replace(".", "-"); // BRK.B → BRK-B
            querySymbols.add(yahooId);
            yahooToPortfolio.put(yahooId.toUpperCase(), pid.toUpperCase());
            yahooToPortfolio.put(pid.toUpperCase(), pid.toUpperCase()); // 동일한 경우
        }

        List<YahooStockVO> result = advStockYahooParser.fetchQuotesBySymbols(querySymbols);

        double usdKrwRate = result.stream()
                .filter(v -> "USDKRW=X".equals(v.getStockId()))
                .mapToDouble(v -> { try { return Double.parseDouble(v.getPrice()); } catch (Exception e) { return 0; } })
                .findFirst().orElse(0);

        if (usdKrwRate <= 0) log.warn("환율 조회 실패 — USD 원화 변환 생략");
        else log.info("USD/KRW 환율: {}", usdKrwRate);

        int saved = 0;
        for (YahooStockVO vo : result) {
            if ("USDKRW=X".equals(vo.getStockId())) continue;
            // Yahoo 반환 ID → 포트폴리오 원본 ID 복원 (BRK-B → BRK.B)
            String portfolioId = yahooToPortfolio.getOrDefault(vo.getStockId().toUpperCase(), vo.getStockId());
            vo.setStockId(portfolioId);
            if ("USD".equalsIgnoreCase(vo.getCurrency())) {
                if (usdKrwRate <= 0) {
                    log.warn("{} 저장 생략 — 환율 조회 실패로 USD 원가($) 그대로 저장 방지", portfolioId);
                    continue;
                }
                try {
                    double usdPrice = Double.parseDouble(vo.getPrice());
                    long krwPrice = Math.round(usdPrice * usdKrwRate);
                    vo.setPrice(String.valueOf(krwPrice));
                    log.info("{} USD→KRW: ${} × {} = {}원", portfolioId, usdPrice, usdKrwRate, krwPrice);
                } catch (NumberFormatException e) {
                    log.warn("{} 저장 생략 — 가격 변환 실패: {}", portfolioId, vo.getPrice());
                    continue;
                }
            }
            advStockRepository.saveFromYahoo(vo);
            saved++;
        }
        log.info("US 주식 가격 동기화 완료: {}건 저장 (환율: {})", saved, usdKrwRate);
    }

    public void saveDailyPrice(String tradingDate, String keyword) throws Exception {
        List<YahooStockVO> stockList = advStockYahooParser.fetchStockList(keyword);
        log.info("Yahoo Finance Daily Price 저장 시작: {}건 (기준일: {})", stockList.size(), tradingDate);

        for (YahooStockVO vo : stockList) {
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

        log.info("Yahoo Finance Daily Price 저장 완료 (기준일: {})", tradingDate);
    }
}
