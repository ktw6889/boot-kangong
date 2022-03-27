package com.kangong.stock.util;

import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockFinancialUtil {
	
	@Autowired
	StockUtil stockUtil;
	
	public Map<String, String> getEncParam(String stockId) throws Exception {
		///company/ajax/cF1001.aspx
		Map<String, String> encMap = new HashMap<String, String>();
		final String stockFinancialURL = "https://navercomp.wisereport.co.kr/v2/company/c1010001.aspx?cmp_cd="+stockId;
		Connection conn = Jsoup.connect(stockFinancialURL);
		Document document = conn.get();
		
		String encTortalHtml = document.toString();
		String[] encTotalSplits = encTortalHtml.split(", encparam: '");
		String encStrHtml = encTotalSplits[encTotalSplits.length-1];
		String[] encSplits = encStrHtml.split("'");
		String encStr = encSplits[0];
		
		String[] idTotalSplits = encTortalHtml.split(", id: '");
		String idStrHtml = idTotalSplits[idTotalSplits.length-1];
		String[] idSplits = idStrHtml.split("'");
		String idStr = idSplits[0];		
		
		encMap.put("encparam", encStr);
		encMap.put("idparam", idStr);
		//log.info("https://navercomp.wisereport.co.kr/v2/company/ajax/cF1001.aspx?fin_typ=0&freq_typ=Y&encparam="+encStr+"&id="+idStr+"&cmp_cd="+stockId);
		
		return encMap;
	}
	
	//===========================================

	public List<StockFinancialVO> getStockFinancial(StockVO stockVO) throws Exception {
		Map<String, String> paramMap = getEncParam(stockVO.getStockId());
		String encStr = (String)paramMap.get("encparam");
		String idStr = (String)paramMap.get("idStr");
		final String stockFinancialURL = "https://navercomp.wisereport.co.kr/v2/company/ajax/cF1001.aspx?fin_typ=0&freq_typ=Y&encparam="+encStr+"&id="+idStr+"&cmp_cd="+stockVO.getStockId();
		//log.info("stockId:"+stockVO.getStockId()+":"+stockFinancialURL);
		Connection conn = Jsoup.connect(stockFinancialURL);
		conn.header("Accept", "text/html, */*; q=0.01");
		conn.header("Content-Encoding", "gzip");
		conn.method(Connection.Method.GET);
		Document document = conn.get();
		
		return setStockFinancialDocument(document, stockVO);
	}

	public List<StockFinancialVO> setStockFinancialDocument(Document document, StockVO stockVO) throws Exception {
		//1. 헤더 가져와서 VO List 만들기
		ArrayList<String> headerList = getStockFinancialHeader(document);
		ArrayList<StockFinancialVO> stockFinancialList = getStockFinancialVOList(headerList, stockVO);
		stockFinancialList = getStockFinancialBody(document, stockFinancialList);
		return stockFinancialList;
	}
	
 	public ArrayList<String> getStockFinancialHeader(Document document) throws Exception {
		Elements financialTable = document.select("table[summary='주요재무정보를 제공합니다.']");
		Elements stockTableHeader =	financialTable.get(1).select("thead tr").get(1).select("th");
		ArrayList<String> headerList = new ArrayList<String>();
		for (Element element : stockTableHeader) {
			headerList.add(element.text().split("/")[0]);
		}		
		return headerList;
	}
	
	public ArrayList<StockFinancialVO> getStockFinancialVOList(ArrayList<String> headerList, StockVO stockVO) throws Exception {
		ArrayList<StockFinancialVO> stockFinancialList = new ArrayList<StockFinancialVO>();
		for(String year : headerList) {
			StockFinancialVO stockFinancialVO = StockFinancialVO.builder().build();
			stockFinancialVO.setYear(year);
			stockFinancialVO.setStockId(stockVO.getStockId());
			stockFinancialVO.setStockMasterId(stockVO.getId());
			stockFinancialList.add(stockFinancialVO);
		}
		return stockFinancialList;
	}
	
	public ArrayList<StockFinancialVO> getStockFinancialBody(Document document, ArrayList<StockFinancialVO> stockFinancialList) throws Exception {
		Elements financialTable = document.select("table[summary='주요재무정보를 제공합니다.']");
		Elements stockTableTr =	financialTable.get(1).select("tbody tr");
		
		for (Element element : stockTableTr) {
			setStockFinancialVOList(element.select("th"), element.select("td"), stockFinancialList);
		}		
		
		return stockFinancialList;
	}

	public ArrayList<StockFinancialVO> setStockFinancialVOList(Elements headElement, Elements tdElements, ArrayList<StockFinancialVO> stockFinancialList) throws Exception {
		Map<String, String> headerAttributeMapper = getHeaderAttributeMapper();

		String text = headElement.get(0).text();
		String attributeName = headerAttributeMapper.get(text);
		if(attributeName==null) 
			return stockFinancialList;
		
		for (int i = 0; i < tdElements.size(); i++) {
			StockFinancialVO stockFinancialVO = stockFinancialList.get(i);
			Class<?> clazz = stockFinancialVO.getClass();
			
			// 값 셋팅
			String value = tdElements.get(i).text();
			Field field = clazz.getDeclaredField(attributeName);
			field.setAccessible(true);			
			field.set(stockFinancialVO, value);			
		}
		
		//System.out.println("stockFinancialList: " + stockFinancialList);
		return stockFinancialList;
	}


	public Map<String, String> getHeaderAttributeMapper() {
		Map<String, String> headerAttributeMapperMap = new HashMap<String, String>();
		headerAttributeMapperMap.put("매출액", "totalSales");
		headerAttributeMapperMap.put("영업이익", "profits");
		headerAttributeMapperMap.put("당기순이익", "earnings");
		headerAttributeMapperMap.put("영업이익률", "profitsRatio");
		headerAttributeMapperMap.put("순이익률", "netProfitRatio");
		headerAttributeMapperMap.put("ROE(%)", "roe");
		headerAttributeMapperMap.put("부채비율", "deptRatio");
		headerAttributeMapperMap.put("자본유보율", "reserveRatio");
		headerAttributeMapperMap.put("EPS(원)", "eps");
		headerAttributeMapperMap.put("PER(배)", "per");		
		headerAttributeMapperMap.put("BPS(원)", "bps");
		headerAttributeMapperMap.put("PBR(배)", "pbr");
		headerAttributeMapperMap.put("현금DPS(원)", "dividendsPerShare");
		headerAttributeMapperMap.put("현금배당수익률", "dividendsRate");
		headerAttributeMapperMap.put("현금배당성향(%)", "dividendsTendency");
		headerAttributeMapperMap.put("자산총계", "totalAssets");
		headerAttributeMapperMap.put("발행주식수(보통주)", "sharesOutstanding");
		headerAttributeMapperMap.put("부채총계", "totalDept");
		headerAttributeMapperMap.put("자본총계", "totalCapital");
		headerAttributeMapperMap.put("자본금", "capital");
		return headerAttributeMapperMap;
	}
	
	public StockFinancialVO insertZero(StockFinancialVO stockFinancialVO) throws Exception{
		Class<?> clazz = stockFinancialVO.getClass();
		Field[] declaredFields =clazz.getDeclaredFields();
		ArrayList<String> exceptAttributeList = new ArrayList<String>();
		exceptAttributeList.add("id");
		exceptAttributeList.add("deleteYn");		
		
		for (Field field : declaredFields) {
			field.setAccessible(true);
			if( field.getType() == String.class && !exceptAttributeList.contains(field.getName()) ) {
				String value = (String)field.get(stockFinancialVO);
				if(value == null || "".equals(value) || "N/A".equals(value)) {
					
					field.set(stockFinancialVO, "0");
				}else {
					field.set(stockFinancialVO, value.replaceAll("[^0-9.]",""));
				}
			}
		}
		return stockFinancialVO;
	}
	
	//===============================================================================================
	
	public Map<String, String> getEncParam2(String stockId) throws Exception {
		///company/ajax/cF1001.aspx
		Map<String, String> encMap = new HashMap<String, String>();
		final String stockFinancialURL = "https://navercomp.wisereport.co.kr/v2/company/c1030001.aspx?cn=&cmp_cd="+stockId;
		Connection conn = Jsoup.connect(stockFinancialURL);
		Document document = conn.get();
		
		String encTortalHtml = document.toString();
		String[] encTotalSplits = encTortalHtml.split("encparam: '");
		String encStrHtml = encTotalSplits[encTotalSplits.length-1];
		String[] encSplits = encStrHtml.split("'");
		String encStr = encSplits[0];
		
		encMap.put("encparam", encStr);
		return encMap;
	}		
	
	public void connectStockInfo(StockVO stockVO) throws Exception{
		String stockInfoUrl = "https://finance.naver.com/item/main.naver?code="+stockVO.getStockId();
		URL url = new URL(stockInfoUrl);		
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setUseCaches(true);
		
		InputStreamReader isr = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
		isr.close();
		urlConn.disconnect();
	}
	
	public void connectStockInfoDetail(StockVO stockVO) throws Exception{
		String stockInfoUrl = "https://navercomp.wisereport.co.kr/v2/company/c1010001.aspx?cmp_cd="+stockVO.getStockId();
		URL url = new URL(stockInfoUrl);		
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setUseCaches(true);
		
		InputStreamReader isr = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
		isr.close();
		urlConn.disconnect();
	}
	
	public void connectStockInfoDetail2(StockVO stockVO) throws Exception{
		String stockInfoUrl = "https://navercomp.wisereport.co.kr/v2/company/c1030001.aspx?cn=&cmp_cd="+stockVO.getStockId();
		URL url = new URL(stockInfoUrl);		
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setUseCaches(true);
		
		InputStreamReader isr = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
		isr.close();
		urlConn.disconnect();
	}
	
	
	public ArrayList<StockFinancialVO> getStockFinacial2(StockVO stockVO) throws Exception{	
		connectStockInfo(stockVO);
		connectStockInfoDetail(stockVO);
		connectStockInfoDetail2(stockVO);
		
		Map<String, String> paramMap = getEncParam2(stockVO.getStockId());
		String encStr = (String)paramMap.get("encparam");
		//encStr = "SHliblVkVWIxbC9xVndad1BoMDNuZz09";
		//String financeUrl = "https://navercomp.wisereport.co.kr/v2/company/cF3002.aspx?cmp_cd=000660&frq=0&rpt=0&finGubun=MAIN&frqTyp=0&cn=&encparam=TFd6SVhQWWF3NkhwZ05NOFlCSENRUT09";
		String financeUrl = "https://navercomp.wisereport.co.kr/v2/company/cF3002.aspx?cmp_cd="+stockVO.getStockId()+"&frq=0&rpt=1&finGubun=MAIN&frqTyp=0&cn=&encparam="+encStr;
				
		URL url = new URL(financeUrl);		
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();		
		
		urlConn.setRequestMethod("GET");
		urlConn.setRequestProperty("Content-Type", "application/json");
		urlConn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
		urlConn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		urlConn.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
		urlConn.setRequestProperty("Connection", "keep-alive");		
		urlConn.setRequestProperty("Cookie","setC1010001=[{\"conGubun\":\"MAIN\",\"cTB23\":\"cns_td1\",\"bandChartGubun\":\"MAIN\",\"finGubun\":\"MAIN\",\"cTB00\":\"cns_td20\"}]; setC1030001=[{\"cTB301\":\"rpt_td2\",\"finGubun\":\"MAIN\",\"frqTyp\":\"0\",\"moreY\":1,\"moreQ\":1,\"moreQQ\":1}]");
		urlConn.setRequestProperty("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");	
		urlConn.setRequestProperty("sec-ch-ua-mobile", "?0");
		urlConn.setRequestProperty("sec-ch-ua-platform", "Windows");
		urlConn.setRequestProperty("Sec-Fetch-Dest", "empty");
		urlConn.setRequestProperty("Sec-Fetch-Mode", "cors");
		urlConn.setRequestProperty("Sec-Fetch-Site", "same-origin");
		urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
		
		
		
		//urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConn.getOutputStream()));
        //commands라는 JSONArray를 담을 JSONObject 생성
        JSONObject commands = new JSONObject();
        commands.put("cmp_cd", stockVO.getStockId());
        commands.put("frq", "0");
        commands.put("rpt", "1");
        commands.put("finGubun", "MAIN");
        commands.put("frqTyp", "0");
        commands.put("encparam", encStr);
       // commands.put("commands", commands);
        //request에 쓰기
        bw.write(commands.toString());
        bw.flush();
        bw.close();
		
		InputStreamReader isr = new InputStreamReader(urlConn.getInputStream(), "UTF-8");		
		
		//BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConn.getOutputStream()));

		JSONObject object = (JSONObject)JSONValue.parse(isr);
		JSONArray dataArray = (JSONArray)object.get("DATA");
		JSONArray yymmArray = (JSONArray)object.get("YYMM");
		
		ArrayList<String> headerList = new ArrayList<String>();
		ArrayList<StockFinancialVO> resultList = new ArrayList<StockFinancialVO>();
		
		for(Object yymmObj : yymmArray) {
			String yymm = (String)yymmObj;
			String year = yymm.split("/")[0];
			headerList.add(year);
			StockFinancialVO financialVO = StockFinancialVO.builder().build();
			financialVO.setYear(year);
			financialVO.setStockId(stockVO.getStockId());
			resultList.add(financialVO);
		}
		
		for(Object dataObj : dataArray) {
			JSONObject jsonObj = (JSONObject)dataObj;
			String accNm = (String)jsonObj.get("ACC_NM");
			resultList = setStockFinancialVOList2(accNm, jsonObj, resultList);
		}
		
		isr.close();
		urlConn.disconnect();
		log.info("JSON:"+resultList);
		
		return resultList;
	}
	
	public ArrayList<StockFinancialVO> setStockFinancialVOList2(String strAttribute, JSONObject jsonObj, ArrayList<StockFinancialVO> stockFinancialList) throws Exception {
		Map<String, String> attributeMapper =  getHeaderAttributeMapper2();

		String attributeName = attributeMapper.get(strAttribute);
		if(attributeName==null) 
			return stockFinancialList;
		
		for (int i = 0; i < stockFinancialList.size(); i++) {
			StockFinancialVO stockFinancialVO = stockFinancialList.get(i);
			Class<?> clazz = stockFinancialVO.getClass();
			
			// 값 셋팅
			Field field = clazz.getDeclaredField(attributeName);
			field.setAccessible(true);	
			
			int index = i+1;
			Object valueObj = jsonObj.get("DATA"+index); 
			if(valueObj instanceof String) {
				field.set(stockFinancialVO, (String)valueObj);	
			}else if(valueObj instanceof Double) {
				field.set(stockFinancialVO, valueObj.toString() );	
			}
					
		}
		
		//System.out.println("stockFinancialList: " + stockFinancialList);
		return stockFinancialList;
	}
		
	public void getStockFinacial3(StockVO stockVO) throws Exception{		
		String financeUrl = "https://navercomp.wisereport.co.kr/v2/company/c1030001.aspx?cn=&cmp_cd="+stockVO.getStockId();
		Document financeDocument = stockUtil.getUrlDocument(financeUrl);
		
		//Header정보 가져오기
		ArrayList<String> headerList = getStockFinancial2Header(financeDocument);
		//stock정보, year정보 셋팅
		//ArrayList<StockFinancialVO> bodyList = getStockFinancialVOList(headerList, stockVO);
	}
	
	
 	public ArrayList<String> getStockFinancial2Header(Document document) throws Exception {
		Elements financialTable = document.select("table[summary='IFRS연결 연간 재무 정보를 제공합니다.']");
		Elements stockTableHeader =	financialTable.get(1).select("thead tr").get(0).select("th div");
		ArrayList<String> headerList = new ArrayList<String>();
		for (Element element : stockTableHeader) {
			headerList.add(element.text().split("/")[0]);
		}	
		log.info(headerList);
		return headerList;
	}
 	
 	public ArrayList<StockFinancialVO> getStockFinancial2Body(Document document, ArrayList<StockFinancialVO> stockFinancialList) throws Exception {
		Elements financialTable = document.select("table[summary='IFRS연결 연간 재무 정보를 제공합니다.']");
		Elements stockTableTr =	financialTable.get(1).select("tbody tr");
		
		for (Element element : stockTableTr) {
			setStockFinancialVOList(element.select("th"), element.select("td"), stockFinancialList);
		}		
		
		return stockFinancialList;
	}

	public ArrayList<StockFinancialVO> setStockFinancial2VOList(Elements headElement, Elements tdElements, ArrayList<StockFinancialVO> stockFinancialList) throws Exception {
		Map<String, String> headerAttributeMapper = getHeaderAttributeMapper();

		String text = headElement.get(0).text();
		String attributeName = headerAttributeMapper.get(text);
		if(attributeName==null) 
			return stockFinancialList;
		
		for (int i = 0; i < tdElements.size(); i++) {
			StockFinancialVO stockFinancialVO = stockFinancialList.get(i);
			Class<?> clazz = stockFinancialVO.getClass();
			
			// 값 셋팅
			String value = tdElements.get(i).text();
			Field field = clazz.getDeclaredField(attributeName);
			field.setAccessible(true);			
			field.set(stockFinancialVO, value);			
		}
		
		//System.out.println("stockFinancialList: " + stockFinancialList);
		return stockFinancialList;
	}
	

			//유동주식수
			//자기주식수 : 전체주식수 - 유동주식수
	
	public Map<String, String> getHeaderAttributeMapper2() {
		Map<String, String> headerAttributeMapperMap = new HashMap<String, String>();
		headerAttributeMapperMap.put("유동자산", "liquidAsset");
		headerAttributeMapperMap.put("유동부채", "liquidDept");
		headerAttributeMapperMap.put("발행주식수", "totalStockQty");
		headerAttributeMapperMap.put("보통주", "commonStockQty");
		headerAttributeMapperMap.put("우선주", "preferredStockQty");
		return headerAttributeMapperMap;
	}
	
	//https://navercomp.wisereport.co.kr/v2/company/c1070001.aspx?cmp_cd=005930&cn=
	//table[summary='기업별 주주현황(최대주주/10%이상/5%이상/기타주주/유동주식)을 제공합니다.'] tbody tr td get(8)
	
}
