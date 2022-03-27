package com.kangong.stock.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.common.service.CommonService;
import com.kangong.stock.model.StockCategoryVO;
import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.util.StockCategoryUtil;
import com.kangong.stock.util.StockDailyPriceUtil;
import com.kangong.stock.util.StockFinancialUtil;
import com.kangong.stock.util.StockSeleniumFinancialAnalysisUtil;
import com.kangong.stock.util.StockSeleniumFinancialUtil;
import com.kangong.stock.util.StockUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockService extends CommonService {

	@Autowired
	StockUtil stockUtil;

	@Autowired
	StockFinancialUtil stockFinancialUtil;

	@Autowired
	StockCategoryUtil stockCategoryUtil;
	
	@Autowired
	StockDailyPriceUtil stockDailyPriceUtil;
	
	@Autowired
	StockSeleniumFinancialUtil stockSeleniumFinancialUtil;
	
	@Autowired
	StockSeleniumFinancialAnalysisUtil stockSeleniumFinancialAnalysisUtil;
	
	public void saveStock(String stockId) throws Exception{
		StockVO paramVO = StockVO.builder().build();
		paramVO.setStockId(stockId);
		StockVO stockVO = (StockVO)sqlSession.selectOne("seckim.stock.select", paramVO);
		stockVO = stockUtil.setStockDetail(stockId, stockVO);
		mergeStock(stockUtil.insertZero(stockVO));
	}

	public List<StockVO> getStockList(StockVO stockVO) throws Exception {

		return sqlSession.selectList("seckim.stock.select", stockVO);
	}

	public List<StockVO> saveStockList() throws Exception {
		List<StockVO> stockList = sqlSession.selectList("seckim.stock.select", StockVO.builder().build());
		// 저장
		for (int i = 0; i < stockList.size(); i++) {
			StockVO stockVO = (StockVO) stockList.get(i);
			saveStock(stockVO.getStockId());
		}
		return stockList;
	}

	public void mergeStock(StockVO stockVO) throws Exception {
		sqlSession.update("seckim.stock.save", stockVO);
	}
	
	public StockFinancialVO getStockFinancialVO(StockFinancialVO stockFinancialVO) throws Exception {

		return sqlSession.selectOne("seckim.stock.selectFinancial", stockFinancialVO);
	}

	public void saveFinancialList() throws Exception {
		StockVO stockVO = StockVO.builder().build();
		// stockVO.setStockId("000100");
		List<StockVO> stockList = getStockList(stockVO);
		for (StockVO stock : stockList) {
			try {
				 saveStockFinancial(stock.getStockId());
				// log.info("stockFinancialVOList:"+stockFinancialVOList);
			} catch (Exception e) {
				log.info("Stock Financial 오류: " + stock.getName());
			}
		}
	}
	
	public void saveStockFinancial(String stockId)throws Exception{
		try {
				StockVO paramVO = StockVO.builder().build();
				paramVO.setStockId(stockId);
				StockVO stockVO = (StockVO)sqlSession.selectOne("seckim.stock.select", paramVO); 
				
				List<StockFinancialVO> stockFinancialVOList = stockFinancialUtil.getStockFinancial(stockVO);
		
				for (StockFinancialVO stockFinancialVO : stockFinancialVOList) {
					mergeFinancial(stockFinancialVO);
				}
		} catch (Exception e) {
			//e.printStackTrace();
		}	
	}

	public void mergeFinancial(StockFinancialVO stockFinancialVO) throws Exception {
		stockFinancialVO = stockFinancialUtil.insertZero(stockFinancialVO);
		sqlSession.update("seckim.stock.financailSave", stockFinancialVO);
	}
	
	public void saveFinancialList2() throws Exception {
		StockVO stockVO = StockVO.builder().build();
		// stockVO.setStockId("000100");
		List<StockVO> stockList = getStockList(stockVO);
		for (StockVO stock : stockList) {
			try {
				saveStockFinancial2(stock.getStockId());
				// log.info("stockFinancialVOList:"+stockFinancialVOList);
			} catch (Exception e) {
				log.info("Stock Financial 오류: " + stock.getName());
			}
		}
	}
	
	public void saveStockFinancial2(String stockId)throws Exception{
		try {
				StockVO paramVO = StockVO.builder().build();
				paramVO.setStockId(stockId);
				StockVO stockVO = (StockVO)sqlSession.selectOne("seckim.stock.select", paramVO); 
				
				List<StockFinancialVO> stockFinancialVOList = stockFinancialUtil.getStockFinacial2(stockVO);
				mergeFinancial2(stockFinancialVOList.get(0));
				
		} catch (Exception e) {
			//e.printStackTrace();
		}	
	}
	
	
	
	public void mergeFinancial2(StockFinancialVO stockFinancialVO) throws Exception {
		stockFinancialVO = stockFinancialUtil.insertZero(stockFinancialVO);
		sqlSession.update("seckim.stock.financailSave2", stockFinancialVO);
	}

	public void saveStockCategoryList() throws Exception {
		Map<String, List<StockCategoryVO>> categoryMap = stockCategoryUtil.getStockCateogyList();
		List<StockCategoryVO> stockCategoryList = (List<StockCategoryVO>) categoryMap.get("categoryList");
		List<StockCategoryVO> stockCategoryLinkList = (List<StockCategoryVO>) categoryMap.get("categoryLinkList");
		

		for (StockCategoryVO categoryVO : stockCategoryList) {
			saveStockCategory(categoryVO);
		}
		
		for (StockCategoryVO categoryLinkVO : stockCategoryLinkList) {
			saveStockCategoryLink(categoryLinkVO);
		}
		
		Map<String, List<StockCategoryVO>> themeMap = stockCategoryUtil.getStockThemeList();
		List<StockCategoryVO> stockThemeList = (List<StockCategoryVO>) themeMap.get("categoryList");
		List<StockCategoryVO> stockThemeLinkList = (List<StockCategoryVO>) themeMap.get("categoryLinkList");
		

		for (StockCategoryVO categoryVO : stockThemeList) {
			saveStockCategory(categoryVO);
		}
		
		for (StockCategoryVO categoryLinkVO : stockThemeLinkList) {
			saveStockCategoryLink(categoryLinkVO);
		}

	}
	
	public void saveStockCategory(StockCategoryVO categoryVO) throws Exception {
		sqlSession.update("seckim.stock.stockCategorySave", categoryVO);
	}
	
	public void saveStockCategoryLink(StockCategoryVO categoryVO) throws Exception {
		sqlSession.update("seckim.stock.stockCategoryLinkSave", categoryVO);
	}
	
	public void saveDailyPrice(String stockId) throws Exception {
		StockVO paramVO = StockVO.builder().build();
		paramVO.setStockId(stockId);
		StockVO stockVO = (StockVO)sqlSession.selectOne("seckim.stock.select", paramVO); 
		
		List<StockDailyPriceVO> stockDailyPriceVOList = stockDailyPriceUtil.getStockDailyPriceList(stockVO);
		

		for (StockDailyPriceVO stockDailyPriceVO : stockDailyPriceVOList) {
			if(!"0".equals(stockDailyPriceVO.getTradingDate()) )
			 sqlSession.update("seckim.stock.stockDailyPriceSave", stockDailyPriceVO);
		}
	}
	
	public void saveDailyPriceList() throws Exception {
		StockVO stockVO = StockVO.builder().build();
		List<StockVO> stockList = getStockList(stockVO);
		for (StockVO stock : stockList) {
			try {
				 saveDailyPrice(stock.getStockId());
			} catch (Exception e) {
				log.info("saveDailyPriceList 오류: " + stock.getName());
			}
		}
	}
	
	public void saveSeleniumStockFinancial(String stockId)throws Exception{
		try {
				StockVO paramVO = StockVO.builder().build();
				paramVO.setStockId(stockId);
				StockVO stockVO = (StockVO)sqlSession.selectOne("seckim.stock.select", paramVO); 		
				
				List<StockFinancialVO> stockFinancialVOList = stockSeleniumFinancialUtil.getFinancialEnterpriseState(stockVO);		
				for (StockFinancialVO stockFinancialVO : stockFinancialVOList) {
					mergeFinancial(stockFinancialVO);
				}
		} catch (Exception e) {
			//e.printStackTrace();
		}	
	}
	
	public void saveSeleniumStockFinancialAll() throws Exception{
		StockVO stockVO = StockVO.builder().build();
		List<StockVO> stockList = getStockList(stockVO);
		for(StockVO resultStockVO:stockList) {
			saveSeleniumStockFinancial(resultStockVO.getStockId());
		}
	}
	
	public void saveSeleniumStockFinancialAnalysis(String stockId)throws Exception{
		try {
				StockVO paramVO = StockVO.builder().build();
				paramVO.setStockId(stockId);
				StockVO stockVO = (StockVO)sqlSession.selectOne("seckim.stock.select", paramVO); 		
				
				List<StockFinancialVO> stockFinancialVOList = stockSeleniumFinancialAnalysisUtil.getFinancialAnalysis(stockVO);		
				for (StockFinancialVO stockFinancialVO : stockFinancialVOList) {
					mergeFinancial2(stockFinancialVO);
				}
		} catch (Exception e) {
			//e.printStackTrace();
		}	
	}
	
	public void saveSeleniumStockFinancialAnalysisAll() throws Exception{
		StockVO stockVO = StockVO.builder().build();
		List<StockVO> stockList = getStockList(stockVO);
		for(StockVO paramStockVO:stockList) {
			saveSeleniumStockFinancialAnalysis(paramStockVO.getStockId());
		}
	}
}
