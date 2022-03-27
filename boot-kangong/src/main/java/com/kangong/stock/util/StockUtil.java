package com.kangong.stock.util;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kangong.stock.model.StockVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockUtil {
	
	@Autowired
	StockSeleniumFinancialUtil stockSeleniumFinancialUtil;
	
	public Document getUrlDocument(String url) throws Exception{
		Connection conn = Jsoup.connect(url);
		return conn.get();
	}

	public List<StockVO> getStockList() throws Exception {
		List<StockVO> resultList = new ArrayList<StockVO>();
		for(int i = 1; i < 50; i++) {
			resultList.addAll(getStockListOfPaging(i));
		}
		return resultList;
	}	
	
	
	public List<StockVO> getStockListOfPaging(int i) throws Exception {
		final String stockListURL = "https://finance.naver.com/sise/sise_market_sum.nhn?&page="+i;
		Document document = getUrlDocument(stockListURL);
		
		return getStockListOfDocument(document);
	}

	public List<StockVO> getStockListOfDocument(Document document) throws Exception {
		Elements kosPiTable = document.select("table.type_2 tbody tr");
		List<StockVO> list = new ArrayList<>();
		ArrayList<String> headerList = getStockHeader(document);
		for (Element element : kosPiTable) {
			if (element.attr("onmouseover").isEmpty()) {
				continue;
			}
			list.add(setStockVO(element.select("td"), headerList));
		}
		return list;
	}

	public StockVO setStockVO(Elements tdElements, ArrayList<String> headerList) throws Exception {
		StockVO stockVO = StockVO.builder().build();
		Class<?> clazz = stockVO.getClass();
		Map<String, String> headerAttributeMapper = getHeaderAttributeMapper();

		for (int i = 0; i < tdElements.size(); i++) {
			// 값 셋팅
			String text = tdElements.get(i).text();
			// stockId 셋팅
			if (i == 1) {
				String hrefStr = tdElements.get(i).select("a").attr("href");
				String stockId = hrefStr.substring(hrefStr.indexOf("code=") + 5);
				stockVO.setStockId(stockId);
				log.info("StockId: "+stockId);
			}

			// Field 셋팅
			Field field = null;
			try {
				String fieldName = headerAttributeMapper.get(headerList.get(i));
				field = clazz.getDeclaredField(fieldName);

				if (field == null)
					continue;
				field.setAccessible(true);
				field.set(stockVO, text.replace(",", ""));
			} catch (Exception e) {

			}
		}
		stockVO = setStockDetail(stockVO.getStockId(), stockVO);
		//System.out.println("stockVO: " + stockVO);
		return stockVO;
	}

	public ArrayList<String> getStockHeader(Document document) {
		Elements stockTableBody = document.select("table.type_2 thead tr");
		ArrayList<String> headerList = new ArrayList<String>();
		for (Element element : stockTableBody) {
			for (Element td : element.select("th")) {
				headerList.add(td.text());
			}
			break;
		}
		return headerList;
	}

	public Map<String, String> getHeaderAttributeMapper() {
		Map<String, String> headerAttributeMapperMap = new HashMap<String, String>();
		headerAttributeMapperMap.put("종목명", "name");
		headerAttributeMapperMap.put("현재가", "price");
		headerAttributeMapperMap.put("전일가", "priceBeforeday");
		headerAttributeMapperMap.put("액면가", "faceValue");
		headerAttributeMapperMap.put("시가총액", "marketCapitalization");
		headerAttributeMapperMap.put("상장주식수", "stockQty");
		headerAttributeMapperMap.put("외국인비율", "foreignerRatio");
		headerAttributeMapperMap.put("거래량", "volumn");
		headerAttributeMapperMap.put("PER", "per");
		headerAttributeMapperMap.put("ROE", "roe");
		return headerAttributeMapperMap;
	}

	
	//상세 Stock 정보 가져오기========================================================================
	public StockVO setStockDetail(String stockId, StockVO stockVO) throws Exception {
		final String stockDetailUrl = "https://finance.naver.com/item/main.naver?code="+stockId;
		Connection conn = Jsoup.connect(stockDetailUrl);
		Document document = conn.get();
		return setStockDetailOfDocument(document, stockVO);
	}
	
	public StockVO setStockDetailOfDocument(Document document, StockVO stockVO) throws Exception{
		//종목명
		Elements stockNameElementsH2 = document.select("div.wrap_company h2");
		stockVO.setName(stockNameElementsH2.text());
		
		//현재가		
		  Elements priceElementsEm = document.select("div.today p.no_today em span.blind");
		  stockVO.setPrice(priceElementsEm.text().replaceAll("[^0-9.]",""));
		 
		
		//액면가
		try {
			Element faceValueElementsEm = document.select("table[summary='시가총액 정보'] tbody tr").get(3).select("td em").get(0);
			stockVO.setFaceValue(faceValueElementsEm.text().replaceAll("[^0-9.]",""));
		}catch(Exception e) {
			//log.info("액면가");
		}
		
		//상장주식수
		try {
			Elements stockQtyElementsEm = document.select("table[summary='시가총액 정보'] tbody tr").get(2).select("td em");
			stockVO.setStockQty(stockQtyElementsEm.text().replaceAll("[^0-9.]",""));
		}catch(Exception e) {
			//log.info("상장주식수");
		}
		
		//시가총액
		try {
			BigInteger priceBigInteger = new BigInteger(stockVO.getPrice());
			BigInteger stockQtyBigInteger = new BigInteger(stockVO.getStockQty());
			BigInteger marketCapitalizationBigInteger = priceBigInteger.multiply(stockQtyBigInteger);
			stockVO.setMarketCapitalization(marketCapitalizationBigInteger.toString());		
		}catch(Exception e) {
			
		}
		
		//외국인비율
		try {
			Elements foreignerRatioElementsEm = document.select("table[summary='외국인한도주식수 정보'] tbody tr").get(2).select("td em");
			stockVO.setForeignerRatio(foreignerRatioElementsEm.text().replaceAll("[^0-9.]",""));
		}catch(Exception e) {
			//log.info("외국인 비율");
		}
			
		//거래량
		try {
			Elements volumnElementsEm = document.select("table[summary='주요 시세(전일종가, 시고저가, 거래량, 거래대금)을 제공합니다.'] tbody tr").get(0).select("td").get(2).select("em");
			stockVO.setVolumn(volumnElementsEm.text().replaceAll("[^0-9.]",""));
		}catch(Exception e) {
			log.info(document.select("table[summary='주요 시세(전일종가, 시고저가, 거래량, 거래대금)을 제공합니다.'] tbody tr").html());
		}	
		
		//PER
		Elements perElements = document.select("#_per");
		stockVO.setPer(perElements.text().replaceAll("[^0-9.]",""));
		
		//ROE
		
		//전일가
		try {
			Elements priceBeforedaySpanElements = document.select(".new_totalinfo table tbody tr td.first em span");
			String priceBeforeday = priceBeforedaySpanElements.get(0).text();
			stockVO.setPriceBeforeday(priceBeforeday.replaceAll("[^0-9.]",""));
		}catch(Exception e) {
			document.select(".new_totalinfo table tbody tr td.first em span").html();
		}
		
		//추정PER		
		Elements estimationPerElement = document.select("#_cns_per");
		String estimationPer = estimationPerElement.text();
		stockVO.setEstimationPer(estimationPer.replaceAll("[^0-9.]",""));
		
		//PBR
		Elements pbrElement = document.select("#_pbr");
		String pbr = pbrElement.text();
		stockVO.setPbr(pbr.replaceAll("[^0-9.]",""));
		
		//BPS
		try {
			Elements bpsElementEms = document.select("table.per_table tbody tr").get(2).select("td em");
			String bps = bpsElementEms.get(1).text();
			stockVO.setBps(bps.replaceAll("[^0-9.]",""));
		}catch(Exception e) {}
		
		//동일업종 PER
		try {
			Elements industryPerElement = document.select("table[summary='동일업종 PER 정보'] tbody tr td em");
			String industryPer = industryPerElement.get(0).text();
			stockVO.setIndustryPer(industryPer.replace(",", ""));
		}catch(Exception e) {}
		
		//동일업종 등락률
		try {
			Elements industryBaisseElement = document.select("table[summary='동일업종 PER 정보'] tbody tr td em");
			String industryBaisse = industryBaisseElement.get(1).text();
			stockVO.setIndustryBaisse(industryBaisse.replaceAll("[^0-9.]",""));
		}catch(Exception e) {}	
		
		//투자의견
		try {
			Elements investmentOpinionElement = document.select("table[summary='투자의견 정보'] tbody tr").get(0).select("td span em");
			String investmentOpinion = investmentOpinionElement.get(0).text();
			stockVO.setInvestmentOpinion(investmentOpinion.replaceAll("[^0-9.]",""));
		}catch(Exception e) {}		
		
		//목표주가
		try {
			Elements targetPriceElement = document.select("table[summary='투자의견 정보'] tbody tr").get(0).select("td em");
			String targetPrice = targetPriceElement.get(1).text();
			targetPrice = targetPrice.replaceAll("[^0-9.]","");
			if("".equals(targetPrice) || targetPrice == null ) targetPrice = "0";
			stockVO.setTargetPrice(targetPrice);
		}catch(Exception e) {}		
		
		//52주 최고
		try {
			Elements max52Element = document.select("table[summary='투자의견 정보'] tbody tr").get(1).select("td em");
			String max52 = max52Element.get(0).text();
			stockVO.setMax52(max52.replaceAll("[^0-9.]",""));
		}catch(Exception e) {}		
		
		//52주 최저
		try {
			Elements min52Element = document.select("table[summary='투자의견 정보'] tbody tr").get(1).select("td em");
			String min52 = min52Element.get(1).text();
			stockVO.setMin52(min52.replaceAll("[^0-9.]",""));
		}catch(Exception e) {}		
		
		//배당수익률
		try {
			Elements dividendRateElement = document.select("table[summary='PER/EPS 정보'] tbody tr").get(3).select("td em");
			String dividendRate = dividendRateElement.text();
			stockVO.setDividendRate(dividendRate.replaceAll("[^0-9.]",""));
		}catch(Exception e) {}		
		
		//종목 국가
		stockVO.setNational("KOREA");
		
		//EPS
		Elements epsElements = document.select("#_eps");
		stockVO.setEps(epsElements.text().replaceAll("[^0-9.]",""));
		
		//추정EPS 
		Elements estimationEpsElements = document.select("#_cns_eps");
		stockVO.setEstimationEps(estimationEpsElements.text().replaceAll("[^0-9.]",""));
		
		
		//추가정보 배당률
		try {
			stockVO = stockSeleniumFinancialUtil.getStockInfo(stockVO);
		}catch(Exception e) {}
		
		
		
		//유동자산
		
		//유동부채
		//고정부채 : 부채 - 유동부채 = 비유동부채
		//자기자본 : 자본금
		//발행주식수
		//보통주
		//우선주
		//유동주식수
		//자기주식수 : 전체주식수 - 유동주식수
		
		
		
		//1. 추정EPS * 추정PER
		//2. 현명한초보투자자 공식
		//   적정주가 = (사업가치 + 재산가치 - 고정부채) / 발행주식수
		//   사업가치 = 영업이익 * ((1-법인세율) / 기대수익률)  영업이익: 최근3년간 영업이익평균, 법인세율: 0.25,  기대수익률: 1)주식에 기대수익률과 기업대출금리의 중간값 2)sRim 공식의 할인율
		//   재산가치 = 유동자산 - (유동부채*1.2) + 투자자산   재산가치: 회사가 보유하고 있는 현금과 토지 등 자산, 유동자산 : 1년이내 돈으로 바꿀 수 있는 자산, 유동부채: 만기가 1년 이내 도래하는 부채, 투자자산 : 비유동자산 중에서 기업의 판매활동 이외의 장기간에 결쳐 투자이익을 얻을 목적으로 보유하고 있는 자산
		//3. BPS, EPS, ROE
		// 적정주가 = BPS  = EPS * 10 = ROE * EPS * 100 의 중간값
		//4. 목표시가총액 / 발행주수
		// 목표시가총액 = ( 지배주주순이익 * PER)
		// 지배주주순이익 대신 당기순이익을 사용하기도 함
		//5. 사경인 회계사의 S-rim
		// 기업가치 = 자산가치 + 초과이익의 현재가치
		// = 자기자본 + 자기자본*(ROE-할인율)/할인율
		// 할인율 :  https://www.kisrating.com/ratingsStatistics/statics_spread.do BBB-  5년 8.76
		// 적정주가 = 기업가치 / (유통주식수 - 자기주식수[자사주])
		// ROE - 10%  1차 매도가격
		// ROE - 20% : 매수가격 
		
		return stockVO;
	}
	
	public StockVO insertZero(StockVO stockVO) throws Exception{
		Class<?> clazz = stockVO.getClass();
		Field[] declaredFields =clazz.getDeclaredFields();
		
		for (Field field : declaredFields) {
			field.setAccessible(true);
			if( field.getType() == String.class && ( !"id".equals(field.getName()) && !"deleteYn".equals(field.getName()) && !"discussionRoomUrl".equals(field.getName()) )) {
				String value = (String)field.get(stockVO);
				if(value == null || "".equals(value) || "N/A".equals(value)) {
					field.set(stockVO, "0");
				}
			}
		}
		return stockVO;
	}
	

	// ===========================================================================================

	/*
	 * public void getStockPriceList() {
	 * 
	 * final String stockList =
	 * "https://finance.naver.com/sise/sise_market_sum.nhn?&page=1"; Connection conn
	 * = Jsoup.connect(stockList);
	 * 
	 * try { Document document = conn.get(); String thead =
	 * getStockHeader(document); // 칼럼명 String tbody = getStockList(document); //
	 * 데이터 리스트 System.out.println(thead); System.out.println(tbody);
	 * 
	 * } catch (IOException ignored) { } }
	 * 
	 * 
	 * 
	 * public String getStockList(Document document) { Elements stockTableBody =
	 * document.select("table.type_2 tbody tr"); StringBuilder sb = new
	 * StringBuilder(); for (Element element : stockTableBody) { if
	 * (element.attr("onmouseover").isEmpty()) { continue; }
	 * 
	 * for (Element td : element.select("td")) { String text;
	 * if(td.select(".center a").attr("href").isEmpty()){ text = td.text(); }else{
	 * text = "https://finance.naver.com"+td.select(".center a").attr("href"); }
	 * sb.append(text); sb.append("   "); }
	 * sb.append(System.getProperty("line.separator")); //줄바꿈 } return
	 * sb.toString(); }
	 */
	
	/*
	 * public StockVO createStockVO(Elements td) { StockVO stockVO =
	 * StockVO.builder().build(); Class<?> clazz = stockVO.getClass(); Field[]
	 * fields = clazz.getDeclaredFields();
	 * 
	 * for (int i = 0; i < td.size(); i++) { String text; if
	 * (td.get(i).select(".center a").attr("href").isEmpty()) { text =
	 * td.get(i).text(); } else { text = "https://finance.naver.com" +
	 * td.get(i).select(".center a").attr("href"); } fields[i].setAccessible(true);
	 * try { fields[i].set(stockVO, text); } catch (Exception ignored) { } } return
	 * stockVO; }
	 */

}
