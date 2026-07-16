package com.kangong.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockInterestVO {

  private String stockDivision;
  private String oldDivision;
  private String stockType;
  private String name;
  private String stockId;
  private double qty;
  private long totalPrice;
  private long standard;
  private long addPrice;
  private double stockPotion;
  private int rk;
  private Double month1Rate;
  private Double month3Rate;
  private Double month6Rate;
  private Double month12Rate;
  private long price;
  private Long max52;
  private Long min52;
  private Double rate52;
  private Double pos52;
  private Double actualPotion;

}