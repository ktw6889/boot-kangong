package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockCategoryVO;
import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockCategoryParser;
import com.kangong.stock.parser.StockDailyPriceParser;
import com.kangong.stock.parser.StockFinancialParser;
import com.kangong.stock.parser.StockInfoParser;
import com.kangong.stock.parser.StockSeleniumParser;
import com.kangong.stock.service.StockService;

class StockServiceTest {

	StockService service;
	SqlSession mockSqlSession;
	StockInfoParser mockInfoParser;
	StockFinancialParser mockFinancialParser;
	StockCategoryParser mockCategoryParser;
	StockDailyPriceParser mockDailyPriceParser;
	StockSeleniumParser mockSeleniumParser;

	@BeforeEach
	void setUp() throws Exception {
		service = new StockService();
		mockSqlSession = mock(SqlSession.class);
		mockInfoParser = mock(StockInfoParser.class);
		mockFinancialParser = mock(StockFinancialParser.class);
		mockCategoryParser = mock(StockCategoryParser.class);
		mockDailyPriceParser = mock(StockDailyPriceParser.class);
		mockSeleniumParser = mock(StockSeleniumParser.class);

		injectField("sqlSession", mockSqlSession);
		injectField("stockInfoParser", mockInfoParser);
		injectField("stockFinancialParser", mockFinancialParser);
		injectField("stockCategoryParser", mockCategoryParser);
		injectField("stockDailyPriceParser", mockDailyPriceParser);
		injectField("stockSeleniumParser", mockSeleniumParser);
	}

	private void injectField(String fieldName, Object value) throws Exception {
		Class<?> clazz = service.getClass();
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(service, value);
				return;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException(fieldName);
	}

	// ==================== @Deprecated 제거 검증 ====================

	@Test
	@DisplayName("StockService에 @Deprecated 어노테이션이 없어야 한다")
	void testNoDeprecatedAnnotation() {
		assertFalse(StockService.class.isAnnotationPresent(Deprecated.class));
	}

	// ==================== getStockVO ====================

	@Test
	@DisplayName("getStockVO - stockId로 단건 조회")
	void testGetStockVO() throws Exception {
		StockVO expected = StockVO.builder().stockId("005930").name("삼성전자").build();
		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(expected);

		StockVO result = service.getStockVO("005930");

		assertEquals("삼성전자", result.getName());
		verify(mockSqlSession).selectOne(eq("seckim.stock.select"), any());
	}

	// ==================== getStockList ====================

	@Test
	@DisplayName("getStockList - 목록 조회")
	void testGetStockList() throws Exception {
		List<StockVO> expected = Arrays.asList(
			StockVO.builder().stockId("005930").build(),
			StockVO.builder().stockId("000660").build()
		);
		doReturn(expected).when(mockSqlSession).selectList(eq("seckim.stock.select"), any());

		List<StockVO> result = service.getStockList(StockVO.builder().build());

		assertEquals(2, result.size());
	}

	// ==================== saveStock ====================

	@Test
	@DisplayName("saveStock - 정상 흐름: parse → selenium → merge")
	void testSaveStock() throws Exception {
		StockVO dbVO = StockVO.builder().stockId("005930").build();
		StockVO parsedVO = StockVO.builder().stockId("005930").name("삼성전자").build();
		StockVO dividendVO = StockVO.builder().stockId("005930").name("삼성전자").build();
		StockVO zeroFilledVO = StockVO.builder().stockId("005930").name("삼성전자").build();

		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(dbVO);
		when(mockInfoParser.parseStockDetail("005930", dbVO)).thenReturn(parsedVO);
		when(mockSeleniumParser.getStockDividendInfo(parsedVO)).thenReturn(dividendVO);
		when(mockInfoParser.insertZero(dividendVO)).thenReturn(zeroFilledVO);

		service.saveStock("005930");

		verify(mockInfoParser).parseStockDetail("005930", dbVO);
		verify(mockSeleniumParser).getStockDividendInfo(parsedVO);
		verify(mockSqlSession).update("seckim.stock.save", zeroFilledVO);
	}

