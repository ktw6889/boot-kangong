package com.kangong.stock.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.util.StringUtils;

import com.kangong.stock.model.MacroAdjCoeff;
import com.kangong.stock.model.MacroIndicatorVO;
import com.kangong.stock.model.TrendSignalVO;
import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockInterestParamVO;
import com.kangong.stock.model.StockInterestVO;
import com.kangong.stock.model.StockRecommendVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.model.StockValueScreenVO;
import com.kangong.advstock.parser.AdvStockParser;
import com.kangong.advstock.service.AdvStockYahooService;
import com.kangong.stock.service.MacroIndicatorService;
import com.kangong.stock.service.StockService;
import com.kangong.marketcycle.dto.MarketCycleDashboard;
import com.kangong.marketcycle.service.MarketCycleService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class StockController {

	@Autowired
	StockService stockService;

	@Autowired
	AdvStockParser advStockParser;

	@Autowired
	AdvStockYahooService advStockYahooService;

	@Autowired
	MacroIndicatorService macroIndicatorService;

	@Autowired
	MarketCycleService marketCycleService;


	@RequestMapping(value = "/stock2/macro")
	public String macroIndicator(Model model) {
		model.addAttribute("activeMenu", "macroIndicator");
		return "kims:/stock/stockMacroIndicator";
	}

	@RequestMapping(value = "/stock2/portfolio")
	public String portfolio(Model model) {
		List<StockInterestVO> interestList = new ArrayList<>();
		List<StockInterestParamVO> divisions = new ArrayList<>();
		try {
			divisions = stockService.getStockDivisions();
			interestList = stockService.getStockInterestList(new StockInterestVO());
		} catch (Exception e) {
			log.error("자산배분 대시보드 조회 오류", e);
			model.addAttribute("errorMsg", e.getMessage());
		}
		try {
			MarketCycleDashboard dashboard = marketCycleService.buildDashboard();
			model.addAttribute("cycleDashboard", dashboard);
		} catch (Exception e) {
			log.warn("사계론 국면 로드 실패: {}", e.getMessage());
		}
		try {
			MacroAdjCoeff adjCoeff = macroIndicatorService.computeStockTypeAdjCoeff();
			model.addAttribute("macroAdjCoeff", adjCoeff);
		} catch (Exception e) {
			log.warn("매크로 조정계수 로드 실패: {}", e.getMessage());
		}
		try {
			TrendSignalVO trendSignal = macroIndicatorService.computeTrendSignal();
			model.addAttribute("trendSignal", trendSignal);
		} catch (Exception e) {
			log.warn("200MA 추세 신호 로드 실패: {}", e.getMessage());
		}
		model.addAttribute("interestList", interestList);
		model.addAttribute("divisions", divisions);
		model.addAttribute("activeMenu", "portfolio");
		return "kims:/stock/portfolioMain";
	}

	@RequestMapping(value = "/stock2/api/interest")
	@ResponseBody
	@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:15007", methods = org.springframework.web.bind.annotation.RequestMethod.GET)
	public Map<String, Object> apiInterest() throws Exception {
		List<StockInterestParamVO> divisions = stockService.getStockDivisions();
		List<StockInterestVO> interestList = stockService.getStockInterestList(new StockInterestVO());
		Map<String, Object> result = new java.util.LinkedHashMap<>();
		result.put("divisions", divisions);
		result.put("interestList", interestList);
		return result;
	}

	@RequestMapping(value = "/stock2/api/recommend")
	@ResponseBody
	@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:15007", methods = org.springframework.web.bind.annotation.RequestMethod.GET)
	public Map<String, Object> apiRecommend(@RequestParam(value = "stockDivision", required = false) String stockDivision) throws Exception {
		List<StockRecommendVO> buyList = new ArrayList<>();
		List<StockRecommendVO> sellList = new ArrayList<>();
		StockInterestVO param = new StockInterestVO();
		param.setStockDivision(stockDivision);
		List<StockInterestVO> interestList = stockService.getStockInterestList(param);

		for (StockInterestVO row : interestList) {
			StockRecommendVO rec = StockRecommendVO.from(row);
			if (rec.getAddPrice() > 0) {
				buyList.add(rec);
			} else if (rec.getAddPrice() < 0) {
				sellList.add(rec);
			}
		}
		buyList.sort(Comparator.comparingDouble(StockRecommendVO::getScore).reversed());
		sellList.sort(Comparator.comparingDouble(StockRecommendVO::getScore).reversed());

		Map<String, Object> result = new java.util.LinkedHashMap<>();
		result.put("buyList", buyList);
		result.put("sellList", sellList);
		return result;
	}

	@RequestMapping(value = "/stock2/macro/data")
	@ResponseBody
	public List<MacroIndicatorVO> macroData(@RequestParam("category") String category) {
		return macroIndicatorService.fetchByCategory(category);
	}

	@RequestMapping(value = "/stock2/macro/chart")
	@ResponseBody
	public Map<String, Object> macroChartData(@RequestParam("category") String category) {
		return macroIndicatorService.fetchChartData(category);
	}

	@RequestMapping(value = "/stock2/macro/signals")
	@ResponseBody
	public Map<String, Object> macroSignals() {
		Map<String, Object> result = macroIndicatorService.fetchAllSignals();
		try {
			List<StockInterestParamVO> divisions = stockService.getStockDivisions();
			List<StockInterestVO> interestList = stockService.getStockInterestList(new StockInterestVO());
			@SuppressWarnings("unchecked")
			List<Map<String, String>> assetSignals = (List<Map<String, String>>) result.get("assetSignals");

			MarketCycleDashboard dashboard = marketCycleService.buildDashboard();
			result.put("marketCycle", buildMarketCycleResponse(dashboard));

			result.put("portfolioAllocation",
					macroIndicatorService.computePortfolioAllocation(divisions, interestList, assetSignals, dashboard));
		} catch (Exception e) {
			log.warn("포트폴리오 기반 배분 계산 실패: {}", e.getMessage());
		}
		return result;
	}

	private Map<String, Object> buildMarketCycleResponse(MarketCycleDashboard d) {
		Map<String, Object> m = new java.util.LinkedHashMap<>();
		m.put("phase",         d.getCurrentPhase().name());
		m.put("phaseLabel",    d.getCurrentPhase().getLabel());
		m.put("phaseColor",    d.getCurrentPhase().getColor());
		m.put("phaseSeason",   d.getCurrentPhase().getSeason());
		m.put("progressPercent", d.getProgressPercent());
		m.put("nextPhase",     d.getNextPhase() != null ? d.getNextPhase().name() : null);
		m.put("nextPhaseLabel", d.getNextPhase() != null ? d.getNextPhase().getLabel() : null);
		m.put("diagnosisSummary", d.getDiagnosisSummary());
		m.put("assetOverviews", d.getAssetOverviews());
		return m;
	}

	@RequestMapping(value = "/stock2/value")
	public String stockValueScreen(Model model,
			@RequestParam(value = "perMax", required = false) Double perMax,
			@RequestParam(value = "pbrMax", required = false) Double pbrMax,
			@RequestParam(value = "roeMin", required = false) Double roeMin,
			@RequestParam(value = "dividendMin", required = false) Double dividendMin,
			@RequestParam(value = "debtMax", required = false) Double debtMax,
			@RequestParam(value = "marketCapMin", required = false) Integer marketCapMin,
			@RequestParam(value = "passCountMin", required = false) Integer passCountMin) {
		StockValueScreenVO param = StockValueScreenVO.builder()
				.filterPerMax(perMax != null ? perMax : 10.0)
				.filterPbrMax(pbrMax != null ? pbrMax : 0.8)
				.filterRoeMin(roeMin != null ? roeMin : 8.0)
				.filterDividendMin(dividendMin != null ? dividendMin : 3.5)
				.filterDebtMax(debtMax != null ? debtMax : 100.0)
				.filterMarketCapMin(marketCapMin)
				.filterPassCountMin(passCountMin != null ? passCountMin : 3)
				.build();
		List<StockValueScreenVO> screenList = stockService.getValueScreenList(param);
		calculateBuySellProbability(screenList, param);
		model.addAttribute("screenList", screenList);
		model.addAttribute("filter", param);
		model.addAttribute("activeMenu", "valueScreen");
		return "kims:/stock/stockValueScreen";
	}

	@RequestMapping(value = "/stock2/esg")
	public String stockEsgList(Model model,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "year", required = false) String year,
			@RequestParam(value = "searched", required = false) String searched) {
		if (searched == null && (year == null || year.isEmpty())) {
			year = "2024";
		}
		StockEsgVO param = StockEsgVO.builder()
				.stockId(keyword != null && !keyword.isEmpty() ? keyword : null)
				.year(year != null && !year.isEmpty() ? year : null)
				.build();
		List<StockEsgVO> esgList = stockService.getStockEsgList(param);
		model.addAttribute("esgList", esgList);
		model.addAttribute("searchKeyword", keyword);
		model.addAttribute("searchYear", year);
		model.addAttribute("activeMenu", "esgList");
		return "kims:/stock/stockEsgList";
	}

	@RequestMapping(value = "/stock")
	public String saveStock(Model model) throws Exception {
		model.addAttribute("activeMenu", "stockList");
		return "kims:/stock/stockList";
	}

	@RequestMapping(value = "/stock2")
	public String stockInterestList(Model model,
			@RequestParam(value = "stockDivision", required = false) String stockDivision) throws Exception {
		List<StockInterestVO> interestList = new ArrayList<>();
		List<StockInterestParamVO> divisions = new ArrayList<>();
		try {
			divisions = stockService.getStockDivisions();
			StockInterestVO param = new StockInterestVO();
			param.setStockDivision(stockDivision);
			interestList = stockService.getStockInterestList(param);
		} catch (Exception e) {
			log.error("포트폴리오 현황 조회 오류", e);
			model.addAttribute("errorMsg", e.getMessage());
		}
		try {
			MarketCycleDashboard dashboard = marketCycleService.buildDashboard();
			model.addAttribute("cycleDashboard", dashboard);
		} catch (Exception e) {
			log.warn("사계론 국면 로드 실패: {}", e.getMessage());
		}
		model.addAttribute("interestList", interestList);
		model.addAttribute("divisions", divisions);
		model.addAttribute("selectedDivision", stockDivision);
		model.addAttribute("activeMenu", "interestList");
		return "kims:/stock/stockInterestList";
	}

	@RequestMapping(value = "/stock2/edit")
	public String stockInterestEdit(Model model,
			@RequestParam(value = "stockDivision", required = false) String stockDivision) throws Exception {
		List<StockInterestVO> rawList = new ArrayList<>();
		List<StockInterestParamVO> divisions = new ArrayList<>();
		try {
			divisions = stockService.getStockDivisions();
			StockInterestVO param = new StockInterestVO();
			param.setStockDivision(stockDivision);
			rawList = stockService.getStockInterestRaw(param);
		} catch (Exception e) {
			log.error("포트폴리오 수정 조회 오류", e);
			model.addAttribute("errorMsg", e.getMessage());
		}
		model.addAttribute("rawList", rawList);
		model.addAttribute("divisions", divisions);
		model.addAttribute("selectedDivision", stockDivision);
		model.addAttribute("activeMenu", "interestEdit");
		return "kims:/stock/stockInterestEdit";
	}

	@RequestMapping(value = "/stock2/interest/save")
	@ResponseBody
	public String saveStockInterest(StockInterestVO vo) throws Exception {
		StockVO masterVO = stockService.getStockVO(vo.getStockId());
		if (masterVO != null) {
			vo.setName(masterVO.getName());
		} else {
			StockVO newVO = StockVO.builder().stockId(vo.getStockId()).name(vo.getStockId()).price("0").marketCapitalization("0").build();
			stockService.saveStockMasterMin(newVO);
		}
		if (StringUtils.hasText(vo.getOldDivision()) && !vo.getOldDivision().equals(vo.getStockDivision())) {
			StockInterestVO oldVO = StockInterestVO.builder().stockId(vo.getStockId()).stockDivision(vo.getOldDivision()).build();
			stockService.deleteStockInterest(oldVO);
		}
		stockService.saveStockInterest(vo);
		if (vo.getStockId().matches("[A-Za-z][A-Za-z0-9.\\-]*")) {
			try {
				advStockYahooService.syncSingleUsStock(vo.getStockId());
			} catch (Exception e) {
				log.warn("US 주식 단가 동기화 실패 (저장은 완료): stockId={}, error={}", vo.getStockId(), e.getMessage());
			}
		}
		return "OK";
	}

	@RequestMapping(value = "/stock2/interest/delete")
	@ResponseBody
	public String deleteStockInterest(StockInterestVO vo) throws Exception {
		stockService.deleteStockInterest(vo);
		return "OK";
	}

	@RequestMapping(value = "/stock2/param/save")
	@ResponseBody
	public String saveStockInterestParam(StockInterestParamVO vo) throws Exception {
		stockService.saveStockInterestParam(vo);
		return "OK";
	}

	@RequestMapping(value = "/stock2/param/delete")
	@ResponseBody
	public String deleteStockInterestParam(StockInterestParamVO vo) throws Exception {
		stockService.deleteStockInterestParam(vo);
		return "OK";
	}

	@RequestMapping(value = "/stock2/searchStock")
	@ResponseBody
	public List<StockVO> searchStock(@RequestParam("keyword") String keyword) throws Exception {
		List<StockVO> result = advStockParser.searchStock(keyword);
		if (!result.isEmpty()) return result;
		return stockService.searchStockByName(keyword);
	}

	@RequestMapping(value = "/stock2/recommend")
	public String stockRecommend(Model model,
			@RequestParam(value = "stockDivision", required = false) String stockDivision) throws Exception {
		List<StockRecommendVO> buyList = new ArrayList<>();
		List<StockRecommendVO> sellList = new ArrayList<>();
		List<StockInterestParamVO> divisions = new ArrayList<>();
		try {
			divisions = stockService.getStockDivisions();
			StockInterestVO param = new StockInterestVO();
			param.setStockDivision(stockDivision);
			List<StockInterestVO> interestList = stockService.getStockInterestList(param);

			for (StockInterestVO row : interestList) {
				StockRecommendVO rec = StockRecommendVO.from(row);
				if (rec.getAddPrice() > 0) {
					buyList.add(rec);
				} else if (rec.getAddPrice() < 0) {
					sellList.add(rec);
				}
			}
			buyList.sort(Comparator.comparingDouble(StockRecommendVO::getScore).reversed());
			sellList.sort(Comparator.comparingDouble(StockRecommendVO::getScore).reversed());
		} catch (Exception e) {
			log.error("리밸런싱 추천 조회 오류", e);
			model.addAttribute("errorMsg", e.getMessage());
		}
		model.addAttribute("buyList", buyList);
		model.addAttribute("sellList", sellList);
		model.addAttribute("divisions", divisions);
		model.addAttribute("selectedDivision", stockDivision);
		model.addAttribute("activeMenu", "recommend");
		return "kims:/stock/stockRecommend";
	}

	@RequestMapping(value = "/stock2/excel")
	public void exportExcel(@RequestParam(value = "stockDivision", required = false) String stockDivision,
							HttpServletResponse response) throws Exception {
		StockInterestVO param = new StockInterestVO();
		param.setStockDivision(stockDivision);
		List<StockInterestVO> interestList = stockService.getStockInterestList(param);

		String fileName = "portfolio_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("포트폴리오");

			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);

			CellStyle numberStyle = workbook.createCellStyle();
			numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

			CellStyle rateStyle = workbook.createCellStyle();
			rateStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00\"%\""));

			CellStyle percentStyle = workbook.createCellStyle();
			percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.0\"%\""));

			String[] headers = {"계좌", "종목명", "종목코드", "수량", "평가금액", "기준금액",
					"추가필요", "비중(%)", "실제비중(%)", "순위", "52주최고", "52주최저", "52주비교율(%)",
					"1개월(%)", "3개월(%)", "6개월(%)", "12개월(%)"};
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			CellStyle boldStyle = workbook.createCellStyle();
			Font boldFont = workbook.createFont();
			boldFont.setBold(true);
			boldStyle.setFont(boldFont);

			CellStyle boldNumberStyle = workbook.createCellStyle();
			boldNumberStyle.cloneStyleFrom(numberStyle);
			boldNumberStyle.setFont(boldFont);

			String prevDivision = "";
			long subTotalPrice = 0, subStandard = 0, subAddPrice = 0;
			long grandTotalPrice = 0, grandStandard = 0, grandAddPrice = 0;
			int rowIdx = 1;

			for (int i = 0; i < interestList.size(); i++) {
				StockInterestVO row = interestList.get(i);

				if (!prevDivision.isEmpty() && !prevDivision.equals(row.getStockDivision())) {
					rowIdx = writeSubtotalRow(sheet, rowIdx, prevDivision + " 소계",
							subTotalPrice, subStandard, subAddPrice, boldStyle, boldNumberStyle);
					subTotalPrice = 0;
					subStandard = 0;
					subAddPrice = 0;
				}

				subTotalPrice += row.getTotalPrice();
				subStandard += row.getStandard();
				subAddPrice += row.getAddPrice();
				grandTotalPrice += row.getTotalPrice();
				grandStandard += row.getStandard();
				grandAddPrice += row.getAddPrice();

				Row dataRow = sheet.createRow(rowIdx++);
				dataRow.createCell(0).setCellValue(row.getStockDivision());
				dataRow.createCell(1).setCellValue(row.getName());
				dataRow.createCell(2).setCellValue(row.getStockId());

				Cell qtyCell = dataRow.createCell(3);
				qtyCell.setCellValue(row.getQty());
				qtyCell.setCellStyle(numberStyle);

				Cell totalCell = dataRow.createCell(4);
				totalCell.setCellValue(row.getTotalPrice());
				totalCell.setCellStyle(numberStyle);

				Cell stdCell = dataRow.createCell(5);
				stdCell.setCellValue(row.getStandard());
				stdCell.setCellStyle(numberStyle);

				Cell addCell = dataRow.createCell(6);
				addCell.setCellValue(row.getAddPrice());
				addCell.setCellStyle(numberStyle);

				Cell potionCell = dataRow.createCell(7);
				potionCell.setCellValue(row.getStockPotion());
				potionCell.setCellStyle(percentStyle);

				Cell actualPotionCell = dataRow.createCell(8);
				if (row.getActualPotion() != null) {
					actualPotionCell.setCellValue(row.getActualPotion());
					actualPotionCell.setCellStyle(percentStyle);
				} else {
					actualPotionCell.setCellValue("-");
				}

				dataRow.createCell(9).setCellValue(row.getRk());

				Cell max52Cell = dataRow.createCell(10);
				if (row.getMax52() != null && row.getMax52() > 0) {
					max52Cell.setCellValue(row.getMax52());
					max52Cell.setCellStyle(numberStyle);
				} else {
					max52Cell.setCellValue("-");
				}

				Cell min52Cell = dataRow.createCell(11);
				if (row.getMin52() != null && row.getMin52() > 0) {
					min52Cell.setCellValue(row.getMin52());
					min52Cell.setCellStyle(numberStyle);
				} else {
					min52Cell.setCellValue("-");
				}

				setRateCell(dataRow.createCell(12), row.getRate52(), rateStyle);
				setRateCell(dataRow.createCell(13), row.getMonth1Rate(), rateStyle);
				setRateCell(dataRow.createCell(14), row.getMonth3Rate(), rateStyle);
				setRateCell(dataRow.createCell(15), row.getMonth6Rate(), rateStyle);
				setRateCell(dataRow.createCell(16), row.getMonth12Rate(), rateStyle);

				prevDivision = row.getStockDivision();

				if (i == interestList.size() - 1) {
					rowIdx = writeSubtotalRow(sheet, rowIdx, prevDivision + " 소계",
							subTotalPrice, subStandard, subAddPrice, boldStyle, boldNumberStyle);
				}
			}

			if (!interestList.isEmpty()) {
				rowIdx = writeSubtotalRow(sheet, rowIdx, "전체 합계",
						grandTotalPrice, grandStandard, grandAddPrice, boldStyle, boldNumberStyle);
			}

			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(response.getOutputStream());
		}
	}

	private int writeSubtotalRow(Sheet sheet, int rowIdx, String label,
								 long totalPrice, long standard, long addPrice,
								 CellStyle boldStyle, CellStyle boldNumberStyle) {
		Row row = sheet.createRow(rowIdx++);

		Cell labelCell = row.createCell(0);
		labelCell.setCellValue(label);
		labelCell.setCellStyle(boldStyle);

		Cell tp = row.createCell(4);
		tp.setCellValue(totalPrice);
		tp.setCellStyle(boldNumberStyle);

		Cell st = row.createCell(5);
		st.setCellValue(standard);
		st.setCellStyle(boldNumberStyle);

		Cell ap = row.createCell(6);
		ap.setCellValue(addPrice);
		ap.setCellStyle(boldNumberStyle);

		return rowIdx;
	}

	private void setRateCell(Cell cell, Double rate, CellStyle style) {
		if (rate != null) {
			cell.setCellValue(rate);
			cell.setCellStyle(style);
		} else {
			cell.setCellValue("-");
		}
	}

	@RequestMapping(value = "/stock2/value/excel")
	public void exportValueScreenExcel(
			@RequestParam(value = "perMax", required = false) Double perMax,
			@RequestParam(value = "pbrMax", required = false) Double pbrMax,
			@RequestParam(value = "roeMin", required = false) Double roeMin,
			@RequestParam(value = "dividendMin", required = false) Double dividendMin,
			@RequestParam(value = "debtMax", required = false) Double debtMax,
			@RequestParam(value = "marketCapMin", required = false) Integer marketCapMin,
			@RequestParam(value = "passCountMin", required = false) Integer passCountMin,
			HttpServletResponse response) throws Exception {

		StockValueScreenVO param = StockValueScreenVO.builder()
				.filterPerMax(perMax != null ? perMax : 10.0)
				.filterPbrMax(pbrMax != null ? pbrMax : 0.8)
				.filterRoeMin(roeMin != null ? roeMin : 8.0)
				.filterDividendMin(dividendMin != null ? dividendMin : 3.5)
				.filterDebtMax(debtMax != null ? debtMax : 100.0)
				.filterMarketCapMin(marketCapMin)
				.filterPassCountMin(passCountMin != null ? passCountMin : 3)
				.build();
		List<StockValueScreenVO> screenList = stockService.getValueScreenList(param);

		String fileName = "value_screen_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("가치주 스크리닝");

			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);

			CellStyle numStyle = workbook.createCellStyle();
			numStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

			CellStyle decStyle = workbook.createCellStyle();
			decStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.0"));

			CellStyle dec2Style = workbook.createCellStyle();
			dec2Style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

			String[] headers = {"No", "종목명", "종목코드", "충족", "PER(배)", "업종PER(배)", "업종비율(%)",
					"3Y평균PER(배)", "PBR(배)", "ROE 3Y(%)", "ROE연속(년)", "배당률(%)", "배당성향(%)",
					"부채비율(%)", "배당연속(년)", "영익연속(년)", "현재가(원)", "시가총액(억)", "업종"};
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			for (int i = 0; i < screenList.size(); i++) {
				StockValueScreenVO item = screenList.get(i);
				Row row = sheet.createRow(i + 1);
				int c = 0;

				row.createCell(c++).setCellValue(i + 1);
				row.createCell(c++).setCellValue(item.getName() != null ? item.getName() : "");
				row.createCell(c++).setCellValue(item.getStockId() != null ? item.getStockId() : "");
				row.createCell(c++).setCellValue(item.getPassCount() + "/" + item.getTotalCriteria());

				setDoubleCell(row.createCell(c++), item.getCurrentPer(), decStyle);
				row.createCell(c++).setCellValue(item.getIndustryPer() != null ? item.getIndustryPer() : "");
				setDoubleCell(row.createCell(c++), item.getIndustryPerRatio(), decStyle);
				setDoubleCell(row.createCell(c++), item.getAvgPer3y(), decStyle);
				setDoubleCell(row.createCell(c++), item.getCurrentPbr(), dec2Style);
				setDoubleCell(row.createCell(c++), item.getAvgRoe3y(), decStyle);
				row.createCell(c++).setCellValue(item.getRoePassYears() != null ? item.getRoePassYears() : 0);
				setDoubleCell(row.createCell(c++), item.getCurrentDividendRate(), decStyle);
				setDoubleCell(row.createCell(c++), item.getDividendTendency(), decStyle);
				setDoubleCell(row.createCell(c++), item.getCurrentDebtRatio(), decStyle);
				row.createCell(c++).setCellValue(item.getDivConsecutive() != null ? item.getDivConsecutive() : 0);
				row.createCell(c++).setCellValue(item.getProfitConsecutive() != null ? item.getProfitConsecutive() : 0);

				if (item.getPrice() != null && !item.getPrice().isEmpty()) {
					try {
						Cell priceCell = row.createCell(c++);
						priceCell.setCellValue(Double.parseDouble(item.getPrice().replace(",", "")));
						priceCell.setCellStyle(numStyle);
					} catch (NumberFormatException e) {
						row.createCell(c - 1).setCellValue(item.getPrice());
					}
				} else {
					row.createCell(c++).setCellValue("");
				}

				if (item.getMarketCapitalization() != null && !item.getMarketCapitalization().isEmpty()) {
					try {
						Cell capCell = row.createCell(c++);
						capCell.setCellValue(Double.parseDouble(item.getMarketCapitalization().replace(",", "")));
						capCell.setCellStyle(numStyle);
					} catch (NumberFormatException e) {
						row.createCell(c - 1).setCellValue(item.getMarketCapitalization());
					}
				} else {
					row.createCell(c++).setCellValue("");
				}

				row.createCell(c).setCellValue(item.getSectorName() != null ? item.getSectorName() : "");
			}

			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(response.getOutputStream());
		}
	}

	private void setDoubleCell(Cell cell, Double value, CellStyle style) {
		if (value != null) {
			cell.setCellValue(value);
			cell.setCellStyle(style);
		} else {
			cell.setCellValue("-");
		}
	}

	@RequestMapping(value = "/stock/daily/update")
	public String updateDaily(Model model) throws Exception {
		stockService.saveAllStock();
		stockService.saveDailyPriceAll();
		try {
			advStockYahooService.syncInterestUsStocks();
		} catch (Exception e) {
			log.warn("US 주식 가격 동기화 실패: {}", e.getMessage());
		}
		return "kims:/stock/stockList";
	}
	
	
	@RequestMapping(value = "/stock/list/save")
	public String saveStockList(Model model) throws Exception {
		stockService.saveAllStock();
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

	@RequestMapping(value = "/stock/cashflow/saveAll")
	public String saveCashFlowAll(Model model) throws Exception {
		stockService.saveCashFlowList();
		return "kims:/stock/stockList";
	}

	@RequestMapping(value = "/stock/cashflow/save")
	public String saveCashFlow(Model model, @RequestParam("stockId") String stockId) throws Exception {
		stockService.saveStockCashFlow(stockId);
		return "kims:/stock/stockList";
	}

	@RequestMapping(value = "/stock/valueup/saveAll")
	public String saveValueUpAll(Model model) throws Exception {
		stockService.saveValueUpList();
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
		stockService.saveDailyPriceAll();
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/selenium/financial/save")
	public String saveSeleniumFinancial(Model model, @RequestParam("stockId") String stockId) throws Exception {
		try {
		stockService.saveSeleniumStockFinancial(stockId);
		}catch(Exception e) {
			log.error("Stock 처리 실패", e);
		}
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/selenium/financial/saveAll")
	public String saveSeleniumFinancialAll(Model model) throws Exception {
		try {
		stockService.saveSeleniumStockFinancialAll();
		}catch(Exception e) {
			log.error("Stock 처리 실패", e);
		}
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/selenium/financialAnalysis/save")
	public String saveSeleniumFinancialSecond(Model model, @RequestParam("stockId") String stockId) throws Exception {
		try {
		stockService.saveSeleniumStockFinancialAnalysis(stockId);
		}catch(Exception e) {
			log.error("Stock 처리 실패", e);
		}
		return "kims:/stock/stockList";
	}
	
	@RequestMapping(value = "/stock/selenium/financialAnalysis/saveAll")
	public String saveSeleniumFinancialSecondAll(Model model) throws Exception {
		try {
		stockService.saveSeleniumStockFinancialAnalysisAll();
		}catch(Exception e) {
			log.error("Stock 처리 실패", e);
		}
		return "kims:/stock/stockList";
	}

	private void calculateBuySellProbability(List<StockValueScreenVO> list, StockValueScreenVO filter) {
		double perMax = filter.getFilterPerMax() != null ? filter.getFilterPerMax() : 10.0;
		double pbrMax = filter.getFilterPbrMax() != null ? filter.getFilterPbrMax() : 0.8;
		double debtMax = filter.getFilterDebtMax() != null ? filter.getFilterDebtMax() : 100.0;

		for (StockValueScreenVO item : list) {
			int pass = item.getPassCount() != null ? item.getPassCount() : 0;
			int total = item.getTotalCriteria() != null ? item.getTotalCriteria() : 7;

			// F1. 기본 충족도 (25%) — 스크리닝 기준 통과 비율
			double f1 = (double) pass / total;

			// F2. 밸류에이션 매력도 (25%) — PER·PBR이 기준 대비 얼마나 저렴한지
			double perScore = 0;
			if (item.getCurrentPer() != null && item.getCurrentPer() > 0 && perMax > 0) {
				perScore = Math.max(0, Math.min(1, (perMax - item.getCurrentPer()) / perMax));
			}
			double pbrScore = 0;
			if (item.getCurrentPbr() != null && item.getCurrentPbr() > 0 && pbrMax > 0) {
				pbrScore = Math.max(0, Math.min(1, (pbrMax - item.getCurrentPbr()) / pbrMax));
			}
			double f2 = (perScore + pbrScore) / 2;

			// F3. 수익성 (20%) — ROE 수준 60% + ROE 연속성 40%
			double roeScore = 0;
			if (item.getAvgRoe3y() != null && item.getAvgRoe3y() > 0) {
				roeScore = Math.min(1, item.getAvgRoe3y() / 20.0);
			}
			double roeContScore = item.getRoePassYears() != null ? Math.min(1, item.getRoePassYears() / 3.0) : 0;
			double f3 = roeScore * 0.6 + roeContScore * 0.4;

			// F4. 주주환원 (15%) — 배당수익률 50% + 배당연속성 50%
			double divRateScore = 0;
			if (item.getCurrentDividendRate() != null && item.getCurrentDividendRate() > 0) {
				divRateScore = Math.min(1, item.getCurrentDividendRate() / 7.0);
			}
			double divContScore = item.getDivConsecutive() != null ? Math.min(1, item.getDivConsecutive() / 5.0) : 0;
			double f4 = (divRateScore + divContScore) / 2;

			// F5. 재무안전성 (15%) — 부채비율 안전도 50% + 영업이익 연속성 50%
			double debtScore = 0;
			if (item.getCurrentDebtRatio() != null && item.getCurrentDebtRatio() >= 0 && debtMax > 0) {
				debtScore = Math.max(0, Math.min(1, (debtMax - item.getCurrentDebtRatio()) / debtMax));
			}
			double profitScore = item.getProfitConsecutive() != null ? Math.min(1, item.getProfitConsecutive() / 5.0) : 0;
			double f5 = (debtScore + profitScore) / 2;

			// 가중 합산 → 5~95% 스케일링
			double score = f1 * 0.25 + f2 * 0.25 + f3 * 0.20 + f4 * 0.15 + f5 * 0.15;
			item.setBuyProbability(Math.max(5, Math.min(95, (int) Math.round(score * 90 + 5))));
			item.setSellProbability(Math.max(5, Math.min(85, (int) Math.round((1 - score) * 80 + 5))));
		}
	}

}
