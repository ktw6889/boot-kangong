package com.kangong.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class StockController {
	
	@Autowired
	StockService stockService;
	

	@RequestMapping(value = "/stock")
	public String saveStock(Model model) throws Exception {
		return "kims:/stock/stockList";
	}

	@RequestMapping(value = "/stock/daily/update")
	public String updateDaily(Model model) throws Exception {
		stockService.saveStockList();
		stockService.saveDailyPriceList();
		
		return "kims:/stock/stockList";
	}
	
	
	@RequestMapping(value = "/stock/list/save")
	public String saveStockList(Model model) throws Exception {
		List<StockVO> stockList = stockService.saveStockList();
		System.out.println(stockList);
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/save")
	public String saveStock(Model model, @RequestParam("stockId") String stockId) throws Exception {
		stockService.saveStock(stockId);
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/financial/saveAll")
	public String saveFinancial(Model model) throws Exception {
		stockService.saveFinancialList();
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/financial/save")
	public String saveFinancial(Model model, @RequestParam("stockId") String stockId) throws Exception {
		stockService.saveStockFinancial(stockId);
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/financial2/saveAll")
	public String saveFinancial2(Model model) throws Exception {
		stockService.saveFinancialList2();
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/financial2/save")
	public String saveFinancial2(Model model, @RequestParam("stockId") String stockId) throws Exception {
		stockService.saveStockFinancial2(stockId);
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/category/save")
	public String saveCategoryList(Model model) throws Exception {
		stockService.saveStockCategoryList();
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/dailyPrice/save")
	public String saveDailyPrice(Model model, @RequestParam("stockId") String stockId) throws Exception {
		stockService.saveDailyPrice(stockId);
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/dailyPrice/saveAll")
	public String saveDailyPriceAll(Model model) throws Exception {
		stockService.saveDailyPriceList();
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/selenium/financial/save")
	public String saveSeleniumFinancial(Model model, @RequestParam("stockId") String stockId) throws Exception {
		try {
		stockService.saveSeleniumStockFinancial(stockId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/selenium/financial/saveAll")
	public String saveSeleniumFinancialAll(Model model) throws Exception {
		try {
		stockService.saveSeleniumStockFinancialAll();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/selenium/financialAnalysis/save")
	public String saveSeleniumFinancialSecond(Model model, @RequestParam("stockId") String stockId) throws Exception {
		try {
		stockService.saveSeleniumStockFinancialAnalysis(stockId);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/selenium/financialAnalysis/saveAll")
	public String saveSeleniumFinancialSecondAll(Model model) throws Exception {
		try {
		stockService.saveSeleniumStockFinancialAnalysisAll();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "kims:/stock/stockList";
	}

}
