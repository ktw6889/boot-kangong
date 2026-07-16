package com.kangong.test.stock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockFinancialParser;

import static org.junit.jupiter.api.Assertions.*;

class StockFinancialParserTest {

	private StockFinancialParser parser;

	@BeforeEach
	void setUp() {
		parser = new StockFinancialParser();
	}

	// ==================== URL 상수 검증 ====================

	@Test
	@DisplayName("URL 상수 - WISEREPORT_BASE 기반으로 올바르게 구성")
	void testUrlConstants() throws Exception {
		String base = getStaticField("WISEREPORT_BASE");
		String companyV1 = getStaticField("COMPANY_PAGE_V1");
		String companyV2 = getStaticField("COMPANY_PAGE_V2");
		String apiV1 = getStaticField("FINANCIAL_API_V1");
		String apiV2 = getStaticField("FINANCIAL_API_V2");

		assertTrue(companyV1.startsWith(base));
		assertTrue(companyV2.startsWith(base));
		assertTrue(apiV1.startsWith(base));
		assertTrue(apiV2.startsWith(base));
		assertTrue(companyV1.contains("c1010001"));
		assertTrue(companyV2.contains("c1030001"));
		assertTrue(apiV1.contains("cF1001"));
		assertTrue(apiV2.contains("cF3002"));
	}

	@Test
	@DisplayName("URL 상수 - NAVER_FINANCE_URL이 https로 시작")
	void testNaverFinanceUrl() throws Exception {
		String url = getStaticField("NAVER_FINANCE_URL");
		assertTrue(url.startsWith("https://finance.naver.com"));
	}

	// ==================== extractParamFromHtml ====================

	@Test
	@DisplayName("extractParamFromHtml - encparam 추출")
	void testExtractEncParam() throws Exception {
		String html = "var data = {cmp_cd: '005930', encparam: 'abc123xyz'}; some other text";
		String result = invokeExtractParam(html, "encparam: '");
		assertEquals("abc123xyz", result);
	}

	@Test
	@DisplayName("extractParamFromHtml - id 추출")
	void testExtractIdParam() throws Exception {
		String html = "config = {frq: 0, encparam: 'enc123', id: 'myId456'}; end";
		String result = invokeExtractParam(html, ", id: '");
		assertEquals("myId456", result);
	}

	@Test
	@DisplayName("extractParamFromHtml - 여러 번 등장 시 마지막 값 추출")
	void testExtractParamMultipleOccurrences() throws Exception {
		String html = "first encparam: 'old_value' middle encparam: 'new_value' end";
		String result = invokeExtractParam(html, "encparam: '");
		assertEquals("new_value", result);
	}

	// ==================== V1 HTML 파싱 (parseFinancialDocument) ====================

	@Test
	@DisplayName("V1 HTML - 재무제표 헤더에서 연도 추출")
	void testParseFinancialDocumentYears() throws Exception {
		Document document = Jsoup.parse(buildFinancialHtml());
		StockVO stockVO = StockVO.builder().stockId("005930").id("MASTER_1").build();

		List<StockFinancialVO> result = invokeParseFinancialDocument(document, stockVO);

		assertEquals(3, result.size());
		assertEquals("2021", result.get(0).getYear());
		assertEquals("2022", result.get(1).getYear());
		assertEquals("2023", result.get(2).getYear());
	}

	@Test
	@DisplayName("V1 HTML - stockId와 stockMasterId 설정")
	void testParseFinancialDocumentStockIds() throws Exception {
		Document document = Jsoup.parse(buildFinancialHtml());
		StockVO stockVO = StockVO.builder().stockId("005930").id("MASTER_1").build();

		List<StockFinancialVO> result = invokeParseFinancialDocument(document, stockVO);

		for (StockFinancialVO vo : result) {
			assertEquals("005930", vo.getStockId());
			assertEquals("MASTER_1", vo.getStockMasterId());
		}
	}

	@Test
	@DisplayName("V1 HTML - 매출액, 영업이익 등 필드 매핑")
	void testParseFinancialDocumentFieldMapping() throws Exception {
		Document document = Jsoup.parse(buildFinancialHtml());
		StockVO stockVO = StockVO.builder().stockId("005930").id("M1").build();

		List<StockFinancialVO> result = invokeParseFinancialDocument(document, stockVO);

		assertEquals("100000", result.get(0).getTotalSales());
		assertEquals("200000", result.get(1).getTotalSales());
		assertEquals("300000", result.get(2).getTotalSales());

		assertEquals("50000", result.get(0).getProfits());
		assertEquals("60000", result.get(1).getProfits());
		assertEquals("70000", result.get(2).getProfits());
	}

	@Test
	@DisplayName("V1 HTML - 매핑에 없는 행은 무시")
	void testParseFinancialDocumentIgnoresUnmappedRows() throws Exception {
		String html = buildFinancialHtmlWith("알수없는항목", "999", "888", "777");
		Document document = Jsoup.parse(html);
		StockVO stockVO = StockVO.builder().stockId("005930").id("M1").build();

		List<StockFinancialVO> result = invokeParseFinancialDocument(document, stockVO);

		assertEquals(3, result.size());
		assertNull(result.get(0).getTotalSales());
	}

