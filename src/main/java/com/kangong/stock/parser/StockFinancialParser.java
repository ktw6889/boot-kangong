package com.kangong.stock.parser;

import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockFieldMappingRegistry;

import lombok.extern.log4j.Log4j2;

/**
 * 재무제표 파싱 (기존 StockFinancialUtil 대체)
 * - getStockFinancial: HTML 파싱 방식 (V1)
 * - getStockFinancial2: JSON API 방식 (V2)
 */
@Log4j2
@Component
public class StockFinancialParser extends AbstractStockDataParser {

	private static final String NAVER_FINANCE_URL = "https://finance.naver.com/item/main.naver";
	private static final String WISEREPORT_BASE = "https://navercomp.wisereport.co.kr/v2/company";
	private static final String COMPANY_PAGE_V1 = WISEREPORT_BASE + "/c1010001.aspx";
	private static final String COMPANY_PAGE_V2 = WISEREPORT_BASE + "/c1030001.aspx";
	private static final String FINANCIAL_API_V1 = WISEREPORT_BASE + "/ajax/cF1001.aspx";
	private static final String FINANCIAL_API_V2 = WISEREPORT_BASE + "/cF3002.aspx";

	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36";
	private static final String SESSION_COOKIE =
			"setC1010001=[{\"conGubun\":\"MAIN\",\"cTB23\":\"cns_td1\",\"bandChartGubun\":\"MAIN\",\"finGubun\":\"MAIN\",\"cTB00\":\"cns_td20\"}]; "
					+ "setC1030001=[{\"cTB301\":\"rpt_td2\",\"finGubun\":\"MAIN\",\"frqTyp\":\"0\",\"moreY\":1,\"moreQ\":1,\"moreQQ\":1}]";

	private static final Set<String> ZERO_FILL_EXCLUDE = new HashSet<String>() {{
		add("id");
		add("deleteYn");
	}};

	// ==================== 재무제표 V1 (HTML 파싱) ====================

	/**
	 * 종목 재무제표 파싱 (HTML 방식)
	 */
	public List<StockFinancialVO> getStockFinancial(StockVO stockVO) throws Exception {
		String stockId = stockVO.getStockId();
		Map<String, String> paramMap = getEncParamV1(stockId);
		String url = FINANCIAL_API_V1
				+ "?fin_typ=0&freq_typ=Y&encparam=" + paramMap.get("encparam")
				+ "&id=" + paramMap.get("idparam") + "&cmp_cd=" + stockId;

		Connection conn = Jsoup.connect(url);
		conn.header("Accept", "text/html, */*; q=0.01");
		conn.header("Content-Encoding", "gzip");
		conn.method(Connection.Method.GET);
		Document document = conn.get();

		return parseFinancialDocument(document, stockVO);
	}

	/**
	 * V1용 encparam + id 파라미터 추출
	 */
	private Map<String, String> getEncParamV1(String stockId) throws Exception {
		Document document = Jsoup.connect(COMPANY_PAGE_V1 + "?cmp_cd=" + stockId).get();
		String html = document.toString();

		Map<String, String> encMap = new HashMap<>();
		encMap.put("encparam", extractParamFromHtml(html, ", encparam: '"));
		encMap.put("idparam", extractParamFromHtml(html, ", id: '"));
		return encMap;
	}

	private List<StockFinancialVO> parseFinancialDocument(Document document, StockVO stockVO) throws Exception {
		List<String> headerList = parseFinancialHeader(document);
		ArrayList<StockFinancialVO> voList = createFinancialVOList(headerList, stockVO);
		return parseFinancialBody(document, voList);
	}

	private List<String> parseFinancialHeader(Document document) throws Exception {
		Elements tables = document.select("table[summary='주요재무정보를 제공합니다.']");
		Elements headers = tables.get(1).select("thead tr").get(1).select("th");
		List<String> headerList = new ArrayList<>();
		for (Element el : headers) {
			headerList.add(el.text().split("/")[0]);
		}
		return headerList;
	}

