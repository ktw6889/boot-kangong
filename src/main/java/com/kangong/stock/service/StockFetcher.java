package com.kangong.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import com.kangong.stock.parser.StockMobileParser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockFetcher {

	private static final String BASE_MOBILE_URL = "https://m.stock.naver.com/api";
	private static final String BASE_API_URL = "https://api.stock.naver.com";

	@Autowired
	private StockMobileParser stockMobileParser;

	// ==================== URL 생성 ====================

	public String simpleStockListUrl(int page) {
		return BASE_MOBILE_URL + "/index/KOSPI/enrollStocks?pageSize=25&type=object&page=" + page;
	}

	public String stockDetailUrl(String stockId) {
		return BASE_MOBILE_URL + "/stock/" + stockId + "/integration";
	}

	public String dailyPriceUrl(String stockId, int page) {
		return BASE_MOBILE_URL + "/stock/" + stockId + "/price?pageSize=15&page=" + page;
	}

	public String financialUrl(String stockId) {
		return BASE_MOBILE_URL + "/stock/" + stockId + "/finance/annual";
	}

	public String esgUrl(String stockId) {
		return BASE_MOBILE_URL + "/stock/" + stockId + "/finance/nonFinance";
	}

	public String marketIndexUrl() {
		return BASE_API_URL + "/marketindex/majors/part2";
	}

	public String categoryUrl(int page) {
		return BASE_MOBILE_URL + "/stocks/industry?pageSize=60&page=" + page;
	}

	public String categoryLinkUrl(String categoryNo, int page) {
		return BASE_MOBILE_URL + "/stocks/industry/" + categoryNo + "?pageSize=20&page=" + page;
	}

	// ==================== 데이터 조회 ====================

	public String fetchSimpleStockList(int page) throws Exception {
		return stockMobileParser.getUrlJsonData(simpleStockListUrl(page));
	}

	public String fetchStockDetail(String stockId) throws Exception {
		return stockMobileParser.getUrlJsonData(stockDetailUrl(stockId));
	}

	public String fetchDailyPrice(String stockId, int page) throws Exception {
		return stockMobileParser.getUrlJsonData(dailyPriceUrl(stockId, page));
	}

	public String fetchFinancial(String stockId) throws Exception {
		return stockMobileParser.getUrlJsonData(financialUrl(stockId));
	}

	public CompletableFuture<String> fetchFinancialAsync(String stockId) {
		return stockMobileParser.getUrlJsonDataAsync(financialUrl(stockId));
	}

	public String fetchEsg(String stockId) throws Exception {
		return stockMobileParser.getUrlJsonData(esgUrl(stockId));
	}

	public String fetchMarketIndex() throws Exception {
		return stockMobileParser.getUrlJsonData(marketIndexUrl());
	}

	public String fetchCategory(int page) throws Exception {
		return stockMobileParser.getUrlJsonData(categoryUrl(page));
	}

	public String fetchCategoryLink(String categoryNo, int page) throws Exception {
		return stockMobileParser.getUrlJsonData(categoryLinkUrl(categoryNo, page));
	}
}
