package com.kangong.test.stock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.parser.AbstractStockDataParser;

import static org.junit.jupiter.api.Assertions.*;

class StockDataParserBaseTest {

	private static class TestParser extends AbstractStockDataParser {
		void callSetFieldValue(Object target, String fieldName, String value) {
			setFieldValue(target, fieldName, value);
		}

		<T> T callFillEmptyFieldsWithZero(T target, Set<String> excludeFields) {
			return fillEmptyFieldsWithZero(target, excludeFields);
		}

		<T> T callFillEmptyFieldsWithZeroAndClean(T target, Set<String> excludeFields) {
			return fillEmptyFieldsWithZeroAndClean(target, excludeFields);
		}

		<T> T callMapDataToObject(T target, Map<String, String> mapper, String name, String value) {
			return mapDataToObject(target, mapper, name, value);
		}

		String callExtractNumeric(String value) {
			return extractNumeric(value);
		}

		String callExtractNumericWithSign(String value) {
			return extractNumericWithSign(value);
		}

		String callExtractUrlParam(String url, String paramName) {
			return extractUrlParam(url, paramName);
		}

		boolean callIsNumeric(String str) {
			return isNumeric(str);
		}
	}

	private final TestParser parser = new TestParser();

	private static final Set<String> DEFAULT_EXCLUDE = Set.of("id", "deleteYn", "stockMasterId", "createDate", "updateDate");

	// ==================== setFieldValue 타입 안전성 ====================

