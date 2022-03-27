package com.kangong.stock.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class StockSeleniumFinancialUtil {
	
	@Autowired
	StockService stockService;
	
	ChromeDriver driver;

	// 1. 드라이버 설치 경로
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH = "D:\\Dev\\backup\\chromedriver.exe";
	
	public ChromeDriver getChromeDriver(){
		// WebDriver 경로 설정
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
				
		// 2. WebDriver 옵션 설정
		ChromeOptions options = new ChromeOptions();
		//options.addArguments("--start-maximized");
		//options.addArguments("--disable-popup-blocking");
		
		options.addArguments("--headless"); 
		options.addArguments("--no-sandbox");
        
		if(driver == null)
			driver = new ChromeDriver(options);
		return driver;
	}
	
	public StockVO getStockInfo(StockVO stockVO) throws Exception{
		ChromeDriver driver = getChromeDriver();
		driver.get("https://finance.naver.com/item/main.naver?code="+stockVO.getStockId());
		Thread.sleep(300); // 3. 페이지 로딩 대기 시간
		
		//종목분석
		WebElement analysisElement = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[2]/div[1]/ul/li[6]/a"));
		analysisElement.click();
		Thread.sleep(300); 
		
		//iframe switch
		driver.switchTo().frame("coinfo_cp");
		
		WebElement dividendElement = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[2]/div[1]/div/table/tbody/tr[3]/td/dl/dt[6]/b"));
		stockVO.setDividendRate(dividendElement.getText().replaceAll("[^0-9.-]", ""));
		
		return stockVO;
	}
	
	public List<StockFinancialVO> getFinancialEnterpriseState(StockVO stockVO) throws Exception {
		ChromeDriver driver = getChromeDriver();
		driver.get("https://finance.naver.com/item/main.naver?code="+stockVO.getStockId());
		Thread.sleep(500); // 3. 페이지 로딩 대기 시간
		//종목분석
		WebElement analysisElement = driver.findElement(By.xpath("/html/body/div[3]/div[2]/div[2]/div[1]/ul/li[6]/a"));
		analysisElement.click();
		Thread.sleep(500); 
		//iframe switch
		driver.switchTo().frame("coinfo_cp");
		
		//종목분석 > 기업현황
		WebElement enterpriseStateElement = driver.findElement(By.cssSelector("#header-menu > div.wrapper-menu > dl > dt.on > a"));
		enterpriseStateElement.click();
		Thread.sleep(500);
		//종목분석 > 기업현황 > 연간
		WebElement enterpriseStateYearElement = driver.findElement(By.xpath("//*[@id=\"cns_Tab21\"]"));
		enterpriseStateYearElement.click();
		Thread.sleep(500);
		List<String> headerList = getEnterpriseStateHeaderList(driver);
		List<StockFinancialVO> enterpriseStatetStockFinancialVOList = geEnterpriseStatetStockFinancialVOList(headerList, stockVO);
		
		//종목분석 > 기업현황 > 연간 > financial Summary 실적 Tr
		WebElement enterpriseStateTbodyElement = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[14]/table[2]/tbody"));
		List<WebElement> enterpriseStateTrElementList = enterpriseStateTbodyElement.findElements(By.cssSelector("tr"));
		
		for(WebElement trElement:enterpriseStateTrElementList) {
			setEnterpriseStateBodyList(driver, trElement, enterpriseStatetStockFinancialVOList);
		}
		
		log.info(headerList);
		log.info(enterpriseStatetStockFinancialVOList);
		
		return enterpriseStatetStockFinancialVOList;		
	}
	
	public List<String> getEnterpriseStateHeaderList(ChromeDriver driver) throws Exception{
		ArrayList<String> headerList = new ArrayList<String>();
		//종목분석 > 기업현황 > 연간 > financial Summary Header TR
		WebElement enterpriseStateHeaderTrElement = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[14]/table[2]/thead/tr[2]"));		
		
		List<WebElement> enterpriseStateHeaderList = enterpriseStateHeaderTrElement.findElements(By.cssSelector("th"));
		
		for(WebElement headerElement : enterpriseStateHeaderList) {
			String title = headerElement.getText();
			String year = title.split("/")[0];
			if( year.matches("[+-]?\\d*(\\.\\d+)?")  && !ObjectUtils.isEmpty(year) )  //숫자여부판단
				headerList.add(year);
			
			log.info(year);
		}	
		return headerList;
	}
	
	public List<StockFinancialVO> geEnterpriseStatetStockFinancialVOList(List<String> headerList, StockVO stockVO) throws Exception {
		ArrayList<StockFinancialVO> stockFinancialList = new ArrayList<StockFinancialVO>();
		for(String year : headerList) {
			StockFinancialVO stockFinancialVO = StockFinancialVO.builder().build();
			stockFinancialVO.setYear(year);
			stockFinancialVO.setStockId(stockVO.getStockId());
			stockFinancialVO.setStockMasterId(stockVO.getId());
			
			StockFinancialVO resultStockFinancialVO = stockService.getStockFinancialVO(stockFinancialVO);
			
			if(resultStockFinancialVO == null || ObjectUtils.isEmpty(resultStockFinancialVO.getId())) {
				stockFinancialList.add(stockFinancialVO);
			}else {
				stockFinancialList.add(resultStockFinancialVO);
			}			
		}
		return stockFinancialList;
	}
	
	public List<StockFinancialVO> setEnterpriseStateBodyList(ChromeDriver driver, WebElement trElement, List<StockFinancialVO> stockFinancialList) throws Exception {
		Map<String, String> headerAttributeMapper = getHeaderAttributeMapper();
		
		WebElement thElement = trElement.findElement(By.cssSelector("th"));
		List<WebElement> tdElementList = trElement.findElements(By.cssSelector("td"));
		String text = thElement.getText();
		String attributeName = headerAttributeMapper.get(text);
		if(attributeName==null) 
			return stockFinancialList;
		
		for (int i = 0; i < tdElementList.size(); i++) {
			StockFinancialVO stockFinancialVO = stockFinancialList.get(i);
			Class<?> clazz = stockFinancialVO.getClass();
			
			// 값 셋팅
			String value = tdElementList.get(i).getText();
			Field field = clazz.getDeclaredField(attributeName);
			field.setAccessible(true);			
			field.set(stockFinancialVO, value);			
		}
		
		//System.out.println("stockFinancialList: " + stockFinancialList);
		return stockFinancialList;
	}
	
	public Map<String, String> getHeaderAttributeMapper() {
		Map<String, String> headerAttributeMapperMap = new HashMap<String, String>();
		headerAttributeMapperMap.put("매출액", "totalSales");
		headerAttributeMapperMap.put("영업이익", "profits");
		headerAttributeMapperMap.put("당기순이익", "earnings");
		headerAttributeMapperMap.put("영업이익률", "profitsRatio");
		headerAttributeMapperMap.put("순이익률", "netProfitRatio");
		headerAttributeMapperMap.put("ROE(%)", "roe");
		headerAttributeMapperMap.put("부채비율", "deptRatio");
		headerAttributeMapperMap.put("자본유보율", "reserveRatio");
		headerAttributeMapperMap.put("EPS(원)", "eps");
		headerAttributeMapperMap.put("PER(배)", "per");		
		headerAttributeMapperMap.put("BPS(원)", "bps");
		headerAttributeMapperMap.put("PBR(배)", "pbr");
		headerAttributeMapperMap.put("현금DPS(원)", "dividendsPerShare");
		headerAttributeMapperMap.put("현금배당수익률", "dividendsRate");
		headerAttributeMapperMap.put("현금배당성향(%)", "dividendsTendency");
		headerAttributeMapperMap.put("자산총계", "totalAssets");
		headerAttributeMapperMap.put("발행주식수(보통주)", "sharesOutstanding");
		headerAttributeMapperMap.put("부채총계", "totalDept");
		headerAttributeMapperMap.put("자본총계", "totalCapital");
		headerAttributeMapperMap.put("자본금", "capital");
		return headerAttributeMapperMap;
	}
	
	
	public void getFinancialStatus(ChromeDriver driver, StockVO stockVO) throws Exception {
		//종목분석 > 재무분석				
		WebElement financialAnalysisElement = driver.findElement(By.cssSelector("#header-menu div.wrapper-menu  dl  dt:nth-child(3)  a"));
		financialAnalysisElement.click();
		Thread.sleep(1000);
		//종목분석 > 재무분석 > 재무상태표
		WebElement financialStatusElement = driver.findElement(By.xpath("//*[@id=\"rpt_tab2\"]"));
		financialStatusElement.click();
		Thread.sleep(1000);
		List<String> headerList = getHeaderList(driver);
		log.info(headerList);
	}
	
	public List<String> getHeaderList(ChromeDriver driver) throws Exception{
		ArrayList<String> headerList = new ArrayList<String>();
		//종목분석 > 재무분석 > 재무상태표 > Header TR
		WebElement financialStatusHeaderTrElement = driver.findElement(By.xpath("/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[5]/table[2]/thead/tr"));
		
		
		List<WebElement> financialStatusHeaderList = financialStatusHeaderTrElement.findElements(By.cssSelector("th div"));
		
		for(WebElement headerElement : financialStatusHeaderList) {
			String title = headerElement.getText();
			String year = title.split("/")[0];
			if( year.matches("[+-]?\\d*(\\.\\d+)?")  && !ObjectUtils.isEmpty(year) )  //숫자여부판단
				headerList.add(year);
			
			log.info(year);
		}	
		return headerList;
	}
	
}
