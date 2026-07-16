package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
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
import com.kangong.stock.model.StockInterestVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockService;

/**
 * SPYG 포트폴리오 저장 → ST_STOCK_MASTER KRW 가격 확인 → 평가금액 확인
 * 실제 DB + Yahoo Finance API 호출 (네트워크 필요)
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpygPortfolioIntegrationTest {

    @Autowired
    private AdvStockYahooParser yahooParser;

    @Autowired
    private AdvStockRepository advStockRepository;

    @Autowired
    private AdvStockYahooService advStockYahooService;

    @Autowired
    private StockService stockService;

    @Autowired
    private SqlSession sqlSession;

    // ============================================================
    // Step 1: Yahoo에서 SPYG USD 가격 + 환율 조회 → 예상 KRW 가격 계산
    // ============================================================
    @Test
    @Order(1)
    @DisplayName("1단계: Yahoo Finance에서 SPYG USD 가격 + USDKRW 환율 조회")
    void step1_fetchSpygUsdAndRate() throws Exception {
        System.out.println("\n======== [1단계] Yahoo Finance SPYG + 환율 조회 ========");

        double rate = yahooParser.fetchUsdKrwRate();
        System.out.printf("USD/KRW 환율: %.2f%n", rate);
        assertTrue(rate > 1000, "환율이 1000 이상이어야 함");

        List<YahooStockVO> result = yahooParser.fetchQuotesBySymbols(List.of("SPYG"));
        assertFalse(result.isEmpty(), "SPYG 조회 결과가 비어있음");

        YahooStockVO vo = result.get(0);
        System.out.printf("SPYG USD 가격  : $%s%n", vo.getPrice());
        System.out.printf("SPYG currency  : %s%n", vo.getCurrency());
        System.out.printf("SPYG 종목명    : %s%n", vo.getName());

        assertEquals("USD", vo.getCurrency(), "SPYG는 USD 종목이어야 함");

        double usdPrice = Double.parseDouble(vo.getPrice());
        long expectedKrw = Math.round(usdPrice * rate);
        System.out.printf("예상 KRW 가격  : %,d원 ($%.2f × %.2f)%n", expectedKrw, usdPrice, rate);

        assertTrue(usdPrice > 0, "USD 가격이 0보다 커야 함");
    }

    // ============================================================
    // Step 2: saveFromYahoo 직접 호출 → ST_STOCK_MASTER에 KRW 가격 저장
    // ============================================================
    @Test
    @Order(2)
    @DisplayName("2단계: saveFromYahoo로 KRW 변환 가격을 ST_STOCK_MASTER에 저장")
    void step2_saveSpygKrwToMaster() throws Exception {
        System.out.println("\n======== [2단계] ST_STOCK_MASTER KRW 가격 저장 ========");

        double rate = yahooParser.fetchUsdKrwRate();
        List<YahooStockVO> result = yahooParser.fetchQuotesBySymbols(List.of("SPYG"));
        assertFalse(result.isEmpty(), "SPYG 조회 결과 없음");

        YahooStockVO vo = result.get(0);
        double usdPrice = Double.parseDouble(vo.getPrice());
        long krwPrice = Math.round(usdPrice * rate);

        // USD→KRW 변환 후 저장 (syncInterestUsStocks 로직과 동일)
        vo.setPrice(String.valueOf(krwPrice));
        advStockRepository.saveFromYahoo(vo);

        System.out.printf("저장 완료: SPYG KRW=%,d원%n", krwPrice);

        // 저장 직후 조회해서 확인
        StockVO saved = stockService.getStockVO("SPYG");
        assertNotNull(saved, "ST_STOCK_MASTER에 SPYG가 존재해야 함");
        System.out.printf("ST_STOCK_MASTER 조회 결과: STOCK_ID=%s, NAME=%s, PRICE=%s%n",
            saved.getStockId(), saved.getName(), saved.getPrice());

        long savedPrice = Long.parseLong(saved.getPrice());
        assertTrue(savedPrice > 100_000, "저장된 KRW 가격이 10만원 이상이어야 함 (실제: " + savedPrice + "원)");
        assertEquals(krwPrice, savedPrice, "저장된 KRW 가격이 계산값과 일치해야 함");
    }

    // ============================================================
    // Step 3: ST_STOCK_INTEREST에서 SPYG 존재 여부 확인 + totalPrice 검증
    // ============================================================
    @Test
    @Order(3)
    @DisplayName("3단계: 포트폴리오 수정화면 raw 조회에서 SPYG totalPrice 확인")
    void step3_verifyTotalPriceInPortfolio() throws Exception {
        System.out.println("\n======== [3단계] 포트폴리오 평가금액(totalPrice) 검증 ========");

        StockInterestVO param = new StockInterestVO();
        List<StockInterestVO> rawList = stockService.getStockInterestRaw(param);

        StockInterestVO spyg = rawList.stream()
            .filter(v -> "SPYG".equalsIgnoreCase(v.getStockId()))
            .findFirst()
            .orElse(null);

        if (spyg == null) {
            System.out.println("⚠️  SPYG가 ST_STOCK_INTEREST에 없음 → 포트폴리오에 추가 후 재테스트 필요");
            System.out.println("    현재 포트폴리오 종목 수: " + rawList.size());
            rawList.stream().limit(5).forEach(v ->
                System.out.printf("    - %s (%s) qty=%.2f totalPrice=%,d원%n",
                    v.getStockId(), v.getName(), v.getQty(), v.getTotalPrice()));
            // SPYG가 없으면 DB에서 ST_STOCK_MASTER 저장 여부만 확인하고 통과
            StockVO master = stockService.getStockVO("SPYG");
            assertNotNull(master, "ST_STOCK_MASTER에는 SPYG가 있어야 함 (2단계에서 저장됨)");
            System.out.printf("ST_STOCK_MASTER SPYG 가격: %s원 (OK)%n", master.getPrice());
            return;
        }

        System.out.printf("SPYG 발견 → stockDivision=%s, qty=%.4f, totalPrice=%,d원%n",
            spyg.getStockDivision(), spyg.getQty(), spyg.getTotalPrice());

        StockVO master = stockService.getStockVO("SPYG");
        long masterPrice = Long.parseLong(master.getPrice());
        long expectedTotal = Math.round(masterPrice * spyg.getQty());

        System.out.printf("ST_STOCK_MASTER PRICE : %,d원%n", masterPrice);
        System.out.printf("qty                   : %.4f%n", spyg.getQty());
        System.out.printf("예상 totalPrice       : %,d원%n", expectedTotal);
        System.out.printf("실제 totalPrice       : %,d원%n", spyg.getTotalPrice());

        assertTrue(spyg.getTotalPrice() > 0, "평가금액이 0보다 커야 함 (실제: " + spyg.getTotalPrice() + ")");
        assertEquals(expectedTotal, spyg.getTotalPrice(), 1,
            "totalPrice가 PRICE × qty와 일치해야 함");
    }

    // ============================================================
    // Step 4: syncInterestUsStocks() 전체 흐름 실행 검증
    // ============================================================
    @Test
    @Order(4)
    @DisplayName("4단계: syncInterestUsStocks() 전체 실행 → 포트폴리오 전체 US 종목 검증")
    void step4_syncAllUsStocks() throws Exception {
        System.out.println("\n======== [4단계] syncInterestUsStocks() 전체 실행 ========");

        // 실행 전 포트폴리오에서 US 종목 목록 확인
        List<String> interestIds = advStockRepository.selectInterestStockIds();
        List<String> usIds = interestIds.stream()
            .filter(id -> id.matches("[A-Za-z]+"))
            .toList();

        System.out.println("포트폴리오 전체 종목: " + interestIds);
        System.out.println("US 종목 (알파벳 티커): " + usIds);

        if (usIds.isEmpty()) {
            System.out.println("⚠️  US 종목이 없어 sync 대상 없음 — 포트폴리오에 SPYG 추가 후 재테스트");
            return;
        }

        // syncInterestUsStocks 실행
        advStockYahooService.syncInterestUsStocks();
        System.out.println("syncInterestUsStocks() 완료");

        // 각 US 종목 가격 확인
        for (String stockId : usIds) {
            StockVO vo = stockService.getStockVO(stockId);
            if (vo == null) {
                System.out.printf("  %-8s → ST_STOCK_MASTER에 없음%n", stockId);
                continue;
            }
            long price = 0;
            try { price = Long.parseLong(vo.getPrice()); } catch (Exception ignored) {}
            System.out.printf("  %-8s %-30s PRICE=%,d원 %s%n",
                stockId, vo.getName(), price, price > 0 ? "✅" : "❌ 가격 없음");
            assertTrue(price > 0, stockId + " KRW 가격이 0임 — 동기화 실패");
        }
    }
}