	private ArrayList<StockFinancialVO> createFinancialVOList(List<String> headerList, StockVO stockVO) {
		ArrayList<StockFinancialVO> list = new ArrayList<>();
		for (String year : headerList) {
			StockFinancialVO vo = StockFinancialVO.builder().build();
			vo.setYear(year);
			vo.setStockId(stockVO.getStockId());
			vo.setStockMasterId(stockVO.getId());
			list.add(vo);
		}
		return list;
	}

	private ArrayList<StockFinancialVO> parseFinancialBody(Document document,
														   ArrayList<StockFinancialVO> voList) throws Exception {
		Elements tables = document.select("table[summary='주요재무정보를 제공합니다.']");
		Elements rows = tables.get(1).select("tbody tr");
		Map<String, String> mapper = StockFieldMappingRegistry.financialHtmlTable();

		for (Element row : rows) {
			String headerText = row.select("th").get(0).text();
			String fieldName = mapper.get(headerText);
			if (fieldName == null) continue;

			Elements tdElements = row.select("td");
			for (int i = 0; i < tdElements.size() && i < voList.size(); i++) {
				setFieldValue(voList.get(i), fieldName, tdElements.get(i).text());
			}
		}
		return voList;
	}

	// ==================== 재무제표 V2 (JSON API) ====================

	/**
	 * 종목 재무제표 파싱 (JSON API 방식)
	 */
	public List<StockFinancialVO> getStockFinancial2(StockVO stockVO) throws Exception {
		String stockId = stockVO.getStockId();
		preloadSessionUrls(stockId);

		String encStr = extractEncParamV2(stockId);
		String financeUrl = FINANCIAL_API_V2
				+ "?cmp_cd=" + stockId + "&frq=0&rpt=1&finGubun=MAIN&frqTyp=0&cn=&encparam=" + encStr;

		JSONObject jsonResponse = fetchFinancialJson(financeUrl, stockId, encStr);
		return parseFinancialJson(jsonResponse, stockVO);
	}

	/**
	 * V2 세션 유지를 위한 사전 URL 연결
	 */
	private void preloadSessionUrls(String stockId) throws Exception {
		connectUrl(NAVER_FINANCE_URL + "?code=" + stockId);
		connectUrl(COMPANY_PAGE_V1 + "?cmp_cd=" + stockId);
		connectUrl(COMPANY_PAGE_V2 + "?cn=&cmp_cd=" + stockId);
	}

	/**
	 * V2용 encparam 추출
	 */
	private String extractEncParamV2(String stockId) throws Exception {
		Document document = Jsoup.connect(COMPANY_PAGE_V2 + "?cn=&cmp_cd=" + stockId).get();
		return extractParamFromHtml(document.toString(), "encparam: '");
	}

