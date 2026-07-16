package com.kangong.stock.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kangong.stock.model.StockCategoryLinkVO;
import com.kangong.stock.model.StockCategoryVO;
import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockMarketIndexVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.model.StockValueScreenVO;

@Repository
public class StockRepository {

	@Autowired
	private SqlSession sqlSession;

	// ==================== Stock ====================

	public List<StockVO> selectStockList(StockVO stockVO) {
		return sqlSession.selectList("seckim.stock.select", stockVO);
	}

	public StockVO selectStock(StockVO paramVO) {
		return sqlSession.selectOne("seckim.stock.select", paramVO);
	}

	public void saveSimple(StockVO stockVO) {
		sqlSession.update("seckim.stock.saveSimple", stockVO);
	}

	public void saveSimpleBatch(List<StockVO> stockList) {
		sqlSession.update("seckim.stock.saveSimpleBatch", stockList);
	}

	public void saveStock(StockVO stockVO) {
		sqlSession.update("seckim.stock.save", stockVO);
	}

	public void saveStockBatch(List<StockVO> list) {
		if (list == null || list.isEmpty()) return;
		sqlSession.update("seckim.stock.saveBatch", list);
	}

	public void saveMasterForStockId(StockVO stockVO) {
		sqlSession.update("seckim.mobilestock.saveMasterForStockId", stockVO);
	}

	public List<String> selectInterestStockIds() {
		return sqlSession.selectList("seckim.stock.selectInterestStockIds");
	}

	// ==================== DailyPrice ====================

	public void saveDailyPrice(StockDailyPriceVO vo) {
		sqlSession.update("seckim.stock.saveStockDailyPrice", vo);
	}

	public void saveDailyPriceBatch(List<StockDailyPriceVO> list) {
		if (list == null || list.isEmpty()) return;
		sqlSession.update("seckim.stock.saveStockDailyPriceBatch", list);
	}

	// ==================== Financial ====================

	public void saveFinancial(StockFinancialVO vo) {
		sqlSession.update("seckim.stock.saveFinancail", vo);
	}

	public void saveCashFlow(StockFinancialVO vo) {
		sqlSession.update("seckim.stock.saveCashFlow", vo);
	}

	// ==================== ESG ====================

	public List<StockEsgVO> selectEsgList(StockEsgVO vo) {
		return sqlSession.selectList("seckim.stock.selectStockEsgList", vo);
	}

	public void saveEsg(StockEsgVO vo) {
		sqlSession.update("seckim.stock.saveStockEsg", vo);
	}

	// ==================== MarketIndex ====================

	public void saveMarketIndex(StockMarketIndexVO vo) {
		sqlSession.update("seckim.stock.saveStockMarketIndex", vo);
	}

	public List<StockMarketIndexVO> selectIndexList(StockMarketIndexVO vo) {
		return sqlSession.selectList("seckim.mobilestock.selectIndexList", vo);
	}

	// ==================== Category ====================

	public void saveCategory(StockCategoryVO vo) {
		sqlSession.update("seckim.stock.saveStockCategory", vo);
	}

	public void saveCategoryLink(StockCategoryLinkVO vo) {
		sqlSession.update("seckim.stock.saveStockCategoryLink", vo);
	}

	// ==================== ValueScreen ====================

	public List<StockValueScreenVO> selectValueScreen(StockValueScreenVO vo) {
		return sqlSession.selectList("seckim.stock.selectValueScreen", vo);
	}

	public void updateIndustryPer(StockVO vo) {
		sqlSession.update("seckim.stock.updateIndustryPer", vo);
	}

	public void updateRoeFromFinancial() {
		sqlSession.update("seckim.stock.updateRoeFromFinancial");
	}

	public void updateDividendsTendency() {
		sqlSession.update("seckim.stock.updateDividendsTendency");
	}
}
