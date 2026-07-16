package com.kangong.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MacroIndicatorVO {

	private String symbol;
	private String name;
	private String nameKr;
	private String category;
	private String price;
	private String change;
	private String changePercent;
	private String fiftyTwoWeekHigh;
	private String fiftyTwoWeekLow;
	private String previousClose;
	private String currency;

	private double priceRaw;
	private String signal;       // BUY, SELL, CAUTION, NEUTRAL
	private String signalText;
}
