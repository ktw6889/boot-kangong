package com.kangong.marketcycle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketCycleDashboard {
    private CyclePhase currentPhase;
    private int progressPercent;
    private String diagnosisDate;
    private String diagnosisSummary;
    private String nextPhaseOutlook;

    private String cycleStartDate;
    private String estimatedEndDate;
    private int remainingMonths;
    private int prepareMonths;
    private String durationComment;
    private String prepareComment;

    private List<SectorRecommendation> buyRecommendations;
    private List<SectorRecommendation> sellRecommendations;

    private CyclePhase nextPhase;
    private List<SectorRecommendation> nextBuyRecommendations;
    private List<SectorRecommendation> nextSellRecommendations;

    private List<SectorOverview> sectorOverviews;
    private List<SectorOverview> assetOverviews;
    private List<CycleIndicator> indicators;
    private List<AccountRebalance> accountRebalances;
}
