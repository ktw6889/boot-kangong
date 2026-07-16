package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockMarketIndexVO;
import com.kangong.stock.parser.StockMobileParser;
import com.kangong.stock.service.StockFetcher;
import com.kangong.stock.service.StockJsonConverter;
import com.kangong.stock.service.StockMobileMarketIndexService;
import com.kangong.stock.service.StockRepository;

class StockMobileMarketIndexServiceTest {

	StockMobileMarketIndexService service;
	StockFetcher mockFetcher;
	StockRepository mockRepository;
	StockMobileParser mockParser;
	StockJsonConverter mockConverter;

	@BeforeEach
	void setUp() throws Exception {
		service = new StockMobileMarketIndexService();
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
		Field field = StockMobileMarketIndexService.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(service, value);
	}

	@Test
	@DisplayName("CommonService 상속 제거 확인")
	void testNoCommonServiceInheritance() {
		assertEquals(Object.class, StockMobileMarketIndexService.class.getSuperclass());
	}

	@Test
	@DisplayName("saveStockMarketIndex - 정상 흐름: StockFetcher → JSON 파싱 → StockRepository 저장")
	void testSaveStockMarketIndex_normalFlow() throws Exception {
		String json = TestResourceLoader.loadJson("testdata/market-index.json");

		when(mockFetcher.fetchMarketIndex()).thenReturn(json);
		when(mockConverter.getExceptZeroFields()).thenReturn(Arrays.asList("id", "deleteYn"));

		service.saveStockMarketIndex();

		verify(mockFetcher).fetchMarketIndex();
		verify(mockRepository).saveMarketIndex(any(StockMarketIndexVO.class));
	}

	@Test
	@DisplayName("getStockMarketIndexVOByJsonConvert - domesticInterest 매핑")
	void testJsonConvert_domesticInterest() throws Exception {
		String json = TestResourceLoader.loadJson("testdata/market-index.json");

		StockMarketIndexVO result = service.getStockMarketIndexVOByJsonConvert(json);

		assertNotNull(result);
		assertNotNull(result.getYyyymmdd());
		assertEquals("3.50", result.getDomesticInterestCall());
	}

	@Test
	@DisplayName("getStockMarketIndexVOByJsonConvert - 여러 카테고리 데이터 매핑")
	void testJsonConvert_multipleCategories() throws Exception {
		String json = TestResourceLoader.loadJson("testdata/market-index-multi-category.json");

		StockMarketIndexVO result = service.getStockMarketIndexVOByJsonConvert(json);

		assertEquals("3.50", result.getDomesticInterestCall());
		assertEquals("5.50", result.getStandardInterestUs());
		assertEquals("2024.50", result.getMetalGc());
		assertEquals("4.12", result.getBondUs10yt());
		assertEquals("75.30", result.getEnergyCl());
	}

	@Test
	@DisplayName("getMarketIndexKeyList - 6개 카테고리 키 포함")
	void testMarketIndexKeyList() throws Exception {
		List<String> keys = service.getMarketIndexKeyList();

		assertEquals(6, keys.size());
		assertTrue(keys.contains("domesticInterest"));
		assertTrue(keys.contains("standardInterest"));
		assertTrue(keys.contains("metals"));
		assertTrue(keys.contains("transport"));
		assertTrue(keys.contains("bond"));
		assertTrue(keys.contains("energy"));
	}

	@Test
	@DisplayName("getIndexList - StockRepository로 위임")
	void testGetIndexList_delegatesToRepository() throws Exception {
		StockMarketIndexVO param = StockMarketIndexVO.builder().build();
		List<StockMarketIndexVO> expected = Arrays.asList(
			StockMarketIndexVO.builder().yyyymmdd("20240115").build()
		);
		doReturn(expected).when(mockRepository).selectIndexList(param);

		List<StockMarketIndexVO> result = service.getIndexList(param);

		assertEquals(1, result.size());
		verify(mockRepository).selectIndexList(param);
	}
}