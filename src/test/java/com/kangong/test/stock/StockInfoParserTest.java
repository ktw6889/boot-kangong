package com.kangong.test.stock;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockInfoParser;

import static org.junit.jupiter.api.Assertions.*;

class StockInfoParserTest {

	private StockInfoParser parser;

	@BeforeEach
	void setUp() {
		parser = new StockInfoParser();
	}

	// ==================== parseStockDetailFromDocument ====================

	@Test
	@DisplayName("parseStockDetail - 전체 필드가 정상 파싱되는 경우")
	void testAllFieldsParsed() throws Exception {
		Document document = Jsoup.parse(buildFullHtml());
		StockVO stockVO = StockVO.builder().stockId("005930").build();

		StockVO result = parser.parseStockDetailFromDocument(document, stockVO);

		assertEquals("삼성전자", result.getName());
		assertEquals("68000", result.getPrice());
		assertEquals("5000", result.getFaceValue());
		assertEquals("500000", result.getStockQty());
		assertEquals("34000000000", result.getMarketCapitalization());
		assertEquals("45.5", result.getForeignerRatio());
		assertEquals("12345678", result.getVolumn());
		assertEquals("12.5", result.getPer());
		assertEquals("67000", result.getPriceBeforeday());
		assertEquals("11.2", result.getEstimationPer());
		assertEquals("1.3", result.getPbr());
		assertEquals("52000", result.getBps());
		assertEquals("15.3", result.getIndustryPer());
		assertEquals("2.5", result.getIndustryBaisse());
		assertEquals("4.2", result.getInvestmentOpinion());
		assertEquals("80000", result.getTargetPrice());
		assertEquals("75000", result.getMax52());
		assertEquals("55000", result.getMin52());
		assertEquals("1.8", result.getDividendRate());
		assertEquals("KOREA", result.getNational());
		assertEquals("5777", result.getEps());
		assertEquals("6200", result.getEstimationEps());
	}

	@Test
	@DisplayName("parseStockDetail - 테이블 데이터가 없으면 해당 필드는 null 유지")
	void testMissingTableData() throws Exception {
		String html = "<html><body>"
				+ "<div class='wrap_company'><h2>테스트종목</h2></div>"
				+ "<div class='today'><p class='no_today'><em><span class='blind'>10000</span></em></p></div>"
				+ "<em id='_per'>8.5</em>"
				+ "<em id='_pbr'>0.9</em>"
				+ "<em id='_eps'>3000</em>"
				+ "<em id='_cns_per'>9.1</em>"
				+ "<em id='_cns_eps'>3200</em>"
				+ "</body></html>";

		Document document = Jsoup.parse(html);
		StockVO stockVO = StockVO.builder().stockId("000660").build();

		StockVO result = parser.parseStockDetailFromDocument(document, stockVO);

		assertEquals("테스트종목", result.getName());
		assertEquals("10000", result.getPrice());
		assertEquals("8.5", result.getPer());
		assertEquals("0.9", result.getPbr());
		assertEquals("3000", result.getEps());
		assertEquals("KOREA", result.getNational());

		assertNull(result.getFaceValue());
		assertNull(result.getStockQty());
		assertNull(result.getForeignerRatio());
		assertNull(result.getVolumn());
		assertNull(result.getBps());
		assertNull(result.getIndustryPer());
		assertNull(result.getInvestmentOpinion());
		assertNull(result.getTargetPrice());
		assertNull(result.getMax52());
		assertNull(result.getMin52());
		assertNull(result.getDividendRate());
	}

	@Test
	@DisplayName("parseStockDetail - 시가총액은 현재가 * 상장주식수로 계산")
	void testMarketCapCalculation() throws Exception {
		String html = "<html><body>"
				+ "<div class='wrap_company'><h2>테스트</h2></div>"
				+ "<div class='today'><p class='no_today'><em><span class='blind'>50,000</span></em></p></div>"
				+ "<table summary='시가총액 정보'><tbody>"
				+ "<tr><td></td></tr><tr><td></td></tr>"
				+ "<tr><td><em>1,000,000</em></td></tr>"
				+ "<tr><td><em>500</em><em>기타</em></td></tr>"
				+ "</tbody></table>"
				+ "<em id='_per'></em><em id='_pbr'></em><em id='_eps'></em>"
				+ "<em id='_cns_per'></em><em id='_cns_eps'></em>"
				+ "</body></html>";

		Document document = Jsoup.parse(html);
		StockVO stockVO = StockVO.builder().stockId("TEST").build();

		StockVO result = parser.parseStockDetailFromDocument(document, stockVO);

		assertEquals("50000", result.getPrice());
		assertEquals("1000000", result.getStockQty());
		assertEquals("50000000000", result.getMarketCapitalization());
	}

