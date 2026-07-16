package com.kangong.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MacroAdjCoeff {
    private int    stockAdj;   // 주식 조정% (음수 = 매도)
    private int    bondAdj;    // 채권 조정%
    private int    commAdj;    // 현물(금/원자재) 조정%
    private String stockSig;   // BUY / SELL / HOLD
    private String bondSig;
    private String commSig;
    private double vix;        // 참조 지표
    private double tnx;        // 10Y 금리(%)
    private double spread;     // 10Y-3M 스프레드(%p)
    private double dxy;        // 달러 인덱스
    private String fetchedAt;  // 마지막 갱신 시각
}
