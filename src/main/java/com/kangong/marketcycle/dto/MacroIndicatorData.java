package com.kangong.marketcycle.dto;

import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MacroIndicatorData {
    private double baseRate;
    private double baseRateChange6m;
    private double m2GrowthRate;
    private double earningsGrowthRate;
    private double pmi;
    private double kospiPer;
    private double historicalPerAvg;
    private double fedRate;
    private double fedRateChange6m;
    private double exchangeRate;

    public static MacroIndicatorData current() {
        return MacroIndicatorData.builder()
                .baseRate(2.50)
                .baseRateChange6m(0.0)
                .m2GrowthRate(5.59)
                .earningsGrowthRate(8.0)
                .pmi(49.5)
                .kospiPer(24.0)
                .historicalPerAvg(19.6)
                .fedRate(3.63)
                .fedRateChange6m(-0.01)
                .exchangeRate(1529)
                .build();
    }
}