	@Test
	@DisplayName("saveStock - Selenium 실패 시에도 저장 진행")
	void testSaveStock_seleniumFailure() throws Exception {
		StockVO dbVO = StockVO.builder().stockId("005930").build();
		StockVO parsedVO = StockVO.builder().stockId("005930").name("삼성전자").build();
		StockVO zeroFilledVO = StockVO.builder().stockId("005930").name("삼성전자").build();

		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(dbVO);
		when(mockInfoParser.parseStockDetail("005930", dbVO)).thenReturn(parsedVO);
		when(mockSeleniumParser.getStockDividendInfo(parsedVO)).thenThrow(new RuntimeException("Selenium error"));
		when(mockInfoParser.insertZero(parsedVO)).thenReturn(zeroFilledVO);

		service.saveStock("005930");

		verify(mockSqlSession).update("seckim.stock.save", zeroFilledVO);
	}

	// ==================== saveAllStock (saveStockList 제거 확인) ====================

	@Test
	@DisplayName("saveAllStock - 전체 종목 반복 저장")
	void testSaveAllStock() throws Exception {
		List<StockVO> stockList = Arrays.asList(
			StockVO.builder().stockId("005930").name("삼성전자").build(),
			StockVO.builder().stockId("000660").name("SK하이닉스").build()
		);
		doReturn(stockList).when(mockSqlSession).selectList(eq("seckim.stock.select"), any());

		StockVO vo1 = StockVO.builder().stockId("005930").build();
		StockVO vo2 = StockVO.builder().stockId("000660").build();
		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(vo1, vo2);
		when(mockInfoParser.parseStockDetail(anyString(), any())).thenReturn(StockVO.builder().build());
		when(mockSeleniumParser.getStockDividendInfo(any())).thenReturn(StockVO.builder().build());
		when(mockInfoParser.insertZero(any())).thenReturn(StockVO.builder().build());

		service.saveAllStock();

		verify(mockInfoParser, times(2)).parseStockDetail(anyString(), any());
		verify(mockSqlSession, times(2)).update(eq("seckim.stock.save"), any());
	}

	@Test
	@DisplayName("saveAllStock - 빈 목록이면 처리 없음")
	void testSaveAllStock_emptyList() throws Exception {
		doReturn(new ArrayList<>()).when(mockSqlSession).selectList(eq("seckim.stock.select"), any());

		service.saveAllStock();

		verify(mockSqlSession, never()).update(eq("seckim.stock.save"), any());
	}

	@Test
	@DisplayName("saveStockList 메서드가 제거되었다")
	void testSaveStockListRemoved() {
		assertThrows(NoSuchMethodException.class, () ->
			StockService.class.getMethod("saveStockList"));
	}

	// ==================== saveDailyPriceAll (saveDailyPriceList 제거 확인) ====================

	@Test
	@DisplayName("saveDailyPrice - tradingDate가 '0'이면 저장하지 않음")
	void testSaveDailyPrice_skipZeroDate() throws Exception {
		StockVO stockVO = StockVO.builder().stockId("005930").build();
		List<StockDailyPriceVO> prices = Arrays.asList(
			StockDailyPriceVO.builder().stockId("005930").tradingDate("2024-01-15").build(),
			StockDailyPriceVO.builder().stockId("005930").tradingDate("0").build()
		);

		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(stockVO);
		when(mockDailyPriceParser.getStockDailyPriceList(stockVO)).thenReturn(prices);

		service.saveDailyPrice("005930");

		verify(mockSqlSession, times(1)).update(eq("seckim.stock.saveStockDailyPrice"), any());
	}

