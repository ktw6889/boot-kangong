package com.kangong.advstock.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YahooStockVO {

    private String id;
    private Timestamp createDate;
    private Timestamp updateDate;
    private String deleteYn;
    private String stockId;
    private String name;
    private String price;
    private String priceChange;
    private String fluctuationRate;
    private String nav;
    private String volumn;
    private String marketCapitalization;
    private String currency;
}
