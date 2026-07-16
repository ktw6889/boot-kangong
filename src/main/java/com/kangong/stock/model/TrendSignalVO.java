package com.kangong.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TrendSignalVO {
    private String spSignal;   // BUY/SELL/HOLD
    private double spPrice;    // S&P500 현재가
    private double spMa200;    // 200일 이동평균
    private double spDiffPct;  // (현재-200MA)/200MA*100
    private double spPE;       // Trailing PE ratio
    private String ksSignal;   // KOSPI 신호
    private double ksPrice;
    private double ksMa200;
    private double ksDiffPct;
    private String fetchedAt;
}
