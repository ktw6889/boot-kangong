package com.kangong.marketcycle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleIndicator {
    private String name;
    private String value;
    private String trend;      // UP, DOWN, FLAT
    private String signal;     // POSITIVE, NEGATIVE, NEUTRAL
    private String description;
}
