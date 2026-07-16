package com.kangong.stock.parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockFieldMappingRegistry;

import lombok.extern.log4j.Log4j2;

/**
 * 주식 기본정보 파싱 (기존 StockUtil 대체)
 * - 공통 로직은 AbstractStockDataParser에서 상속
 * - Selenium 의존성 제거 (배당수익률은 StockSeleniumParser에서 별도 처리)
 */
@Log4j2
@Component
public class StockInfoParser extends AbstractStockDataParser {

	private static final Set<String> ZERO_FILL_EXCLUDE = new HashSet<String>() {{
		add("id");
		add("deleteYn");
		add("discussionRoomUrl");
	}};

	/**
	 * 전체 주식 시세 리스트 (49페이지) 가져오기
	 */
	public List<StockVO> getStockList() throws Exception {
		List<StockVO> resultList = new ArrayList<>();
		for (int i = 1; i < 50; i++) {
			resultList.addAll(getStockListOfPaging(i));
		}
		return resultList;
	}

	/**
	 * 특정 페이지의 주식 시세 리스트
	 */
	public List<StockVO> getStockListOfPaging(int page) throws Exception {
		String url = "https://finance.naver.com/sise/sise_market_sum.nhn?&page=" + page;
		Document document = fetchDocument(url);
		return parseStockListFromDocument(document);
	}

	/**
	 * Document에서 주식 목록 파싱
	 */
	public List<StockVO> parseStockListFromDocument(Document document) throws Exception {
		Elements rows = document.select("table.type_2 tbody tr");
		List<StockVO> list = new ArrayList<>();
		List<String> headerList = parseTableHeader(document);

		for (Element row : rows) {
			if (row.attr("onmouseover").isEmpty()) {
				continue;
			}
			list.add(parseStockRow(row.select("td"), headerList));
		}
		return list;
	}

	/**
	 * 테이블 행에서 StockVO 파싱
	 */
	private StockVO parseStockRow(Elements tdElements, List<String> headerList) throws Exception {
		StockVO stockVO = StockVO.builder().build();
		Map<String, String> mapper = StockFieldMappingRegistry.stockHtmlTable();

		for (int i = 0; i < tdElements.size(); i++) {
			String text = tdElements.get(i).text();

			// stockId 추출
			if (i == 1) {
				String hrefStr = tdElements.get(i).select("a").attr("href");
				String stockId = extractUrlParam(hrefStr, "code");
				stockVO.setStockId(stockId);
				log.info("StockId: " + stockId);
			}

			// 헤더 매핑으로 필드 설정
			if (i < headerList.size()) {
				setFieldByHeaderMapping(stockVO, headerList.get(i), removeComma(text), mapper);
			}
		}
		return stockVO;
	}

	/**
	 * 테이블 헤더 파싱
	 */
	private List<String> parseTableHeader(Document document) {
		Elements headerElements = document.select("table.type_2 thead tr");
		List<String> headerList = new ArrayList<>();
		for (Element element : headerElements) {
			for (Element th : element.select("th")) {
				headerList.add(th.text());
			}
			break;
		}
		return headerList;
	}

	/**
	 * 개별 종목 상세정보 파싱
	 * (기존 StockUtil.setStockDetail + setStockDetailOfDocument 통합)
	 * 참고: 배당수익률(selenium)은 StockSeleniumParser에서 별도 처리
	 */
	public StockVO parseStockDetail(String stockId, StockVO stockVO) throws Exception {
		String url = "https://finance.naver.com/item/main.naver?code=" + stockId;
		Document document = fetchDocument(url);
		return parseStockDetailFromDocument(document, stockVO);
	}

