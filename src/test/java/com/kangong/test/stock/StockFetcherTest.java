package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.parser.StockMobileParser;
import com.kangong.stock.service.StockFetcher;

class StockFetcherTest {

	StockFetcher stockFetcher;
	StockMobileParser mockParser;

	@BeforeEach
	void setUp() throws Exception {
		stockFetcher = new StockFetcher();
		mockParser = mock(StockMobileParser.class);
		Field field = StockFetcher.class.getDeclaredField("stockMobileParser");
		field.setAccessible(true);
		field.set(stockFetcher, mockParser);
	}

	// ==================== URL 생성 테스트 ====================

	@Test
	@DisplayName("simpleStockListUrl - 페이지 번호 포함")
	void testSimpleStockListUrl() {
		String url = stockFetcher.simpleStockListUrl(3);
		assertEquals("https://m.stock.naver.com/api/index/KOSPI/enrollStocks?pageSize=25&type=object&page=3", url);
	}

	@Test
	@DisplayName("stockDetailUrl - stockId 포함")
	void testStockDetailUrl() {
		String url = stockFetcher.stockDetailUrl("005930");
		assertEquals("https://m.stock.naver.com/api/stock/005930/integration", url);
	}

	@Test
	@DisplayName("dailyPriceUrl - stockId와 페이지 번호 포함")
	void testDailyPriceUrl() {
		String url = stockFetcher.dailyPriceUrl("005930", 2);
		assertEquals("https://m.stock.naver.com/api/stock/005930/price?pageSize=15&page=2", url);
	}

	@Test
	@DisplayName("financialUrl - stockId 포함")
	void testFinancialUrl() {
		String url = stockFetcher.financialUrl("000660");
		assertEquals("https://m.stock.naver.com/api/stock/000660/finance/annual", url);
	}

	@Test
	@DisplayName("esgUrl - stockId 포함")
	void testEsgUrl() {
		String url = stockFetcher.esgUrl("005930");
		assertEquals("https://m.stock.naver.com/api/stock/005930/finance/nonFinance", url);
	}

	@Test
	@DisplayName("marketIndexUrl - 고정 URL")
	void testMarketIndexUrl() {
		String url = stockFetcher.marketIndexUrl();
		assertEquals("https://api.stock.naver.com/marketindex/majors/part2", url);
	}

	@Test
	@DisplayName("categoryUrl - 페이지 번호 포함")
	void testCategoryUrl() {
		String url = stockFetcher.categoryUrl(1);
		assertEquals("https://m.stock.naver.com/api/stocks/industry?pageSize=60&page=1", url);
	}

	@Test
	@DisplayName("categoryLinkUrl - categoryNo와 페이지 번호 포함")
	void testCategoryLinkUrl() {
		String url = stockFetcher.categoryLinkUrl("123", 2);
		assertEquals("https://m.stock.naver.com/api/stocks/industry/123?pageSize=20&page=2", url);
	}

	// ==================== URL 일관성 테스트 ====================

	@Test
	@DisplayName("모든 mobile URL은 같은 base URL 사용")
	void testMobileBaseUrl() {
		String base = "https://m.stock.naver.com/api";
		assertTrue(stockFetcher.simpleStockListUrl(1).startsWith(base));
		assertTrue(stockFetcher.stockDetailUrl("005930").startsWith(base));
		assertTrue(stockFetcher.dailyPriceUrl("005930", 1).startsWith(base));
		assertTrue(stockFetcher.financialUrl("005930").startsWith(base));
		assertTrue(stockFetcher.esgUrl("005930").startsWith(base));
		assertTrue(stockFetcher.categoryUrl(1).startsWith(base));
		assertTrue(stockFetcher.categoryLinkUrl("1", 1).startsWith(base));
	}

	@Test
	@DisplayName("marketIndex URL은 다른 base URL 사용")
	void testApiBaseUrl() {
		assertTrue(stockFetcher.marketIndexUrl().startsWith("https://api.stock.naver.com"));
	}

