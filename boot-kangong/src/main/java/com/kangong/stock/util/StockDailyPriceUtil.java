package com.kangong.stock.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockDailyPriceUtil {
	
	@Autowired
	StockUtil stockUtil;
	
	public List<StockDailyPriceVO> getStockDailyPriceList(StockVO stockVO) throws Exception {
		List<StockDailyPriceVO> resultList = new ArrayList<StockDailyPriceVO>();
		for(int i = 1; i < 2; i++) {
			resultList.addAll(getStockDailyPriceListOfPaging(stockVO, i));
		}
		return resultList;
	}
	
	public List<StockDailyPriceVO> getStockDailyPriceListOfPaging(StockVO stockVO, int i) throws Exception {
		final String stockDailyPriceListURL = "https://finance.naver.com/item/frgn.naver?code="+stockVO.getStockId()+"&page="+i;
		Document document = stockUtil.getUrlDocument(stockDailyPriceListURL);
		
		return getStockDailyPriceListOfDocument(stockVO, document);
	}
	
	public List<StockDailyPriceVO> getStockDailyPriceListOfDocument(StockVO stockVO, Document document) throws Exception {
		Elements dailyPriceTableTr = document.select("table[summary='외국인 기관 순매매 거래량에 관한표이며 날짜별로 정보를 제공합니다.'] tbody tr");
		List<StockDailyPriceVO> list = new ArrayList<>();
		ArrayList<String> headerList = getStockDailyPriceHeader(document);
		for (Element trElement : dailyPriceTableTr) {
			if (trElement.attr("onmouseover").isEmpty()) {
				continue;
			}
			list.add(setStockDailyPriceVO(stockVO, trElement.select("td"), headerList));
		}
		return list;
	}
	
	public ArrayList<String> getStockDailyPriceHeader(Document document) {		
		ArrayList<String> headerList = new ArrayList<String>();
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
	
	public StockDailyPriceVO setStockDailyPriceVO(StockVO stockVO, Elements tdElements, ArrayList<String> headerList) throws Exception {
		StockDailyPriceVO stockDailyPriceVO = StockDailyPriceVO.builder().build();
		stockDailyPriceVO.setStockId(stockVO.getStockId());
		Class<?> clazz = stockDailyPriceVO.getClass();
		Map<String, String> headerAttributeMapper = getHeaderAttributeMapper();

		for (int i = 0; i < tdElements.size(); i++) {
			// 값 셋팅
			String text = tdElements.get(i).select("span").text();
			

			// Field 셋팅
			Field field = null;
			try {
				String fieldName = headerAttributeMapper.get(headerList.get(i));
				field = clazz.getDeclaredField(fieldName);

				if (field == null)
					continue;
				field.setAccessible(true);
				field.set(stockDailyPriceVO, text.replaceAll("[^0-9.-]", ""));
			} catch (Exception e) {

			}
		}
		
		return insertZero(stockDailyPriceVO);
	}
	
	public Map<String, String> getHeaderAttributeMapper() {
		Map<String, String> headerAttributeMapperMap = new HashMap<String, String>();
		headerAttributeMapperMap.put("날짜", "tradingDate");
		headerAttributeMapperMap.put("종가", "closingPrice");
		headerAttributeMapperMap.put("전일비", "previousDayRate");
		headerAttributeMapperMap.put("등락률", "fluctuationRate");
		headerAttributeMapperMap.put("거래량", "volumn");
		headerAttributeMapperMap.put("기관_순매매량", "organTradingVolumn");
		headerAttributeMapperMap.put("외국인_순매매량", "foreignTradingVolumn");
		headerAttributeMapperMap.put("외국인_보유주수", "foreignHoldingVolumn");
		headerAttributeMapperMap.put("외국인_보유율", "foreignHoldingRate");
		return headerAttributeMapperMap;
	}
	
	public StockDailyPriceVO insertZero(StockDailyPriceVO stockDailyPriceVO) throws Exception{
		Class<?> clazz = stockDailyPriceVO.getClass();
		Field[] declaredFields =clazz.getDeclaredFields();
		
		for (Field field : declaredFields) {
			field.setAccessible(true);
			if( field.getType() == String.class && ( !"id".equals(field.getName()) && !"stockId".equals(field.getName()) )) {
				String value = (String)field.get(stockDailyPriceVO);
				if(value == null || "".equals(value) || "N/A".equals(value)) {
					field.set(stockDailyPriceVO, "0");
				}
			}
		}
		return stockDailyPriceVO;
	}

}
