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
public class AccountRebalance {
    private String accountName;
    private String accountType;
    private List<HoldingItem> holdings;
    private String rebalanceSummary;
}
