package com.kangong.stock.parser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockMobileParser extends AbstractStockDataParser {

	public String getUrlJsonData(String httpUrl) throws Exception {
		return fetchJsonData(httpUrl);
	}

	public CompletableFuture<String> getUrlJsonDataAsync(String httpUrl) {
		return fetchJsonDataAsync(httpUrl);
	}

	public String getTrimValue(String value) throws Exception {
		return trimValue(value);
	}

	public Object setObjectForMappingData(Object target, java.util.Map<String, String> attributeMapper, String name, String value) throws Exception {
		return mapDataToObject(target, attributeMapper, name, value);
	}

	public Object insertZero(Object target, List excludeFields) throws Exception {
		java.util.Set<String> excludeSet = new java.util.HashSet<>(excludeFields);
		return fillEmptyFieldsWithZeroAndClean(target, excludeSet);
	}
}
