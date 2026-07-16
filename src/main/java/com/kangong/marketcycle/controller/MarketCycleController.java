package com.kangong.marketcycle.controller;

import com.kangong.marketcycle.dto.CyclePhase;
import com.kangong.marketcycle.service.MacroDataFetcher;
import com.kangong.marketcycle.service.MarketCycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/marketcycle")
@Log4j2
@RequiredArgsConstructor
public class MarketCycleController {

    private final MarketCycleService marketCycleService;
    private final MacroDataFetcher macroDataFetcher;

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        log.info("market cycle dashboard");
        model.addAttribute("dashboard", marketCycleService.buildDashboard());
        model.addAttribute("allPhases", Arrays.asList(CyclePhase.values()));
        return "thymeleaf/marketcycle/dashboard";
    }

    @PostMapping("/refresh")
    public String refresh() {
        macroDataFetcher.clearCache();
        return "redirect:/marketcycle";
    }
}