	/**
	 * 상세 Document에서 StockVO 필드 파싱
	 */
	public StockVO parseStockDetailFromDocument(Document document, StockVO stockVO) throws Exception {
		stockVO.setName(document.select("div.wrap_company h2").text());
		stockVO.setPrice(extractNumeric(document.select("div.today p.no_today em span.blind").text()));
		stockVO.setPer(extractNumeric(document.select("#_per").text()));
		stockVO.setEstimationPer(extractNumeric(document.select("#_cns_per").text()));
		stockVO.setPbr(extractNumeric(document.select("#_pbr").text()));
		stockVO.setEps(extractNumeric(document.select("#_eps").text()));
		stockVO.setEstimationEps(extractNumeric(document.select("#_cns_eps").text()));
		stockVO.setNational("KOREA");

		// 테이블 기반 추출 (데이터 없을 수 있어 safeSet으로 보호)
		safeSet(() -> stockVO.setFaceValue(extractNumeric(
				document.select("table[summary='시가총액 정보'] tbody tr").get(3).select("td em").get(0).text())));

		safeSet(() -> stockVO.setStockQty(extractNumeric(
				document.select("table[summary='시가총액 정보'] tbody tr").get(2).select("td em").text())));

		safeSet(() -> stockVO.setMarketCapitalization(
				new BigInteger(stockVO.getPrice()).multiply(new BigInteger(stockVO.getStockQty())).toString()));

		safeSet(() -> stockVO.setForeignerRatio(extractNumeric(
				document.select("table[summary='외국인한도주식수 정보'] tbody tr").get(2).select("td em").text())));

		safeSet(() -> stockVO.setVolumn(extractNumeric(
				document.select("table[summary='주요 시세(전일종가, 시고저가, 거래량, 거래대금)을 제공합니다.'] tbody tr")
						.get(0).select("td").get(2).select("em").text())));

		safeSet(() -> stockVO.setPriceBeforeday(extractNumeric(
				document.select(".new_totalinfo table tbody tr td.first em span").get(0).text())));

		safeSet(() -> stockVO.setBps(extractNumeric(
				document.select("table.per_table tbody tr").get(2).select("td em").get(1).text())));

		safeSet(() -> stockVO.setIndustryPer(removeComma(
				document.select("table[summary='동일업종 PER 정보'] tbody tr td em").get(0).text())));

		safeSet(() -> stockVO.setIndustryBaisse(extractNumeric(
				document.select("table[summary='동일업종 PER 정보'] tbody tr td em").get(1).text())));

		safeSet(() -> stockVO.setInvestmentOpinion(extractNumeric(
				document.select("table[summary='투자의견 정보'] tbody tr").get(0).select("td span em").get(0).text())));

		safeSet(() -> stockVO.setTargetPrice(extractNumeric(
				document.select("table[summary='투자의견 정보'] tbody tr").get(0).select("td em").get(1).text())));

		safeSet(() -> stockVO.setMax52(extractNumeric(
				document.select("table[summary='투자의견 정보'] tbody tr").get(1).select("td em").get(0).text())));

		safeSet(() -> stockVO.setMin52(extractNumeric(
				document.select("table[summary='투자의견 정보'] tbody tr").get(1).select("td em").get(1).text())));

		safeSet(() -> stockVO.setDividendRate(extractNumeric(
				document.select("table[summary='PER/EPS 정보'] tbody tr").get(3).select("td em").text())));

		return stockVO;
	}

	private void safeSet(Runnable extraction) {
		try {
			extraction.run();
		} catch (Exception e) {
			// 파싱 실패 시 기존 값 유지
		}
	}

	public String parseIndustryPer(String stockId) {
		try {
			String url = "https://finance.naver.com/item/main.naver?code=" + stockId;
			Document document = fetchDocument(url);
			return removeComma(document.select("table[summary='동일업종 PER 정보'] tbody tr td em").get(0).text());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 빈 필드 "0" 채우기
	 */
	public StockVO insertZero(StockVO stockVO) {
		return fillEmptyFieldsWithZero(stockVO, ZERO_FILL_EXCLUDE);
	}

}