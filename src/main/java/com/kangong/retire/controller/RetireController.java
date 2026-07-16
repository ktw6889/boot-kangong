package com.kangong.retire.controller;

import com.kangong.retire.dto.RealEstateItem;
import com.kangong.retire.dto.RetireInputDto;
import com.kangong.retire.dto.RetireYearResult;
import com.kangong.retire.service.RetireSimulationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/retire")
@Log4j2
@RequiredArgsConstructor
public class RetireController {

    private final RetireSimulationService simulationService;

    @GetMapping({"", "/"})
    public String input(Model model) {
        List<RealEstateItem> defaultRe = new ArrayList<>();
        defaultRe.add(RealEstateItem.builder()
                .name("LG동수원자이1차").value(59500).officialPrice(33600)
                .type("HOUSE").owner("SPOUSE").saleYear(0).growthRate(2.0).build());
        defaultRe.add(RealEstateItem.builder()
                .name("수원역 유팰리스 오피스텔").value(10000).officialPrice(8888)
                .type("COMMERCIAL").owner("SPOUSE").saleYear(0).growthRate(1.0).build());
        defaultRe.add(RealEstateItem.builder()
                .name("안성상가").value(10000).officialPrice(7504)
                .type("COMMERCIAL").owner("SPOUSE").saleYear(0).growthRate(1.0).build());
        defaultRe.add(RealEstateItem.builder()
                .name("홍익아파트").value(6000).officialPrice(3540)
                .type("HOUSE").owner("SPOUSE").saleYear(2030).growthRate(2.0).build());
        defaultRe.add(RealEstateItem.builder()
                .name("울주 땅").value(15000).officialPrice(15000)
                .type("LAND").owner("SELF").saleYear(2030).growthRate(1.0).build());
        defaultRe.add(RealEstateItem.builder()
                .name("예린보증금").value(1000).officialPrice(0)
                .type("HOUSE").owner("SELF").saleYear(0).growthRate(0.0).build());

        RetireInputDto defaultInput = RetireInputDto.builder()
                .birthYear(1973)
                .spouseBirthYear(1976)
                .retireYear(2030)
                .monthlyInvestment(300)
                .monthlyExpense(500)
                .nationalPension(160)
                .spouseNationalPension(30)
                .pensionStartAge(65)
                .annualReturn(6.0)
                .inflationRate(2.5)
                .depositSelf(2881)
                .stocksSelf(10299)
                .pensionSavingSelf(19214)
                .irpSelf(20465)
                .isaSelf(2252)
                .insuranceSelf(3370)
                .loanSelf(650)
                .depositSpouse(0)
                .stocksSpouse(0)
                .pensionSavingSpouse(0)
                .irpSpouse(0)
                .isaSpouse(0)
                .insuranceSpouse(4960)
                .loanSpouse(8000)
                .realEstateList(defaultRe)
                .build();

        model.addAttribute("input", defaultInput);
        return "thymeleaf/retire/input";
    }

    @PostMapping("/result")
    public String result(RetireInputDto input, Model model) {
        log.info("retire simulation input: {}", input);

        List<RetireYearResult> results = simulationService.simulate(input);

        long depletionAge = results.stream()
                .filter(RetireYearResult::isDepleted)
                .mapToInt(RetireYearResult::getAge)
                .findFirst()
                .orElse(-1);

        model.addAttribute("input", input);
        model.addAttribute("results", results);
        model.addAttribute("depletionAge", depletionAge);

        return "thymeleaf/retire/result";
    }

