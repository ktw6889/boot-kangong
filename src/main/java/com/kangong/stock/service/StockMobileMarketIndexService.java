package com.kangong.stock.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.stock.model.StockMarketIndexVO;
import com.kangong.stock.parser.StockMobileParser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockMobileMarketIndexService {

	@Autowired
	private StockFetcher stockFetcher;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockMobileParser stockMobileParser;

	@Autowired
	private StockJsonConverter stockJsonConverter;

	/**
	 * Market Index 저장
	 */
	public void saveStockMarketIndex() throws Exception {
		String strUrlJson = stockFetcher.fetchMarketIndex();
		StockMarketIndexVO marketIndexVO = getStockMarketIndexVOByJsonConvert(strUrlJson);
		saveStockMarketIndex(marketIndexVO);
	}

	/**
	 * MarketIndex VO Set
	 */
	public StockMarketIndexVO getStockMarketIndexVOByJsonConvert(String paramJsonData) throws Exception {
		StockMarketIndexVO marketIndexVO = StockMarketIndexVO.builder().build();
		String strYmd = getStrNowDateByFormat("yyyyMMdd");
		marketIndexVO.setYyyymmdd(strYmd);

		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(paramJsonData);
		List<String> marketIndexKeyList = getMarketIndexKeyList();

		for (String marketIndexKey : marketIndexKeyList) {
			JSONArray domesticInterestJsonArray = (JSONArray) jsonObject.get(marketIndexKey);
			for (int marketDataIndex = 0; marketDataIndex < domesticInterestJsonArray.size(); marketDataIndex++) {
				JSONObject maketIndexDataJsonObject = (JSONObject) domesticInterestJsonArray.get(marketDataIndex);
				String key = (String) maketIndexDataJsonObject.get("reutersCode");
				String value = (String) maketIndexDataJsonObject.get("closePrice");
				marketIndexVO = (StockMarketIndexVO) stockMobileParser.setObjectForMappingData(marketIndexVO, StockFieldMappingRegistry.marketIndexMobileApi(), key, value);
			}
		}

		return marketIndexVO;
	}

	public String getStrNowDateByFormat(String format) throws Exception {
		Date nowDate = new Date();
		SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
		String strNewDtFormat = dtFormat.format(nowDate);

		return strNewDtFormat;
	}

	public List<String> getMarketIndexKeyList() throws Exception {
		List<String> marketIndexList = new ArrayList<String>();
		marketIndexList.add("domesticInterest");
		marketIndexList.add("standardInterest");
		marketIndexList.add("metals");
		marketIndexList.add("transport");
		marketIndexList.add("bond");
		marketIndexList.add("energy");

		return marketIndexList;
	}

	public void saveStockMarketIndex(StockMarketIndexVO stockMarketIndexVO) throws Exception {
		log.info("StockMarketIndexVO: {}", stockMarketIndexVO);
		stockMarketIndexVO = (StockMarketIndexVO) stockMobileParser.insertZero(stockMarketIndexVO, stockJsonConverter.getExceptZeroFields());
		stockRepository.saveMarketIndex(stockMarketIndexVO);
	}

	public List<StockMarketIndexVO> getIndexList(StockMarketIndexVO vo) throws Exception {
		return stockRepository.selectIndexList(vo);
	}
}