	@Test
	@DisplayName("setFieldValue - String 필드에 값 설정")
	void setFieldValue_stringField() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		parser.callSetFieldValue(vo, "totalSales", "1000");
		assertEquals("1000", vo.getTotalSales());
	}

	@Test
	@DisplayName("setFieldValue - non-String 필드(Timestamp)에 설정 시도 시 무시")
	void setFieldValue_nonStringFieldIgnored() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		parser.callSetFieldValue(vo, "createDate", "2024-01-01");
		assertNull(vo.getCreateDate());
	}

	@Test
	@DisplayName("setFieldValue - 존재하지 않는 필드는 예외 없이 무시")
	void setFieldValue_nonExistentFieldIgnored() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		assertDoesNotThrow(() -> parser.callSetFieldValue(vo, "nonExistentField", "value"));
	}

	@Test
	@DisplayName("setFieldValue - 여러 String 필드 연속 설정")
	void setFieldValue_multipleFields() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		parser.callSetFieldValue(vo, "totalSales", "100");
		parser.callSetFieldValue(vo, "profits", "200");
		parser.callSetFieldValue(vo, "eps", "300");
		assertEquals("100", vo.getTotalSales());
		assertEquals("200", vo.getProfits());
		assertEquals("300", vo.getEps());
	}

	// ==================== fillEmptyFieldsWithZero / fillEmptyFieldsWithZeroAndClean ====================

	@Test
	@DisplayName("fillEmptyFieldsWithZero - null 필드를 0으로 채움")
	void fillZero_fillsNull() {
		StockFinancialVO vo = StockFinancialVO.builder().stockId("005930").year("2024").build();
		StockFinancialVO result = parser.callFillEmptyFieldsWithZero(vo, DEFAULT_EXCLUDE);
		assertEquals("005930", result.getStockId());
		assertEquals("2024", result.getYear());
		assertEquals("0", result.getTotalSales());
		assertEquals("0", result.getProfits());
	}

	@Test
	@DisplayName("fillEmptyFieldsWithZero - 기존 값은 변경하지 않음 (clean 안 함)")
	void fillZero_doesNotClean() {
		StockFinancialVO vo = StockFinancialVO.builder().totalSales("1,234,567억원").build();
		StockFinancialVO result = parser.callFillEmptyFieldsWithZero(vo, DEFAULT_EXCLUDE);
		assertEquals("1,234,567억원", result.getTotalSales());
	}

	@Test
	@DisplayName("fillEmptyFieldsWithZeroAndClean - null은 0으로, 기존 값은 숫자만 추출")
	void fillZeroAndClean_fillsAndCleans() {
		StockFinancialVO vo = StockFinancialVO.builder()
				.totalSales("1,234,567억원")
				.profits("50.5%")
				.build();
		StockFinancialVO result = parser.callFillEmptyFieldsWithZeroAndClean(vo, DEFAULT_EXCLUDE);
		assertEquals("1234567", result.getTotalSales());
		assertEquals("50.5", result.getProfits());
		assertEquals("0", result.getEps());
	}

	@Test
	@DisplayName("fillEmptyFieldsWithZero - N/A와 빈 문자열을 0으로 변환")
	void fillZero_convertsNAAndEmpty() {
		StockFinancialVO vo = StockFinancialVO.builder().totalSales("N/A").profits("").build();
		StockFinancialVO result = parser.callFillEmptyFieldsWithZero(vo, DEFAULT_EXCLUDE);
		assertEquals("0", result.getTotalSales());
		assertEquals("0", result.getProfits());
	}

	@Test
	@DisplayName("fillEmptyFieldsWithZeroAndClean - N/A를 0으로 변환")
	void fillZeroAndClean_convertsNA() {
		StockFinancialVO vo = StockFinancialVO.builder().totalSales("N/A").build();
		StockFinancialVO result = parser.callFillEmptyFieldsWithZeroAndClean(vo, DEFAULT_EXCLUDE);
		assertEquals("0", result.getTotalSales());
	}

	@Test
	@DisplayName("fillEmptyFieldsWithZero - 제외 필드는 변경하지 않음")
	void fillZero_excludeFieldsUntouched() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		parser.callFillEmptyFieldsWithZero(vo, Set.of("id", "deleteYn", "stockMasterId"));
		assertNull(vo.getId());
		assertNull(vo.getDeleteYn());
		assertNull(vo.getStockMasterId());
	}

	@Test
	@DisplayName("fillEmptyFieldsWithZero - non-String 필드(Timestamp)는 건드리지 않음")
	void fillZero_nonStringFieldsUntouched() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		parser.callFillEmptyFieldsWithZero(vo, Set.of());
		assertNull(vo.getCreateDate());
		assertNull(vo.getUpdateDate());
	}

	// ==================== mapDataToObject 제네릭 반환 타입 ====================

	@Test
	@DisplayName("mapDataToObject - 캐스팅 없이 원래 타입으로 반환")
	void mapDataToObject_returnsOriginalType() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		Map<String, String> mapper = new HashMap<>();
		mapper.put("매출액", "totalSales");

		StockFinancialVO result = parser.callMapDataToObject(vo, mapper, "매출액", "1000");
		assertSame(vo, result);
		assertEquals("1000", result.getTotalSales());
	}

	@Test
	@DisplayName("mapDataToObject - 매핑에 없는 키는 무시")
	void mapDataToObject_ignoresUnknownKey() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		Map<String, String> mapper = new HashMap<>();
		mapper.put("매출액", "totalSales");

		StockFinancialVO result = parser.callMapDataToObject(vo, mapper, "없는키", "1000");
		assertSame(vo, result);
		assertNull(result.getTotalSales());
	}

	@Test
	@DisplayName("mapDataToObject - 쉼표 포함 값은 trimValue로 숫자만 추출")
	void mapDataToObject_trimsValue() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		Map<String, String> mapper = new HashMap<>();
		mapper.put("영업이익", "profits");
		parser.callMapDataToObject(vo, mapper, "영업이익", "1,234,567");
		assertEquals("1234567", vo.getProfits());
	}

	// ==================== 유틸리티 메서드 ====================

	@Test
	@DisplayName("extractNumeric - 숫자와 소수점만 추출")
	void extractNumeric_normal() {
		assertEquals("1234.56", parser.callExtractNumeric("1,234.56원"));
	}

	@Test
	@DisplayName("extractNumeric - null이면 0 반환")
	void extractNumeric_null() {
		assertEquals("0", parser.callExtractNumeric(null));
	}

	@Test
	@DisplayName("extractNumeric - 숫자 없으면 0 반환")
	void extractNumeric_noDigits() {
		assertEquals("0", parser.callExtractNumeric("abc"));
	}

	@Test
	@DisplayName("extractNumericWithSign - 부호 포함 추출")
	void extractNumericWithSign_negative() {
		assertEquals("-12.5", parser.callExtractNumericWithSign("-12.5%"));
	}

	@Test
	@DisplayName("extractUrlParam - 파라미터 추출")
	void extractUrlParam_found() {
		assertEquals("005930", parser.callExtractUrlParam("http://example.com?code=005930", "code"));
	}

	@Test
	@DisplayName("extractUrlParam - 없는 파라미터는 빈 문자열")
	void extractUrlParam_notFound() {
		assertEquals("", parser.callExtractUrlParam("http://example.com?code=005930", "name"));
	}

	@Test
	@DisplayName("isNumeric - 유효한 숫자")
	void isNumeric_valid() {
		assertTrue(parser.callIsNumeric("123"));
		assertTrue(parser.callIsNumeric("12.5"));
		assertTrue(parser.callIsNumeric("+3.14"));
		assertTrue(parser.callIsNumeric("-0.5"));
	}

	@Test
	@DisplayName("isNumeric - 유효하지 않은 값")
	void isNumeric_invalid() {
		assertFalse(parser.callIsNumeric(null));
		assertFalse(parser.callIsNumeric(""));
		assertFalse(parser.callIsNumeric("abc"));
	}
}
