package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockFetcher;
import com.kangong.stock.service.StockJsonConverter;
import com.kangong.stock.service.StockMobileMarketIndexService;
import com.kangong.stock.service.StockMobileService;
import com.kangong.stock.service.StockRepository;

class StockMobileServiceTest {

	StockMobileService service;
	StockFetcher mockFetcher;
	StockRepository mockRepository;
	StockJsonConverter mockConverter;
	StockMobileMarketIndexService mockMarketIndexService;

	@BeforeEach
	void setUp() throws Exception {
		service = new StockMobileService();
		mockFetcher = mock(StockFetcher.class);
		mockRepository = mock(StockRepository.class);
		mockConverter = mock(StockJsonConverter.class);
		mockMarketIndexService = mock(StockMobileMarketIndexService.class);

		injectField("stockFetcher", mockFetcher);
		injectField("stockRepository", mockRepository);
		injectField("stockJsonConverter", mockConverter);
		injectField("stockMobileMarketIndexService", mockMarketIndexService);
	}

	private void injectField(String fieldName, Object value) throws Exception {
		Field field = StockMobileService.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(service, value);
	}

	// ==================== 조회 ====================

	@Test
	@DisplayName("getStockList - StockRepository로 위임")
	void testGetStockList() throws Exception {
		StockVO param = StockVO.builder().build();
		List<StockVO> expected = Arrays.asList(StockVO.builder().stockId("005930").build());
		doReturn(expected).when(mockRepository).selectStockList(any());

		List<StockVO> result = service.getStockList(param);

		assertEquals(1, result.size());
		verify(mockRepository).selectStockList(any());
	}

	@Test
	@DisplayName("getStockVO - stockId로 단건 조회")
	void testGetStockVO() throws Exception {
		StockVO expected = StockVO.builder().stockId("005930").name("삼성전자").build();
		doReturn(expected).when(mockRepository).selectStock(any());

		StockVO result = service.getStockVO("005930");

		assertEquals("삼성전자", result.getName());
		verify(mockRepository).selectStock(any());
	}

	// ==================== saveStock ====================

	@Test
	@DisplayName("saveStock - 정상 흐름: fetch → validate → convert → save")
	void testSaveStock_normalFlow() throws Exception {
		String stockId = "005930";
		String jsonData = "{\"stockName\":\"삼성전자\"}";
		StockVO existingVO = StockVO.builder().stockId(stockId).build();
		StockVO convertedVO = StockVO.builder().stockId(stockId).name("삼성전자").build();

		when(mockFetcher.fetchStockDetail(stockId)).thenReturn(jsonData);
		when(mockConverter.checkValidJson(jsonData)).thenReturn(true);
		doReturn(existingVO).when(mockRepository).selectStock(any());
		when(mockConverter.convertStockDetail(existingVO, jsonData)).thenReturn(convertedVO);

		service.saveStock(stockId);

		verify(mockFetcher).fetchStockDetail(stockId);
		verify(mockConverter).checkValidJson(jsonData);
		verify(mockConverter).convertStockDetail(existingVO, jsonData);
		verify(mockRepository).saveStock(convertedVO);
	}

	@Test
	@DisplayName("saveStock - 유효하지 않은 JSON이면 저장하지 않음")
	void testSaveStock_invalidJson() throws Exception {
		String stockId = "005930";
		String invalidJson = "{\"code\":\"StockConflict\"}";

		when(mockFetcher.fetchStockDetail(stockId)).thenReturn(invalidJson);
		when(mockConverter.checkValidJson(invalidJson)).thenReturn(false);

		service.saveStock(stockId);

		verify(mockFetcher).fetchStockDetail(stockId);
		verify(mockConverter).checkValidJson(invalidJson);
		verify(mockRepository, never()).saveStock(any());
	}

	// ==================== saveDailyPrice ====================

	@Test
	@DisplayName("saveDailyPrice(stockId, page) - fetch → convert → save")
	void testSaveDailyPrice_singlePage() throws Exception {
		String stockId = "005930";
		String jsonData = "[]";
		List<StockDailyPriceVO> dailyPrices = Arrays.asList(
			StockDailyPriceVO.builder().stockId(stockId).tradingDate("2024-01-15").build(),
			StockDailyPriceVO.builder().stockId(stockId).tradingDate("2024-01-14").build()
		);

		when(mockFetcher.fetchDailyPrice(stockId, 1)).thenReturn(jsonData);
		when(mockConverter.convertDailyPriceList(stockId, jsonData)).thenReturn(dailyPrices);

		service.saveDailyPrice(stockId, 1);

		verify(mockFetcher).fetchDailyPrice(stockId, 1);
		verify(mockConverter).convertDailyPriceList(stockId, jsonData);
		verify(mockRepository, times(2)).saveDailyPrice(any());
	}

