package com.kangong.stock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockMobileParser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockJsonConverter {

	private static final List<String> EXCEPT_ZERO_FIELDS = List.of(
			"id", "deleteYn", "discussionRoomUrl", "name", "stockId", "yyyymmdd"
	);

	@Autowired
	StockMobileParser stockMobileParser;

	// ==================== JSON 파싱 유틸리티 ====================

	private JSONObject parseJsonObject(String json) throws Exception {
		Object parsed = new JSONParser().parse(json);
		if (!(parsed instanceof JSONObject)) {
			throw new IllegalArgumentException("Expected JSONObject but got " + parsed.getClass().getSimpleName());
		}
		return (JSONObject) parsed;
	}

	private JSONArray parseJsonArray(String json) throws Exception {
		Object parsed = new JSONParser().parse(json);
		if (!(parsed instanceof JSONArray)) {
			throw new IllegalArgumentException("Expected JSONArray but got " + parsed.getClass().getSimpleName());
		}
		return (JSONArray) parsed;
	}

	private String toStringValue(Object value) {
		if (value == null) return null;
		return String.valueOf(value);
	}

	// ==================== 변환 ====================

	public boolean checkValidJson(String strUrlJson) {
		try {
			JSONObject jsonObject = parseJsonObject(strUrlJson);
			return !"StockConflict".equals(toStringValue(jsonObject.get("code")));
		} catch (Exception e) {
			return false;
		}
	}

	public ArrayList<StockVO> convertSimpleStockList(String paramJsonData) throws Exception {
		ArrayList<StockVO> resultList = new ArrayList<>();
		JSONObject jsonObject = parseJsonObject(paramJsonData);
		JSONArray stockJsonArray = (JSONArray) jsonObject.get("stocks");

		for (int i = 0; i < stockJsonArray.size(); i++) {
			StockVO stockVO = new StockVO();
			JSONObject item = (JSONObject) stockJsonArray.get(i);

			stockVO.setStockId(toStringValue(item.get("itemCode")));
			stockVO.setName(toStringValue(item.get("stockName")));
			stockVO.setPrice(stockMobileParser.getTrimValue(toStringValue(item.get("closePrice"))));
			stockVO.setMarketCapitalization(stockMobileParser.getTrimValue(toStringValue(item.get("marketValue"))));

			resultList.add(stockVO);
		}

		return resultList;
	}

	public StockVO convertStockDetail(StockVO stockVO, String paramJsonData) throws Exception {
		JSONObject jsonObject = parseJsonObject(paramJsonData);
		stockVO.setName(toStringValue(jsonObject.get("stockName")));

		JSONArray totalInfosArray = (JSONArray) jsonObject.get("totalInfos");
		setTotalInfos(stockVO, totalInfosArray);

		JSONObject consensusInfo = (JSONObject) jsonObject.get("consensusInfo");
		setConsensusInfo(stockVO, consensusInfo);

		// totalInfos.openPrice(시가)는 현재가가 아니므로, dealTrendInfos[0].closePrice(당일 실제가)로 보정
		String actualPrice = extractLatestClosePrice(jsonObject);
		if (actualPrice != null && !actualPrice.isEmpty() && !"0".equals(actualPrice)) {
			stockVO.setPrice(actualPrice);
		}

		// ETF 등 openPrice가 "-"인 경우 price가 빈 값 → lastClosePrice(전일가)로 대체
		if (stockVO.getPrice() == null || stockVO.getPrice().isEmpty() || "0".equals(stockVO.getPrice())) {
			String fallback = stockVO.getPriceBeforeday();
			if (fallback != null && !fallback.isEmpty() && !"0".equals(fallback)) {
				stockVO.setPrice(fallback);
			}
		}

		stockMobileParser.insertZero(stockVO, EXCEPT_ZERO_FIELDS);
		return stockVO;
	}

	private String extractLatestClosePrice(JSONObject jsonObject) throws Exception {
		Object raw = jsonObject.get("dealTrendInfos");
		if (!(raw instanceof JSONArray) || ((JSONArray) raw).isEmpty()) return null;
		JSONObject latest = (JSONObject) ((JSONArray) raw).get(0);
		return stockMobileParser.getTrimValue(toStringValue(latest.get("closePrice")));
	}

	private void setTotalInfos(StockVO stockVO, JSONArray stockJsonArray) throws Exception {
		Map<String, String> attributeMapper = StockFieldMappingRegistry.stockMobileApi();
		for (int i = 0; i < stockJsonArray.size(); i++) {
			JSONObject item = (JSONObject) stockJsonArray.get(i);
			String code = toStringValue(item.get("code"));
			String value = stockMobileParser.getTrimValue(toStringValue(item.get("value")));
			stockMobileParser.setObjectForMappingData(stockVO, attributeMapper, code, value);
		}
	}

	private void setConsensusInfo(StockVO stockVO, JSONObject stockJsonObject) throws Exception {
		if (stockJsonObject == null) return;
		Map<String, String> attributeMapper = StockFieldMappingRegistry.stockMobileApi();

		String recommMean = toStringValue(stockJsonObject.get("recommMean"));
		stockMobileParser.setObjectForMappingData(stockVO, attributeMapper, "recommMean", recommMean);

		String priceTargetMean = stockMobileParser.getTrimValue(toStringValue(stockJsonObject.get("priceTargetMean")));
		stockMobileParser.setObjectForMappingData(stockVO, attributeMapper, "priceTargetMean", priceTargetMean);
	}

	public List<StockDailyPriceVO> convertDailyPriceList(String stockId, String paramJsonData) throws Exception {
		List<StockDailyPriceVO> result = new ArrayList<>();
		if (paramJsonData == null || paramJsonData.isBlank()) {
			return result;
		}
		JSONArray jsonArray = parseJsonArray(paramJsonData);
		Map<String, String> mapper = StockFieldMappingRegistry.dailyPriceMobileApi();

		for (int i = 0; i < jsonArray.size(); i++) {
			StockDailyPriceVO vo = StockDailyPriceVO.builder().build();
			vo.setStockId(stockId);
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);

			for (String jsonKey : mapper.keySet()) {
				String value = toStringValue(jsonObject.get(jsonKey));
				stockMobileParser.setObjectForMappingData(vo, mapper, jsonKey, value);
			}

			result.add(vo);
		}
		return result;
	}

	public List<String> getExceptZeroFields() {
		return EXCEPT_ZERO_FIELDS;
	}
}
