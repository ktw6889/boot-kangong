package com.kangong.marketcycle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldingItem {
    private String name;
    private String code;
    private String category;
    private int currentWeight;
    private int targetWeight;
    private String weightDiffStr;
    private String action;
    private String reason;
}
