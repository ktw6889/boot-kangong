package com.kangong.test.stock;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.config.StockSeleniumSelectorConfig;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockSeleniumParser;

import static org.junit.jupiter.api.Assertions.*;

class StockSeleniumParserTest {

	StockSeleniumSelectorConfig selectorConfig;
	StockSeleniumParser parser;

	@BeforeEach
	void setUp() {
		selectorConfig = new StockSeleniumSelectorConfig();
		parser = new StockSeleniumParser(selectorConfig);
	}

	// ==================== SelectorConfig 기본값 검증 ====================

	@Test
	@DisplayName("SelectorConfig 기본값이 모두 비어있지 않음")
	void testAllSelectorDefaultsNotEmpty() {
		assertNotNull(selectorConfig.getNaverFinanceUrl());
		assertNotNull(selectorConfig.getIframeCoinfo());
		assertNotNull(selectorConfig.getAnalysisTab());
		assertNotNull(selectorConfig.getDividendValue());
		assertNotNull(selectorConfig.getEnterpriseState());
		assertNotNull(selectorConfig.getAnnualTab());
		assertNotNull(selectorConfig.getEnterpriseHeader());
		assertNotNull(selectorConfig.getEnterpriseTbody());
		assertNotNull(selectorConfig.getFinancialAnalysis());
		assertNotNull(selectorConfig.getBalanceSheetTab());
		assertNotNull(selectorConfig.getBalanceSheetHeader());
		assertNotNull(selectorConfig.getBalanceSheetTbody());

		assertFalse(selectorConfig.getAnalysisTab().isEmpty());
		assertFalse(selectorConfig.getDividendValue().isEmpty());
		assertFalse(selectorConfig.getEnterpriseState().isEmpty());
	}

	@Test
	@DisplayName("NAVER_FINANCE_URL 기본값이 올바른 형식")
	void testNaverFinanceUrl() {
		String url = selectorConfig.getNaverFinanceUrl();
		assertTrue(url.startsWith("https://finance.naver.com/"));
		assertTrue(url.endsWith("code="));
	}

	@Test
	@DisplayName("XPath 셀렉터 기본값이 유효한 XPath 형식")
	void testXPathSelectorsFormat() {
		List<String> xpathValues = Arrays.asList(
			selectorConfig.getAnalysisTab(),
			selectorConfig.getDividendValue(),
			selectorConfig.getAnnualTab(),
			selectorConfig.getEnterpriseHeader(),
			selectorConfig.getEnterpriseTbody(),
			selectorConfig.getFinancialAnalysis(),
			selectorConfig.getBalanceSheetTab(),
			selectorConfig.getBalanceSheetHeader(),
			selectorConfig.getBalanceSheetTbody()
		);
		for (String value : xpathValues) {
			assertTrue(value.startsWith("/") || value.startsWith("./"),
				"XPath 형식이 아님: " + value);
		}
	}

	@Test
	@DisplayName("CSS 셀렉터 기본값이 CSS 선택자 형식")
	void testCssSelectorFormat() {
		String value = selectorConfig.getEnterpriseState();
		assertTrue(value.contains("#") || value.contains(".") || value.contains(">"),
			"CSS 선택자 형식이 아님: " + value);
	}

	@Test
	@DisplayName("SelectorConfig 값 변경 시 반영됨")
	void testSelectorConfigOverride() {
		selectorConfig.setAnalysisTab("/custom/xpath");
		assertEquals("/custom/xpath", selectorConfig.getAnalysisTab());
	}

	// ==================== 리팩토링 구조 검증 ====================

	@Test
	@DisplayName("waitAndClick 메서드 존재")
	void testWaitAndClickMethodExists() {
		assertDoesNotThrow(() ->
			StockSeleniumParser.class.getDeclaredMethod("waitAndClick",
				org.openqa.selenium.By.class));
	}

	@Test
	@DisplayName("waitForElement 메서드 존재")
	void testWaitForElementMethodExists() {
		assertDoesNotThrow(() ->
			StockSeleniumParser.class.getDeclaredMethod("waitForElement",
				org.openqa.selenium.By.class));
	}

	@Test
	@DisplayName("waitForFrameAndSwitch 메서드 존재")
	void testWaitForFrameAndSwitchMethodExists() {
		assertDoesNotThrow(() ->
			StockSeleniumParser.class.getDeclaredMethod("waitForFrameAndSwitch",
				String.class));
	}

