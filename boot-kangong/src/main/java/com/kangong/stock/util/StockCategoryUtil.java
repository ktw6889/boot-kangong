package com.kangong.stock.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.kangong.stock.model.StockCategoryVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockCategoryUtil {
	
	public Map<String, List<StockCategoryVO>> getStockCateogyList() throws Exception {
		final String stockCategoryListURL= "https://finance.naver.com/sise/sise_group.naver?type=upjong";
		Connection conn = Jsoup.connect(stockCategoryListURL);
		Document document = conn.get();
		
		return getStockCategoryListOfDocument(document);
	}

	public Map<String, List<StockCategoryVO>> getStockCategoryListOfDocument(Document document) throws Exception {
 		Elements categoryTableLists = document.select("table[summary='업종별 전일대비 시세에 관한 표이며 등락현황 정보를 제공합니다.'] tbody tr");
		Map<String, List<StockCategoryVO>> resultMap = new HashMap<String, List<StockCategoryVO>>();
 		List<StockCategoryVO> list = new ArrayList<>();
		List<StockCategoryVO> linkList = new ArrayList<>();
		
		for(int i=3; i < categoryTableLists.size(); i++){
			Element trElement = categoryTableLists.get(i);
			Element tdElement = trElement.select("td").get(0);
			String industryURL = tdElement.select("a").attr("href");
			if(ObjectUtils.isEmpty(industryURL)) continue;
			
			String[] industryURLSplits = industryURL.split("no=");			
			
			StockCategoryVO categoryVO =  StockCategoryVO.builder().build();
			categoryVO.setCategoryType("Industry");
			categoryVO.setCategoryName(tdElement.text());			
			categoryVO.setCategoryNo(industryURLSplits[1]);			
			
			//log.info("categoryVO:"+categoryVO);
			
			list.add(categoryVO);
			linkList.addAll(getStockCategoyLinkList(categoryVO));
		}
		log.info("StockCategoryList:"+list);
		log.info("linkList:"+linkList);
		
		resultMap.put("categoryList", list);
		resultMap.put("categoryLinkList", linkList);
		
		return resultMap;
	}
	
	
	public List<StockCategoryVO> getStockCategoyLinkList(StockCategoryVO categoryVO) throws Exception{
		String categoryLinkListURL = "https://finance.naver.com/sise/sise_group_detail.naver?type=upjong&no="+categoryVO.getCategoryNo();
		Connection conn = Jsoup.connect(categoryLinkListURL);
		Document document = conn.get();
		Elements categoryTableLists = document.select("table[summary='업종별 시세 리스트'] tbody tr");
		List<StockCategoryVO> linkList = new ArrayList<>();
		
		for(Element trElement : categoryTableLists) {
			Element tdElement = trElement.select("td").get(0);
			String linkURL = tdElement.select("a").attr("href");
			if(ObjectUtils.isEmpty(linkURL)) continue;
			String[] linkURLSplits = linkURL.split("code=");
			
			StockCategoryVO categoryLinkVO =  StockCategoryVO.builder().build();
			categoryLinkVO.setStockId(linkURLSplits[1]);
			categoryLinkVO.setCategoryNo(categoryVO.getCategoryNo() );
			linkList.add(categoryLinkVO);
		}		
		return linkList;
	}
	
	//=======================================================================================================
	public Map<String, List<StockCategoryVO>> getStockThemeList() throws Exception {
		Map<String, List<StockCategoryVO>>  returnMap = new HashMap<String, List<StockCategoryVO>> ();
		List<StockCategoryVO> stockThemeList = new ArrayList<StockCategoryVO>();
		List<StockCategoryVO> stockThemeLinkList = new ArrayList<StockCategoryVO>();
		for(int i=0;i<10;i++) {
			Map<String, List<StockCategoryVO>> resultMap = getStockThemeListOfPaging(i);
			stockThemeList.addAll(resultMap.get("categoryList"));
			stockThemeLinkList.addAll(resultMap.get("categoryLinkList"));
		}
		
		returnMap.put("categoryList", stockThemeList);
		returnMap.put("categoryLinkList", stockThemeLinkList);
		return returnMap;
	}
		
	public Map<String, List<StockCategoryVO>> getStockThemeListOfPaging(int i) throws Exception {
		final String stockThemeListURL= "https://finance.naver.com/sise/theme.naver?&page="+i;
		Connection conn = Jsoup.connect(stockThemeListURL);
		Document document = conn.get();
		
		return getStockThemeListOfDocument(document);
	}
	
	public Map<String, List<StockCategoryVO>> getStockThemeListOfDocument(Document document) throws Exception {
 		Elements categoryTableLists = document.select("table[summary='테마별 전일대비 시세에 관한 표이며 등락현황 정보를 제공합니다.'] tbody tr");
		Map<String, List<StockCategoryVO>> resultMap = new HashMap<String, List<StockCategoryVO>>();
 		List<StockCategoryVO> list = new ArrayList<>();
		List<StockCategoryVO> linkList = new ArrayList<>();
		
		for(int i=3; i < categoryTableLists.size(); i++){
			Element trElement = categoryTableLists.get(i);
			Element tdElement = trElement.select("td").get(0);
			String industryURL = tdElement.select("a").attr("href");
			if(ObjectUtils.isEmpty(industryURL)) continue;
			
			String[] industryURLSplits = industryURL.split("no=");			
			
			StockCategoryVO categoryVO =  StockCategoryVO.builder().build();
			categoryVO.setCategoryType("theme");
			categoryVO.setCategoryName(tdElement.text());			
			categoryVO.setCategoryNo(industryURLSplits[1]);			
			
			//log.info("categoryVO:"+categoryVO);
			
			list.add(categoryVO);
			linkList.addAll(getStockThemeLinkList(categoryVO));
		}
		log.info("StockCategoryList:"+list);
		log.info("linkList:"+linkList);
		
		resultMap.put("categoryList", list);
		resultMap.put("categoryLinkList", linkList);
		
		return resultMap;
	}
	
	public List<StockCategoryVO> getStockThemeLinkList(StockCategoryVO categoryVO) throws Exception{
		String themeLinkListURL = "https://finance.naver.com/sise/sise_group_detail.naver?type=theme&no="+categoryVO.getCategoryNo();
		Connection conn = Jsoup.connect(themeLinkListURL);
		Document document = conn.get();
		Elements categoryTableLists = document.select("table[summary='업종별 시세 리스트'] tbody tr");
		List<StockCategoryVO> linkList = new ArrayList<>();
		
		for(Element trElement : categoryTableLists) {
			Element tdElement = trElement.select("td").get(0);
			String linkURL = tdElement.select("a").attr("href");
			if(ObjectUtils.isEmpty(linkURL)) continue;
			String[] linkURLSplits = linkURL.split("code=");
			
			StockCategoryVO categoryLinkVO =  StockCategoryVO.builder().build();
			categoryLinkVO.setStockId(linkURLSplits[1]);
			categoryLinkVO.setCategoryNo(categoryVO.getCategoryNo() );
			linkList.add(categoryLinkVO);
		}		
		return linkList;
	}

}
