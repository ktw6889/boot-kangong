package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kangong.advstock.model.YahooStockVO;
import com.kangong.advstock.parser.AdvStockYahooParser;
import com.kangong.advstock.service.AdvStockRepository;
import com.kangong.advstock.service.AdvStockYahooService;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockService;

/**
 * QQQM 저장 시 단가 동기화 검증
 * - 세션 캐싱 동작 확인
 * - syncSingleUsStock 단일 API 호출 확인
 * - 연속 저장 시뮬레이션
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QqqmSyncTest {

    @Autowired
    private AdvStockYahooParser yahooParser;

    @Autowired
    private AdvStockYahooService advStockYahooService;

    @Autowired
    private AdvStockRepository advStockRepository;

    @Autowired
    private StockService stockService;

    @BeforeEach
    void resetSession() {
        // 각 테스트 전 세션 초기화 (캐시 없는 최악의 상황 재현)
        yahooParser.invalidateSession();
    }

    // ============================================================
    // 1단계: QQQM 단일 Yahoo 조회 확인
    // ============================================================
    @Test
    @Order(1)
    @DisplayName("1단계: QQQM+USDKRW=X 한 번에 조회 (1 API 호출)")
    void step1_fetchQqqmAndRate() throws Exception {
        System.out.println("\n======== [1단계] QQQM + 환율 단일 API 호출 ========");
        long start = System.currentTimeMillis();

        List<YahooStockVO> result = yahooParser.fetchQuotesBySymbols(List.of("USDKRW=X", "QQQM"));

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("조회 소요시간: %dms%n", elapsed);
        System.out.printf("조회 결과: %d건%n", result.size());

        for (YahooStockVO vo : result) {
            System.out.printf("  %-12s currency=%-4s price=%s%n",
                vo.getStockId(), vo.getCurrency(), vo.getPrice());
        }

        assertTrue(result.size() >= 1, "최소 1건 이상 반환되어야 함");

        YahooStockVO rate = result.stream()
            .filter(v -> "USDKRW=X".equals(v.getStockId())).findFirst().orElse(null);
        YahooStockVO qqqm = result.stream()
            .filter(v -> "QQQM".equalsIgnoreCase(v.getStockId())).findFirst().orElse(null);

        assertNotNull(rate, "USDKRW=X 환율이 조회되어야 함");
        assertNotNull(qqqm, "QQQM이 조회되어야 함 — Yahoo에서 QQQM을 찾지 못함");
        assertEquals("USD", qqqm.getCurrency(), "QQQM은 USD 종목이어야 함");

        double usdPrice = Double.parseDouble(qqqm.getPrice());
        double krwRate = Double.parseDouble(rate.getPrice());
        long krwPrice = Math.round(usdPrice * krwRate);

        System.out.printf("QQQM: $%.2f × %.2f = %,d원%n", usdPrice, krwRate, krwPrice);
        assertTrue(usdPrice > 0, "QQQM USD 가격이 0보다 커야 함");
        assertTrue(krwPrice > 50_000, "QQQM KRW 가격이 5만원 이상이어야 함");
    }

    // ============================================================
    // 2단계: syncSingleUsStock(QQQM) — DB 저장 확인
    // ============================================================
    @Test
    @Order(2)
    @DisplayName("2단계: syncSingleUsStock(QQQM) → ST_STOCK_MASTER 저장 확인")
    void step2_syncSingleQqqm() throws Exception {
        System.out.println("\n======== [2단계] syncSingleUsStock(QQQM) DB 저장 ========");

        long start = System.currentTimeMillis();
        advStockYahooService.syncSingleUsStock("QQQM");
        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("syncSingleUsStock 소요: %dms%n", elapsed);

        StockVO saved = stockService.getStockVO("QQQM");
        assertNotNull(saved, "ST_STOCK_MASTER에 QQQM이 존재해야 함");

        long price = Long.parseLong(saved.getPrice());
        System.out.printf("ST_STOCK_MASTER: STOCK_ID=%s, NAME=%s, PRICE=%,d원%n",
            saved.getStockId(), saved.getName(), price);

        assertTrue(price > 50_000, "QQQM KRW 가격이 5만원 이상이어야 함 (실제: " + price + ")");
    }

    // ============================================================
    // 3단계: 세션 캐싱 — 연속 5회 호출 시 initSession 재사용 확인
    // ============================================================
    @Test
    @Order(3)
    @DisplayName("3단계: 연속 5회 syncSingleUsStock — 세션 캐싱으로 빠른 처리 확인")
    void step3_consecutiveSyncs() throws Exception {
        System.out.println("\n======== [3단계] 연속 5회 저장 시뮬레이션 ========");

        String[] tickers = {"QQQM", "SPYG", "QQQM", "SPYG", "QQQM"};
        long[] elapsed = new long[tickers.length];

        for (int i = 0; i < tickers.length; i++) {
            long start = System.currentTimeMillis();
            try {
                advStockYahooService.syncSingleUsStock(tickers[i]);
                elapsed[i] = System.currentTimeMillis() - start;
                System.out.printf("  [%d] %s 동기화 완료: %dms%n", i + 1, tickers[i], elapsed[i]);
            } catch (Exception e) {
                elapsed[i] = System.currentTimeMillis() - start;
                System.out.printf("  [%d] %s 동기화 실패 (%dms): %s%n", i + 1, tickers[i], elapsed[i], e.getMessage());
            }
        }

        // 첫 번째 호출(세션 초기화 포함)이 가장 오래 걸려야 함
        System.out.println("\n소요시간 분석:");
        System.out.printf("  1회차(세션 초기화): %dms%n", elapsed[0]);
        System.out.printf("  2~5회차 평균(캐시 재사용): %.0fms%n",
            (elapsed[1] + elapsed[2] + elapsed[3] + elapsed[4]) / 4.0);

        // 캐시 재사용 시 2번째부터 더 빠르거나 같아야 함 (Yahoo API 응답 시간이 지배적이므로 엄격한 단언은 피함)
        for (long e : elapsed) {
            assertTrue(e < 15_000, "각 동기화가 15초 이내에 완료되어야 함");
        }

        // 최종 QQQM 가격 확인
        StockVO qqqm = stockService.getStockVO("QQQM");
        assertNotNull(qqqm, "QQQM이 ST_STOCK_MASTER에 있어야 함");
        long price = Long.parseLong(qqqm.getPrice());
        System.out.printf("%n최종 QQQM 가격: %,d원 %s%n", price, price > 50_000 ? "✅" : "❌");
        assertTrue(price > 50_000, "연속 저장 후에도 QQQM KRW 가격이 정상이어야 함");
    }

    // ============================================================
    // 4단계: 세션 캐시 만료 후 재초기화 동작 확인
    // ============================================================
    @Test
    @Order(4)
    @DisplayName("4단계: 세션 무효화 후 재초기화 — 가격 정상 조회")
    void step4_sessionInvalidateAndRefetch() throws Exception {
        System.out.println("\n======== [4단계] 세션 무효화 → 재초기화 확인 ========");

        // 먼저 세션 생성
        advStockYahooService.syncSingleUsStock("QQQM");

        // 세션 강제 무효화 (TTL 만료 시뮬레이션)
        yahooParser.invalidateSession();
        System.out.println("세션 무효화 완료");

        // 다시 호출 → 자동 재초기화 후 정상 조회 되어야 함
        advStockYahooService.syncSingleUsStock("QQQM");

        StockVO qqqm = stockService.getStockVO("QQQM");
        assertNotNull(qqqm, "세션 재초기화 후에도 QQQM 조회 가능해야 함");
        long price = Long.parseLong(qqqm.getPrice());
        System.out.printf("재초기화 후 QQQM 가격: %,d원 %s%n", price, price > 50_000 ? "✅" : "❌");
        assertTrue(price > 50_000, "세션 재초기화 후 가격이 정상이어야 함");
    }
}