	@Test
	@DisplayName("같은 파라미터로 호출하면 같은 URL 반환")
	void testUrlDeterminism() {
		assertEquals(stockFetcher.stockDetailUrl("005930"), stockFetcher.stockDetailUrl("005930"));
		assertEquals(stockFetcher.dailyPriceUrl("005930", 1), stockFetcher.dailyPriceUrl("005930", 1));
	}

	// ==================== fetch 위임 테스트 ====================

	@Test
	@DisplayName("fetchSimpleStockList - parser에 올바른 URL 전달")
	void testFetchSimpleStockList() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenReturn("{\"stocks\":[]}");

		stockFetcher.fetchSimpleStockList(5);

		verify(mockParser).getUrlJsonData(
			"https://m.stock.naver.com/api/index/KOSPI/enrollStocks?pageSize=25&type=object&page=5");
	}

	@Test
	@DisplayName("fetchStockDetail - parser에 올바른 URL 전달")
	void testFetchStockDetail() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenReturn("{}");

		stockFetcher.fetchStockDetail("005930");

		verify(mockParser).getUrlJsonData(
			"https://m.stock.naver.com/api/stock/005930/integration");
	}

	@Test
	@DisplayName("fetchDailyPrice - parser에 올바른 URL 전달")
	void testFetchDailyPrice() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenReturn("[]");

		stockFetcher.fetchDailyPrice("000660", 3);

		verify(mockParser).getUrlJsonData(
			"https://m.stock.naver.com/api/stock/000660/price?pageSize=15&page=3");
	}

	@Test
	@DisplayName("fetchFinancial - parser에 올바른 URL 전달")
	void testFetchFinancial() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenReturn("{}");

		stockFetcher.fetchFinancial("005930");

		verify(mockParser).getUrlJsonData(
			"https://m.stock.naver.com/api/stock/005930/finance/annual");
	}

	@Test
	@DisplayName("fetchEsg - parser에 올바른 URL 전달")
	void testFetchEsg() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenReturn("{}");

		stockFetcher.fetchEsg("005930");

		verify(mockParser).getUrlJsonData(
			"https://m.stock.naver.com/api/stock/005930/finance/nonFinance");
	}

	@Test
	@DisplayName("fetchMarketIndex - parser에 올바른 URL 전달")
	void testFetchMarketIndex() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenReturn("{}");

		stockFetcher.fetchMarketIndex();

		verify(mockParser).getUrlJsonData(
			"https://api.stock.naver.com/marketindex/majors/part2");
	}

	@Test
	@DisplayName("fetchCategory - parser에 올바른 URL 전달")
	void testFetchCategory() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenReturn("{}");

		stockFetcher.fetchCategory(2);

		verify(mockParser).getUrlJsonData(
			"https://m.stock.naver.com/api/stocks/industry?pageSize=60&page=2");
	}

	@Test
	@DisplayName("fetchCategoryLink - parser에 올바른 URL 전달")
	void testFetchCategoryLink() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenReturn("{}");

		stockFetcher.fetchCategoryLink("456", 3);

		verify(mockParser).getUrlJsonData(
			"https://m.stock.naver.com/api/stocks/industry/456?pageSize=20&page=3");
	}

	@Test
	@DisplayName("fetch 메서드가 parser 응답을 그대로 반환")
	void testFetchReturnsParserResponse() throws Exception {
		String expected = "{\"stockName\":\"삼성전자\"}";
		when(mockParser.getUrlJsonData(anyString())).thenReturn(expected);

		String result = stockFetcher.fetchStockDetail("005930");

		assertEquals(expected, result);
	}

	@Test
	@DisplayName("parser 예외가 그대로 전파됨")
	void testFetchPropagatesException() throws Exception {
		when(mockParser.getUrlJsonData(anyString())).thenThrow(new Exception("연결 실패"));

		assertThrows(Exception.class, () -> stockFetcher.fetchStockDetail("005930"));
	}
}