	@Test
	@DisplayName("V1 HTML - 빈 tbody일 때 빈 VO 리스트 반환")
	void testParseFinancialDocumentEmptyBody() throws Exception {
		String html = "<html><body>"
				+ "<table summary='주요재무정보를 제공합니다.'><tbody></tbody></table>"
				+ "<table summary='주요재무정보를 제공합니다.'>"
				+ "<thead><tr><th>col</th></tr><tr><th>2023/12</th></tr></thead>"
				+ "<tbody></tbody>"
				+ "</table>"
				+ "</body></html>";
		Document document = Jsoup.parse(html);
		StockVO stockVO = StockVO.builder().stockId("TEST").build();

		List<StockFinancialVO> result = invokeParseFinancialDocument(document, stockVO);

		assertEquals(1, result.size());
		assertEquals("2023", result.get(0).getYear());
		assertNull(result.get(0).getTotalSales());
	}

	// ==================== V2 JSON 파싱 (parseFinancialJson) ====================

	@Test
	@DisplayName("V2 JSON - YYMM 배열에서 연도 추출")
	void testParseFinancialJsonYears() throws Exception {
		JSONObject json = buildFinancialJson();
		StockVO stockVO = StockVO.builder().stockId("005930").build();

		List<StockFinancialVO> result = invokeParseFinancialJson(json, stockVO);

		assertEquals(3, result.size());
		assertEquals("2021", result.get(0).getYear());
		assertEquals("2022", result.get(1).getYear());
		assertEquals("2023", result.get(2).getYear());
	}

	@Test
	@DisplayName("V2 JSON - stockId 설정")
	void testParseFinancialJsonStockId() throws Exception {
		JSONObject json = buildFinancialJson();
		StockVO stockVO = StockVO.builder().stockId("000660").build();

		List<StockFinancialVO> result = invokeParseFinancialJson(json, stockVO);

		for (StockFinancialVO vo : result) {
			assertEquals("000660", vo.getStockId());
		}
	}

	@Test
	@DisplayName("V2 JSON - DATA에서 필드 매핑 (String 값)")
	void testParseFinancialJsonStringValues() throws Exception {
		JSONObject json = buildFinancialJson();
		StockVO stockVO = StockVO.builder().stockId("005930").build();

		List<StockFinancialVO> result = invokeParseFinancialJson(json, stockVO);

		assertEquals("500000", result.get(0).getLiquidAsset());
		assertEquals("600000", result.get(1).getLiquidAsset());
		assertEquals("700000", result.get(2).getLiquidAsset());
	}

	@Test
	@DisplayName("V2 JSON - DATA에서 필드 매핑 (Double 값)")
	void testParseFinancialJsonDoubleValues() throws Exception {
		JSONObject json = buildFinancialJson();
		StockVO stockVO = StockVO.builder().stockId("005930").build();

		List<StockFinancialVO> result = invokeParseFinancialJson(json, stockVO);

		assertEquals("150000.5", result.get(0).getLiquidDept());
		assertEquals("160000.3", result.get(1).getLiquidDept());
		assertEquals("170000.1", result.get(2).getLiquidDept());
	}

	@Test
	@DisplayName("V2 JSON - ACC_NM이 매핑에 없으면 무시")
	void testParseFinancialJsonIgnoresUnmapped() throws Exception {
		JSONObject json = new JSONObject();
		JSONArray yymm = new JSONArray();
		yymm.add("2023/12");
		json.put("YYMM", yymm);

		JSONArray data = new JSONArray();
		JSONObject unknownRow = new JSONObject();
		unknownRow.put("ACC_NM", "알수없는항목");
		unknownRow.put("DATA1", "999");
		data.add(unknownRow);
		json.put("DATA", data);

		StockVO stockVO = StockVO.builder().stockId("TEST").build();
		List<StockFinancialVO> result = invokeParseFinancialJson(json, stockVO);

		assertEquals(1, result.size());
		assertNull(result.get(0).getLiquidAsset());
	}

	@Test
	@DisplayName("V2 JSON - 빈 DATA 배열")
	void testParseFinancialJsonEmptyData() throws Exception {
		JSONObject json = new JSONObject();
		JSONArray yymm = new JSONArray();
		yymm.add("2023/12");
		json.put("YYMM", yymm);
		json.put("DATA", new JSONArray());

		StockVO stockVO = StockVO.builder().stockId("TEST").build();
		List<StockFinancialVO> result = invokeParseFinancialJson(json, stockVO);

		assertEquals(1, result.size());
		assertEquals("2023", result.get(0).getYear());
	}

	// ==================== insertZero ====================

