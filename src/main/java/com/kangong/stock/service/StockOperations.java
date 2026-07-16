package com.kangong.stock.service;

import java.util.List;

import com.kangong.stock.model.StockVO;

public interface StockOperations {

	StockVO getStockVO(String stockId) throws Exception;

	List<StockVO> getStockList(StockVO stockVO) throws Exception;

	void saveStock(String stockId) throws Exception;

	void saveAllStock() throws Exception;

	void saveDailyPrice(String stockId) throws Exception;

	void saveDailyPriceAll() throws Exception;
}
