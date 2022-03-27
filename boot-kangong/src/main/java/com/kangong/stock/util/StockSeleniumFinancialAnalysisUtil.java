package com.kangong.stock.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockSeleniumFinancialAnalysisUtil {

	@Autowired
	StockSeleniumFinancialUtil stockSeleniumFinancialUtil;
	
	@Autowired
	StockService stockService;
	
	public List<StockFinancialVO> getFinancialAnalysis(StockVO stockVO) throws Exception {
		ChromeDriver driver = stockSeleniumFinancialUtil.getChromeDriver();
		driver.get("https://finance.naver.com/item/main.naver?code="+stockVO.getStockId());
		Thread.sleep(500); // 3. 페이지 로딩 대기 시간
		//종목분석
		WebElement analysisElement = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[2]/div[1]/ul/li[6]/a"));
		analysisElement.click();
		Thread.sleep(500); 
		//iframe switch
		driver.switchTo().frame("coinfo_cp");
		
		//종목분석 > 재무분석
		WebElement financialAnalysisElement = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[1]/div[1]/dl/dt[3]/a"));
		financialAnalysisElement.click();
		Thread.sleep(500);
		//종목분석 > 재무분석 > 재무상태표
		WebElement financialAnalysisTableElement = driver.findElement(By.xpath("//*[@id=\"rpt_tab2\"]"));
		financialAnalysisTableElement.click();
		Thread.sleep(500);
		
		List<String> headerList = getFinancialAnalysisTableHeaderList(driver);
		List<StockFinancialVO> financialAnalysisTableStockFinancialVOList = geFinancialAnalysisStockFinancialVOList(headerList, stockVO);
		
		//종목분석 > 재무분석 > 재무상태표 > 실적 Tr
		WebElement financialAnalysisTableTbodyElement = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[5]/table[2]/tbody"));		
		List<WebElement> financialAnalysisTableTrElementList = financialAnalysisTableTbodyElement.findElements(By.cssSelector("tr"));
		
		for(WebElement trElement:financialAnalysisTableTrElementList) {
			//log.info(trElement.getAttribute("innerHTML"));
			setFinancialAnalysisTableBodyList(driver, trElement, financialAnalysisTableStockFinancialVOList);
		}
		
		//log.info(headerList);
		//log.info(financialAnalysisTableStockFinancialVOList);
		
		return financialAnalysisTableStockFinancialVOList;		
	}
	
	public List<String> getFinancialAnalysisTableHeaderList(ChromeDriver driver) throws Exception{
		ArrayList<String> headerList = new ArrayList<String>();
		//종목분석 > 재무분석 > 재무상태표 > Table Header TR
		WebElement financialAnalysisTableTrElement = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[5]/table[2]/thead/tr"));		
		
		List<WebElement> financialAnalysisTableHeaderList = financialAnalysisTableTrElement.findElements(By.cssSelector("th div"));
		
		for(WebElement headerElement : financialAnalysisTableHeaderList) {
			String title = headerElement.getAttribute("innerHTML");//headerElement.getText();
			String year = title.split("/")[0];
			if( year.matches("[+-]?\\d*(\\.\\d+)?")  && !ObjectUtils.isEmpty(year) )  //숫자여부판단
				headerList.add(year);
			
			log.info(year);
		}	
		return headerList;
	}
	
	public List<StockFinancialVO> geFinancialAnalysisStockFinancialVOList(List<String> headerList, StockVO stockVO) throws Exception {
		ArrayList<StockFinancialVO> stockFinancialList = new ArrayList<StockFinancialVO>();
		for(String year : headerList) {
			StockFinancialVO stockFinancialVO = StockFinancialVO.builder().build();
			stockFinancialVO.setYear(year);
			stockFinancialVO.setStockId(stockVO.getStockId());
			stockFinancialVO.setStockMasterId(stockVO.getId());
			
			StockFinancialVO resultStockFinancialVO = stockService.getStockFinancialVO(stockFinancialVO);
			
			if(ObjectUtils.isEmpty(resultStockFinancialVO) && ObjectUtils.isEmpty(resultStockFinancialVO.getId())) {
				stockFinancialList.add(stockFinancialVO);
			}else {
				stockFinancialList.add(resultStockFinancialVO);
			}			
		}
		return stockFinancialList;
	}
	
	public List<StockFinancialVO> setFinancialAnalysisTableBodyList(ChromeDriver driver, WebElement trElement, List<StockFinancialVO> stockFinancialList) throws Exception {
		Map<String, String> headerAttributeMapper = getHeaderAttributeMapper();
		
		WebElement thElement = trElement.findElement(By.cssSelector("td[title]"));
		List<WebElement> tdElementList = trElement.findElements(By.cssSelector("td"));
		String text = thElement.getText().trim();
		String attributeName = headerAttributeMapper.get(text);
		if(attributeName==null) 
			return stockFinancialList;
		
		for (int i = 1; i < tdElementList.size()-2; i++) {
			int index = i-1;
			StockFinancialVO stockFinancialVO = stockFinancialList.get(index);
			Class<?> clazz = stockFinancialVO.getClass();
			
			// 값 셋팅
			String value = tdElementList.get(i).getText().trim();
			Field field = clazz.getDeclaredField(attributeName);
			field.setAccessible(true);			
			field.set(stockFinancialVO, value);			
		}
		
		//System.out.println("stockFinancialList: " + stockFinancialList);
		return stockFinancialList;
	}
	
	public Map<String, String> getHeaderAttributeMapper() {
		Map<String, String> headerAttributeMapperMap = new HashMap<String, String>();
		headerAttributeMapperMap.put("유동자산", "liquidAsset");
		headerAttributeMapperMap.put("유동부채", "liquidDept");
		headerAttributeMapperMap.put("발행주식수", "totalStockQty");
		headerAttributeMapperMap.put("보통주", "commonStockQty");
		headerAttributeMapperMap.put("우선주", "preferredStockQty");
		return headerAttributeMapperMap;
	}
}
