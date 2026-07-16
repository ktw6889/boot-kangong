package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.advstock.model.YahooStockVO;
import com.kangong.advstock.parser.AdvStockYahooParser;

/**
 * US 주식 Yahoo Finance 가격 조회 + USD→KRW 변환 검증 (Spring 컨텍스트 불필요)
 */
class UsStockYahooSyncTest {

    AdvStockYahooParser parser;

    @BeforeEach
    void setUp() {
        parser = new AdvStockYahooParser();
    }

    @Test
    @DisplayName("USDKRW=X 환율 조회 — 1000 이상 반환 여부 확인")
    void testFetchUsdKrwRate() throws Exception {
        double rate = parser.fetchUsdKrwRate();
        System.out.println("USD/KRW 환율: " + rate);
        assertTrue(rate > 1000, "환율이 1000원 이상이어야 함 (실제: " + rate + ")");
    }

    @Test
    @DisplayName("SPYG 가격 조회 — USD 가격 및 currency 필드 확인")
    void testFetchSpygUsdPrice() throws Exception {
        List<YahooStockVO> result = parser.fetchQuotesBySymbols(List.of("SPYG"));
        assertFalse(result.isEmpty(), "SPYG 조회 결과가 비어있음");

        YahooStockVO vo = result.get(0);
        System.out.println("SPYG stockId : " + vo.getStockId());
        System.out.println("SPYG name    : " + vo.getName());
        System.out.println("SPYG price   : " + vo.getPrice());
        System.out.println("SPYG currency: " + vo.getCurrency());

        assertEquals("SPYG", vo.getStockId());
        assertEquals("USD", vo.getCurrency(), "SPYG currency는 USD여야 함");
        double price = Double.parseDouble(vo.getPrice());
        assertTrue(price > 0, "SPYG 가격이 0보다 커야 함");
    }

    @Test
    @DisplayName("SPYG USD→KRW 변환 결과 검증")
    void testSpygKrwConversion() throws Exception {
        double rate = parser.fetchUsdKrwRate();

        List<YahooStockVO> result = parser.fetchQuotesBySymbols(List.of("SPYG"));
        assertFalse(result.isEmpty(), "SPYG 조회 결과가 비어있음");

        YahooStockVO vo = result.get(0);
        double usdPrice = Double.parseDouble(vo.getPrice());
        long krwPrice = Math.round(usdPrice * rate);

        System.out.printf("SPYG: $%.2f × %.2f = %,d원%n", usdPrice, rate, krwPrice);

        assertTrue(krwPrice > 50_000, "SPYG KRW 가격이 5만원 이상이어야 함 (실제: " + krwPrice + "원)");
    }

    @Test
    @DisplayName("복수 US 주식 조회 — SPYG, QQQ, AAPL")
    void testFetchMultipleUsStocks() throws Exception {
        double rate = parser.fetchUsdKrwRate();

        List<YahooStockVO> result = parser.fetchQuotesBySymbols(List.of("SPYG", "QQQ", "AAPL"));
        System.out.println("조회 건수: " + result.size());

        for (YahooStockVO vo : result) {
            double usdPrice = 0;
            long krwPrice = 0;
            try {
                usdPrice = Double.parseDouble(vo.getPrice());
                if ("USD".equalsIgnoreCase(vo.getCurrency())) {
                    krwPrice = Math.round(usdPrice * rate);
                }
            } catch (NumberFormatException ignored) {}

            System.out.printf("%-6s %-30s currency=%-4s $%-8.2f → %,d원%n",
                vo.getStockId(), vo.getName(), vo.getCurrency(), usdPrice, krwPrice);
        }

        assertFalse(result.isEmpty(), "조회 결과가 비어있으면 안 됨");
    }
}
