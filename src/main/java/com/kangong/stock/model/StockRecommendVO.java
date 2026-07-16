package com.kangong.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRecommendVO {

  private String stockDivision;
  private String stockType;
  private String name;
  private String stockId;
  private double qty;
  private long totalPrice;
  private long price;
  private double stockPotion;
  private Double actualPotion;
  private double deviation;
  private long addPrice;
  private int recommendQty;
  private long recommendAmount;
  private double score;
  private String reason;
  private Double pos52;
  private Double month1Rate;
  private Double month3Rate;

  public static StockRecommendVO from(StockInterestVO src) {
    double actual = src.getActualPotion() != null ? src.getActualPotion() : 0;
    double dev = actual - src.getStockPotion();

    StringBuilder reason = new StringBuilder();
    double score = Math.abs(dev);

    if (src.getPos52() != null && src.getPos52() < 30) {
      score += 2;
      reason.append("52주 저점 근접. ");
    }
    if (src.getPos52() != null && src.getPos52() > 80) {
      score += 1;
      reason.append("52주 고점 근접. ");
    }
    if (src.getMonth1Rate() != null && src.getMonth1Rate() < -5) {
      score += 1.5;
      reason.append("1개월 급락. ");
    }
    if (src.getMonth3Rate() != null && src.getMonth3Rate() > 15) {
      score += 1;
      reason.append("3개월 급등. ");
    }

    if (dev < 0) {
      reason.insert(0, "목표비중 대비 부족. ");
    } else if (dev > 0) {
      reason.insert(0, "목표비중 대비 초과. ");
    }

    int recQty = 0;
    long recAmount = Math.abs(src.getAddPrice());
    if (src.getPrice() > 0) {
      recQty = (int) (recAmount / src.getPrice());
    }

    return StockRecommendVO.builder()
        .stockDivision(src.getStockDivision())
        .stockType(src.getStockType())
        .name(src.getName())
        .stockId(src.getStockId())
        .qty(src.getQty())
        .totalPrice(src.getTotalPrice())
        .price(src.getPrice())
        .stockPotion(src.getStockPotion())
        .actualPotion(src.getActualPotion())
        .deviation(Math.round(dev * 10.0) / 10.0)
        .addPrice(src.getAddPrice())
        .recommendQty(recQty)
        .recommendAmount(recAmount)
        .score(Math.round(score * 10.0) / 10.0)
        .reason(reason.toString().trim())
        .pos52(src.getPos52())
        .month1Rate(src.getMonth1Rate())
        .month3Rate(src.getMonth3Rate())
        .build();
  }
}
