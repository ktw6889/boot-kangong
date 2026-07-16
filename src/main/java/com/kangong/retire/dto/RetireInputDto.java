package com.kangong.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetireInputDto {

    private int birthYear;
    private int spouseBirthYear;
    private int retireYear;

    private long monthlyInvestment;
    private long monthlyExpense;

    private long nationalPension;
    private long spouseNationalPension;
    private int pensionStartAge;

    private double annualReturn;
    private double inflationRate;

    // 본인 금융자산 (만원)
    private long depositSelf;
    private long stocksSelf;
    private long pensionSavingSelf;
    private long irpSelf;
    private long isaSelf;

    // 배우자 금융자산 (만원)
    private long depositSpouse;
    private long stocksSpouse;
    private long pensionSavingSpouse;
    private long irpSpouse;
    private long isaSpouse;

    // 보험 해약환급금 (만원)
    private long insuranceSelf;
    private long insuranceSpouse;

    // 대출 (만원, 양수로 입력)
    private long loanSelf;
    private long loanSpouse;

    // 부동산 목록
    @Builder.Default
    private List<RealEstateItem> realEstateList = new ArrayList<>();
}
