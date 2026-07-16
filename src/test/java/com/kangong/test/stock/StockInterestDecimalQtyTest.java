package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.kangong.stock.controller.StockController;
import com.kangong.stock.model.StockInterestVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockService;
import com.kangong.stock.service.MacroIndicatorService;

/**
 * SPYG 16.831214 같은 소수 수량을 포트폴리오에 저장할 때
 * qty int→double 변경으로 정상 처리되는지 검증
 */
class StockInterestDecimalQtyTest {

    StockController controller;
    StockService mockStockService;

    @BeforeEach
    void setUp() throws Exception {
        controller = new StockController();
        mockStockService = mock(StockService.class);
        injectField("stockService", mockStockService);
    }

    // ──────────────────────────────────────────
    // 1. VO 타입 검증: qty 필드가 double인지 확인
    // ──────────────────────────────────────────
    @Test
    @DisplayName("StockInterestVO.qty 는 double 타입이어야 한다")
    void qtyFieldShouldBeDouble() throws Exception {
        Field qtyField = StockInterestVO.class.getDeclaredField("qty");
        assertEquals(double.class, qtyField.getType(),
            "qty가 int면 소수 수량(예: 16.831214) 바인딩 시 NumberFormatException 발생");
    }

    // ──────────────────────────────────────────
    // 2. 소수 수량 설정/조회 정상 동작 확인
    // ──────────────────────────────────────────
    @Test
    @DisplayName("소수 수량 16.831214 을 VO에 설정하고 조회할 수 있다")
    void shouldAcceptDecimalQty() {
        StockInterestVO vo = new StockInterestVO();
        vo.setQty(16.831214);

        assertEquals(16.831214, vo.getQty(), 0.000001,
            "소수 수량이 정확하게 저장/조회되어야 한다");
    }

    // ──────────────────────────────────────────
    // 3. 컨트롤러 saveStockInterest — 기존 종목 있는 경우
    // ──────────────────────────────────────────
    @Test
    @DisplayName("saveStockInterest: 기존 종목 있을 때 소수 수량 저장 정상 처리")
    void saveInterestWithExistingStock() throws Exception {
        StockInterestVO vo = StockInterestVO.builder()
            .stockId("SPYG")
            .stockDivision("미국주식")
            .qty(16.831214)
            .stockPotion(20.0)
            .build();

        StockVO masterVO = StockVO.builder()
            .stockId("SPYG")
            .name("Spdr Series Trust")
            .price("70000")
            .marketCapitalization("0")
            .build();

        when(mockStockService.getStockVO("SPYG")).thenReturn(masterVO);
        doNothing().when(mockStockService).saveStockInterest(any(StockInterestVO.class));

        String result = controller.saveStockInterest(vo);

        assertEquals("OK", result);
        verify(mockStockService).saveStockInterest(vo);
        assertEquals("Spdr Series Trust", vo.getName(), "종목명이 마스터에서 설정되어야 한다");
    }

    // ──────────────────────────────────────────
    // 4. 컨트롤러 saveStockInterest — 마스터 없는 신규 종목
    // ──────────────────────────────────────────
    @Test
    @DisplayName("saveStockInterest: 마스터에 없는 종목도 소수 수량으로 저장 가능")
    void saveInterestWithNewStock() throws Exception {
        StockInterestVO vo = StockInterestVO.builder()
            .stockId("SPYG")
            .stockDivision("미국주식")
            .qty(16.831214)
            .stockPotion(20.0)
            .build();

        when(mockStockService.getStockVO("SPYG")).thenReturn(null);
        doNothing().when(mockStockService).saveStockMasterMin(any(StockVO.class));
        doNothing().when(mockStockService).saveStockInterest(any(StockInterestVO.class));

        String result = controller.saveStockInterest(vo);

        assertEquals("OK", result);
        verify(mockStockService).saveStockMasterMin(any(StockVO.class));
        verify(mockStockService).saveStockInterest(vo);
    }

    // ──────────────────────────────────────────
    // 5. 정수 수량도 여전히 정상 동작 (회귀 테스트)
    // ──────────────────────────────────────────
    @Test
    @DisplayName("정수 수량(예: 10)도 double 타입에서 정상 저장된다 — 회귀 확인")
    void integerQtyStillWorksAfterDoubleChange() throws Exception {
        StockInterestVO vo = StockInterestVO.builder()
            .stockId("005930")
            .stockDivision("국내주식")
            .qty(10.0)
            .stockPotion(15.0)
            .build();

        StockVO masterVO = StockVO.builder()
            .stockId("005930").name("삼성전자").price("70000").marketCapitalization("0").build();

        when(mockStockService.getStockVO("005930")).thenReturn(masterVO);
        doNothing().when(mockStockService).saveStockInterest(any(StockInterestVO.class));

        String result = controller.saveStockInterest(vo);

        assertEquals("OK", result);
        assertEquals(10.0, vo.getQty(), 0.0001);
    }

    // ──────────────────────────────────────────
    // 6. Spring 바인딩 시뮬레이션: "16.831214" 문자열 → double 변환
    // ──────────────────────────────────────────
    @Test
    @DisplayName("문자열 '16.831214'을 double 필드에 직접 set — Spring 바인딩 시뮬레이션")
    void springBindingSimulation() {
        // Spring MVC는 내부적으로 Double.parseDouble(str) 로 변환
        // int일 때는 Integer.parseInt("16.831214") → NumberFormatException
        String inputQty = "16.831214";

        assertDoesNotThrow(() -> {
            double parsed = Double.parseDouble(inputQty);
            StockInterestVO vo = new StockInterestVO();
            vo.setQty(parsed);
            assertEquals(16.831214, vo.getQty(), 0.000001);
        }, "소수 문자열을 double로 파싱하는 데 실패해서는 안 된다");

        // int였을 때 발생하던 오류 재현
        assertThrows(NumberFormatException.class, () -> {
            Integer.parseInt(inputQty);
        }, "int로 파싱하면 NumberFormatException 발생 — 수정 전 버그 재현");
    }

    // ──────────────────────────────────────────
    // Helper
    // ──────────────────────────────────────────
    private void injectField(String fieldName, Object value) throws Exception {
        Field f = StockController.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(controller, value);
    }
}
