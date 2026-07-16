package com.kangong.stock.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockVO {

  
  
  private String id; //ID
  private Timestamp createDate; //생성일
  private Timestamp updateDate; //수정일
  private String deleteYn; //삭제여부
  private String stockId; //종목_ID
  private String name; //종목명
  private String price; //현재가
  private String priceBeforeday; //전일가
  private String faceValue; //액면가
  private String marketCapitalization; //시가총액
  private String stockQty; //상장주식수
  private String foreignerRatio; //외국인비율
  private String volumn; //거래량
  private String per; //PER
  private String estimationPer; //추정PER
  private String roe; //ROE
  private String pbr; //PBR
  private String bps; //BPS
  private String industryPer; //동일업종 PER
  private String industryBaisse; //동일업종 등락률
  private String investmentOpinion; //투자의견
  private String targetPrice; //목표주가
  private String max52; //52주 최고
  private String min52; //52주 최저
  private String dividendRate; //배당수익률
  private String national; //종목 국가
  private String eps; //EPS
  private String estimationEps; //추정EPS
  private String liquidStockQty; //유동주식수
  private String fundPay;

  private String sectorName;        //업종명
  private String priceChange;       //전일대비 변동액
  private String fluctuationRate;   //등락률
  private String openPrice;         //시가
  private String highPrice;         //고가
  private String lowPrice;          //저가
  private String tradingValue;      //거래대금

  private String discussionRoomUrl;    // 토론방 url
  private String valueUpYn;           // 코리아밸류업지수 편입 여부

  public String getDiscussionRoomUrl() {
    return "https://finance.naver.com"+discussionRoomUrl;
  }
  
}
