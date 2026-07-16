package com.kangong.stock.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.kangong.stock.config.StockSeleniumSelectorConfig;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockFieldMappingRegistry;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class StockSeleniumParser extends AbstractStockDataParser {

	private final StockSeleniumSelectorConfig selectorConfig;

	@Value("${stock.selenium.driver-id:webdriver.chrome.driver}")
	private String webDriverId;

	@Value("${stock.selenium.driver-path:D:\\\\Dev\\\\Program\\\\chromedriver.exe}")
	private String webDriverPath;

	@Value("${stock.selenium.wait-seconds:10}")
	private long waitSeconds;

	private ChromeDriver driver;
	private WebDriverWait driverWait;

	// ==================== ChromeDriver 관리 ====================

	public ChromeDriver getChromeDriver() {
		if (driver == null) {
			System.setProperty(webDriverId, webDriverPath);
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless");
			options.addArguments("--no-sandbox");
			driver = new ChromeDriver(options);
			driverWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(waitSeconds));
		}
		return driver;
	}

	@PreDestroy
	public void closeDriver() {
		if (driver != null) {
			driver.quit();
			driver = null;
			driverWait = null;
		}
	}

	// ==================== Selenium 대기 유틸리티 ====================

	private WebElement waitForElement(By by) {
		return driverWait.until(ExpectedConditions.presenceOfElementLocated(by));
	}

	private void waitAndClick(By by) {
		driverWait.until(ExpectedConditions.elementToBeClickable(by)).click();
	}

	private void waitForFrameAndSwitch(String frameName) {
		driverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
	}

	// ==================== 배당수익률 조회 ====================

	public StockVO getStockDividendInfo(StockVO stockVO) throws Exception {
		getChromeDriver().get(selectorConfig.getNaverFinanceUrl() + stockVO.getStockId());

		waitAndClick(By.xpath(selectorConfig.getAnalysisTab()));
		waitForFrameAndSwitch(selectorConfig.getIframeCoinfo());

		WebElement dividendElement = waitForElement(By.xpath(selectorConfig.getDividendValue()));
		stockVO.setDividendRate(dividendElement.getText().replaceAll("[^0-9.-]", ""));

		return stockVO;
	}

	// ==================== 기업현황 재무제표 ====================

	public List<StockFinancialVO> getFinancialEnterpriseState(StockVO stockVO,
															  Function<StockFinancialVO, StockFinancialVO> financialLookup) throws Exception {
		getChromeDriver().get(selectorConfig.getNaverFinanceUrl() + stockVO.getStockId());

		waitAndClick(By.xpath(selectorConfig.getAnalysisTab()));
		waitForFrameAndSwitch(selectorConfig.getIframeCoinfo());
		waitAndClick(By.cssSelector(selectorConfig.getEnterpriseState()));
		waitAndClick(By.xpath(selectorConfig.getAnnualTab()));

		List<String> headerList = parseSeleniumHeaderList(selectorConfig.getEnterpriseHeader(), "th");
		List<StockFinancialVO> voList = createFinancialVOListWithLookup(headerList, stockVO, financialLookup);

		WebElement tbody = waitForElement(By.xpath(selectorConfig.getEnterpriseTbody()));
		List<WebElement> rows = tbody.findElements(By.cssSelector("tr"));

		Map<String, String> mapper = StockFieldMappingRegistry.financialHtmlTable();
		for (WebElement row : rows) {
			parseSeleniumBodyRow(row, voList, mapper, "th");
		}

		log.info(headerList);
		log.info(voList);
		return voList;
	}

	// ==================== 재무분석 재무상태표 ====================

	public List<StockFinancialVO> getFinancialAnalysis(StockVO stockVO,
													   Function<StockFinancialVO, StockFinancialVO> financialLookup) throws Exception {
		getChromeDriver().get(selectorConfig.getNaverFinanceUrl() + stockVO.getStockId());

		waitAndClick(By.xpath(selectorConfig.getAnalysisTab()));
		waitForFrameAndSwitch(selectorConfig.getIframeCoinfo());
		waitAndClick(By.xpath(selectorConfig.getFinancialAnalysis()));
		waitAndClick(By.xpath(selectorConfig.getBalanceSheetTab()));

		List<String> headerList = parseSeleniumHeaderList(selectorConfig.getBalanceSheetHeader(), "th div");
		List<StockFinancialVO> voList = createFinancialVOListWithLookup(headerList, stockVO, financialLookup);

		WebElement tbody = waitForElement(By.xpath(selectorConfig.getBalanceSheetTbody()));
		List<WebElement> rows = tbody.findElements(By.cssSelector("tr"));

		Map<String, String> mapper = StockFieldMappingRegistry.balanceSheetHtmlTable();
		for (WebElement row : rows) {
			parseSeleniumAnalysisBodyRow(row, voList, mapper);
		}

		return voList;
	}

	// ==================== 공통 Selenium 파싱 로직 ====================

	private List<String> parseSeleniumHeaderList(String headerXPath,
												 String cellSelector) throws Exception {
		List<String> headerList = new ArrayList<>();
		WebElement headerRow = waitForElement(By.xpath(headerXPath));
		List<WebElement> headerCells = headerRow.findElements(By.cssSelector(cellSelector));

		for (WebElement cell : headerCells) {
			String text = "th div".equals(cellSelector)
					? cell.getAttribute("innerHTML")
					: cell.getText();
			String year = text.split("/")[0];
			if (isNumeric(year) && !ObjectUtils.isEmpty(year)) {
				headerList.add(year);
				log.info(year);
			}
		}
		return headerList;
	}

	private List<StockFinancialVO> createFinancialVOListWithLookup(List<String> headerList, StockVO stockVO,
																   Function<StockFinancialVO, StockFinancialVO> financialLookup) throws Exception {
		List<StockFinancialVO> list = new ArrayList<>();
		for (String year : headerList) {
			StockFinancialVO vo = StockFinancialVO.builder().build();
			vo.setYear(year);
			vo.setStockId(stockVO.getStockId());
			vo.setStockMasterId(stockVO.getId());

			if (financialLookup != null) {
				StockFinancialVO existing = financialLookup.apply(vo);
				if (existing != null && !ObjectUtils.isEmpty(existing.getId())) {
					list.add(existing);
					continue;
				}
			}
			list.add(vo);
		}
		return list;
	}

	private void parseSeleniumBodyRow(WebElement row, List<StockFinancialVO> voList,
									  Map<String, String> mapper, String headerSelector) throws Exception {
		WebElement th = row.findElement(By.cssSelector(headerSelector));
		List<WebElement> tdList = row.findElements(By.cssSelector("td"));
		String headerText = th.getText();
		String fieldName = mapper.get(headerText);
		if (fieldName == null) return;

		for (int i = 0; i < tdList.size() && i < voList.size(); i++) {
			setFieldValue(voList.get(i), fieldName, tdList.get(i).getText());
		}
	}

	private void parseSeleniumAnalysisBodyRow(WebElement row, List<StockFinancialVO> voList,
											  Map<String, String> mapper) throws Exception {
		WebElement titleTd = row.findElement(By.cssSelector("td[title]"));
		List<WebElement> tdList = row.findElements(By.cssSelector("td"));
		String headerText = titleTd.getText().trim();
		String fieldName = mapper.get(headerText);
		if (fieldName == null) return;

		for (int i = 1; i < tdList.size() - 2 && (i - 1) < voList.size(); i++) {
			setFieldValue(voList.get(i - 1), fieldName, tdList.get(i).getText().trim());
		}
	}
}
