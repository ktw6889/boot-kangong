package com.kangong.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDailyPriceVO {

	private String id; //ID
	private String stockId; //종목ID
	private String tradingDate; //날짜
	private String closingPrice; //종가
	private String previousDayRate; //전일비
	private String fluctuationRate; //등락률
	private String volumn; //거래량
	private String organTradingVolumn; //기관_매매량
	private String foreignTradingVolumn; //외국인_매매량
	private String foreignHoldingVolumn; //외국인_보유주수
	private String foreignHoldingRate; //외국인_보유율
}