	@Test
	@DisplayName("parseStockDetail - 시가총액 계산 실패 시 null 유지")
	void testMarketCapCalculationFailure() throws Exception {
		String html = "<html><body>"
				+ "<div class='wrap_company'><h2>테스트</h2></div>"
				+ "<div class='today'><p class='no_today'><em><span class='blind'>가격없음</span></em></p></div>"
				+ "<em id='_per'></em><em id='_pbr'></em><em id='_eps'></em>"
				+ "<em id='_cns_per'></em><em id='_cns_eps'></em>"
				+ "</body></html>";

		Document document = Jsoup.parse(html);
		StockVO stockVO = StockVO.builder().stockId("TEST").build();

		StockVO result = parser.parseStockDetailFromDocument(document, stockVO);

		assertNull(result.getMarketCapitalization());
	}

	@Test
	@DisplayName("parseStockDetail - 쉼표, 통화기호 등 제거하여 숫자만 추출")
	void testNumericExtraction() throws Exception {
		String html = "<html><body>"
				+ "<div class='wrap_company'><h2>테스트</h2></div>"
				+ "<div class='today'><p class='no_today'><em><span class='blind'>68,000원</span></em></p></div>"
				+ "<em id='_per'>12.50배</em>"
				+ "<em id='_pbr'>1.30배</em>"
				+ "<em id='_eps'>5,777원</em>"
				+ "<em id='_cns_per'>11.20배</em>"
				+ "<em id='_cns_eps'>6,200원</em>"
				+ "</body></html>";

		Document document = Jsoup.parse(html);
		StockVO stockVO = StockVO.builder().stockId("005930").build();

		StockVO result = parser.parseStockDetailFromDocument(document, stockVO);

		assertEquals("68000", result.getPrice());
		assertEquals("12.50", result.getPer());
		assertEquals("1.30", result.getPbr());
		assertEquals("5777", result.getEps());
	}

	@Test
	@DisplayName("parseStockDetail - 빈 문서에서도 예외 없이 처리")
	void testEmptyDocument() throws Exception {
		Document document = Jsoup.parse("<html><body></body></html>");
		StockVO stockVO = StockVO.builder().stockId("EMPTY").build();

		StockVO result = assertDoesNotThrow(
				() -> parser.parseStockDetailFromDocument(document, stockVO));

		assertEquals("EMPTY", result.getStockId());
		assertEquals("KOREA", result.getNational());
	}

	@Test
	@DisplayName("parseStockDetail - 기존 StockVO 값이 보존되는지 확인")
	void testExistingValuesPreserved() throws Exception {
		Document document = Jsoup.parse("<html><body>"
				+ "<div class='wrap_company'><h2>새이름</h2></div>"
				+ "<div class='today'><p class='no_today'><em><span class='blind'>99000</span></em></p></div>"
				+ "<em id='_per'>10</em><em id='_pbr'>1.1</em><em id='_eps'>5000</em>"
				+ "<em id='_cns_per'>10.5</em><em id='_cns_eps'>5500</em>"
				+ "</body></html>");

		StockVO stockVO = StockVO.builder()
				.stockId("005930")
				.roe("15.5")
				.build();

		StockVO result = parser.parseStockDetailFromDocument(document, stockVO);

		assertEquals("005930", result.getStockId());
		assertEquals("15.5", result.getRoe());
		assertEquals("새이름", result.getName());
	}