	@Test
	@DisplayName("selectorConfig 필드가 final로 주입됨")
	void testSelectorConfigField() throws Exception {
		var field = StockSeleniumParser.class.getDeclaredField("selectorConfig");
		assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()));
		assertEquals(StockSeleniumSelectorConfig.class, field.getType());
	}

	@Test
	@DisplayName("driverWait 필드가 존재하고 WebDriverWait 타입")
	void testDriverWaitFieldExists() throws Exception {
		var field = StockSeleniumParser.class.getDeclaredField("driverWait");
		assertEquals(org.openqa.selenium.support.ui.WebDriverWait.class, field.getType());
	}

	// ==================== createFinancialVOListWithLookup 순수 로직 테스트 ====================

	private List<StockFinancialVO> invokeCreateFinancialVOList(
			List<String> headerList, StockVO stockVO,
			Function<StockFinancialVO, StockFinancialVO> lookup) throws Exception {
		Method method = StockSeleniumParser.class.getDeclaredMethod(
			"createFinancialVOListWithLookup", List.class, StockVO.class, Function.class);
		method.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<StockFinancialVO> result = (List<StockFinancialVO>) method.invoke(parser, headerList, stockVO, lookup);
		return result;
	}

	@Test
	@DisplayName("createFinancialVOListWithLookup - lookup 없이 새 VO 생성")
	void testCreateFinancialVOList_noLookup() throws Exception {
		StockVO stockVO = StockVO.builder().id("MASTER_1").stockId("005930").build();
		List<String> years = Arrays.asList("2022", "2023", "2024");

		List<StockFinancialVO> result = invokeCreateFinancialVOList(years, stockVO, null);

		assertEquals(3, result.size());
		for (int i = 0; i < years.size(); i++) {
			assertEquals(years.get(i), result.get(i).getYear());
			assertEquals("005930", result.get(i).getStockId());
			assertEquals("MASTER_1", result.get(i).getStockMasterId());
			assertNull(result.get(i).getId());
		}
	}

	@Test
	@DisplayName("createFinancialVOListWithLookup - lookup이 null 반환하면 새 VO 사용")
	void testCreateFinancialVOList_lookupReturnsNull() throws Exception {
		StockVO stockVO = StockVO.builder().id("M1").stockId("000660").build();
		List<String> years = Arrays.asList("2023");

		List<StockFinancialVO> result = invokeCreateFinancialVOList(years, stockVO, vo -> null);

		assertEquals(1, result.size());
		assertEquals("2023", result.get(0).getYear());
		assertEquals("000660", result.get(0).getStockId());
	}

	@Test
	@DisplayName("createFinancialVOListWithLookup - lookup이 기존 VO 반환하면 기존 VO 사용")
	void testCreateFinancialVOList_lookupReturnsExisting() throws Exception {
		StockVO stockVO = StockVO.builder().id("M1").stockId("005930").build();
		List<String> years = Arrays.asList("2022", "2023");

		StockFinancialVO existingVO = StockFinancialVO.builder()
			.id("EXISTING_ID").year("2022").stockId("005930")
			.totalSales("1000000").build();

		Function<StockFinancialVO, StockFinancialVO> lookup = vo -> {
			if ("2022".equals(vo.getYear())) return existingVO;
			return null;
		};

		List<StockFinancialVO> result = invokeCreateFinancialVOList(years, stockVO, lookup);

		assertEquals(2, result.size());
		assertSame(existingVO, result.get(0));
		assertEquals("EXISTING_ID", result.get(0).getId());
		assertEquals("1000000", result.get(0).getTotalSales());
		assertNull(result.get(1).getId());
		assertEquals("2023", result.get(1).getYear());
	}

	@Test
	@DisplayName("createFinancialVOListWithLookup - lookup이 id 없는 VO 반환하면 새 VO 사용")
	void testCreateFinancialVOList_lookupReturnsVOWithoutId() throws Exception {
		StockVO stockVO = StockVO.builder().id("M1").stockId("005930").build();
		List<String> years = Arrays.asList("2024");

		StockFinancialVO emptyVO = StockFinancialVO.builder().year("2024").build();

		List<StockFinancialVO> result = invokeCreateFinancialVOList(years, stockVO,
			vo -> emptyVO);

		assertEquals(1, result.size());
		assertNotSame(emptyVO, result.get(0));
		assertEquals("005930", result.get(0).getStockId());
	}

	@Test
	@DisplayName("createFinancialVOListWithLookup - 빈 headerList면 빈 리스트 반환")
	void testCreateFinancialVOList_emptyHeaders() throws Exception {
		StockVO stockVO = StockVO.builder().stockId("005930").build();

		List<StockFinancialVO> result = invokeCreateFinancialVOList(
			Arrays.asList(), stockVO, null);

		assertTrue(result.isEmpty());
	}

	// ==================== closeDriver 안전성 ====================

	@Test
	@DisplayName("closeDriver - driver가 null일 때 예외 없이 동작")
	void testCloseDriver_nullDriver() {
		assertDoesNotThrow(() -> parser.closeDriver());
	}
}
