package com.kangong.advstock.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kangong.advstock.service.AdvStockService;
import com.kangong.stock.model.StockVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class AdvStockController {

    private static final String LIST_VIEW = "kims:/advstock/advStockList";
    private static final String REDIRECT_LIST = "redirect:/advstock";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private AdvStockService advStockService;

    @RequestMapping(value = "/advstock")
    public String list(Model model) {
        List<StockVO> stockList;
        try {
            stockList = advStockService.getStockList(StockVO.builder().build());
        } catch (Exception e) {
            log.error("종목 목록 조회 실패: {}", e.getMessage());
            stockList = new ArrayList<>();
            model.addAttribute("errorMessage", "DB 조회 오류: " + extractRootCause(e)
                    + " → create-advstock.sql의 ALTER TABLE을 먼저 실행해주세요.");
        }
        model.addAttribute("stockList", stockList);
        model.addAttribute("today", LocalDate.now().format(DATE_FMT));
        model.addAttribute("activeMenu", "advStockList");
        return LIST_VIEW;
    }

    @RequestMapping(value = "/advstock/downloadAll")
    public String downloadAll(@RequestParam(value = "tradingDate", required = false) String tradingDate,
                              RedirectAttributes ra) {
        tradingDate = resolveDate(tradingDate);
        log.info("KOSPI 전체 다운로드 시작 (Master+Daily+Category+삭제처리)");
        try {
            advStockService.saveAll(tradingDate);
            ra.addFlashAttribute("successMessage", "전체 다운로드 완료");
        } catch (Exception e) {
            log.error("전체 다운로드 실패", e);
            ra.addFlashAttribute("errorMessage", "다운로드 실패: " + extractRootCause(e));
        }
        return REDIRECT_LIST;
    }

    @RequestMapping(value = "/advstock/downloadMaster")
    public String downloadMaster(RedirectAttributes ra) {
        log.info("종목 Master 다운로드 시작");
        try {
            advStockService.saveStockMaster();
            ra.addFlashAttribute("successMessage", "종목 Master 다운로드 완료");
        } catch (Exception e) {
            log.error("종목 Master 다운로드 실패", e);
            ra.addFlashAttribute("errorMessage", "다운로드 실패: " + extractRootCause(e));
        }
        return REDIRECT_LIST;
    }

    @RequestMapping(value = "/advstock/downloadDailyPrice")
    public String downloadDailyPrice(@RequestParam(value = "tradingDate", required = false) String tradingDate,
                                     RedirectAttributes ra) {
        tradingDate = resolveDate(tradingDate);
        log.info("Daily Price 다운로드 시작 (기준일: {})", tradingDate);
        try {
            advStockService.saveDailyPrice(tradingDate);
            ra.addFlashAttribute("successMessage", "Daily Price 다운로드 완료 (기준일: " + tradingDate + ")");
        } catch (Exception e) {
            log.error("Daily Price 다운로드 실패", e);
            ra.addFlashAttribute("errorMessage", "다운로드 실패: " + extractRootCause(e));
        }
        return REDIRECT_LIST;
    }

    @RequestMapping(value = "/advstock/downloadCategory")
    public String downloadCategory(RedirectAttributes ra) {
        log.info("업종 Category 다운로드 시작");
        try {
            advStockService.saveCategory();
            ra.addFlashAttribute("successMessage", "업종 Category 다운로드 완료");
        } catch (Exception e) {
            log.error("업종 Category 다운로드 실패", e);
            ra.addFlashAttribute("errorMessage", "다운로드 실패: " + extractRootCause(e));
        }
        return REDIRECT_LIST;
    }

    // ==================== Excel ====================

    @RequestMapping(value = "/advstock/excel")
    public void exportExcel(HttpServletResponse response) throws Exception {
        List<StockVO> stockList = advStockService.getStockList(StockVO.builder().build());

        String fileName = "kospi_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("코스피 종목");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

            CellStyle decimalStyle = workbook.createCellStyle();
            decimalStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            String[] headers = {"No", "종목코드", "종목명", "현재가", "거래량", "시가총액", "PER", "PBR", "배당률"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < stockList.size(); i++) {
                StockVO vo = stockList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(vo.getStockId());
                row.createCell(2).setCellValue(vo.getName());
                setNumericCell(row.createCell(3), vo.getPrice(), numberStyle);
                setNumericCell(row.createCell(4), vo.getVolumn(), numberStyle);
                setNumericCell(row.createCell(5), vo.getMarketCapitalization(), numberStyle);
                setNumericCell(row.createCell(6), vo.getPer(), decimalStyle);
                setNumericCell(row.createCell(7), vo.getPbr(), decimalStyle);
                setNumericCell(row.createCell(8), vo.getDividendRate(), decimalStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    private void setNumericCell(Cell cell, String value, CellStyle style) {
        if (value != null && !value.isEmpty() && !"0".equals(value)) {
            try {
                cell.setCellValue(Double.parseDouble(value.replace(",", "")));
                cell.setCellStyle(style);
            } catch (NumberFormatException e) {
                cell.setCellValue(value);
            }
        }
    }

    private String resolveDate(String tradingDate) {
        if (tradingDate == null || tradingDate.isEmpty()) {
            return LocalDate.now().format(DATE_FMT);
        }
        if (tradingDate.length() == 8) {
            return tradingDate.substring(0, 4) + "-" + tradingDate.substring(4, 6) + "-" + tradingDate.substring(6, 8);
        }
        return tradingDate;
    }

    private String extractRootCause(Exception e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        String msg = cause.getMessage();
        return msg != null ? msg : cause.getClass().getSimpleName();
    }
}
