package com.kangong.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetireYearResult {

    private int year;
    private int age;
    private int spouseAge;

    // 자산 현황 (만원)
    private long deposit;
    private long stocks;
    private long pensionSaving;
    private long irp;
    private long isa;
    private long financialTotal;
    private long realEstate;
    private long totalAssets;

    // 수입 (만원/년)
    private long pensionIncome;
    private long investmentIncome;

    // 지출 (만원/년)
    private long livingExpense;
    private long tax;
    private long healthInsurance;
    private long totalExpense;

    // 인출
    private long withdrawal;
    private String withdrawalSource;

    private long selfFinancialTotal;
    private long spouseFinancialTotal;
    private long realEstateSaleProceeds;

    private boolean depleted;
}
