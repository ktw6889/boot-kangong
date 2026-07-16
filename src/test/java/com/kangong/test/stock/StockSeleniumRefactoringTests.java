package com.kangong.test.stock;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.service.StockFieldMappingRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StockSeleniumFinancialUtil 리팩토링 검증 테스트
 * - getHeaderAttributeMapper 중복 제거 후 StockFinancialParser 위임이 정상 동작하는지 확인
 * - DB/Selenium 없이 순수 로직만 검증
 */
class StockSeleniumRefactoringTests {

	@Test
	@DisplayName("getFinancialHeaderMapper - 기업현황 매퍼가 모든 필수 키를 포함하는지 확인")
	void testFinancialHeaderMapperContainsAllKeys() {
		Map<String, String> mapper = StockFieldMappingRegistry.financialHtmlTable();

		assertNotNull(mapper);
		assertEquals(20, mapper.size());

		assertEquals("totalSales", mapper.get("매출액"));
		assertEquals("profits", mapper.get("영업이익"));
		assertEquals("earnings", mapper.get("당기순이익"));
		assertEquals("profitsRatio", mapper.get("영업이익률"));
		assertEquals("netProfitRatio", mapper.get("순이익률"));
		assertEquals("roe", mapper.get("ROE(%)"));
		assertEquals("deptRatio", mapper.get("부채비율"));
		assertEquals("reserveRatio", mapper.get("자본유보율"));
		assertEquals("eps", mapper.get("EPS(원)"));
		assertEquals("per", mapper.get("PER(배)"));
		assertEquals("bps", mapper.get("BPS(원)"));
		assertEquals("pbr", mapper.get("PBR(배)"));
		assertEquals("dividendsPerShare", mapper.get("현금DPS(원)"));
		assertEquals("dividendsRate", mapper.get("현금배당수익률"));
		assertEquals("dividendsTendency", mapper.get("현금배당성향(%)"));
		assertEquals("totalAssets", mapper.get("자산총계"));
		assertEquals("sharesOutstanding", mapper.get("발행주식수(보통주)"));
		assertEquals("totalDept", mapper.get("부채총계"));
		assertEquals("totalCapital", mapper.get("자본총계"));
		assertEquals("capital", mapper.get("자본금"));
	}

	@Test
	@DisplayName("getFinancialHeaderMapper2 - 재무상태표 매퍼가 모든 필수 키를 포함하는지 확인")
	void testFinancialHeaderMapper2ContainsAllKeys() {
		Map<String, String> mapper = StockFieldMappingRegistry.balanceSheetHtmlTable();

		assertNotNull(mapper);
		assertEquals(5, mapper.size());

		assertEquals("liquidAsset", mapper.get("유동자산"));
		assertEquals("liquidDept", mapper.get("유동부채"));
		assertEquals("totalStockQty", mapper.get("발행주식수"));
		assertEquals("commonStockQty", mapper.get("보통주"));
		assertEquals("preferredStockQty", mapper.get("우선주"));
	}

	@Test
	@DisplayName("매퍼의 필드명이 StockFinancialVO에 실제 존재하는지 확인")
	void testMapperFieldNamesExistInVO() {
		Map<String, String> mapper1 = StockFieldMappingRegistry.financialHtmlTable();
		Map<String, String> mapper2 = StockFieldMappingRegistry.balanceSheetHtmlTable();

		StockFinancialVO vo = StockFinancialVO.builder().build();
		Class<?> clazz = vo.getClass();

		for (Map.Entry<String, String> entry : mapper1.entrySet()) {
			assertDoesNotThrow(() -> clazz.getDeclaredField(entry.getValue()),
					"StockFinancialVO에 필드가 없음: " + entry.getValue() + " (키: " + entry.getKey() + ")");
		}

		for (Map.Entry<String, String> entry : mapper2.entrySet()) {
			assertDoesNotThrow(() -> clazz.getDeclaredField(entry.getValue()),
					"StockFinancialVO에 필드가 없음: " + entry.getValue() + " (키: " + entry.getKey() + ")");
		}
	}

	@Test
	@DisplayName("매퍼 간 키 충돌이 없는지 확인")
	void testNoKeyConflictBetweenMappers() {
		Map<String, String> mapper1 = StockFieldMappingRegistry.financialHtmlTable();
		Map<String, String> mapper2 = StockFieldMappingRegistry.balanceSheetHtmlTable();

		for (String key : mapper2.keySet()) {
			if (mapper1.containsKey(key)) {
				fail("두 매퍼 간 키 충돌: " + key);
			}
		}
	}

	@Test
	@DisplayName("매퍼에 없는 키를 조회하면 null 반환")
	void testMapperReturnsNullForUnknownKey() {
		Map<String, String> mapper = StockFieldMappingRegistry.financialHtmlTable();
		assertNull(mapper.get("존재하지않는키"));
	}

	@Test
	@DisplayName("리플렉션으로 StockFinancialVO 필드 설정이 정상 동작하는지 확인")
	void testReflectionFieldSetting() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		Map<String, String> mapper = StockFieldMappingRegistry.financialHtmlTable();

		String fieldName = mapper.get("매출액");
		java.lang.reflect.Field field = vo.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(vo, "1000");

		assertEquals("1000", vo.getTotalSales());
	}

	@Test
	@DisplayName("리플렉션으로 재무상태표 VO 필드 설정이 정상 동작하는지 확인")
	void testReflectionFieldSettingForMapper2() throws Exception {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		Map<String, String> mapper = StockFieldMappingRegistry.balanceSheetHtmlTable();

		String fieldName = mapper.get("유동자산");
		java.lang.reflect.Field field = vo.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(vo, "5000");

		assertEquals("5000", vo.getLiquidAsset());
	}

	@Test
	@DisplayName("매퍼의 모든 필드에 값을 설정하고 읽을 수 있는지 전수 검증")
	void testAllMapperFieldsWritable() throws Exception {
		Map<String, String> mapper = StockFieldMappingRegistry.financialHtmlTable();
		StockFinancialVO vo = StockFinancialVO.builder().build();

		for (Map.Entry<String, String> entry : mapper.entrySet()) {
			java.lang.reflect.Field field = vo.getClass().getDeclaredField(entry.getValue());
			field.setAccessible(true);
			field.set(vo, "test_" + entry.getKey());

			String actual = (String) field.get(vo);
			assertEquals("test_" + entry.getKey(), actual,
					"필드 값 설정/읽기 실패: " + entry.getValue());
		}
	}
}