    @PostMapping("/result/excel")
    public void exportExcel(RetireInputDto input, HttpServletResponse response) throws Exception {
        List<RetireYearResult> results = simulationService.simulate(input);

        String fileName = "retire_simulation_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle hStyle = createHeaderStyle(wb, IndexedColors.DARK_BLUE);
            CellStyle hGreen = createHeaderStyle(wb, IndexedColors.DARK_GREEN);
            CellStyle hRed   = createHeaderStyle(wb, IndexedColors.DARK_RED);
            CellStyle hPurp  = createHeaderStyle(wb, IndexedColors.VIOLET);
            CellStyle numFmt = wb.createCellStyle();
            numFmt.setDataFormat(wb.createDataFormat().getFormat("#,##0"));

            // === Sheet 1: 요약 ===
            Sheet s1 = wb.createSheet("요약");
            int r = 0;
            r = addSummaryRow(s1, r, hStyle, "입력값", "");
            r = addSummaryRow(s1, r, null, "출생연도(본인/배우자)", input.getBirthYear() + " / " + input.getSpouseBirthYear());
            r = addSummaryRow(s1, r, null, "은퇴연도", String.valueOf(input.getRetireYear()));
            r = addSummaryRow(s1, r, null, "월 투자금 (만원)", String.valueOf(input.getMonthlyInvestment()));
            r = addSummaryRow(s1, r, null, "월 지출 (만원)", String.valueOf(input.getMonthlyExpense()));
            r = addSummaryRow(s1, r, null, "국민연금 본인 (만원/월)", String.valueOf(input.getNationalPension()));
            r = addSummaryRow(s1, r, null, "국민연금 배우자 (만원/월)", String.valueOf(input.getSpouseNationalPension()));
            r = addSummaryRow(s1, r, null, "연금 개시 나이", String.valueOf(input.getPensionStartAge()));
            r = addSummaryRow(s1, r, null, "기대수익률 (%)", String.valueOf(input.getAnnualReturn()));
            r = addSummaryRow(s1, r, null, "물가상승률 (%)", String.valueOf(input.getInflationRate()));
            r = addSummaryRow(s1, r, hGreen, "초기 금융자산 (만원)", "");
            r = addSummaryRow(s1, r, null, "예금(본인/배우자)", input.getDepositSelf() + " / " + input.getDepositSpouse());
            r = addSummaryRow(s1, r, null, "주식(본인/배우자)", input.getStocksSelf() + " / " + input.getStocksSpouse());
            r = addSummaryRow(s1, r, null, "연금저축(본인/배우자)", input.getPensionSavingSelf() + " / " + input.getPensionSavingSpouse());
            r = addSummaryRow(s1, r, null, "IRP(본인/배우자)", input.getIrpSelf() + " / " + input.getIrpSpouse());
            r = addSummaryRow(s1, r, null, "ISA(본인/배우자)", input.getIsaSelf() + " / " + input.getIsaSpouse());
            r = addSummaryRow(s1, r, null, "보험(본인/배우자)", input.getInsuranceSelf() + " / " + input.getInsuranceSpouse());
            r = addSummaryRow(s1, r, null, "대출(본인/배우자)", input.getLoanSelf() + " / " + input.getLoanSpouse());
            if (input.getRealEstateList() != null && !input.getRealEstateList().isEmpty()) {
                r = addSummaryRow(s1, r, hRed, "부동산", "");
                for (RealEstateItem re : input.getRealEstateList()) {
                    r = addSummaryRow(s1, r, null, re.getName(),
                            re.getValue() + "만원 (공시" + re.getOfficialPrice() + ") / " + re.getOwner()
                            + (re.getSaleYear() > 0 ? " / 매도:" + re.getSaleYear() : ""));
                }
            }
            r = addSummaryRow(s1, r, hPurp, "시뮬레이션 결과", "");
            RetireYearResult first = results.get(0);
            RetireYearResult last  = results.get(results.size() - 1);
            long depAge = results.stream().filter(RetireYearResult::isDepleted).mapToInt(RetireYearResult::getAge).findFirst().orElse(-1);
            r = addSummaryRow(s1, r, null, "초기 총자산 (만원)", String.format("%,d", first.getTotalAssets()));
            r = addSummaryRow(s1, r, null, "100세 잔여자산 (만원)", String.format("%,d", last.getTotalAssets()));
            r = addSummaryRow(s1, r, null, "자산 소진 시점", depAge > 0 ? depAge + "세" : "100세까지 유지");
            s1.setColumnWidth(0, 7000);
            s1.setColumnWidth(1, 10000);

            // === Sheet 2: 연도별 상세 ===
            Sheet s2 = wb.createSheet("연도별 상세");
            String[] headers = {"연도", "나이(본인)", "나이(배우자)", "금융자산", "본인금융", "배우자금융",
                    "예금", "주식", "연금저축", "IRP", "ISA", "부동산", "총자산",
                    "국민연금", "투자수익", "생활비", "세금", "건보료", "총지출", "인출액", "인출원천"};
            Row hRow = s2.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(hStyle);
                s2.setColumnWidth(i, i == 20 ? 8000 : 3800);
            }
            for (int i = 0; i < results.size(); i++) {
                RetireYearResult yr = results.get(i);
                Row row = s2.createRow(i + 1);
                int col = 0;
                row.createCell(col++).setCellValue(yr.getYear());
                row.createCell(col++).setCellValue(yr.getAge());
                row.createCell(col++).setCellValue(yr.getSpouseAge());
                setNumCell(row.createCell(col++), yr.getFinancialTotal(), numFmt);
                setNumCell(row.createCell(col++), yr.getSelfFinancialTotal(), numFmt);
                setNumCell(row.createCell(col++), yr.getSpouseFinancialTotal(), numFmt);
                setNumCell(row.createCell(col++), yr.getDeposit(), numFmt);
                setNumCell(row.createCell(col++), yr.getStocks(), numFmt);
                setNumCell(row.createCell(col++), yr.getPensionSaving(), numFmt);
                setNumCell(row.createCell(col++), yr.getIrp(), numFmt);
                setNumCell(row.createCell(col++), yr.getIsa(), numFmt);
                setNumCell(row.createCell(col++), yr.getRealEstate(), numFmt);
                setNumCell(row.createCell(col++), yr.getTotalAssets(), numFmt);
                setNumCell(row.createCell(col++), yr.getPensionIncome(), numFmt);
                setNumCell(row.createCell(col++), yr.getInvestmentIncome(), numFmt);
                setNumCell(row.createCell(col++), yr.getLivingExpense(), numFmt);
                setNumCell(row.createCell(col++), yr.getTax(), numFmt);
                setNumCell(row.createCell(col++), yr.getHealthInsurance(), numFmt);
                setNumCell(row.createCell(col++), yr.getTotalExpense(), numFmt);
                setNumCell(row.createCell(col++), yr.getWithdrawal(), numFmt);
                row.createCell(col).setCellValue(yr.getWithdrawalSource() != null ? yr.getWithdrawalSource() : "");
            }

            wb.write(response.getOutputStream());
        }
    }

    private CellStyle createHeaderStyle(Workbook wb, IndexedColors bg) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(bg.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private int addSummaryRow(Sheet sheet, int rowNum, CellStyle labelStyle, String label, String value) {
        Row row = sheet.createRow(rowNum);
        Cell c0 = row.createCell(0);
        c0.setCellValue(label);
        if (labelStyle != null) c0.setCellStyle(labelStyle);
        row.createCell(1).setCellValue(value);
        return rowNum + 1;
    }

    private void setNumCell(Cell cell, long value, CellStyle style) {
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
