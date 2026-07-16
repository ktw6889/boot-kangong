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
public class StockMarketIndexVO {

	private String id; //ID
	private Timestamp createDate; //생성일
	private Timestamp updateDate; //수정일
	private String deleteYn; //삭제여부
	private String yyyymmdd; //년월일
	private String domesticInterestCall; //콜금리 (1일)
	private String domesticInterestCd; //CD금리 (91일)
	private String domesticInterestCofixManf; //COFIX 신규취급액
	private String domesticInterestCofixOutb; //COFIX 잔액
	private String domesticInterestNcofixOutb; //COFIX 신잔액
	private String standardInterestUs; //미국연방준비은행
	private String standardInterestKr; //한국은행
	private String standardInterestEu; //유럽중앙은행
	private String standardInterestGb; //영국은행
	private String standardInterestJp; //일본은행
	private String metalGc; //국제 금
	private String metalCmdt; //국내 금
	private String metalSi; //은
	private String metalHg; //구리(선물)
	private String metalPl; //백금
	private String transportCcf; //중국컨테이너 운임지수
	private String transportScf; //상하이컨테이너 운임지수
	private String transportBadi; //BDI 건화물선지수
	private String transportBack; //BCI 케이프사이즈지수
	private String transportBpni; //BPI 파나막스지수
	private String transportBsis; //BSI 수프라막스지수
	private String transportBhsi; //BHI 핸디사이즈지수
	private String transportBaid; //BDTI 원유유조선지수
	private String transportBait; //BCTI 석유제품선지수
	private String bondUs10yt; //미국 국채 10년
	private String bondKr10yt; //한국 국채 10년
	private String bondJp10yt; //일본 국채 10년
	private String bondDe10yt; //독일 국채 10년
	private String bondCn10yt; //중국 국채 10년
	private String energyCl; //WTI
	private String energyLco; //브렌트유
	private String energyRb; //RBOB 가솔린
	private String energyHo; //난방유
	private String energyDcb; //두바이유
	
	private String indexType; //index type

}
