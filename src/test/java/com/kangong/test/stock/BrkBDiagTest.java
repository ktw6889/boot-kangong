package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.advstock.model.YahooStockVO;
import com.kangong.advstock.parser.AdvStockYahooParser;

/**
 * BRK.B 가져오기 실패 원인 진단
 */
class BrkBDiagTest {

    AdvStockYahooParser parser = new AdvStockYahooParser();

    @Test
    @DisplayName("진단 1: [A-Za-z]+ 필터에서 BRK.B 매칭 여부")
    void diag1_filterMatch() {
        String id = "BRK.B";
        System.out.println("[진단 1] stockId 필터 테스트");
        System.out.println("  [A-Za-z]+  매칭: " + id.matches("[A-Za-z]+"));
        System.out.println("  [A-Za-z][A-Za-z0-9.\\-]* 매칭: " + id.matches("[A-Za-z][A-Za-z0-9.\\-]*"));

        assertFalse(id.matches("[A-Za-z]+"), "BRK.B는 현재 필터([A-Za-z]+)에 매칭 안 됨 — 이게 문제!");
        assertTrue(id.matches("[A-Za-z][A-Za-z0-9.\\-]*"), "새 필터로는 매칭되어야 함");
    }

    @Test
    @DisplayName("진단 2: Yahoo에 BRK.B 쿼리 → 반환 symbol/stockId 확인")
    void diag2_yahooReturnedSymbol() throws Exception {
        System.out.println("\n[진단 2] Yahoo Finance BRK.B 조회 결과");

        List<YahooStockVO> result = parser.fetchQuotesBySymbols(List.of("BRK.B", "BRK-B"));
        System.out.println("조회 결과 건수: " + result.size());

        for (YahooStockVO vo : result) {
            System.out.printf("  stockId=%-8s name=%-30s currency=%s price=%s%n",
                vo.getStockId(), vo.getName(), vo.getCurrency(), vo.getPrice());
        }

        assertFalse(result.isEmpty(), "BRK.B 또는 BRK-B 조회 결과가 있어야 함");
    }

    @Test
    @DisplayName("진단 3: BRK-B 로 조회 시 stockId 값 확인 (convertToVO 처리)")
    void diag3_brkBWithDash() throws Exception {
        System.out.println("\n[진단 3] BRK-B 심볼로 조회");

        List<YahooStockVO> result = parser.fetchQuotesBySymbols(List.of("BRK-B"));
        System.out.println("조회 결과 건수: " + result.size());

        for (YahooStockVO vo : result) {
            System.out.printf("  stockId=%-8s name=%-30s currency=%s price=%s%n",
                vo.getStockId(), vo.getName(), vo.getCurrency(), vo.getPrice());
        }
    }
}
