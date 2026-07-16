package com.kangong.stock.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockFieldMappingRegistry;

import lombok.extern.log4j.Log4j2;

/**
 * 일별 시세 파싱 (기존 StockDailyPriceUtil 대체)
 * - fetchDocument()는 AbstractStockDataParser에서 상속 (기존 StockUtil.getUrlDocument 의존성 제거)
 * - insertZero()는 fillEmptyFieldsWithZero()로 통합
 * - Reflection 필드 설정은 setFieldByHeaderMapping()으로 통합
 */
@Log4j2
@Component
public class StockDailyPriceParser extends AbstractStockDataParser {

	private static final Set<String> ZERO_FILL_EXCLUDE = new HashSet<String>() {{
		add("id");
		add("stockId");
	}};

	/**
	 * 특정 종목의 일별 시세 리스트 가져오기
	 */
	public List<StockDailyPriceVO> getStockDailyPriceList(StockVO stockVO) throws Exception {
		List<StockDailyPriceVO> resultList = new ArrayList<>();
		for (int i = 1; i < 2; i++) {
			resultList.addAll(getStockDailyPriceListOfPaging(stockVO, i));
		}
		return resultList;
	}

	/**
	 * 특정 페이지의 일별 시세 파싱
	 */
	private List<StockDailyPriceVO> getStockDailyPriceListOfPaging(StockVO stockVO, int page) throws Exception {
		String url = "https://finance.naver.com/item/frgn.naver?code=" + stockVO.getStockId() + "&page=" + page;
		Document document = fetchDocument(url);
		return parseDailyPriceFromDocument(stockVO, document);
	}

	/**
	 * Document에서 일별 시세 목록 파싱
	 */
	private List<StockDailyPriceVO> parseDailyPriceFromDocument(StockVO stockVO, Document document) throws Exception {
		Elements rows = document.select(
				"table[summary='외국인 기관 순매매 거래량에 관한표이며 날짜별로 정보를 제공합니다.'] tbody tr");
		List<StockDailyPriceVO> list = new ArrayList<>();
		List<String> headerList = getFixedHeaderList();

		for (Element row : rows) {
			if (row.attr("onmouseover").isEmpty()) {
				continue;
			}
			list.add(parseDailyPriceRow(stockVO, row.select("td"), headerList));
		}
		return list;
	}

	/**
	 * 테이블 행에서 StockDailyPriceVO 파싱
	 */
	private StockDailyPriceVO parseDailyPriceRow(StockVO stockVO, Elements tdElements,
												  List<String> headerList) throws Exception {
		StockDailyPriceVO vo = StockDailyPriceVO.builder().build();
		vo.setStockId(stockVO.getStockId());
		Map<String, String> mapper = StockFieldMappingRegistry.dailyPriceHtmlTable();

		for (int i = 0; i < tdElements.size(); i++) {
			String text = tdElements.get(i).select("span").text();
			if (i < headerList.size()) {
				String fieldName = mapper.get(headerList.get(i));
				if (fieldName != null) {
					setFieldValue(vo, fieldName, extractNumericWithSign(text));
				}
			}
		}
		return insertZero(vo);
	}

	/**
	 * 빈 필드 "0" 채우기
	 */
	public StockDailyPriceVO insertZero(StockDailyPriceVO vo) {
		return fillEmptyFieldsWithZero(vo, ZERO_FILL_EXCLUDE);
	}

	/**
	 * 고정 헤더 목록 (기존 StockDailyPriceUtil.getStockDailyPriceHeader)
	 */
	private List<String> getFixedHeaderList() {
		List<String> headerList = new ArrayList<>();
		headerList.add("날짜");
		headerList.add("종가");
		headerList.add("전일비");
		headerList.add("등락률");
		headerList.add("거래량");
		headerList.add("기관_순매매량");
		headerList.add("외국인_순매매량");
		headerList.add("외국인_보유주수");
		headerList.add("외국인_보유율");
		return headerList;
	}

}