package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockCategoryLinkVO;
import com.kangong.stock.model.StockCategoryVO;
import com.kangong.stock.service.StockFetcher;
import com.kangong.stock.service.StockMobileCategory;
import com.kangong.stock.service.StockRepository;

class StockMobileCategoryTest {

	StockMobileCategory service;
	StockFetcher mockFetcher;
	StockRepository mockRepository;

	@BeforeEach
	void setUp() throws Exception {
		service = new StockMobileCategory();
		mockFetcher = mock(StockFetcher.class);
		mockRepository = mock(StockRepository.class);

		injectField("stockFetcher", mockFetcher);
		injectField("stockRepository", mockRepository);
	}

	private void injectField(String fieldName, Object value) throws Exception {
		Field field = StockMobileCategory.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(service, value);
	}

	@Test
	@DisplayName("CommonService 상속 제거 확인")
	void testNoCommonServiceInheritance() {
		assertEquals(Object.class, StockMobileCategory.class.getSuperclass());
	}

	// ==================== JSON 변환 ====================

	@Test
	@DisplayName("parseCategoryJson - 정상 파싱")
	void testCategoryJsonConvert_normal() throws Exception {
		String json = "{\"groups\":[" +
			"{\"name\":\"반도체\",\"no\":261}," +
			"{\"name\":\"자동차\",\"no\":262}" +
			"]}";

		List<StockCategoryVO> result = service.parseCategoryJson(json, "INDUSTRY");

		assertEquals(2, result.size());
		assertEquals("반도체", result.get(0).getCategoryName());
		assertEquals("261", result.get(0).getCategoryNo());
		assertEquals("INDUSTRY", result.get(0).getCategoryType());
		assertEquals("자동차", result.get(1).getCategoryName());
		assertEquals("262", result.get(1).getCategoryNo());
	}

	@Test
	@DisplayName("parseCategoryJson - 빈 groups이면 빈 리스트")
	void testCategoryJsonConvert_empty() throws Exception {
		List<StockCategoryVO> result = service.parseCategoryJson("{\"groups\":[]}", "INDUSTRY");
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("parseCategoryLinkJson - 정상 파싱")
	void testCategoryLinkJsonConvert_normal() throws Exception {
		String json = "{\"stocks\":[" +
			"{\"itemCode\":\"005930\"}," +
			"{\"itemCode\":\"000660\"}" +
			"]}";

		List<StockCategoryLinkVO> result = service.parseCategoryLinkJson(json, "261");

		assertEquals(2, result.size());
		assertEquals("261", result.get(0).getCategoryNo());
		assertEquals("005930", result.get(0).getStockId());
		assertEquals("000660", result.get(1).getStockId());
	}

	@Test
	@DisplayName("parseCategoryLinkJson - 빈 stocks이면 빈 리스트")
	void testCategoryLinkJsonConvert_empty() throws Exception {
		List<StockCategoryLinkVO> result = service.parseCategoryLinkJson("{\"stocks\":[]}", "261");
		assertTrue(result.isEmpty());
	}

	// ==================== 저장 오케스트레이션 ====================

	@Test
	@DisplayName("saveStockCategory - 2페이지 배치 처리")
	void testSaveStockCategory() throws Exception {
		String json = "{\"groups\":[{\"name\":\"테스트\",\"no\":100}]}";
		String linkJson = "{\"stocks\":[]}";

		when(mockFetcher.fetchCategory(anyInt())).thenReturn(json);
		when(mockFetcher.fetchCategoryLink(anyString(), anyInt())).thenReturn(linkJson);

		service.saveStockCategory();

		verify(mockFetcher).fetchCategory(1);
		verify(mockFetcher).fetchCategory(2);
		verify(mockRepository, times(2)).saveCategory(any(StockCategoryVO.class));
	}

	@Test
	@DisplayName("saveStockCategoryLink - 5페이지 배치 처리")
	void testSaveStockCategoryLink() throws Exception {
		StockCategoryVO categoryVO = StockCategoryVO.builder().categoryNo("261").build();
		String linkJson = "{\"stocks\":[{\"itemCode\":\"005930\"}]}";

		when(mockFetcher.fetchCategoryLink(eq("261"), anyInt())).thenReturn(linkJson);

		service.saveStockCategoryLink(categoryVO);

		verify(mockFetcher, times(5)).fetchCategoryLink(eq("261"), anyInt());
		verify(mockRepository, times(5)).saveCategoryLink(any(StockCategoryLinkVO.class));
	}

	// ==================== 의존성 확인 ====================

	@Test
	@DisplayName("StockFetcher 필드 존재")
	void testHasStockFetcherField() throws Exception {
		Field field = StockMobileCategory.class.getDeclaredField("stockFetcher");
		assertEquals(StockFetcher.class, field.getType());
	}

	@Test
	@DisplayName("StockRepository 필드 존재")
	void testHasStockRepositoryField() throws Exception {
		Field field = StockMobileCategory.class.getDeclaredField("stockRepository");
		assertEquals(StockRepository.class, field.getType());
	}

	@Test
	@DisplayName("StockMobileParser 필드 없음 (StockFetcher로 대체)")
	void testNoStockMobileParserField() {
		assertThrows(NoSuchFieldException.class, () ->
			StockMobileCategory.class.getDeclaredField("stockMobileParser"));
	}
}