package com.kangong.advstock.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kangong.advstock.model.YahooStockVO;
import com.kangong.advstock.service.AdvStockYahooService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class AdvStockYahooController {

    private static final String LIST_VIEW = "kims:/advstock/advStockYahooList";
    private static final String REDIRECT_LIST = "redirect:/advstock/yahoo";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private AdvStockYahooService advStockYahooService;

    @RequestMapping(value = "/advstock/yahoo")
    public String list(Model model,
                       @RequestParam(value = "keyword", required = false) String keyword) {
        List<YahooStockVO> stockList;
        try {
            stockList = advStockYahooService.fetchStockList(keyword);
        } catch (Exception e) {
            log.error("Yahoo Finance 목록 조회 실패: {}", e.getMessage());
            stockList = new ArrayList<>();
            model.addAttribute("errorMessage", "Yahoo Finance 조회 실패: " + extractRootCause(e));
        }
        model.addAttribute("stockList", stockList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("today", LocalDate.now().format(DATE_FMT));
        model.addAttribute("activeMenu", "advStockYahooList");
        return LIST_VIEW;
    }

    @RequestMapping(value = "/advstock/yahoo/downloadMaster")
    public String downloadMaster(@RequestParam(value = "keyword", required = false) String keyword,
                                 RedirectAttributes ra) {
        log.info("Yahoo Finance Master 다운로드 시작 (keyword: {})", keyword);
        try {
            advStockYahooService.saveMaster(keyword);
            ra.addFlashAttribute("successMessage", "Yahoo Finance Master 다운로드 완료");
        } catch (Exception e) {
            log.error("Yahoo Finance Master 다운로드 실패", e);
            ra.addFlashAttribute("errorMessage", "다운로드 실패: " + extractRootCause(e));
        }
        return REDIRECT_LIST;
    }

    @RequestMapping(value = "/advstock/yahoo/downloadDailyPrice")
    public String downloadDailyPrice(@RequestParam(value = "tradingDate", required = false) String tradingDate,
                                     @RequestParam(value = "keyword", required = false) String keyword,
                                     RedirectAttributes ra) {
        tradingDate = resolveDate(tradingDate);
        log.info("Yahoo Finance Daily Price 다운로드 시작 (기준일: {}, keyword: {})", tradingDate, keyword);
        try {
            advStockYahooService.saveDailyPrice(tradingDate, keyword);
            ra.addFlashAttribute("successMessage", "Yahoo Finance Daily Price 다운로드 완료 (기준일: " + tradingDate + ")");
        } catch (Exception e) {
            log.error("Yahoo Finance Daily Price 다운로드 실패", e);
            ra.addFlashAttribute("errorMessage", "다운로드 실패: " + extractRootCause(e));
        }
        return REDIRECT_LIST;
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
