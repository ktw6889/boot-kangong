package com.kangong.stock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "stock.selenium.selector")
public class StockSeleniumSelectorConfig {

	private String naverFinanceUrl = "https://finance.naver.com/item/main.naver?code=";
	private String iframeCoinfo = "coinfo_cp";

	private String analysisTab = "/html/body/div[3]/div[2]/div[2]/div[1]/ul/li[6]/a";
	private String dividendValue = "/html/body/div/form/div[1]/div/div[2]/div[1]/div/table/tbody/tr[3]/td/dl/dt[6]/b";

	private String enterpriseState = "#header-menu > div.wrapper-menu > dl > dt.on > a";
	private String annualTab = "//*[@id=\"cns_Tab21\"]";
	private String enterpriseHeader = "/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[14]/table[2]/thead/tr[2]";
	private String enterpriseTbody = "/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[14]/table[2]/tbody";

	private String financialAnalysis = "/html/body/div/form/div[1]/div/div[2]/div[1]/dl/dt[3]/a";
	private String balanceSheetTab = "//*[@id=\"rpt_tab2\"]";
	private String balanceSheetHeader = "/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[5]/table[2]/thead/tr";
	private String balanceSheetTbody = "/html/body/div/form/div[1]/div/div[2]/div[3]/div/div/div[5]/table[2]/tbody";
}
