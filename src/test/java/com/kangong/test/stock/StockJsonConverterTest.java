package com.kangong.test.stock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockMobileParser;
import com.kangong.stock.service.StockFieldMappingRegistry;
import com.kangong.stock.service.StockJsonConverter;

import static org.junit.jupiter.api.Assertions.*;

class StockJsonConverterTest {

	StockJsonConverter converter;

	@BeforeEach
	void setUp() throws Exception {
		converter = new StockJsonConverter();
		StockMobileParser parser = new StockMobileParser();
		Field field = StockJsonConverter.class.getDeclaredField("stockMobileParser");
		field.setAccessible(true);
		field.set(converter, parser);
	}

	// ==================== checkValidJson ====================

	@Test
	@DisplayName("checkValidJson - 유효한 JSON이면 true")
	void testCheckValidJson_valid() {
		String json = "{\"stockName\":\"삼성전자\",\"totalInfos\":[]}";
		assertTrue(converter.checkValidJson(json));
	}

	@Test
	@DisplayName("checkValidJson - StockConflict 코드이면 false")
	void testCheckValidJson_stockConflict() {
		String json = "{\"code\":\"StockConflict\",\"message\":\"종목 충돌\"}";
		assertFalse(converter.checkValidJson(json));
	}

	@Test
	@DisplayName("checkValidJson - 유효하지 않은 JSON 문자열이면 false")
	void testCheckValidJson_invalidJson() {
		assertFalse(converter.checkValidJson("이것은 JSON이 아닙니다"));
	}

	@Test
	@DisplayName("checkValidJson - null이면 false")
	void testCheckValidJson_null() {
		assertFalse(converter.checkValidJson(null));
	}

	@Test
	@DisplayName("checkValidJson - code 필드가 없으면 true")
	void testCheckValidJson_noCodeField() {
		String json = "{\"stockName\":\"테스트\"}";
		assertTrue(converter.checkValidJson(json));
	}

	// ==================== convertSimpleStockList ====================

	@Test
	@DisplayName("convertSimpleStockList - 정상 변환")
	void testConvertSimpleStockList() throws Exception {
		String json = "{\"stocks\":[" +
			"{\"itemCode\":\"005930\",\"stockName\":\"삼성전자\",\"closePrice\":\"68,000\",\"marketValue\":\"4,059,452\"}," +
			"{\"itemCode\":\"000660\",\"stockName\":\"SK하이닉스\",\"closePrice\":\"112,500\",\"marketValue\":\"819,003\"}" +
			"]}";

		ArrayList<StockVO> result = converter.convertSimpleStockList(json);

		assertEquals(2, result.size());

		StockVO first = result.get(0);
		assertEquals("005930", first.getStockId());
		assertEquals("삼성전자", first.getName());
		assertEquals("68000", first.getPrice());
		assertEquals("4059452", first.getMarketCapitalization());

		StockVO second = result.get(1);
		assertEquals("000660", second.getStockId());
		assertEquals("SK하이닉스", second.getName());
		assertEquals("112500", second.getPrice());
		assertEquals("819003", second.getMarketCapitalization());
	}

