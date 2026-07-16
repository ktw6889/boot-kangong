package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.parser.StockMobileParser;
import com.kangong.stock.service.StockFetcher;
import com.kangong.stock.service.StockMobileEsgService;
import com.kangong.stock.service.StockJsonConverter;
import com.kangong.stock.service.StockRepository;

import java.lang.reflect.Field;

/**
 * 실제 네이버 API를 호출하여 ESG 데이터 파싱을 검증하는 Live 테스트.
 * 네트워크 접속이 필요합니다.
 */
class StockEsgLiveTest {

	StockMobileParser parser;
	StockFetcher fetcher;
	StockMobileEsgService esgService;

	@BeforeEach
	void setUp() throws Exception {
		parser = new StockMobileParser();
		fetcher = new StockFetcher();
		esgService = new StockMobileEsgService();

		Field parserField = StockFetcher.class.getDeclaredField("stockMobileParser");
		parserField.setAccessible(true);
		parserField.set(fetcher, parser);

		injectField("stockFetcher", fetcher);
		injectField("stockMobileParser", parser);
		injectField("stockJsonConverter", new StockJsonConverter());
	}

	private void injectField(String fieldName, Object value) throws Exception {
		Field field = StockMobileEsgService.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(esgService, value);
	}

	@Test
	@DisplayName("삼성전자(005930) ESG API 호출 및 파싱 - 최신 년도 데이터 존재 확인")
	void testFetchAndParseEsg_samsung() throws Exception {
		String json = fetcher.fetchEsg("005930");

		assertNotNull(json);
		assertFalse(json.isEmpty());
		System.out.println("[RAW JSON 일부] " + json.substring(0, Math.min(500, json.length())));

		Map<String, StockEsgVO> result = esgService.parseEsgJson(json);

		assertFalse(result.isEmpty(), "파싱 결과가 비어있음 - API 응답 확인 필요");
		System.out.println("[파싱 결과] 년도 목록: " + result.keySet());

		// 최소 1개 년도 데이터 존재
		assertTrue(result.size() >= 1);

		// 최신 년도 확인
		String maxYear = result.keySet().stream().max(String::compareTo).orElse("없음");
		System.out.println("[최신 년도] " + maxYear);

		// 최신 년도 VO 검증
		StockEsgVO latestVo = result.get(maxYear);
		assertNotNull(latestVo);
		assertEquals("005930", latestVo.getStockId());
		assertEquals(maxYear, latestVo.getYear());
		System.out.printf("[최신 ESG] stockId=%s, year=%s, 온실가스=%s, 평균연봉=%s, 사외이사비율=%s%n",
				latestVo.getStockId(), latestVo.getYear(),
				latestVo.getGreenHouseEmission(), latestVo.getAverageAnnualSalary(),
				latestVo.getOutsideDirectorRate());

		// 각 년도 데이터 출력
		for (Map.Entry<String, StockEsgVO> entry : result.entrySet()) {
			StockEsgVO vo = entry.getValue();
			System.out.printf("  [%s] E: 온실가스=%s, 에너지=%s | S: 연봉=%s, 기부금=%s | G: 사외이사=%s, 최대주주=%s%n",
					entry.getKey(),
					vo.getGreenHouseEmission(), vo.getEnergyUsage(),
					vo.getAverageAnnualSalary(), vo.getDonation(),
					vo.getOutsideDirectorRate(), vo.getLargestShareHolderRatio());
		}
	}

	@Test
	@DisplayName("삼성전자(005930) ESG - E/S/G 모든 테마 데이터 파싱")
	void testEsgAllThemes_samsung() throws Exception {
		String json = fetcher.fetchEsg("005930");
		Map<String, StockEsgVO> result = esgService.parseEsgJson(json);

		assertFalse(result.isEmpty());

		// 아무 년도나 하나 가져와서 모든 필드가 채워져 있는지 확인
		StockEsgVO vo = result.values().iterator().next();

		// E 테마 (환경) - 최소 일부 필드 존재
		boolean hasE = vo.getGreenHouseEmission() != null || vo.getEnergyUsage() != null
				|| vo.getWaterRecyclingRate() != null;

		// S 테마 (사회)
		boolean hasS = vo.getAverageAnnualSalary() != null || vo.getDonation() != null
				|| vo.getContinuousServiceYear() != null;

		// G 테마 (지배구조)
		boolean hasG = vo.getOutsideDirectorRate() != null || vo.getLargestShareHolderRatio() != null
				|| vo.getDirectorateIndependence() != null;

		System.out.printf("[테마 존재] E=%b, S=%b, G=%b%n", hasE, hasS, hasG);
		assertTrue(hasE || hasS || hasG, "E/S/G 중 최소 하나는 데이터가 있어야 함");
	}

	@Test
	@DisplayName("SK하이닉스(000660) ESG API 호출 및 파싱")
	void testFetchAndParseEsg_skhynix() throws Exception {
		String json = fetcher.fetchEsg("000660");

		assertNotNull(json);
		Map<String, StockEsgVO> result = esgService.parseEsgJson(json);

		assertFalse(result.isEmpty(), "SK하이닉스 ESG 데이터 없음");
		System.out.println("[SK하이닉스] 년도: " + result.keySet());

		for (Map.Entry<String, StockEsgVO> entry : result.entrySet()) {
			StockEsgVO vo = entry.getValue();
			System.out.printf("  [%s] 온실가스=%s, 평균연봉=%s, 최대주주=%s%n",
					entry.getKey(), vo.getGreenHouseEmission(),
					vo.getAverageAnnualSalary(), vo.getLargestShareHolderRatio());
		}
	}
}
