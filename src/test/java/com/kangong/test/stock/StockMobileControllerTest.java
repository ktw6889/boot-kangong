package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.controller.StockMobileController;
import com.kangong.stock.service.StockMobileCategory;
import com.kangong.stock.service.StockMobileEsgService;
import com.kangong.stock.service.StockMobileFinancialService;
import com.kangong.stock.service.StockMobileMarketIndexService;
import com.kangong.stock.service.StockMobileService;

class StockMobileControllerTest {

	StockMobileController controller;
	StockMobileService mockService;
	StockMobileFinancialService mockFinancial;
	StockMobileCategory mockCategory;
	StockMobileEsgService mockEsg;
	StockMobileMarketIndexService mockMarketIndex;

	@BeforeEach
	void setUp() throws Exception {
		controller = new StockMobileController();
		mockService = mock(StockMobileService.class);
		mockFinancial = mock(StockMobileFinancialService.class);
		mockCategory = mock(StockMobileCategory.class);
		mockEsg = mock(StockMobileEsgService.class);
		mockMarketIndex = mock(StockMobileMarketIndexService.class);

		injectField("stockMobileService", mockService);
		injectField("stockMobileFinancial", mockFinancial);
		injectField("stockMobileCategory", mockCategory);
		injectField("stockMobileEsgService", mockEsg);
		injectField("stockMobileMarketIndexService", mockMarketIndex);
	}

	private void injectField(String fieldName, Object value) throws Exception {
		Field field = StockMobileController.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(controller, value);
	}

	// ==================== STOCK_LIST_VIEW 상수 검증 ====================

	@Test
	@DisplayName("STOCK_LIST_VIEW 상수가 올바른 값")
	void testStockListViewConstant() throws Exception {
		Field field = StockMobileController.class.getDeclaredField("STOCK_LIST_VIEW");
		field.setAccessible(true);
		assertEquals("kims:/stock/stockList", field.get(null));
	}

	// ==================== delegateToService 헬퍼 검증 ====================

	@Test
	@DisplayName("delegateToService 메서드 존재")
	void testDelegateToServiceMethodExists() {
		assertDoesNotThrow(() -> {
			Method method = StockMobileController.class.getDeclaredMethod("delegateToService",
				Class.forName("com.kangong.stock.controller.StockMobileController$ServiceAction"));
			assertNotNull(method);
		});
	}

	// ==================== 14개 위임 핸들러 — 서비스 호출 + 뷰 반환 검증 ====================

	@Test
	@DisplayName("updateDailyStock → stockMobileService.updateDailyStock()")
	void testUpdateDailyStock() throws Exception {
		String result = controller.updateDailyStock();
		assertEquals("kims:/stock/stockList", result);
		verify(mockService).updateDailyStock();
	}

	@Test
	@DisplayName("saveSimpleStockList → stockMobileService.saveSimpleStockList()")
	void testSaveSimpleStockList() throws Exception {
		String result = controller.saveSimpleStockList();
		assertEquals("kims:/stock/stockList", result);
		verify(mockService).saveSimpleStockList();
	}

	@Test
	@DisplayName("updateDaily → stockMobileService.updateDailyInfo()")
	void testUpdateDaily() throws Exception {
		String result = controller.updateDaily();
		assertEquals("kims:/stock/stockList", result);
		verify(mockService).updateDailyInfo();
	}

	@Test
	@DisplayName("saveAllStockId → stockMobileService.saveAllStockId()")
	void testSaveAllStockId() throws Exception {
		String result = controller.saveAllStockId();
		assertEquals("kims:/stock/stockList", result);
		verify(mockService).saveAllStockId();
	}

	@Test
	@DisplayName("saveStock(stockId) → stockMobileService.saveStock(stockId)")
	void testSaveStock() throws Exception {
		String result = controller.saveStock("005930");
		assertEquals("kims:/stock/stockList", result);
		verify(mockService).saveStock("005930");
	}