	@Test
	@DisplayName("convertSimpleStockList - 빈 stocks 배열이면 빈 리스트 반환")
	void testConvertSimpleStockList_empty() throws Exception {
		String json = "{\"stocks\":[]}";
		ArrayList<StockVO> result = converter.convertSimpleStockList(json);
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("convertSimpleStockList - 단일 종목")
	void testConvertSimpleStockList_single() throws Exception {
		String json = "{\"stocks\":[" +
			"{\"itemCode\":\"373220\",\"stockName\":\"LG에너지솔루션\",\"closePrice\":\"434,000\",\"marketValue\":\"1,015,560\"}" +
			"]}";

		ArrayList<StockVO> result = converter.convertSimpleStockList(json);
		assertEquals(1, result.size());
		assertEquals("373220", result.get(0).getStockId());
		assertEquals("LG에너지솔루션", result.get(0).getName());
	}

	// ==================== convertStockDetail ====================

	@Test
	@DisplayName("convertStockDetail - totalInfos + consensusInfo 변환")
	void testConvertStockDetail() throws Exception {
		String json = "{\"stockName\":\"삼성전자\"," +
			"\"totalInfos\":[" +
			"{\"code\":\"per\",\"value\":\"12.5\"}," +
			"{\"code\":\"pbr\",\"value\":\"1.3\"}," +
			"{\"code\":\"marketValue\",\"value\":\"4,059,452\"}," +
			"{\"code\":\"eps\",\"value\":\"5,777\"}" +
			"]," +
			"\"consensusInfo\":{\"recommMean\":\"3.8\",\"priceTargetMean\":\"80,000\"}}";

		StockVO stockVO = StockVO.builder().stockId("005930").build();
		StockVO result = converter.convertStockDetail(stockVO, json);

		assertEquals("삼성전자", result.getName());
		assertEquals("005930", result.getStockId());
		assertEquals("12.5", result.getPer());
		assertEquals("1.3", result.getPbr());
		assertEquals("4059452", result.getMarketCapitalization());
		assertEquals("5777", result.getEps());
		assertEquals("3.8", result.getInvestmentOpinion());
		assertEquals("80000", result.getTargetPrice());
	}

	@Test
	@DisplayName("convertStockDetail - consensusInfo가 null인 경우")
	void testConvertStockDetail_noConsensus() throws Exception {
		String json = "{\"stockName\":\"테스트종목\"," +
			"\"totalInfos\":[{\"code\":\"per\",\"value\":\"10.0\"}]," +
			"\"consensusInfo\":null}";

		StockVO stockVO = StockVO.builder().stockId("999999").build();
		StockVO result = converter.convertStockDetail(stockVO, json);

		assertEquals("테스트종목", result.getName());
		assertEquals("10.0", result.getPer());
		assertNotNull(result.getStockId());
	}

	@Test
	@DisplayName("convertStockDetail - null/빈 값 필드는 0으로 채워짐")
	void testConvertStockDetail_emptyFieldsFilled() throws Exception {
		String json = "{\"stockName\":\"테스트\"," +
			"\"totalInfos\":[{\"code\":\"per\",\"value\":\"5.0\"}]," +
			"\"consensusInfo\":null}";

		StockVO stockVO = StockVO.builder().stockId("123456").build();
		StockVO result = converter.convertStockDetail(stockVO, json);

		assertEquals("0", result.getPbr());
		assertEquals("0", result.getBps());
		assertEquals("0", result.getVolumn());
	}

	// ==================== convertDailyPriceList ====================

	@Test
	@DisplayName("convertDailyPriceList - 정상 변환")
	void testConvertDailyPriceList() throws Exception {
		String json = "[" +
			"{\"localTradedAt\":\"2024-01-15\",\"closePrice\":\"68,000\",\"compareToPreviousClosePrice\":\"500\",\"fluctuationsRatio\":\"0.74\",\"accumulatedTradingVolume\":12023540}," +
			"{\"localTradedAt\":\"2024-01-14\",\"closePrice\":\"67,500\",\"compareToPreviousClosePrice\":\"300\",\"fluctuationsRatio\":\"0.44\",\"accumulatedTradingVolume\":8500000}" +
			"]";

		List<StockDailyPriceVO> result = converter.convertDailyPriceList("005930", json);

		assertEquals(2, result.size());

		StockDailyPriceVO first = result.get(0);
		assertEquals("005930", first.getStockId());
		assertEquals("2024-01-15", first.getTradingDate());
		assertEquals("68000", first.getClosingPrice());
		assertEquals("500", first.getPreviousDayRate());
		assertEquals("0.74", first.getFluctuationRate());
		assertEquals("12023540", first.getVolumn());

		StockDailyPriceVO second = result.get(1);
		assertEquals("005930", second.getStockId());
		assertEquals("2024-01-14", second.getTradingDate());
		assertEquals("67500", second.getClosingPrice());
	}

	@Test
	@DisplayName("convertDailyPriceList - 빈 JSON 배열이면 빈 리스트 반환")
	void testConvertDailyPriceList_empty() throws Exception {
		List<StockDailyPriceVO> result = converter.convertDailyPriceList("005930", "[]");
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("convertDailyPriceList - 각 항목이 독립적인 VO로 생성됨")
	void testConvertDailyPriceList_independentVOs() throws Exception {
		String json = "[" +
			"{\"localTradedAt\":\"2024-01-15\",\"closePrice\":\"100\",\"compareToPreviousClosePrice\":\"10\",\"fluctuationsRatio\":\"1.0\",\"accumulatedTradingVolume\":1000}," +
			"{\"localTradedAt\":\"2024-01-14\",\"closePrice\":\"200\",\"compareToPreviousClosePrice\":\"20\",\"fluctuationsRatio\":\"2.0\",\"accumulatedTradingVolume\":2000}" +
			"]";

		List<StockDailyPriceVO> result = converter.convertDailyPriceList("005930", json);

		assertNotSame(result.get(0), result.get(1));
		assertNotEquals(result.get(0).getTradingDate(), result.get(1).getTradingDate());
		assertNotEquals(result.get(0).getClosingPrice(), result.get(1).getClosingPrice());
	}

	// ==================== Attribute Mapper Tests ====================

	@Test
	@DisplayName("getStockVOAttributeMapper - 17개 매핑 키 포함")
	void testStockVOAttributeMapper_size() {
		Map<String, String> mapper = StockFieldMappingRegistry.stockMobileApi();
		assertEquals(17, mapper.size());
	}

	@Test
	@DisplayName("getStockVOAttributeMapper - 주요 매핑 값 확인")
	void testStockVOAttributeMapper_values() {
		Map<String, String> mapper = StockFieldMappingRegistry.stockMobileApi();

		assertEquals("price", mapper.get("openPrice"));
		assertEquals("priceBeforeday", mapper.get("lastClosePrice"));
		assertEquals("volumn", mapper.get("accumulatedTradingVolume"));
		assertEquals("marketCapitalization", mapper.get("marketValue"));
		assertEquals("foreignerRatio", mapper.get("foreignRate"));
		assertEquals("max52", mapper.get("highPriceOf52Weeks"));
		assertEquals("min52", mapper.get("lowPriceOf52Weeks"));
		assertEquals("per", mapper.get("per"));
		assertEquals("eps", mapper.get("eps"));
		assertEquals("estimationPer", mapper.get("cnsPer"));
		assertEquals("estimationEps", mapper.get("cnsEps"));
		assertEquals("pbr", mapper.get("pbr"));
		assertEquals("bps", mapper.get("bps"));
		assertEquals("dividendRate", mapper.get("dividendYieldRatio"));
		assertEquals("investmentOpinion", mapper.get("recommMean"));
		assertEquals("targetPrice", mapper.get("priceTargetMean"));
		assertEquals("fundPay", mapper.get("fundPay"));
	}

	@Test
	@DisplayName("getDailyPriceAttributeMapper - 5개 매핑 키 포함")
	void testDailyPriceAttributeMapper() {
		Map<String, String> mapper = StockFieldMappingRegistry.dailyPriceMobileApi();

		assertEquals(5, mapper.size());
		assertEquals("tradingDate", mapper.get("localTradedAt"));
		assertEquals("closingPrice", mapper.get("closePrice"));
		assertEquals("previousDayRate", mapper.get("compareToPreviousClosePrice"));
		assertEquals("fluctuationRate", mapper.get("fluctuationsRatio"));
		assertEquals("volumn", mapper.get("accumulatedTradingVolume"));
	}

	// ==================== Mapper → VO Field Validation ====================

	@Test
	@DisplayName("StockVO 매퍼의 모든 필드명이 VO에 실제 존재")
	void testStockVOMapperFieldsExist() {
		Map<String, String> mapper = StockFieldMappingRegistry.stockMobileApi();
		Class<?> clazz = StockVO.class;

		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			assertDoesNotThrow(() -> clazz.getDeclaredField(entry.getValue()),
				"StockVO에 필드가 없음: " + entry.getValue() + " (키: " + entry.getKey() + ")");
		}
	}

	@Test
	@DisplayName("StockDailyPriceVO 매퍼의 모든 필드명이 VO에 실제 존재")
	void testDailyPriceMapperFieldsExist() {
		Map<String, String> mapper = StockFieldMappingRegistry.dailyPriceMobileApi();
		Class<?> clazz = StockDailyPriceVO.class;

		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			assertDoesNotThrow(() -> clazz.getDeclaredField(entry.getValue()),
				"StockDailyPriceVO에 필드가 없음: " + entry.getValue() + " (키: " + entry.getKey() + ")");
		}
	}

