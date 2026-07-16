package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockMobileParser;
import com.kangong.stock.service.StockFetcher;
import com.kangong.stock.service.StockMobileFinancialService;
import com.kangong.stock.service.StockRepository;

class StockMobileFinancialServiceTest {

	StockMobileFinancialService service;
	StockFetcher mockFetcher;
	StockRepository mockRepository;
	StockMobileParser mockParser;

	@BeforeEach
	void setUp() throws Exception {
		service = new StockMobileFinancialService();
		mockFetcher = mock(StockFetcher.class);
		mockRepository = mock(StockRepository.class);
		mockParser = new StockMobileParser();

		injectField("stockFetcher", mockFetcher);
		injectField("stockRepository", mockRepository);
		injectField("stockMobileParser", mockParser);
	}

	private void injectField(String fieldName, Object value) throws Exception {
		Field field = StockMobileFinancialService.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(service, value);
	}

	@Test
	@DisplayName("CommonService 상속 제거 확인")
	void testNoCommonServiceInheritance() {
		assertEquals(Object.class, StockMobileFinancialService.class.getSuperclass());
	}

	@Test
	@DisplayName("saveStockFinancial - 정상 흐름: StockFetcher → JSON 파싱 → StockRepository 저장")
	void testSaveStockFinancial_normalFlow() throws Exception {
		String stockId = "005930";
		String json = "{\"financeInfo\":{" +
			"\"itemCode\":\"005930\"," +
			"\"trTitleList\":[{\"key\":\"2023/12\"}]," +
			"\"rowList\":[{\"title\":\"매출액\",\"columns\":{\"2023/12\":{\"value\":\"258,935\"}}}]" +
			"}}";

		when(mockFetcher.fetchFinancial(stockId)).thenReturn(json);

		service.saveStockFinancial(stockId);

		verify(mockFetcher).fetchFinancial(stockId);
		verify(mockRepository).saveFinancial(any(StockFinancialVO.class));
	}

	@Test
	@DisplayName("saveStockFinancial - financeInfo가 null이면 저장하지 않음")
	void testSaveStockFinancial_nullFinanceInfo() throws Exception {
		when(mockFetcher.fetchFinancial("005930")).thenReturn("{\"financeInfo\":null}");

		service.saveStockFinancial("005930");

		verify(mockFetcher).fetchFinancial("005930");
		verify(mockRepository, never()).saveFinancial(any());
	}

	@Test
	@DisplayName("getStockFinancialListByJsonConvert - 다년도 데이터 파싱")
	void testJsonConvert_multiYear() throws Exception {
		String json = "{\"financeInfo\":{" +
			"\"itemCode\":\"005930\"," +
			"\"trTitleList\":[{\"key\":\"2022/12\"},{\"key\":\"2023/12\"}]," +
			"\"rowList\":[{\"title\":\"매출액\",\"columns\":{\"2022/12\":{\"value\":\"302,231\"},\"2023/12\":{\"value\":\"258,935\"}}}]" +
			"}}";

		Map<String, StockFinancialVO> result = service.getStockFinancialListByJsonConvert(json);

		assertEquals(2, result.size());
		assertTrue(result.containsKey("2022/12"));
		assertTrue(result.containsKey("2023/12"));
		assertEquals("2022", result.get("2022/12").getYear());
		assertEquals("2023", result.get("2023/12").getYear());
		assertEquals("005930", result.get("2022/12").getStockId());
	}

	@Test
	@DisplayName("getStockFinancialListByJsonConvert - 빈 financeInfo이면 빈 맵 반환")
	void testJsonConvert_empty() throws Exception {
		Map<String, StockFinancialVO> result = service.getStockFinancialListByJsonConvert("{\"financeInfo\":null}");
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("saveStockFinancialAll - StockRepository에서 종목 목록 직접 조회")
	void testSaveAll_usesRepositoryDirectly() throws Exception {
		doReturn(Arrays.asList(StockVO.builder().stockId("005930").build()))
			.when(mockRepository).selectStockList(any());
		when(mockFetcher.fetchFinancial(anyString())).thenReturn("{\"financeInfo\":null}");

		service.saveStockFinancialAll();

		verify(mockRepository).selectStockList(any());
	}

	@Test
	@DisplayName("getExceptStockFinancialListZeroAttribute - 3개 제외 필드")
	void testExceptFields() {
		List<String> fields = service.getExceptStockFinancialListZeroAttribute();

		assertEquals(3, fields.size());
		assertTrue(fields.contains("id"));
		assertTrue(fields.contains("deleteYn"));
		assertTrue(fields.contains("stockMasterId"));
	}
}