	@Test
	@DisplayName("parseStockDetail - 투자의견 테이블에서 의견/목표주가/52주 최고최저 파싱")
	void testInvestmentTableParsing() throws Exception {
		String html = "<html><body>"
				+ "<div class='wrap_company'><h2>테스트</h2></div>"
				+ "<div class='today'><p class='no_today'><em><span class='blind'>10000</span></em></p></div>"
				+ "<em id='_per'></em><em id='_pbr'></em><em id='_eps'></em>"
				+ "<em id='_cns_per'></em><em id='_cns_eps'></em>"
				+ "<table summary='투자의견 정보'><tbody>"
				+ "<tr><td><span><em>4.2</em></span></td><td><em>85,000</em></td></tr>"
				+ "<tr><td><em>90,000</em><em>60,000</em></td></tr>"
				+ "</tbody></table>"
				+ "</body></html>";

		Document document = Jsoup.parse(html);
		StockVO stockVO = StockVO.builder().stockId("TEST").build();

		StockVO result = parser.parseStockDetailFromDocument(document, stockVO);

		assertEquals("4.2", result.getInvestmentOpinion());
		assertEquals("85000", result.getTargetPrice());
		assertEquals("90000", result.getMax52());
		assertEquals("60000", result.getMin52());
	}

	// ==================== insertZero ====================

	@Test
	@DisplayName("insertZero - null 필드를 0으로 채움")
	void testInsertZeroFillsNull() {
		StockVO stockVO = StockVO.builder().stockId("005930").name("삼성전자").build();

		StockVO result = parser.insertZero(stockVO);

		assertEquals("005930", result.getStockId());
		assertEquals("삼성전자", result.getName());
		assertEquals("0", result.getPrice());
		assertEquals("0", result.getPer());
		assertEquals("0", result.getPbr());
		assertEquals("0", result.getEps());
		assertEquals("0", result.getVolumn());
	}

	@Test
	@DisplayName("insertZero - 제외 필드(id, deleteYn, discussionRoomUrl)는 변경하지 않음")
	void testInsertZeroExcludeFields() {
		StockVO stockVO = StockVO.builder().build();

		StockVO result = parser.insertZero(stockVO);

		assertNull(result.getId());
		assertNull(result.getDeleteYn());
	}

	@Test
	@DisplayName("insertZero - N/A를 0으로 변환")
	void testInsertZeroConvertsNA() {
		StockVO stockVO = StockVO.builder()
				.stockId("TEST")
				.per("N/A")
				.pbr("N/A")
				.build();

		StockVO result = parser.insertZero(stockVO);

		assertEquals("0", result.getPer());
		assertEquals("0", result.getPbr());
	}

	@Test
	@DisplayName("insertZero - 이미 값이 있는 필드는 유지")
	void testInsertZeroKeepsExistingValues() {
		StockVO stockVO = StockVO.builder()
				.stockId("005930")
				.price("68000")
				.per("12.5")
				.build();

		StockVO result = parser.insertZero(stockVO);

		assertEquals("68000", result.getPrice());
		assertEquals("12.5", result.getPer());
	}

	// ==================== parseStockListFromDocument ====================

	@Test
	@DisplayName("parseStockList - 정상적인 시세 테이블 파싱")
	void testParseStockList() throws Exception {
		String html = "<html><body>"
				+ "<table class='type_2'>"
				+ "<thead><tr><th>N</th><th>종목명</th><th>현재가</th></tr></thead>"
				+ "<tbody>"
				+ "<tr onmouseover='mouseOn(this)'>"
				+ "<td>1</td>"
				+ "<td><a href='/item/main.naver?code=005930'>삼성전자</a></td>"
				+ "<td>68,000</td>"
				+ "</tr>"
				+ "<tr><td colspan='3'></td></tr>"
				+ "<tr onmouseover='mouseOn(this)'>"
				+ "<td>2</td>"
				+ "<td><a href='/item/main.naver?code=000660'>SK하이닉스</a></td>"
				+ "<td>112,500</td>"
				+ "</tr>"
				+ "</tbody>"
				+ "</table>"
				+ "</body></html>";

		Document document = Jsoup.parse(html);
		List<StockVO> result = parser.parseStockListFromDocument(document);

		assertEquals(2, result.size());
		assertEquals("005930", result.get(0).getStockId());
		assertEquals("000660", result.get(1).getStockId());
	}

