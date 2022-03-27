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
public class StockFinancialVO {
	private String id; //ID
	private Timestamp createDate; //생성일
	private Timestamp updateDate; //수정일
	private String deleteYn; //삭제여부
	private String totalSales; //매출액(억원)
	private String profits; //영업이익(억원)
	private String earnings; //당기순이익(억원)
	private String profitsRatio; //영업이익률(%)
	private String netProfitRatio; //순이익률(%)
	private String roe; //ROE(%)
	private String deptRatio; //부채비율(%)
	private String reserveRatio; //유보율(%)
	private String eps; //EPS(원)
	private String per; //PER(배)
	private String bps; //BPS(배)
	private String pbr; //PBR(배)
	private String dividendsPerShare; //주당배당금(원)
	private String dividendsRate; //시가배당률(%)
	private String dividendsTendency; //배당성향(%)
	private String year; //년도
	private String stockId; //종목ID
	private String stockMasterId; //MASTER_ID
	private String sharesOutstanding; //발행주식수
	private String totalAssets; //자산총계
	private String totalDept; //부채총계
	private String totalCapital; //자본총계
	private String capital; //자본금
	
	private String liquidAsset; //유동자산 
	private String liquidDept; //유동부채
	private String totalStockQty; //발행주식수
	private String commonStockQty; //보통주
	private String preferredStockQty; //우선주
	
}