	@Test
	@DisplayName("insertZero - null 필드를 0으로 채우고 숫자 외 문자 제거")
	void testInsertZero() {
		StockFinancialVO vo = StockFinancialVO.builder()
				.stockId("005930")
				.year("2023")
				.totalSales("1,234,567억원")
				.profits("50.5%")
				.build();

		StockFinancialVO result = parser.insertZero(vo);

		assertEquals("005930", result.getStockId());
		assertEquals("2023", result.getYear());
		assertEquals("1234567", result.getTotalSales());
		assertEquals("50.5", result.getProfits());
		assertEquals("0", result.getEarnings());
		assertEquals("0", result.getEps());
	}

	@Test
	@DisplayName("insertZero - 제외 필드(id, deleteYn)는 변경하지 않음")
	void testInsertZeroExcludes() {
		StockFinancialVO vo = StockFinancialVO.builder().build();
		StockFinancialVO result = parser.insertZero(vo);

		assertNull(result.getId());
		assertNull(result.getDeleteYn());
	}

	@Test
	@DisplayName("insertZero - N/A를 0으로 변환")
	void testInsertZeroConvertsNA() {
		StockFinancialVO vo = StockFinancialVO.builder()
				.stockId("TEST")
				.totalSales("N/A")
				.profits("N/A")
				.build();

		StockFinancialVO result = parser.insertZero(vo);

		assertEquals("0", result.getTotalSales());
		assertEquals("0", result.getProfits());
	}

	// ==================== Helper: 리플렉션 호출 ====================

	private String getStaticField(String fieldName) throws Exception {
		Field field = StockFinancialParser.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return (String) field.get(null);
	}

	private String invokeExtractParam(String html, String prefix) throws Exception {
		Method method = StockFinancialParser.class.getDeclaredMethod(
				"extractParamFromHtml", String.class, String.class);
		method.setAccessible(true);
		return (String) method.invoke(parser, html, prefix);
	}

	@SuppressWarnings("unchecked")
	private List<StockFinancialVO> invokeParseFinancialDocument(Document document, StockVO stockVO) throws Exception {
		Method method = StockFinancialParser.class.getDeclaredMethod(
				"parseFinancialDocument", Document.class, StockVO.class);
		method.setAccessible(true);
		return (List<StockFinancialVO>) method.invoke(parser, document, stockVO);
	}

	@SuppressWarnings("unchecked")
	private List<StockFinancialVO> invokeParseFinancialJson(JSONObject jsonObj, StockVO stockVO) throws Exception {
		Method method = StockFinancialParser.class.getDeclaredMethod(
				"parseFinancialJson", JSONObject.class, StockVO.class);
		method.setAccessible(true);
		return (List<StockFinancialVO>) method.invoke(parser, jsonObj, stockVO);
	}

	// ==================== Helper: 테스트 데이터 ====================

	private String buildFinancialHtml() {
		return buildFinancialHtmlFull(
				new String[]{"매출액", "영업이익"},
				new String[][]{
						{"100000", "200000", "300000"},
						{"50000", "60000", "70000"}
				}
		);
	}

	private String buildFinancialHtmlWith(String headerName, String v1, String v2, String v3) {
		return buildFinancialHtmlFull(
				new String[]{headerName},
				new String[][]{{v1, v2, v3}}
		);
	}

	private String buildFinancialHtmlFull(String[] headers, String[][] values) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		// 첫 번째 테이블 (더미 - parser는 tables.get(1)을 사용)
		sb.append("<table summary='주요재무정보를 제공합니다.'><tbody></tbody></table>");
		// 두 번째 테이블 (실제 데이터)
		sb.append("<table summary='주요재무정보를 제공합니다.'>");
		sb.append("<thead>");
		sb.append("<tr><th>주요재무정보</th></tr>");
		sb.append("<tr><th>2021/12</th><th>2022/12</th><th>2023/12</th></tr>");
		sb.append("</thead>");
		sb.append("<tbody>");
		for (int i = 0; i < headers.length; i++) {
			sb.append("<tr>");
			sb.append("<th>").append(headers[i]).append("</th>");
			for (String val : values[i]) {
				sb.append("<td>").append(val).append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</body></html>");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildFinancialJson() {
		JSONObject json = new JSONObject();

		JSONArray yymm = new JSONArray();
		yymm.add("2021/12");
		yymm.add("2022/12");
		yymm.add("2023/12");
		json.put("YYMM", yymm);

		JSONArray data = new JSONArray();

		// String 값 행 (유동자산)
		JSONObject row1 = new JSONObject();
		row1.put("ACC_NM", "유동자산");
		row1.put("DATA1", "500000");
		row1.put("DATA2", "600000");
		row1.put("DATA3", "700000");
		data.add(row1);

		// Double 값 행 (유동부채)
		JSONObject row2 = new JSONObject();
		row2.put("ACC_NM", "유동부채");
		row2.put("DATA1", 150000.5);
		row2.put("DATA2", 160000.3);
		row2.put("DATA3", 170000.1);
		data.add(row2);

		json.put("DATA", data);
		return json;
	}
}