	@Test
	@DisplayName("parseStockList - onmouseover 없는 행은 무시")
	void testParseStockListSkipsNonDataRows() throws Exception {
		String html = "<html><body>"
				+ "<table class='type_2'>"
				+ "<thead><tr><th>N</th><th>종목명</th></tr></thead>"
				+ "<tbody>"
				+ "<tr><td colspan='2'>구분선</td></tr>"
				+ "<tr onmouseover='mouseOn(this)'>"
				+ "<td>1</td>"
				+ "<td><a href='/item/main.naver?code=005930'>삼성전자</a></td>"
				+ "</tr>"
				+ "<tr><td colspan='2'>구분선</td></tr>"
				+ "</tbody>"
				+ "</table>"
				+ "</body></html>";

		Document document = Jsoup.parse(html);
		List<StockVO> result = parser.parseStockListFromDocument(document);

		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("parseStockList - 빈 테이블이면 빈 리스트 반환")
	void testParseStockListEmptyTable() throws Exception {
		String html = "<html><body>"
				+ "<table class='type_2'>"
				+ "<thead><tr><th>N</th></tr></thead>"
				+ "<tbody></tbody>"
				+ "</table>"
				+ "</body></html>";

		Document document = Jsoup.parse(html);
		List<StockVO> result = parser.parseStockListFromDocument(document);

		assertTrue(result.isEmpty());
	}

	// ==================== Helper ====================

	private String buildFullHtml() {
		return "<html><body>"
				+ "<div class='wrap_company'><h2>삼성전자</h2></div>"
				+ "<div class='today'><p class='no_today'><em><span class='blind'>68,000</span></em></p></div>"

				+ "<em id='_per'>12.5</em>"
				+ "<em id='_cns_per'>11.2</em>"
				+ "<em id='_pbr'>1.3</em>"
				+ "<em id='_eps'>5,777</em>"
				+ "<em id='_cns_eps'>6,200</em>"

				// 시가총액 정보 (row 2: 상장주식수, row 3: 액면가)
				+ "<table summary='시가총액 정보'><tbody>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>500,000</em></td></tr>"
				+ "<tr><td><em>5,000</em><em>기타</em></td></tr>"
				+ "</tbody></table>"

				// 외국인한도주식수 정보 (row 2)
				+ "<table summary='외국인한도주식수 정보'><tbody>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>45.5%</em></td></tr>"
				+ "</tbody></table>"

				// 주요 시세 (row 0 > td[2] > em)
				+ "<table summary='주요 시세(전일종가, 시고저가, 거래량, 거래대금)을 제공합니다.'><tbody>"
				+ "<tr><td>시가</td><td>고가</td><td><em>12,345,678</em></td></tr>"
				+ "</tbody></table>"

				// 전일가
				+ "<div class='new_totalinfo'><table><tbody><tr>"
				+ "<td class='first'><em><span>67,000</span></em></td>"
				+ "</tr></tbody></table></div>"

				// BPS (per_table row 2 > td em[1])
				+ "<table class='per_table'><tbody>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>기타</em><em>52,000</em></td></tr>"
				+ "</tbody></table>"

				// 동일업종 PER (em[0]: PER, em[1]: 등락률)
				+ "<table summary='동일업종 PER 정보'><tbody><tr><td>"
				+ "<em>15.3</em><em>2.5%</em>"
				+ "</td></tr></tbody></table>"

				// 투자의견 (row 0: 투자의견+목표주가, row 1: 52주 최고/최저)
				+ "<table summary='투자의견 정보'><tbody>"
				+ "<tr><td><span><em>4.2</em></span></td><td><em>80,000</em></td></tr>"
				+ "<tr><td><em>75,000</em><em>55,000</em></td></tr>"
				+ "</tbody></table>"

				// 배당수익률 (PER/EPS 정보 row 3)
				+ "<table summary='PER/EPS 정보'><tbody>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>기타</em></td></tr>"
				+ "<tr><td><em>1.8%</em></td></tr>"
				+ "</tbody></table>"

				+ "</body></html>";
	}
}
