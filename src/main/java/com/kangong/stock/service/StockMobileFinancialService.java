package com.kangong.stock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.common.util.BatchOperationHandler;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockMobileParser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockMobileFinancialService {

	@Autowired
	private StockFetcher stockFetcher;

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockMobileParser stockMobileParser;

	public void saveStockFinancialAll() throws Exception {
		List<StockVO> stockVOList = stockRepository.selectStockList(StockVO.builder().build());
		BatchOperationHandler.executeBatchParallel(stockVOList,
			stockVO -> saveStockFinancialOnly(stockVO.getStockId()),
			stockVO -> stockFetcher.financialUrl(stockVO.getStockId()),
			"saveStockFinancialAll", log);

		stockRepository.updateDividendsTendency();
		stockRepository.updateRoeFromFinancial();
		log.info("배당성향 계산 및 ROE 마스터 갱신 완료");
	}

	public void saveStockFinancial(String stockId) throws Exception {
		saveStockFinancialOnly(stockId);
		stockRepository.updateDividendsTendency();
	}

	private void saveStockFinancialOnly(String stockId) throws Exception {
		String strUrlJson = stockFetcher.fetchFinancial(stockId);
		Map<String, StockFinancialVO> stockFinancialVOMap = getStockFinancialListByJsonConvert(strUrlJson);
		saveFinancialMap(stockFinancialVOMap);
	}

	public Map<String, StockFinancialVO> getStockFinancialListByJsonConvert(String paramJsonData) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(paramJsonData);
		JSONObject stockFinancialJsonObject = (JSONObject) jsonObject.get("financeInfo");

		if (stockFinancialJsonObject == null)
			return new HashMap<String, StockFinancialVO>();

		List<String> financialKeyList = getFinancialKey(stockFinancialJsonObject);
		Map<String, StockFinancialVO> stockFinancialVOMap = setFinancialList(financialKeyList, stockFinancialJsonObject);

		return stockFinancialVOMap;
	}

	public List<String> getFinancialKey(JSONObject stockFinancialJsonObject) throws Exception {
		List<String> financialKeyList = new ArrayList<String>();
		JSONArray jsonArray = (JSONArray) stockFinancialJsonObject.get("trTitleList");

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			if ("Y".equals(jsonObject.get("isConsensus"))) {
				continue;
			}
			financialKeyList.add((String) jsonObject.get("key"));
		}
		return financialKeyList;
	}

	public Map<String, StockFinancialVO> setFinancialList(List<String> financialKeyList, JSONObject stockFinancialJsonObject) throws Exception {
		Map<String, StockFinancialVO> stockFinancialVOMap = buildFinancialVOMap(financialKeyList, stockFinancialJsonObject);

		JSONArray rowList = (JSONArray) stockFinancialJsonObject.get("rowList");
		for (int i = 0; i < rowList.size(); i++) {
			applyRowToFinancialMap((JSONObject) rowList.get(i), stockFinancialVOMap);
		}

		return stockFinancialVOMap;
	}

	private Map<String, StockFinancialVO> buildFinancialVOMap(List<String> financialKeyList, JSONObject stockFinancialJsonObject) {
		Map<String, StockFinancialVO> map = new HashMap<String, StockFinancialVO>();
		String itemCode = (String) stockFinancialJsonObject.get("itemCode");
		for (String key : financialKeyList) {
			StockFinancialVO vo = StockFinancialVO.builder().build();
			vo.setStockId(itemCode);
			vo.setYear(key.substring(0, 4));
			map.put(key, vo);
		}
		return map;
	}

	private void applyRowToFinancialMap(JSONObject rowJsonObject, Map<String, StockFinancialVO> stockFinancialVOMap) throws Exception {
		String name = (String) rowJsonObject.get("title");
		JSONObject columns = (JSONObject) rowJsonObject.get("columns");
		for (Map.Entry<String, StockFinancialVO> entry : stockFinancialVOMap.entrySet()) {
			JSONObject yearObject = (JSONObject) columns.get(entry.getKey());
			String value = (String) yearObject.get("value");
			stockMobileParser.setObjectForMappingData(entry.getValue(), StockFieldMappingRegistry.financialMobileApi(), name, value);
		}
	}

	public void saveFinancialMap(Map<String, StockFinancialVO> stockFinancialVOMap) throws Exception {
		for (String key : stockFinancialVOMap.keySet()) {
			StockFinancialVO stockFinancialVO = stockFinancialVOMap.get(key);
			mergeFinancial(stockFinancialVO);
		}
	}

	public void mergeFinancial(StockFinancialVO stockFinancialVO) throws Exception {
		stockFinancialVO = (StockFinancialVO) stockMobileParser.insertZero(stockFinancialVO, getExceptStockFinancialListZeroAttribute());
		stockRepository.saveFinancial(stockFinancialVO);
	}

	public List<String> getExceptStockFinancialListZeroAttribute() {
		List<String> exceptStockFinacialList = new ArrayList<String>();
		exceptStockFinacialList.add("id");
		exceptStockFinacialList.add("deleteYn");
		exceptStockFinacialList.add("stockMasterId");

		return exceptStockFinacialList;
	}
}
