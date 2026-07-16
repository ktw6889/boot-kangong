package com.kangong.stock.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.common.util.BatchOperationHandler;
import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockMobileParser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockMobileEsgService {

	private static final Map<String, String> ESG_FIELD_MAP = StockFieldMappingRegistry.esgMobileApi();

	private static final Map<String, List<String>> THEME_KEYS = ESG_FIELD_MAP.keySet().stream()
			.collect(Collectors.groupingBy(key -> key.substring(0, 1)));

	@Autowired
	private StockFetcher stockFetcher;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockMobileParser stockMobileParser;

	@Autowired
	private StockJsonConverter stockJsonConverter;

	public void saveStockEsgAll() throws Exception {
		List<StockVO> stockVOList = stockRepository.selectStockList(StockVO.builder().build());
		BatchOperationHandler.executeBatchParallel(stockVOList,
			stockVO -> saveStockEsg(stockVO.getStockId()),
			stockVO -> stockFetcher.esgUrl(stockVO.getStockId()),
			"saveStockEsgAll", log);
	}

	public void saveStockEsg(String stockId) throws Exception {
		log.info("StockId: {}", stockId);
		String strUrlJson = stockFetcher.fetchEsg(stockId);
		Map<String, StockEsgVO> stockEsgVOMap = parseEsgJson(strUrlJson);
		for (StockEsgVO stockEsgVO : stockEsgVOMap.values()) {
			saveStockEsgVO(stockEsgVO);
		}
	}

	public Map<String, StockEsgVO> parseEsgJson(String paramJsonData) throws Exception {
		Map<String, StockEsgVO> stockEsgVOMap = new HashMap<>();

		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(paramJsonData);
		JSONObject nonFinanceInfo = (JSONObject) jsonObject.get("nonFinanceInfo");
		JSONArray themes = (JSONArray) jsonObject.get("themes");

		for (int i = 0; i < themes.size(); i++) {
			String themeCode = (String) ((JSONObject) themes.get(i)).get("code");
			JSONObject themeInfo = (JSONObject) nonFinanceInfo.get(themeCode);
			List<String> keys = THEME_KEYS.get(themeCode);
			if (keys == null || themeInfo == null) continue;

			for (String key : keys) {
				JSONArray items = (JSONArray) themeInfo.get(key);
				if (items == null) continue;

				for (int j = 0; j < items.size(); j++) {
					JSONObject item = (JSONObject) items.get(j);
					String year = (String) item.get("baseYear");
					String itemCode = (String) item.get("itemCode");
					String score = (String) item.get("score");

					StockEsgVO vo = stockEsgVOMap.computeIfAbsent(year, y -> {
						StockEsgVO newVO = StockEsgVO.builder().build();
						newVO.setStockId(itemCode);
						newVO.setYear(y);
						return newVO;
					});

					stockMobileParser.setObjectForMappingData(vo, ESG_FIELD_MAP, key, score);
				}
			}
		}

		return stockEsgVOMap;
	}

	private void saveStockEsgVO(StockEsgVO stockEsgVO) throws Exception {
		log.info("StockEsgVO: {}", stockEsgVO);
		stockEsgVO = (StockEsgVO) stockMobileParser.insertZero(stockEsgVO, stockJsonConverter.getExceptZeroFields());
		stockRepository.saveEsg(stockEsgVO);
	}
}
