package com.kangong.marketcycle.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleDeterminationResult {
    private CyclePhase phase;
    private CyclePhase nextPhase;
    private int progressPercent;
    private int confidence;
    private Map<CyclePhase, Double> phaseScores;
    private List<String> signals;
    private String summary;
}
