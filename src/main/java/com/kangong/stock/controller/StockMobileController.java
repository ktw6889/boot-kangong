package com.kangong.stock.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kangong.board.model.BoardVO;
import com.kangong.stock.model.StockMarketIndexVO;
import com.kangong.stock.service.StockMobileCategory;
import com.kangong.stock.service.StockMobileEsgService;
import com.kangong.stock.service.StockMobileFinancialService;
import com.kangong.stock.service.StockMobileMarketIndexService;
import com.kangong.stock.service.StockMobileService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class StockMobileController {

	private static final String STOCK_LIST_VIEW = "kims:/stock/stockList";

	@Autowired
	StockMobileService stockMobileService;

	@Autowired
	StockMobileFinancialService stockMobileFinancial;

	@Autowired
	StockMobileCategory stockMobileCategory;

	@Autowired
	StockMobileEsgService stockMobileEsgService;

	@Autowired
	StockMobileMarketIndexService stockMobileMarketIndexService;

	@Value("classpath:data/sample-stock-data.json")
	private Resource sampleStockDataResource;

	@FunctionalInterface
	private interface ServiceAction {
		void execute() throws Exception;
	}

	private String delegateToService(ServiceAction action) throws Exception {
		action.execute();
		return STOCK_LIST_VIEW;
	}

	// ==================== 위임 핸들러 ====================

	@RequestMapping(value = "/stockMobile/updateDailyStock")
	public String updateDailyStock() throws Exception {
		return delegateToService(stockMobileService::updateDailyStock);
	}

	@RequestMapping(value = "/stockMobile/saveSimpleList")
	public String saveSimpleStockList() throws Exception {
		return delegateToService(stockMobileService::saveSimpleStockList);
	}

	@RequestMapping(value = "/stockMobile/updateDaily")
	public String updateDaily() throws Exception {
		return delegateToService(stockMobileService::updateDailyInfo);
	}

	@RequestMapping(value = "/stockMobile/saveAllStockId")
	public String saveAllStockId() throws Exception {
		return delegateToService(stockMobileService::saveAllStockId);
	}

	@RequestMapping(value = "/stockMobile/save")
	public String saveStock(@RequestParam("stockId") String stockId) throws Exception {
		return delegateToService(() -> stockMobileService.saveStock(stockId));
	}

	@RequestMapping(value = "/stockMobile/saveAll")
	public String saveAllStock() throws Exception {
		return delegateToService(stockMobileService::saveAllStock);
	}

	@RequestMapping(value = "/stockMobile/saveDaillyPriceMig")
	public String saveDaillyPriceAllMig() throws Exception {
		return delegateToService(stockMobileService::saveDailyPriceAllMigration);
	}

	@RequestMapping(value = "/stockMobile/saveDaillyPrice")
	public String saveDaillyPrice(@RequestParam("stockId") String stockId) throws Exception {
		return delegateToService(() -> stockMobileService.saveDailyPrice(stockId, 1));
	}

	@RequestMapping(value = "/stockMobile/saveStockFinancial")
	public String saveStockFinancial(@RequestParam("stockId") String stockId) throws Exception {
		return delegateToService(() -> stockMobileFinancial.saveStockFinancial(stockId));
	}

	@RequestMapping(value = "/stockMobile/saveStockFinancialAll")
	public String saveStockFinancialAll() throws Exception {
		return delegateToService(stockMobileFinancial::saveStockFinancialAll);
	}

	@RequestMapping(value = "/stockMobile/saveStockCategory")
	public String saveStockCategory() throws Exception {
		return delegateToService(stockMobileCategory::saveStockCategory);
	}

	@RequestMapping(value = "/stockMobile/saveStockEsg")
	public String saveStockEsg(@RequestParam("stockId") String stockId) throws Exception {
		return delegateToService(() -> stockMobileEsgService.saveStockEsg(stockId));
	}

	@RequestMapping(value = "/stockMobile/saveStockEsgAll")
	public String saveStockEsgAll() throws Exception {
		return delegateToService(stockMobileEsgService::saveStockEsgAll);
	}

	@RequestMapping(value = "/stockMobile/saveStockMarketIndex")
	public String saveStockMarketIndex() throws Exception {
		return delegateToService(stockMobileMarketIndexService::saveStockMarketIndex);
	}

	// ==================== 개별 핸들러 ====================

	@RequestMapping(value = "/stockMobile/index/list")
	public String stockIndexList(Model model, StockMarketIndexVO indexVO) throws Exception {
		log.info("indexList");
		List<StockMarketIndexVO> resultList = stockMobileMarketIndexService.getIndexList(indexVO);
		model.addAttribute("indexList", resultList);
		model.addAttribute("activeMenu", "indexList");
		return "kims:/stock/stockIndexList";
	}

	@ResponseBody
	@RequestMapping(value = "/stockMobile/jsonData")
	public String getJsonData(BoardVO boardVO) throws Exception {
		return StreamUtils.copyToString(sampleStockDataResource.getInputStream(), StandardCharsets.UTF_8);
	}

}
