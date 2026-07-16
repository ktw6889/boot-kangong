package com.kangong.stock.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.common.util.BatchOperationHandler;
import com.kangong.stock.model.StockCategoryLinkVO;
import com.kangong.stock.model.StockCategoryVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockMobileCategory {

	@Autowired
	private StockFetcher stockFetcher;

	@Autowired
	private StockRepository stockRepository;

	public void saveStockCategory() throws Exception {
		List<Integer> pages = Arrays.asList(1, 2);
		BatchOperationHandler.executeBatch(pages,
			this::saveStockCategoryPage,
			stockFetcher::categoryUrl,
			"saveStockCategory", log);
	}

	private void saveStockCategoryPage(int pageNum) throws Exception {
		String strUrlJson = stockFetcher.fetchCategory(pageNum);
		List<StockCategoryVO> categoryList = parseCategoryJson(strUrlJson, "INDUSTRY");
		for (StockCategoryVO categoryVO : categoryList) {
			stockRepository.saveCategory(categoryVO);
			saveStockCategoryLink(categoryVO);
		}
	}

	public List<StockCategoryVO> parseCategoryJson(String paramJsonData, String categoryType) throws Exception {
		return parseJsonArray(paramJsonData, "groups", item -> {
			StockCategoryVO vo = StockCategoryVO.builder().build();
			vo.setCategoryName((String) item.get("name"));
			vo.setCategoryNo(String.valueOf(item.get("no")));
			vo.setCategoryType(categoryType);
			return vo;
		});
	}

	public void saveStockCategoryLink(StockCategoryVO stockCategoryVO) throws Exception {
		String categoryNo = stockCategoryVO.getCategoryNo();
		List<Integer> pages = Arrays.asList(1, 2, 3, 4, 5);
		BatchOperationHandler.executeBatch(pages,
			pageNum -> saveStockCategoryLinkPage(categoryNo, pageNum),
			pageNum -> stockFetcher.categoryLinkUrl(categoryNo, pageNum),
			"saveStockCategoryLink(categoryNo:" + categoryNo + ")", log);
	}

	private void saveStockCategoryLinkPage(String categoryNo, int pageNum) throws Exception {
		String strUrlJson = stockFetcher.fetchCategoryLink(categoryNo, pageNum);
		List<StockCategoryLinkVO> linkList = parseCategoryLinkJson(strUrlJson, categoryNo);
		for (StockCategoryLinkVO linkVO : linkList) {
			stockRepository.saveCategoryLink(linkVO);
		}
	}

	public List<StockCategoryLinkVO> parseCategoryLinkJson(String paramJsonData, String categoryNo) throws Exception {
		return parseJsonArray(paramJsonData, "stocks", item -> {
			StockCategoryLinkVO vo = StockCategoryLinkVO.builder().build();
			vo.setCategoryNo(categoryNo);
			vo.setStockId((String) item.get("itemCode"));
			return vo;
		});
	}

	private <T> List<T> parseJsonArray(String json, String arrayKey,
									   Function<JSONObject, T> mapper) throws Exception {
		List<T> result = new ArrayList<>();
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
		JSONArray array = (JSONArray) jsonObject.get(arrayKey);
		for (int i = 0; i < array.size(); i++) {
			result.add(mapper.apply((JSONObject) array.get(i)));
		}
		return result;
	}
}
