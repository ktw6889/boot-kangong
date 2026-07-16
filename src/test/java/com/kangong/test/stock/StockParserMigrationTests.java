package com.kangong.test.stock;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockMarketIndexVO;
import com.kangong.stock.parser.StockFinancialParser;
import com.kangong.stock.parser.StockMobileParser;
import com.kangong.stock.service.StockFieldMappingRegistry;

import static org.junit.jupiter.api.Assertions.*;

class StockParserMigrationTests {

	private final StockMobileParser parser = new StockMobileParser();

	// ==================== AbstractStockDataParser (StockMobileParser 경유) ====================

	@Test
	@DisplayName("trimValue - 숫자와 소수점만 추출")
	void testTrimValueNumeric() throws Exception {
		assertEquals("1234.56", parser.getTrimValue("1,234.56원"));
	}

	@Test
	@DisplayName("trimValue - 날짜 형식은 그대로 반환")
	void testTrimValueDate() throws Exception {
		assertEquals("2024-01-15", parser.getTrimValue("2024-01-15"));
	}

	@Test
	@DisplayName("trimValue - null 입력시 0 반환")
	void testTrimValueNull() throws Exception {
		assertEquals("0", parser.getTrimValue(null));
	}

	@Test
	@DisplayName("trimValue - 순수 숫자")
	void testTrimValuePlainNumber() throws Exception {
		assertEquals("500", parser.getTrimValue("500"));
	}

