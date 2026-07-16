package com.kangong.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockValueScreenVO {

    // === 종목 기본 정보 ===
    private String stockId;
    private String name;
    private String price;
    private String marketCapitalization;
    private String industryPer;

    // === 스크리닝 지표 (현재값) ===
    private Double currentPer;
    private Double currentPbr;
    private Double currentRoe;
    private Double currentDividendRate;
    private Double currentDebtRatio;
    private Double dividendTendency;

    // === 3개년 평균/연속 ===
    private Double avgPer3y;
    private Double avgRoe3y;
    private Integer roePassYears;

    // === 배당/수익 연속성 ===
    private Integer divPayYears;
    private Integer divConsecutive;
    private Integer profitConsecutive;

    // === 유동성 / 현금흐름 ===
    private Double currentRatio;
    private Double operatingCashFlow;

    // === 밸류업 ===
    private String valueUpYn;

    // === 업종 비교 ===
    private Double industryPerRatio;

    // === 부가 정보 ===
    private String max52;
    private String min52;
    private String sectorName;

    // === 필터 조건 (입력) ===
    private Double filterPerMax;
    private Double filterPbrMax;
    private Double filterRoeMin;
    private Double filterDividendMin;
    private Double filterDebtMax;
    private String filterUseIndustryPer;
    private Integer filterMarketCapMin;
    private Integer filterPassCountMin;

    // === 통과 여부 ===
    private Integer passCount;
    private Integer totalCriteria;

    // === 매수/매도 확률 ===
    private Integer buyProbability;
    private Integer sellProbability;
}