	@Test
	@DisplayName("saveDailyPriceAll - 전체 종목 반복 저장")
	void testSaveDailyPriceAll() throws Exception {
		List<StockVO> stockList = Arrays.asList(
			StockVO.builder().stockId("005930").name("삼성전자").build()
		);
		StockVO stockVO = StockVO.builder().stockId("005930").build();
		List<StockDailyPriceVO> prices = Arrays.asList(
			StockDailyPriceVO.builder().stockId("005930").tradingDate("2024-01-15").build()
		);

		doReturn(stockList).when(mockSqlSession).selectList(eq("seckim.stock.select"), any());
		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(stockVO);
		when(mockDailyPriceParser.getStockDailyPriceList(any())).thenReturn(prices);

		service.saveDailyPriceAll();

		verify(mockSqlSession).update(eq("seckim.stock.saveStockDailyPrice"), any());
	}

	@Test
	@DisplayName("saveDailyPriceList 메서드가 제거되었다")
	void testSaveDailyPriceListRemoved() {
		assertThrows(NoSuchMethodException.class, () ->
			StockService.class.getMethod("saveDailyPriceList"));
	}

	// ==================== mergeFinancial 통합 검증 ====================

	@Test
	@DisplayName("saveStockFinancial - mergeFinancial에 saveFinancail SQL ID 사용")
	void testSaveStockFinancial() throws Exception {
		StockVO stockVO = StockVO.builder().stockId("005930").build();
		StockFinancialVO fin1 = StockFinancialVO.builder().stockId("005930").year("2023").build();
		StockFinancialVO fin2 = StockFinancialVO.builder().stockId("005930").year("2022").build();

		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(stockVO);
		when(mockFinancialParser.getStockFinancial(stockVO)).thenReturn(Arrays.asList(fin1, fin2));
		when(mockFinancialParser.insertZero(any())).thenAnswer(inv -> inv.getArgument(0));

		service.saveStockFinancial("005930");

		verify(mockSqlSession, times(2)).update(eq("seckim.stock.saveFinancail"), any());
		verify(mockSqlSession, never()).update(eq("seckim.stock.saveFinancail2"), any());
	}

	@Test
	@DisplayName("saveStockFinancial2 - mergeFinancial에 saveFinancail2 SQL ID 사용")
	void testSaveStockFinancial2() throws Exception {
		StockVO stockVO = StockVO.builder().stockId("005930").build();
		StockFinancialVO fin = StockFinancialVO.builder().stockId("005930").year("2023").build();

		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(stockVO);
		when(mockFinancialParser.getStockFinancial2(stockVO)).thenReturn(Arrays.asList(fin));
		when(mockFinancialParser.insertZero(any())).thenAnswer(inv -> inv.getArgument(0));

		service.saveStockFinancial2("005930");

		verify(mockSqlSession, times(1)).update(eq("seckim.stock.saveFinancail2"), any());
		verify(mockSqlSession, never()).update(eq("seckim.stock.saveFinancail"), any());
	}

	@Test
	@DisplayName("mergeFinancial이 private으로 변경되었다")
	void testMergeFinancialIsPrivate() {
		assertThrows(NoSuchMethodException.class, () ->
			StockService.class.getMethod("mergeFinancial", StockFinancialVO.class));
		assertThrows(NoSuchMethodException.class, () ->
			StockService.class.getMethod("mergeFinancial2", StockFinancialVO.class));
	}

	// ==================== saveFinancialList / saveFinancialList2 ====================

	@Test
	@DisplayName("saveFinancialList - 전체 종목에 대해 saveStockFinancial 호출")
	void testSaveFinancialList() throws Exception {
		List<StockVO> stockList = Arrays.asList(
			StockVO.builder().stockId("005930").name("삼성전자").build()
		);
		StockVO stockVO = StockVO.builder().stockId("005930").build();
		StockFinancialVO fin = StockFinancialVO.builder().stockId("005930").year("2023").build();

		doReturn(stockList).when(mockSqlSession).selectList(eq("seckim.stock.select"), any());
		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(stockVO);
		when(mockFinancialParser.getStockFinancial(any())).thenReturn(Arrays.asList(fin));
		when(mockFinancialParser.insertZero(any())).thenAnswer(inv -> inv.getArgument(0));

		service.saveFinancialList();

		verify(mockSqlSession).update(eq("seckim.stock.saveFinancail"), any());
	}

