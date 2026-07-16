package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockMobileParser;
import com.kangong.stock.service.StockFieldMappingRegistry;
import com.kangong.stock.service.StockFetcher;
import com.kangong.stock.service.StockJsonConverter;
import com.kangong.stock.service.StockMobileEsgService;
import com.kangong.stock.service.StockRepository;

class StockMobileEsgServiceTest {

	StockMobileEsgService service;
	StockFetcher mockFetcher;
	StockRepository mockRepository;
	StockMobileParser mockParser;
	StockJsonConverter mockConverter;

	@BeforeEach
	void setUp() throws Exception {
		service = new StockMobileEsgService();
		mockFetcher = mock(StockFetcher.class);
		mockRepository = mock(StockRepository.class);
		mockParser = new StockMobileParser();
		mockConverter = mock(StockJsonConverter.class);

		injectField("stockFetcher", mockFetcher);
		injectField("stockRepository", mockRepository);
		injectField("stockMobileParser", mockParser);
		injectField("stockJsonConverter", mockConverter);
	}

	private void injectField(String fieldName, Object value) throws Exception {
		Field field = StockMobileEsgService.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(service, value);
	}

	@Test
	@DisplayName("CommonService 상속 제거 확인")
	void testNoCommonServiceInheritance() {
		assertEquals(Object.class, StockMobileEsgService.class.getSuperclass());
	}

	@Test
	@DisplayName("saveStockEsg - 정상 흐름: StockFetcher → JSON 파싱 → StockRepository 저장")
	void testSaveStockEsg_normalFlow() throws Exception {
		String stockId = "005930";
		String json = TestResourceLoader.loadJson("testdata/esg-e-theme.json");

		when(mockFetcher.fetchEsg(stockId)).thenReturn(json);
		when(mockConverter.getExceptZeroFields()).thenReturn(Arrays.asList("id", "deleteYn"));

		service.saveStockEsg(stockId);

		verify(mockFetcher).fetchEsg(stockId);
		verify(mockRepository).saveEsg(any(StockEsgVO.class));
	}

	@Test
	@DisplayName("parseEsgJson - E 테마 데이터 파싱")
	void testEsgJsonConvert_eTheme() throws Exception {
		String json = TestResourceLoader.loadJson("testdata/esg-e-theme.json");

		Map<String, StockEsgVO> result = service.parseEsgJson(json);

		assertEquals(1, result.size());
		assertTrue(result.containsKey("2023"));
		StockEsgVO vo = result.get("2023");
		assertEquals("005930", vo.getStockId());
		assertEquals("2023", vo.getYear());
		assertEquals("85", vo.getWaterRecyclingRate());
		assertEquals("72", vo.getGreenHouseEmission());
	}

	@Test
	@DisplayName("parseEsgJson - 다년도 데이터 파싱")
	void testEsgJsonConvert_multiYear() throws Exception {
		String json = TestResourceLoader.loadJson("testdata/esg-e-theme-multi-year.json");

		Map<String, StockEsgVO> result = service.parseEsgJson(json);

		assertEquals(2, result.size());
		assertTrue(result.containsKey("2022"));
		assertTrue(result.containsKey("2023"));
	}

	@Test
	@DisplayName("THEME_KEYS - ESG_FIELD_MAP에서 자동 파생, E/S/G 3개 테마")
	void testThemeKeys() throws Exception {
		Field field = StockMobileEsgService.class.getDeclaredField("THEME_KEYS");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, List<String>> themeKeys = (Map<String, List<String>>) field.get(null);

		assertEquals(3, themeKeys.size());
		assertEquals(5, themeKeys.get("E").size());
		assertEquals(4, themeKeys.get("S").size());
		assertEquals(5, themeKeys.get("G").size());
		assertTrue(themeKeys.get("E").contains("E01"));
		assertTrue(themeKeys.get("S").contains("S01"));
		assertTrue(themeKeys.get("G").contains("G01"));
	}

	@Test
	@DisplayName("THEME_KEYS와 ESG_FIELD_MAP의 키가 일치")
	void testThemeKeysMatchFieldMap() throws Exception {
		Field field = StockMobileEsgService.class.getDeclaredField("THEME_KEYS");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, List<String>> themeKeys = (Map<String, List<String>>) field.get(null);

		long totalKeys = themeKeys.values().stream().mapToLong(List::size).sum();
		assertEquals(StockFieldMappingRegistry.esgMobileApi().size(), totalKeys);
	}

	@Test
	@DisplayName("saveStockEsgAll - StockRepository에서 종목 목록 직접 조회")
	void testSaveAll_usesRepositoryDirectly() throws Exception {
		doReturn(Arrays.asList(StockVO.builder().stockId("005930").build()))
			.when(mockRepository).selectStockList(any());
		when(mockFetcher.fetchEsg(anyString())).thenReturn("{\"nonFinanceInfo\":{},\"themes\":[]}");

		service.saveStockEsgAll();

		verify(mockRepository).selectStockList(any());
	}
}
