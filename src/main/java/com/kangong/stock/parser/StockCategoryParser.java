package com.kangong.stock.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.kangong.stock.model.StockCategoryVO;

import lombok.extern.log4j.Log4j2;

/**
 * 업종/테마 카테고리 파싱 (기존 StockCategoryUtil 대체)
 * - fetchDocument()는 AbstractStockDataParser에서 상속 (기존 Jsoup.connect 직접 호출 제거)
 * - URL 파라미터 추출은 extractUrlParam()으로 통합
 */
@Log4j2
@Component
public class StockCategoryParser extends AbstractStockDataParser {

	// ==================== 업종 카테고리 ====================

	/**
	 * 업종별 카테고리 + 카테고리 링크 목록 가져오기
	 */
	public Map<String, List<StockCategoryVO>> getStockCategoryList() throws Exception {
		String url = "https://finance.naver.com/sise/sise_group.naver?type=upjong";
		Document document = fetchDocument(url);
		return parseCategoryDocument(document, "Industry",
				"table[summary='업종별 전일대비 시세에 관한 표이며 등락현황 정보를 제공합니다.'] tbody tr",
				"upjong");
	}

	// ==================== 테마 카테고리 ====================

	/**
	 * 테마별 카테고리 + 카테고리 링크 목록 가져오기 (10페이지)
	 */
	public Map<String, List<StockCategoryVO>> getStockThemeList() throws Exception {
		Map<String, List<StockCategoryVO>> returnMap = new HashMap<>();
		List<StockCategoryVO> allThemeList = new ArrayList<>();
		List<StockCategoryVO> allThemeLinkList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Map<String, List<StockCategoryVO>> pageResult = getStockThemeListOfPaging(i);
			allThemeList.addAll(pageResult.get("categoryList"));
			allThemeLinkList.addAll(pageResult.get("categoryLinkList"));
		}

		returnMap.put("categoryList", allThemeList);
		returnMap.put("categoryLinkList", allThemeLinkList);
		return returnMap;
	}

	private Map<String, List<StockCategoryVO>> getStockThemeListOfPaging(int page) throws Exception {
		String url = "https://finance.naver.com/sise/theme.naver?&page=" + page;
		Document document = fetchDocument(url);
		return parseCategoryDocument(document, "theme",
				"table[summary='테마별 전일대비 시세에 관한 표이며 등락현황 정보를 제공합니다.'] tbody tr",
				"theme");
	}

	// ==================== 공통 파싱 로직 ====================

	/**
	 * 카테고리 Document 파싱 (업종/테마 공통)
	 * (기존 getStockCategoryListOfDocument와 getStockThemeListOfDocument는 selector와 type만 달랐음 → 통합)
	 */
	private Map<String, List<StockCategoryVO>> parseCategoryDocument(Document document, String categoryType,
																	 String tableSelector, String linkType) throws Exception {
		Elements rows = document.select(tableSelector);
		Map<String, List<StockCategoryVO>> resultMap = new HashMap<>();
		List<StockCategoryVO> categoryList = new ArrayList<>();
		List<StockCategoryVO> linkList = new ArrayList<>();

		for (int i = 3; i < rows.size(); i++) {
			Element row = rows.get(i);
			Element td = row.select("td").get(0);
			String industryURL = td.select("a").attr("href");
			if (ObjectUtils.isEmpty(industryURL)) continue;

			String categoryNo = extractUrlParam(industryURL, "no");

			StockCategoryVO categoryVO = StockCategoryVO.builder().build();
			categoryVO.setCategoryType(categoryType);
			categoryVO.setCategoryName(td.text());
			categoryVO.setCategoryNo(categoryNo);

			categoryList.add(categoryVO);
			linkList.addAll(parseCategoryLinkList(categoryVO, linkType));
		}

		log.info("StockCategoryList:" + categoryList);
		log.info("linkList:" + linkList);

		resultMap.put("categoryList", categoryList);
		resultMap.put("categoryLinkList", linkList);
		return resultMap;
	}

	/**
	 * 카테고리에 속한 종목 링크 목록 파싱
	 * (기존 getStockCategoyLinkList와 getStockThemeLinkList는 URL type 파라미터만 달랐음 → 통합)
	 */
	private List<StockCategoryVO> parseCategoryLinkList(StockCategoryVO categoryVO, String type) throws Exception {
		String url = "https://finance.naver.com/sise/sise_group_detail.naver?type=" + type
				+ "&no=" + categoryVO.getCategoryNo();
		Document document = fetchDocument(url);
		Elements rows = document.select("table[summary='업종별 시세 리스트'] tbody tr");
		List<StockCategoryVO> linkList = new ArrayList<>();

		for (Element row : rows) {
			Element td = row.select("td").get(0);
			String linkURL = td.select("a").attr("href");
			if (ObjectUtils.isEmpty(linkURL)) continue;

			String stockId = extractUrlParam(linkURL, "code");

			StockCategoryVO linkVO = StockCategoryVO.builder().build();
			linkVO.setStockId(stockId);
			linkVO.setCategoryNo(categoryVO.getCategoryNo());
			linkList.add(linkVO);
		}
		return linkList;
	}
}