	@Test
	@DisplayName("saveFinancialList2 - 전체 종목에 대해 saveStockFinancial2 호출")
	void testSaveFinancialList2() throws Exception {
		List<StockVO> stockList = Arrays.asList(
			StockVO.builder().stockId("005930").name("삼성전자").build()
		);
		StockVO stockVO = StockVO.builder().stockId("005930").build();
		StockFinancialVO fin = StockFinancialVO.builder().stockId("005930").year("2023").build();

		doReturn(stockList).when(mockSqlSession).selectList(eq("seckim.stock.select"), any());
		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any())).thenReturn(stockVO);
		when(mockFinancialParser.getStockFinancial2(any())).thenReturn(Arrays.asList(fin));
		when(mockFinancialParser.insertZero(any())).thenAnswer(inv -> inv.getArgument(0));

		service.saveFinancialList2();

		verify(mockSqlSession).update(eq("seckim.stock.saveFinancail2"), any());
	}

	// ==================== forEachStock 에러 처리 ====================

	@Test
	@DisplayName("forEachStock - 하나의 종목 실패 시 나머지 계속 처리")
	void testForEachStock_errorHandling() throws Exception {
		List<StockVO> stockList = Arrays.asList(
			StockVO.builder().stockId("FAIL1").name("실패종목").build(),
			StockVO.builder().stockId("005930").name("삼성전자").build()
		);
		doReturn(stockList).when(mockSqlSession).selectList(eq("seckim.stock.select"), any());

		StockVO stockVO = StockVO.builder().stockId("005930").build();
		when(mockSqlSession.selectOne(eq("seckim.stock.select"), any()))
			.thenThrow(new RuntimeException("DB error"))
			.thenReturn(stockVO);
		when(mockInfoParser.parseStockDetail(anyString(), any())).thenReturn(StockVO.builder().build());
		when(mockSeleniumParser.getStockDividendInfo(any())).thenReturn(StockVO.builder().build());
		when(mockInfoParser.insertZero(any())).thenReturn(StockVO.builder().build());

		service.saveAllStock();

		verify(mockSqlSession, times(1)).update(eq("seckim.stock.save"), any());
	}

	// ==================== saveStockCategoryList ====================

	@Test
	@DisplayName("saveStockCategoryList - 업종 + 테마 모두 저장")
	void testSaveStockCategoryList() throws Exception {
		Map<String, List<StockCategoryVO>> categoryMap = new HashMap<>();
		categoryMap.put("categoryList", Arrays.asList(StockCategoryVO.builder().build()));
		categoryMap.put("categoryLinkList", Arrays.asList(StockCategoryVO.builder().build()));

		Map<String, List<StockCategoryVO>> themeMap = new HashMap<>();
		themeMap.put("categoryList", Arrays.asList(StockCategoryVO.builder().build()));
		themeMap.put("categoryLinkList", Arrays.asList(StockCategoryVO.builder().build()));

		when(mockCategoryParser.getStockCategoryList()).thenReturn(categoryMap);
		when(mockCategoryParser.getStockThemeList()).thenReturn(themeMap);

		service.saveStockCategoryList();

		verify(mockSqlSession, times(2)).update(eq("seckim.stock.saveStockCategory"), any());
		verify(mockSqlSession, times(2)).update(eq("seckim.stock.saveStockCategoryLink"), any());
	}

	// ==================== StockOperations 인터페이스 구현 검증 ====================

	@Test
	@DisplayName("StockService는 StockOperations를 구현한다")
	void testImplementsStockOperations() {
		assertTrue(com.kangong.stock.service.StockOperations.class.isAssignableFrom(StockService.class));
	}
}