	@Test
	@DisplayName("setObjectForMappingData - 매핑된 필드에 값 설정")
	void testMappingDataSetsField() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		Map<String, String> mapper = new HashMap<>();
		mapper.put("매출액", "totalSales");
		parser.setObjectForMappingData(vo, mapper, "매출액", "1000");
		assertEquals("1000", vo.getTotalSales());
	}

	@Test
	@DisplayName("setObjectForMappingData - 매핑에 없는 키는 무시")
	void testMappingDataIgnoresUnknownKey() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		Map<String, String> mapper = new HashMap<>();
		mapper.put("매출액", "totalSales");
		parser.setObjectForMappingData(vo, mapper, "없는키", "1000");
		assertNull(vo.getTotalSales());
	}

	@Test
	@DisplayName("setObjectForMappingData - 쉼표 포함 값은 숫자만 추출")
	void testMappingDataCleansComma() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		Map<String, String> mapper = new HashMap<>();
		mapper.put("영업이익", "profits");
		parser.setObjectForMappingData(vo, mapper, "영업이익", "1,234,567");
		assertEquals("1234567", vo.getProfits());
	}

	@Test
	@DisplayName("insertZero - null 필드를 0으로 채움")
	void testInsertZeroFillsNull() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		vo.setStockId("005930");
		vo.setYear("2024");
		List<String> excludeList = Arrays.asList("id", "deleteYn", "stockMasterId");
		StockFinancialVO result = (StockFinancialVO) parser.insertZero(vo, excludeList);
		assertEquals("005930", result.getStockId());
		assertEquals("2024", result.getYear());
		assertEquals("0", result.getTotalSales());
		assertEquals("0", result.getProfits());
		assertEquals("0", result.getEps());
	}

	@Test
	@DisplayName("insertZero - 제외 필드는 변경하지 않음")
	void testInsertZeroSkipsExcluded() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		List<String> excludeList = Arrays.asList("id", "deleteYn", "stockMasterId");
		StockFinancialVO result = (StockFinancialVO) parser.insertZero(vo, excludeList);
		assertNull(result.getId());
		assertNull(result.getDeleteYn());
		assertNull(result.getStockMasterId());
	}

	@Test
	@DisplayName("insertZero - N/A를 0으로 변환")
	void testInsertZeroConvertsNA() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		vo.setTotalSales("N/A");
		List<String> excludeList = Arrays.asList("id", "deleteYn", "stockMasterId");
		StockFinancialVO result = (StockFinancialVO) parser.insertZero(vo, excludeList);
		assertEquals("0", result.getTotalSales());
	}

	@Test
	@DisplayName("insertZero - 숫자 외 문자 제거")
	void testInsertZeroCleansNonNumeric() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		vo.setTotalSales("1,234,567억원");
		vo.setProfits("50.5%");
		List<String> excludeList = Arrays.asList("id", "deleteYn", "stockMasterId");
		StockFinancialVO result = (StockFinancialVO) parser.insertZero(vo, excludeList);
		assertEquals("1234567", result.getTotalSales());
		assertEquals("50.5", result.getProfits());
	}

	// ==================== StockFinancialParser 매퍼 ====================

	@Test
	@DisplayName("기업현황 매퍼 - 20개 키, 모든 필드가 VO에 존재")
	void testFinancialMapper1() {
		Map<String, String> mapper = StockFieldMappingRegistry.financialHtmlTable();
		assertEquals(20, mapper.size());
		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			assertDoesNotThrow(() -> StockFinancialVO.class.getDeclaredField(entry.getValue()),
					"필드 없음: " + entry.getValue());
		}
	}

	@Test
	@DisplayName("재무상태표 매퍼 - 5개 키, 모든 필드가 VO에 존재")
	void testFinancialMapper2() {
		Map<String, String> mapper = StockFieldMappingRegistry.balanceSheetHtmlTable();
		assertEquals(5, mapper.size());
		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			assertDoesNotThrow(() -> StockFinancialVO.class.getDeclaredField(entry.getValue()),
					"필드 없음: " + entry.getValue());
		}
	}

	// ==================== 모바일 재무 매퍼 ====================

	@Test
	@DisplayName("모바일 재무 매퍼 - 13개 키, VO 필드 존재 및 Reflection 설정/조회")
	void testMobileFinancialMapper() throws Exception {
		Map<String, String> mapper = getMobileFinancialMapper();
		assertEquals(13, mapper.size());
		StockFinancialVO vo = StockFinancialVO.builder().build();
		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			Field field = vo.getClass().getDeclaredField(entry.getValue());
			field.setAccessible(true);
			field.set(vo, "v_" + entry.getKey());
			assertEquals("v_" + entry.getKey(), field.get(vo));
		}
	}

	// ==================== ESG 매퍼 ====================

	@Test
	@DisplayName("ESG 매퍼 - 14개 키, VO 필드 존재")
	void testEsgMapperFieldsExist() {
		Map<String, String> mapper = getEsgMapper();
		assertEquals(14, mapper.size());
		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			assertDoesNotThrow(() -> StockEsgVO.class.getDeclaredField(entry.getValue()),
					"필드 없음: " + entry.getValue());
		}
	}

	@Test
	@DisplayName("ESG 매퍼 - E/S/G 테마 코드 분류")
	void testEsgThemeClassification() {
		Map<String, String> mapper = getEsgMapper();
		assertEquals(5, mapper.keySet().stream().filter(k -> k.startsWith("E")).count());
		assertEquals(4, mapper.keySet().stream().filter(k -> k.startsWith("S")).count());
		assertEquals(5, mapper.keySet().stream().filter(k -> k.startsWith("G")).count());
	}

	@Test
	@DisplayName("ESG 매퍼 - Reflection 전체 필드 설정/조회")
	void testEsgMapperReflection() throws Exception {
		StockEsgVO vo = StockEsgVO.builder().build();
		Map<String, String> mapper = getEsgMapper();
		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			Field field = vo.getClass().getDeclaredField(entry.getValue());
			field.setAccessible(true);
			field.set(vo, "s_" + entry.getKey());
			assertEquals("s_" + entry.getKey(), field.get(vo));
		}
	}

	// ==================== 시장지수 매퍼 ====================

	@Test
	@DisplayName("시장지수 매퍼 - 34개 키, VO 필드 존재")
	void testMarketIndexMapperFieldsExist() {
		Map<String, String> mapper = getMarketIndexMapper();
		assertEquals(34, mapper.size());
		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			assertDoesNotThrow(() -> StockMarketIndexVO.class.getDeclaredField(entry.getValue()),
					"필드 없음: " + entry.getValue());
		}
	}

	@Test
	@DisplayName("시장지수 매퍼 - 카테고리별 분류 확인")
	void testMarketIndexCategoryClassification() {
		Map<String, String> mapper = getMarketIndexMapper();
		assertEquals(5, mapper.values().stream().filter(v -> v.startsWith("domesticInterest")).count());
		assertEquals(5, mapper.values().stream().filter(v -> v.startsWith("standardInterest")).count());
		assertEquals(5, mapper.values().stream().filter(v -> v.startsWith("metal")).count());
		assertEquals(9, mapper.values().stream().filter(v -> v.startsWith("transport")).count());
		assertEquals(5, mapper.values().stream().filter(v -> v.startsWith("bond")).count());
		assertEquals(5, mapper.values().stream().filter(v -> v.startsWith("energy")).count());
	}

	@Test
	@DisplayName("시장지수 매퍼 - Reflection 전체 필드 설정/조회")
	void testMarketIndexMapperReflection() throws Exception {
		StockMarketIndexVO vo = StockMarketIndexVO.builder().build();
		Map<String, String> mapper = getMarketIndexMapper();
		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			Field field = vo.getClass().getDeclaredField(entry.getValue());
			field.setAccessible(true);
			field.set(vo, "v_" + entry.getKey());
			assertEquals("v_" + entry.getKey(), field.get(vo));
		}
	}

	// ==================== 일별시세 매퍼 ====================

	@Test
	@DisplayName("일별시세 매퍼 - 9개 키, VO 필드 존재")
	void testDailyPriceMapperFieldsExist() {
		Map<String, String> mapper = getDailyPriceMapper();
		assertEquals(9, mapper.size());
		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			assertDoesNotThrow(() -> StockDailyPriceVO.class.getDeclaredField(entry.getValue()),
					"필드 없음: " + entry.getValue());
		}
	}

	// ==================== Util 삭제 확인 ====================

	@Test
	@DisplayName("7개 Util 클래스가 모두 삭제되었는지 확인")
	void testAllUtilClassesDeleted() {
		String[] utilClasses = {
				"com.kangong.stock.util.StockMobileUtil",
				"com.kangong.stock.util.StockUtil",
				"com.kangong.stock.util.StockFinancialUtil",
				"com.kangong.stock.util.StockCategoryUtil",
				"com.kangong.stock.util.StockDailyPriceUtil",
				"com.kangong.stock.util.StockSeleniumFinancialUtil",
				"com.kangong.stock.util.StockSeleniumFinancialAnalysisUtil"
		};
		for (String className : utilClasses) {
			assertThrows(ClassNotFoundException.class,
					() -> Class.forName(className),
					className + " 가 아직 존재합니다");
		}
	}

	// ==================== Parser 존재 및 상속 확인 ====================

	@Test
	@DisplayName("7개 Parser 클래스가 모두 존재하는지 확인")
	void testAllParserClassesExist() {
		String[] parserClasses = {
				"com.kangong.stock.parser.AbstractStockDataParser",
				"com.kangong.stock.parser.StockMobileParser",
				"com.kangong.stock.parser.StockInfoParser",
				"com.kangong.stock.parser.StockFinancialParser",
				"com.kangong.stock.parser.StockCategoryParser",
				"com.kangong.stock.parser.StockDailyPriceParser",
				"com.kangong.stock.parser.StockSeleniumParser"
		};
		for (String className : parserClasses) {
			assertDoesNotThrow(() -> Class.forName(className),
					className + " 를 찾을 수 없습니다");
		}
	}

	@Test
	@DisplayName("모든 Parser가 AbstractStockDataParser를 상속하는지 확인")
	void testAllParsersInheritAbstract() throws Exception {
		Class<?> superClazz = Class.forName("com.kangong.stock.parser.AbstractStockDataParser");
		String[] parserClasses = {
				"com.kangong.stock.parser.StockMobileParser",
				"com.kangong.stock.parser.StockInfoParser",
				"com.kangong.stock.parser.StockFinancialParser",
				"com.kangong.stock.parser.StockCategoryParser",
				"com.kangong.stock.parser.StockDailyPriceParser",
				"com.kangong.stock.parser.StockSeleniumParser"
		};
		for (String className : parserClasses) {
			Class<?> clazz = Class.forName(className);
			assertTrue(superClazz.isAssignableFrom(clazz),
					className + " 가 AbstractStockDataParser를 상속하지 않습니다");
		}
	}

	// ==================== Helper Methods ====================

	private Map<String, String> getMobileFinancialMapper() {
		Map<String, String> mapper = new HashMap<>();
		mapper.put("매출액", "totalSales");
		mapper.put("영업이익", "profits");
		mapper.put("당기순이익", "earnings");
		mapper.put("영업이익률", "profitsRatio");
		mapper.put("순이익률", "netProfitRatio");
		mapper.put("ROE", "roe");
		mapper.put("부채비율", "deptRatio");
		mapper.put("유보율", "reserveRatio");
		mapper.put("EPS", "eps");
		mapper.put("PER", "per");
		mapper.put("BPS", "bps");
		mapper.put("PBR", "pbr");
		mapper.put("주당배당금", "dividendsPerShare");
		return mapper;
	}

	private Map<String, String> getEsgMapper() {
		Map<String, String> mapper = new HashMap<>();
		mapper.put("E01", "waterRecyclingRate");
		mapper.put("E02", "greenHouseEmission");
		mapper.put("E03", "energyUsage");
		mapper.put("E04", "wasteRecyclingRate");
		mapper.put("E05", "fineDustUsage");
		mapper.put("S01", "donation");
		mapper.put("S02", "continuousServiceYear");
		mapper.put("S04", "nonRegularEmplymentRate");
		mapper.put("S05", "averageAnnualSalary");
		mapper.put("G01", "executiveAverageAnnualSalary");
		mapper.put("G02", "salaryRatio");
		mapper.put("G03", "outsideDirectorRate");
		mapper.put("G04", "directorateIndependence");
		mapper.put("G05", "largestShareHolderRatio");
		return mapper;
	}

	private Map<String, String> getMarketIndexMapper() {
		Map<String, String> mapper = new HashMap<>();
		mapper.put("KRCALLBOKK", "domesticInterestCall");
		mapper.put("KFIA114000", "domesticInterestCd");
		mapper.put("KRCOFIXMANF", "domesticInterestCofixManf");
		mapper.put("KRCOFIXOUTB", "domesticInterestCofixOutb");
		mapper.put("KRNCOFIXOUTB", "domesticInterestNcofixOutb");
		mapper.put("USFOMC=ECIX", "standardInterestUs");
		mapper.put("KROCRT=ECIX", "standardInterestKr");
		mapper.put("EUECBR=ECIX", "standardInterestEu");
		mapper.put("GBBOEI=ECIX", "standardInterestGb");
		mapper.put("JPINTN=ECIX", "standardInterestJp");
		mapper.put("GCcv1", "metalGc");
		mapper.put("CMDT_GD", "metalCmdt");
		mapper.put("SIcv1", "metalSi");
		mapper.put("HGcv1", "metalHg");
		mapper.put("PLcv1", "metalPl");
		mapper.put(".CCFIDXSSE", "transportCcf");
		mapper.put(".SCFIDXSSE", "transportScf");
		mapper.put(".BADI", "transportBadi");
		mapper.put(".BACI", "transportBack");
		mapper.put(".BPNI", "transportBpni");
		mapper.put(".BSIS", "transportBsis");
		mapper.put(".BHSI", "transportBhsi");
		mapper.put(".BAID", "transportBaid");
		mapper.put(".BAIT", "transportBait");
		mapper.put("US10YT=RR", "bondUs10yt");
		mapper.put("KR10YT=RR", "bondKr10yt");
		mapper.put("JP10YT=RR", "bondJp10yt");
		mapper.put("DE10YT=RR", "bondDe10yt");
		mapper.put("CN10YT=RR", "bondCn10yt");
		mapper.put("CLcv1", "energyCl");
		mapper.put("LCOcv1", "energyLco");
		mapper.put("RBcv1", "energyRb");
		mapper.put("HOcv1", "energyHo");
		mapper.put("DCBc1", "energyDcb");
		return mapper;
	}

	private Map<String, String> getDailyPriceMapper() {
		Map<String, String> mapper = new HashMap<>();
		mapper.put("날짜", "tradingDate");
		mapper.put("종가", "closingPrice");
		mapper.put("전일비", "previousDayRate");
		mapper.put("등락률", "fluctuationRate");
		mapper.put("거래량", "volumn");
		mapper.put("기관_순매매량", "organTradingVolumn");
		mapper.put("외국인_순매매량", "foreignTradingVolumn");
		mapper.put("외국인_보유주수", "foreignHoldingVolumn");
		mapper.put("외국인_보유율", "foreignHoldingRate");
		return mapper;
	}
}