	@Test
	@DisplayName("saveDailyPrice(stockId) - DAILY_PRICE_PAGE_COUNT(3)만큼 페이지 반복")
	void testSaveDailyPrice_multiplePages() throws Exception {
		String stockId = "005930";
		when(mockFetcher.fetchDailyPrice(eq(stockId), anyInt())).thenReturn("[]");
		when(mockConverter.convertDailyPriceList(eq(stockId), anyString()))
			.thenReturn(new ArrayList<>());

		service.saveDailyPrice(stockId);

		verify(mockFetcher).fetchDailyPrice(stockId, 1);
		verify(mockFetcher).fetchDailyPrice(stockId, 2);
		verify(mockFetcher).fetchDailyPrice(stockId, 3);
	}

	@Test
	@DisplayName("saveDailyPrice - 빈 리스트면 repository 호출 없음")
	void testSaveDailyPrice_emptyList() throws Exception {
		when(mockFetcher.fetchDailyPrice(anyString(), anyInt())).thenReturn("[]");
		when(mockConverter.convertDailyPriceList(anyString(), anyString()))
			.thenReturn(new ArrayList<>());

		service.saveDailyPrice("005930", 1);

		verify(mockRepository, never()).saveDailyPrice(any());
	}

	// ==================== saveSimpleStockListPage ====================

	@Test
	@DisplayName("saveSimpleStockListPage - fetch → convert → 각 항목 saveSimple")
	void testSaveSimpleStockListPage() throws Exception {
		String json = "{\"stocks\":[]}";
		List<StockVO> stocks = Arrays.asList(
			StockVO.builder().stockId("005930").build(),
			StockVO.builder().stockId("000660").build()
		);

		when(mockFetcher.fetchSimpleStockList(1)).thenReturn(json);
		when(mockConverter.convertSimpleStockList(json)).thenReturn(new ArrayList<>(stocks));

		java.lang.reflect.Method method = StockMobileService.class.getDeclaredMethod(
			"saveSimpleStockListPage", int.class);
		method.setAccessible(true);
		method.invoke(service, 1);

		verify(mockFetcher).fetchSimpleStockList(1);
		verify(mockConverter).convertSimpleStockList(json);
		verify(mockRepository, times(2)).saveSimple(any());
	}

	// ==================== saveAllStock ====================

	@Test
	@DisplayName("saveAllStock - 전체 종목 조회 후 각각 saveStock 호출")
	void testSaveAllStock() throws Exception {
		List<StockVO> stockList = Arrays.asList(
			StockVO.builder().stockId("005930").build(),
			StockVO.builder().stockId("000660").build()
		);

		doReturn(stockList).when(mockRepository).selectStockList(any());
		when(mockFetcher.fetchStockDetail(anyString())).thenReturn("{\"stockName\":\"test\"}");
		when(mockConverter.checkValidJson(anyString())).thenReturn(true);
		doReturn(StockVO.builder().build()).when(mockRepository).selectStock(any());
		when(mockConverter.convertStockDetail(any(), anyString())).thenReturn(StockVO.builder().build());

		service.saveAllStock();

		verify(mockRepository).selectStockList(any());
		verify(mockFetcher, times(2)).fetchStockDetail(anyString());
	}

	@Test
	@DisplayName("saveAllStock - 빈 목록이면 fetch 호출 없음")
	void testSaveAllStock_emptyList() throws Exception {
		doReturn(new ArrayList<>()).when(mockRepository).selectStockList(any());

		service.saveAllStock();

		verify(mockFetcher, never()).fetchStockDetail(anyString());
	}

	// ==================== updateDailyInfo ====================

	@Test
	@DisplayName("updateDailyInfo - MarketIndex 서비스 호출 포함")
	void testUpdateDailyInfo() throws Exception {
		doReturn(new ArrayList<>()).when(mockRepository).selectStockList(any());

		service.updateDailyInfo();

		verify(mockMarketIndexService).saveStockMarketIndex();
	}

	// ==================== 구조 검증 ====================

	@Test
	@DisplayName("StockMobileService는 더 이상 CommonService를 상속하지 않음")
	void testNoCommonServiceInheritance() {
		assertEquals(Object.class, StockMobileService.class.getSuperclass());
	}

	@Test
	@DisplayName("StockMobileService에 sqlSession 필드가 없음")
	void testNoSqlSessionField() {
		assertThrows(NoSuchFieldException.class, () ->
			StockMobileService.class.getDeclaredField("sqlSession"));
	}

	@Test
	@DisplayName("StockFetcher 필드 존재")
	void testHasStockFetcherField() throws Exception {
		Field field = StockMobileService.class.getDeclaredField("stockFetcher");
		assertEquals(StockFetcher.class, field.getType());
	}

	@Test
	@DisplayName("StockRepository 필드 존재")
	void testHasStockRepositoryField() throws Exception {
		Field field = StockMobileService.class.getDeclaredField("stockRepository");
		assertEquals(StockRepository.class, field.getType());
	}
}