package com.kangong.marketcycle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectorOverview {
    private String sector;
    private int buyProbability;
    private int sellProbability;
    private String signal;        // BUY, SELL, HOLD
    private String stock1;
    private String stock1Code;
    private String stock2;
    private String stock2Code;
    private String reason;

    private int buyProbability3m;
    private int sellProbability3m;
    private int buyProbability6m;
    private int sellProbability6m;
}
