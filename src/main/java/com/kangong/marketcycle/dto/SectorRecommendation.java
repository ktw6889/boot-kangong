package com.kangong.marketcycle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectorRecommendation {
    private String sector;
    private String reason;
    private String stock1;
    private String stock1Code;
    private String stock1Reason;
    private String stock2;
    private String stock2Code;
    private String stock2Reason;
}
