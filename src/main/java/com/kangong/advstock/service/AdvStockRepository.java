package com.kangong.advstock.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kangong.advstock.model.YahooStockVO;
import com.kangong.stock.model.StockCategoryLinkVO;
import com.kangong.stock.model.StockCategoryVO;
import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockVO;

@Repository
public class AdvStockRepository {

    @Autowired
    private SqlSession sqlSession;

    // ==================== ST_STOCK_MASTER ====================

    public void saveFromNaver(StockVO vo) {
        sqlSession.update("seckim.advstock.saveFromNaver", vo);
    }

    public void markDeleted(List<String> activeStockIds) {
        Map<String, Object> param = new HashMap<>();
        param.put("activeStockIds", activeStockIds);
        sqlSession.update("seckim.advstock.markDeleted", param);
    }

    public void restoreIfDeleted(String stockId) {
        sqlSession.update("seckim.advstock.restoreIfDeleted", stockId);
    }

    public List<StockVO> selectList(StockVO vo) {
        return sqlSession.selectList("seckim.advstock.selectList", vo);
    }

    public List<String> selectInterestStockIds() {
        return sqlSession.selectList("seckim.advstock.selectInterestStockIds");
    }

    // ==================== ST_STOCK_DAILY_PRICE ====================

    public void saveDailyPrice(StockDailyPriceVO vo) {
        sqlSession.update("seckim.advstock.saveDailyPrice", vo);
    }

    // ==================== ST_STOCK_CATEGORY / LINK ====================

    public void saveCategory(StockCategoryVO vo) {
        sqlSession.update("seckim.advstock.saveCategory", vo);
    }

    public void saveCategoryLink(StockCategoryLinkVO vo) {
        sqlSession.update("seckim.advstock.saveCategoryLink", vo);
    }

    // ==================== Yahoo Finance ====================

    public void saveFromYahoo(YahooStockVO vo) {
        sqlSession.update("seckim.advstock.saveFromYahoo", vo);
    }
}