	private void connectUrl(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setUseCaches(true);
		try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8")) {
			// 세션 유지를 위해 읽기만 수행
		} finally {
			conn.disconnect();
		}
	}

	private JSONObject fetchFinancialJson(String financeUrl, String stockId, String encStr) throws Exception {
		URL url = new URL(financeUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		setApiRequestHeaders(conn);

		JSONObject commands = new JSONObject();
		commands.put("cmp_cd", stockId);
		commands.put("frq", "0");
		commands.put("rpt", "1");
		commands.put("finGubun", "MAIN");
		commands.put("frqTyp", "0");
		commands.put("encparam", encStr);

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
			bw.write(commands.toString());
		}

		JSONObject result;
		try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8")) {
			result = (JSONObject) JSONValue.parse(isr);
		} finally {
			conn.disconnect();
		}
		return result;
	}

	private void setApiRequestHeaders(HttpURLConnection conn) {
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
		conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		conn.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Cookie", SESSION_COOKIE);
		conn.setRequestProperty("Sec-Fetch-Dest", "empty");
		conn.setRequestProperty("Sec-Fetch-Mode", "cors");
		conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
		conn.setRequestProperty("User-Agent", USER_AGENT);
	}

	private List<StockFinancialVO> parseFinancialJson(JSONObject jsonObj, StockVO stockVO) throws Exception {
		JSONArray dataArray = (JSONArray) jsonObj.get("DATA");
		JSONArray yymmArray = (JSONArray) jsonObj.get("YYMM");

		ArrayList<StockFinancialVO> resultList = new ArrayList<>();
		for (Object yymmObj : yymmArray) {
			String year = ((String) yymmObj).split("/")[0];
			StockFinancialVO vo = StockFinancialVO.builder().build();
			vo.setYear(year);
			vo.setStockId(stockVO.getStockId());
			resultList.add(vo);
		}

		Map<String, String> mapper = StockFieldMappingRegistry.balanceSheetHtmlTable();
		for (Object dataObj : dataArray) {
			JSONObject data = (JSONObject) dataObj;
			String fieldName = mapper.get((String) data.get("ACC_NM"));
			if (fieldName == null) continue;

			for (int i = 0; i < resultList.size(); i++) {
				Object valueObj = data.get("DATA" + (i + 1));
				if (valueObj instanceof String) {
					setFieldValue(resultList.get(i), fieldName, (String) valueObj);
				} else if (valueObj instanceof Double) {
					setFieldValue(resultList.get(i), fieldName, valueObj.toString());
				}
			}
		}

		log.info("JSON:" + resultList);
		return resultList;
	}

	// ==================== 현금흐름표 (JSON API) ====================

	/**
	 * 영업활동현금흐름 파싱 (wisereport cF3002.aspx rpt=3 현금흐름표)
	 */
	public List<StockFinancialVO> getStockCashFlow(StockVO stockVO) throws Exception {
		String stockId = stockVO.getStockId();
		preloadSessionUrls(stockId);

		String encStr = extractEncParamV2(stockId);
		String cashFlowUrl = FINANCIAL_API_V2
				+ "?cmp_cd=" + stockId + "&frq=0&rpt=3&finGubun=MAIN&frqTyp=0&cn=&encparam=" + encStr;

		JSONObject jsonResponse = fetchFinancialJson(cashFlowUrl, stockId, encStr);
		return parseCashFlowJson(jsonResponse, stockVO);
	}

	private List<StockFinancialVO> parseCashFlowJson(JSONObject jsonObj, StockVO stockVO) throws Exception {
		JSONArray dataArray = (JSONArray) jsonObj.get("DATA");
		JSONArray yymmArray = (JSONArray) jsonObj.get("YYMM");

		ArrayList<StockFinancialVO> resultList = new ArrayList<>();
		for (Object yymmObj : yymmArray) {
			String year = ((String) yymmObj).split("/")[0];
			StockFinancialVO vo = StockFinancialVO.builder().build();
			vo.setYear(year);
			vo.setStockId(stockVO.getStockId());
			resultList.add(vo);
		}

		Map<String, String> mapper = StockFieldMappingRegistry.cashFlowApi();
		for (Object dataObj : dataArray) {
			JSONObject data = (JSONObject) dataObj;
			String accNm = (String) data.get("ACC_NM");
			if (accNm == null) continue;
			String fieldName = mapper.get(accNm.trim());
			if (fieldName == null) continue;

			for (int i = 0; i < resultList.size(); i++) {
				Object valueObj = data.get("DATA" + (i + 1));
				if (valueObj instanceof String) {
					setFieldValue(resultList.get(i), fieldName, (String) valueObj);
				} else if (valueObj instanceof Double) {
					setFieldValue(resultList.get(i), fieldName, valueObj.toString());
				}
			}
		}

		return resultList;
	}

	// ==================== 공통 유틸 ====================

	/**
	 * HTML 소스에서 JavaScript 변수 값 추출
	 */
	private String extractParamFromHtml(String html, String prefix) {
		String[] splits = html.split(prefix);
		return splits[splits.length - 1].split("'")[0];
	}

	/**
	 * 빈 필드 "0" 채우기 + 숫자 외 문자 제거
	 */
	public StockFinancialVO insertZero(StockFinancialVO vo) {
		return fillEmptyFieldsWithZeroAndClean(vo, ZERO_FILL_EXCLUDE);
	}

}