	@Test
	@DisplayName("saveAllStock → stockMobileService.saveAllStock()")
	void testSaveAllStock() throws Exception {
		String result = controller.saveAllStock();
		assertEquals("kims:/stock/stockList", result);
		verify(mockService).saveAllStock();
	}

	@Test
	@DisplayName("saveDaillyPriceAllMig → stockMobileService.saveDailyPriceAllMigration()")
	void testSaveDaillyPriceAllMig() throws Exception {
		String result = controller.saveDaillyPriceAllMig();
		assertEquals("kims:/stock/stockList", result);
		verify(mockService).saveDailyPriceAllMigration();
	}

	@Test
	@DisplayName("saveDaillyPrice(stockId) → stockMobileService.saveDailyPrice(stockId, 1)")
	void testSaveDaillyPrice() throws Exception {
		String result = controller.saveDaillyPrice("005930");
		assertEquals("kims:/stock/stockList", result);
		verify(mockService).saveDailyPrice("005930", 1);
	}

	@Test
	@DisplayName("saveStockFinancial(stockId) → stockMobileFinancial.saveStockFinancial(stockId)")
	void testSaveStockFinancial() throws Exception {
		String result = controller.saveStockFinancial("005930");
		assertEquals("kims:/stock/stockList", result);
		verify(mockFinancial).saveStockFinancial("005930");
	}

	@Test
	@DisplayName("saveStockFinancialAll → stockMobileFinancial.saveStockFinancialAll()")
	void testSaveStockFinancialAll() throws Exception {
		String result = controller.saveStockFinancialAll();
		assertEquals("kims:/stock/stockList", result);
		verify(mockFinancial).saveStockFinancialAll();
	}

	@Test
	@DisplayName("saveStockCategory → stockMobileCategory.saveStockCategory()")
	void testSaveStockCategory() throws Exception {
		String result = controller.saveStockCategory();
		assertEquals("kims:/stock/stockList", result);
		verify(mockCategory).saveStockCategory();
	}

	@Test
	@DisplayName("saveStockEsg(stockId) → stockMobileEsgService.saveStockEsg(stockId)")
	void testSaveStockEsg() throws Exception {
		String result = controller.saveStockEsg("005930");
		assertEquals("kims:/stock/stockList", result);
		verify(mockEsg).saveStockEsg("005930");
	}

	@Test
	@DisplayName("saveStockEsgAll → stockMobileEsgService.saveStockEsgAll()")
	void testSaveStockEsgAll() throws Exception {
		String result = controller.saveStockEsgAll();
		assertEquals("kims:/stock/stockList", result);
		verify(mockEsg).saveStockEsgAll();
	}

	@Test
	@DisplayName("saveStockMarketIndex → stockMobileMarketIndexService.saveStockMarketIndex()")
	void testSaveStockMarketIndex() throws Exception {
		String result = controller.saveStockMarketIndex();
		assertEquals("kims:/stock/stockList", result);
		verify(mockMarketIndex).saveStockMarketIndex();
	}

	// ==================== 예외 전파 검증 ====================

	@Test
	@DisplayName("서비스 예외가 컨트롤러로 전파됨")
	void testExceptionPropagation() throws Exception {
		doThrow(new RuntimeException("DB error")).when(mockService).updateDailyStock();
		assertThrows(RuntimeException.class, () -> controller.updateDailyStock());
	}

	// ==================== 핸들러에 Model 파라미터 제거 확인 ====================

	@Test
	@DisplayName("위임 핸들러에 Model 파라미터가 없음")
	void testNoModelParameterInDelegateHandlers() throws Exception {
		assertDoesNotThrow(() ->
			StockMobileController.class.getMethod("updateDailyStock"));
		assertDoesNotThrow(() ->
			StockMobileController.class.getMethod("saveSimpleStockList"));
		assertDoesNotThrow(() ->
			StockMobileController.class.getMethod("saveAllStock"));
		assertDoesNotThrow(() ->
			StockMobileController.class.getMethod("saveStockFinancialAll"));
		assertDoesNotThrow(() ->
			StockMobileController.class.getMethod("saveStockCategory"));
	}
}
