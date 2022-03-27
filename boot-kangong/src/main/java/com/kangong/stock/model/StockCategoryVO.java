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
public class StockCategoryVO {
	private String id; //ID
	private String categoryType; //업종종류
	private String categoryNo; //업종NO
	private String categoryName; //업종명
	private String stockId;
}
