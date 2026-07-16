package com.kangong.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealEstateItem {
    private String name;
    private long value;
    private long officialPrice;
    private String type;
    private String owner;
    private int saleYear;
    private double growthRate;
}