	@Test
	@DisplayName("매퍼에 없는 키 조회 시 null 반환")
	void testMapperReturnsNullForUnknownKey() {
		Map<String, String> mapper = StockFieldMappingRegistry.stockMobileApi();
		assertNull(mapper.get("존재하지않는키"));
	}

	// ==================== getExceptZeroFields ====================

	@Test
	@DisplayName("getExceptZeroFields - 6개 제외 필드 포함")
	void testGetExceptZeroFields() {
		List<String> fields = converter.getExceptZeroFields();

		assertEquals(6, fields.size());
		assertTrue(fields.contains("id"));
		assertTrue(fields.contains("deleteYn"));
		assertTrue(fields.contains("discussionRoomUrl"));
		assertTrue(fields.contains("name"));
		assertTrue(fields.contains("stockId"));
		assertTrue(fields.contains("yyyymmdd"));
	}

	@Test
	@DisplayName("getExceptZeroFields - 제외 필드는 0으로 채워지지 않음")
	void testExceptZeroFieldsPreserved() throws Exception {
		String json = "{\"stockName\":\"테스트\"," +
			"\"totalInfos\":[]," +
			"\"consensusInfo\":null}";

		StockVO stockVO = StockVO.builder().stockId("005930").name("원래이름").build();
		StockVO result = converter.convertStockDetail(stockVO, json);

		assertEquals("테스트", result.getName());
		assertEquals("005930", result.getStockId());
	}
}
