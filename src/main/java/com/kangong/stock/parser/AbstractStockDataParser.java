package com.kangong.stock.parser;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lombok.extern.log4j.Log4j2;

/**
 * Stock 데이터 파싱 공통 베이스 클래스
 * - 7개 유틸리티 클래스(StockUtil, StockDailyPriceUtil, StockFinancialUtil,
 *   StockCategoryUtil, StockSeleniumFinancialUtil, StockSeleniumFinancialAnalysisUtil,
 *   StockMobileUtil)에서 중복되는 공통 로직을 통합
 */
@Log4j2
public abstract class AbstractStockDataParser {

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	// ==================== URL 호출 ====================

	/**
	 * Jsoup을 사용하여 URL에서 Document를 가져온다.
	 * (기존: StockUtil.getUrlDocument, StockFinancialUtil/StockCategoryUtil 내 중복 코드)
	 */
	protected Document fetchDocument(String url) throws Exception {
		Connection conn = Jsoup.connect(url);
		return conn.get();
	}

	/**
	 * Jsoup Connection에 커스텀 헤더를 설정하여 Document를 가져온다.
	 */
	protected Document fetchDocumentWithHeaders(String url, Map<String, String> headers) throws Exception {
		Connection conn = Jsoup.connect(url);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			conn.header(entry.getKey(), entry.getValue());
		}
		return conn.get();
	}

	/**
	 * HttpClient로 JSON 문자열을 가져온다. (커넥션 풀링 재사용)
	 */
	protected String fetchJsonData(String httpUrl) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(httpUrl))
				.header("Content-type", "application/json")
				.timeout(Duration.ofSeconds(15))
				.GET()
				.build();
		HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}

	protected CompletableFuture<String> fetchJsonDataAsync(String httpUrl) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(httpUrl))
				.header("Content-type", "application/json")
				.timeout(Duration.ofSeconds(15))
				.GET()
				.build();
		return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body);
	}

	// ==================== Reflection 기반 필드 설정 ====================

	/**
	 * Reflection으로 객체의 필드 값을 설정한다.
	 * (기존: StockUtil.setStockVO, StockDailyPriceUtil.setStockDailyPriceVO,
	 *  StockFinancialUtil.setStockFinancialVOList, StockMobileUtil.setValueForGeneralObject 등에서 반복)
	 */
	protected void setFieldValue(Object target, String fieldName, String value) {
		try {
			Class<?> clazz = target.getClass();
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			if (field.getType() != String.class) {
				log.debug("필드 타입 불일치: {}는 String이 아님 (실제: {})", fieldName, field.getType().getSimpleName());
				return;
			}
			field.set(target, value);
		} catch (NoSuchFieldException e) {
			log.debug("필드를 찾을 수 없음: {}", fieldName);
		} catch (Exception e) {
			log.debug("필드 설정 실패: fieldName={}, value={}", fieldName, value);
		}
	}

	/**
	 * 헤더 매핑을 사용하여 헤더명에 해당하는 필드에 값을 설정한다.
	 * (기존: 모든 Util의 headerAttributeMapper + Field 설정 패턴 통합)
	 */
	protected void setFieldByHeaderMapping(Object target, String headerName, String value,
										   Map<String, String> headerToFieldMapper) {
		String fieldName = headerToFieldMapper.get(headerName);
		if (fieldName != null) {
			setFieldValue(target, fieldName, value);
		}
	}

	// ==================== 빈 값 처리 (insertZero 통합) ====================

	protected <T> T fillEmptyFieldsWithZero(T target, Set<String> excludeFields) {
		return processStringFields(target, excludeFields, false);
	}

	protected <T> T fillEmptyFieldsWithZeroAndClean(T target, Set<String> excludeFields) {
		return processStringFields(target, excludeFields, true);
	}

	private <T> T processStringFields(T target, Set<String> excludeFields, boolean cleanNonNumeric) {
		try {
			Class<?> clazz = target.getClass();
			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getType() != String.class || excludeFields.contains(field.getName())) {
					continue;
				}
				String value = (String) field.get(target);
				if (value == null || value.isEmpty() || "N/A".equals(value)) {
					field.set(target, "0");
				} else if (cleanNonNumeric) {
					field.set(target, value.replaceAll("[^0-9.]", ""));
				}
			}
		} catch (Exception e) {
			log.error("필드 처리 오류", e);
		}
		return target;
	}

	// ==================== 문자열 유틸리티 ====================

	/**
	 * 숫자와 소수점만 추출한다.
	 */
	protected String extractNumeric(String value) {
		if (value == null) return "0";
		String result = value.replaceAll("[^0-9.]", "");
		return result.isEmpty() ? "0" : result;
	}

	/**
	 * 숫자, 소수점, 부호(-)를 추출한다.
	 */
	protected String extractNumericWithSign(String value) {
		if (value == null) return "0";
		String result = value.replaceAll("[^0-9.-]", "");
		return result.isEmpty() ? "0" : result;
	}

	/**
	 * 날짜 형식인지 판별하여, 날짜이면 그대로 반환하고 아니면 숫자만 추출한다.
	 * (기존: StockMobileUtil.getTrimValue)
	 */
	protected String trimValue(String value) {
		if (value == null) return "0";
		String datePattern = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
		if (value.matches(datePattern)) {
			return value;
		}
		return value.replaceAll("[^0-9.]", "");
	}

	/**
	 * 쉼표 제거
	 */
	protected String removeComma(String value) {
		if (value == null) return "";
		return value.replace(",", "");
	}

	// ==================== 매핑 유틸리티 ====================

	/**
	 * 헤더 매핑 기반으로 Object에 값을 설정한다.
	 * (기존: StockMobileUtil.setObjectForMappingData)
	 */
	protected <T> T mapDataToObject(T target, Map<String, String> attributeMapper,
									 String name, String value) {
		String fieldName = attributeMapper.get(name);
		if (fieldName != null) {
			setFieldValue(target, fieldName, trimValue(value));
		}
		return target;
	}

	/**
	 * 숫자 여부 판단 (기존: Selenium util에서 year 파싱 시 사용)
	 */
	protected boolean isNumeric(String str) {
		if (str == null || str.isEmpty()) return false;
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	/**
	 * URL에서 파라미터 값 추출 (기존: code=, no= 파싱에서 반복)
	 */
	protected String extractUrlParam(String url, String paramName) {
		String[] splits = url.split(paramName + "=");
		return splits.length > 1 ? splits[1] : "";
	}
}