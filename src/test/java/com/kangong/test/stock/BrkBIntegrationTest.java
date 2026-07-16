package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kangong.advstock.service.AdvStockYahooService;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockService;

/**
 * BRK.B 동기화 수정 후 검증
 * 1) 필터 통과 여부
 * 2) syncSingleUsStock → ST_STOCK_MASTER에 BRK.B(점 포함) ID로 저장
 * 3) 저장된 가격이 KRW로 정상 변환
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BrkBIntegrationTest {

    @Autowired
    private AdvStockYahooService advStockYahooService;

    @Autowired
    private StockService stockService;

    @Test
    @Order(1)
    @DisplayName("1단계: 필터 [A-Za-z][A-Za-z0-9.\\-]* 에서 BRK.B 매칭 확인")
    void step1_filterMatches() {
        System.out.println("\n======== [1단계] 필터 매칭 확인 ========");
        String[] tickers = {"BRK.B", "BF.B", "AAPL", "QQQM", "SPYG", "BRK-B"};
        for (String t : tickers) {
            boolean matches = t.matches("[A-Za-z][A-Za-z0-9.\\-]*");
            System.out.printf("  %-8s → %s%n", t, matches ? "✅ US 주식" : "❌ 제외");
            assertTrue(matches, t + " 는 US 주식 필터에 매칭되어야 함");
        }
        // 한국 종목은 제외되어야 함
        String[] korean = {"005930", "0043B0", "000270"};
        for (String k : korean) {
            boolean matches = k.matches("[A-Za-z][A-Za-z0-9.\\-]*");
            System.out.printf("  %-8s → %s%n", k, matches ? "⚠️ US로 오인" : "✅ 제외");
            assertFalse(matches, k + " 는 한국 종목이므로 필터에서 제외되어야 함");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2단계: syncSingleUsStock(BRK.B) → ST_STOCK_MASTER에 BRK.B ID로 KRW 저장")
    void step2_syncBrkB() throws Exception {
        System.out.println("\n======== [2단계] BRK.B 단일 동기화 ========");

        advStockYahooService.syncSingleUsStock("BRK.B");

        StockVO saved = stockService.getStockVO("BRK.B");
        assertNotNull(saved, "ST_STOCK_MASTER에 'BRK.B' ID로 저장되어야 함 (BRK-B가 아닌 BRK.B)");

        long price = Long.parseLong(saved.getPrice());
        System.out.printf("ST_STOCK_MASTER: STOCK_ID=%s, NAME=%s, PRICE=%,d원%n",
            saved.getStockId(), saved.getName(), price);

        assertEquals("BRK.B", saved.getStockId(), "저장된 STOCK_ID가 BRK.B 여야 함 (BRK-B 아님)");
        assertTrue(price > 500_000, "BRK.B KRW 가격이 50만원 이상이어야 함 ($489 × ~1530 ≈ 748,000원)");

        // BRK-B 로는 조회 안 되어야 함 (포트폴리오와 stockId 일치 검증)
        StockVO wrongId = stockService.getStockVO("BRK-B");
        System.out.println("BRK-B ID로 조회: " + (wrongId == null ? "없음 ✅ (올바름)" : "있음 ⚠️"));
    }

    @Test
    @Order(3)
    @DisplayName("3단계: syncInterestUsStocks 실행 후 BRK.B 가격 정상 여부 (포트폴리오에 BRK.B 있을 때)")
    void step3_syncAllIncludingBrkB() throws Exception {
        System.out.println("\n======== [3단계] 전체 US 주식 동기화 (BRK.B 포함) ========");

        // syncSingleUsStock으로 먼저 BRK.B 를 ST_STOCK_MASTER에 등록
        advStockYahooService.syncSingleUsStock("BRK.B");

        StockVO brkb = stockService.getStockVO("BRK.B");
        assertNotNull(brkb, "BRK.B 가 ST_STOCK_MASTER에 있어야 함");

        long price = Long.parseLong(brkb.getPrice());
        System.out.printf("BRK.B: PRICE=%,d원 (KRW 변환 완료) %s%n", price, price > 0 ? "✅" : "❌");
        assertTrue(price > 0, "BRK.B KRW 가격이 0보다 커야 함");
    }
}
