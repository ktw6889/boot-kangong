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
public class StockEsgVO {
	private String id; //ID
	private Timestamp createDate; //생성일
	private Timestamp updateDate; //수정일
	private String deleteYn; //삭제여부
	private String year; //년도
	private String stockId; //종목ID
	private String name; //종목명
	private String greenHouseEmission; //온실가스 배출량 E02
	private String energyUsage; //에너지 사용량 E03
	private String fineDustUsage; //미세먼지 배출량 E05
	private String waterRecyclingRate; //용수 재활용률 E01
	private String wasteRecyclingRate; //폐기물 재활용률 E04
	private String averageAnnualSalary; //직원 평균 연봉 S05
	private String nonRegularEmplymentRate; //비정규직 고용률 S04
	private String donation; //기부금 S01
	private String continuousServiceYear; //직원 평균 근속년수 S02
	private String outsideDirectorRate; //사외이사 비율 G03
	private String largestShareHolderRatio; //최대주주 지분율 G05
	private String directorateIndependence; //이사회의 독립성  G04
	private String executiveAverageAnnualSalary; //사내등기임원 평균 보수 G01
	private String salaryRatio; //임원/직원 보수 비율 G02
}
