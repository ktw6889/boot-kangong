select m.name, m.DIVIDEND_RATE as DR, m.per, m.pbr, m.ROE, M.INVESTMENT_OPINION as OPINION, M.PRICE, m.TARGET_PRICE as TP, 
       PRICE/MAX_52, PRICE/MIN_52,
	   m.MARKET_CAPITALIZATION  as market, M.PRICE/M.TARGET_PRICE PR, (100-ROUND(M.PRICE/M.TARGET_PRICE*100))*1.2 PRICE_RK, F.*
from ST_STOCK_MASTER m left join
	(
		 select FF.STOCK_ID, FF.PROFITS_2021, FF.PROFITS_2020, FF.PROFITS_2019
		  , case when FF.PROFITS_2021 <> 0 then FF.PROFITS_2021/FF.PROFITS_2020 else FF.PROFITS_2020 / FF.PROFITS_2019 end PROFITS_RATE
		from 
		(
			select STOCK_ID,  MAX(case when year ='2021' THEN SF.PROFITS else 0 END) as PROFITS_2021,  MAX(case when year ='2020' THEN SF.PROFITS else 0 END) PROFITS_2020,
			    MAX(case when year ='2019' THEN SF.PROFITS else 0 END) as PROFITS_2019, MAX(case when year ='2021' THEN SF.TOTAL_SALES else 0 END) as TOTAL_SALES_2021,
			    MAX(case when year ='2020' THEN SF.TOTAL_SALES else 0 END) as TOTAL_SALES_2020, MAX(case when year ='2019' THEN SF.TOTAL_SALES else 0 END) as TOTAL_SALES_2019
			from ST_STOCK_FINANCIAL SF
			group by STOCK_ID
		) FF	
	)F on M.STOCK_ID = F.STOCK_ID
where 1=1
 and m.MARKET_CAPITALIZATION > 1000000000000
 and m.DIVIDEND_RATE > 3
-- and M.PER < 20
-- and ROE > 0
-- and PBR < 1
-- and ROE < 10
 and (
   m.name not like '%우'
   and m.name not like '%우B'
 )
-- and name like '현대%'
-- order by M.PRICE/M.TARGET_PRICE 
-- order by PRICE_RATE + PBR_RATE + PER_PROFITS_RATE DESC
order by PRICE/MAX_52, m.DIVIDEND_RATE DESC


select C.CATEGORY_NAME, SUM(M.MARKET_CAPITALIZATION) MC
from ST_STOCK_MASTER m, st_stock_category_link L, st_stock_category C
where M.STOCK_ID = L.STOCK_ID 
and L.CATEGORY_NO = C.CATEGORY_NO 
group by C.CATEGORY_NAME
order by MC desc


select C.CATEGORY_NAME, M.*
from ST_STOCK_MASTER m, st_stock_category_link L, st_stock_category C
where M.STOCK_ID = L.STOCK_ID 
and L.CATEGORY_NO = C.CATEGORY_NO 
AND C.CATEGORY_NAME = '지주사'
order by M.MARKET_CAPITALIZATION DESC


select *
from ST_STOCK_MASTER

select *
from st_stock_category ssc

select *
from st_stock_category_link sscl 

select ssf.year, ssf.TOTAL_SALES , ssf.profits, lead
from st_stock_financial ssf 
where ssf.STOCK_ID = '005930'
group by ssf.STOCK_ID 

select *
from st_stock_daily_price ssdp 
where SSDP.STOCK_ID = '005930'
and SSDP.TRADING_DATE > '2021.12.01'
and SSDP.TRADING_DATE < '2022.01.01'

SELECT LAST_DAY(NOW())

select m.name, m.DIVIDEND_RATE as DR, m.per, m.pbr, m.ROE, M.INVESTMENT_OPINION as OPINION, M.PRICE, m.TARGET_PRICE as TP, 
	   m.MARKET_CAPITALIZATION  as market, M.PRICE/M.TARGET_PRICE PR, (100-ROUND(M.PRICE/M.TARGET_PRICE*100))*1.2 PRICE_RK, 
       (case when ROE < 0 or ROE = 0 then 0
            when ROE > 10 then 10-ROUND(ROE/10)
            else 10-ROUND(ROE)+10 end ) * 2 AS ROE_RK,
       (case when PER < 0 or PER >= 100 or PER = 0 then 0
            when PER > 10 then 10-ROUND(PER/10)
            else 10-ROUND(PER)+10 end )  AS PER_RK,
       (case when PBR >= 10 then 0
            when PBR < 10 and PBR >= 1 then 10-ROUND(PBR)
            else 10-ROUND(PBR*10)+10 end )  AS PBR_RK, 
       case when M.PRICE/M.TARGET_PRICE > 1 then 0 when M.TARGET_PRICE = 0 then 1 when M.PRICE/M.TARGET_PRICE > 0.9 then 1 when M.PRICE/M.TARGET_PRICE > 0.8 then 2
         when M.PRICE/M.TARGET_PRICE > 0.75 then 3 when M.PRICE/M.TARGET_PRICE > 0.7 then 4 when M.PRICE/M.TARGET_PRICE > 0.65 then 5 when M.PRICE/M.TARGET_PRICE > 0.6 then 6
         when M.PRICE/M.TARGET_PRICE > 0.55 then 7 when M.PRICE/M.TARGET_PRICE > 0.5 then 8 when M.PRICE/M.TARGET_PRICE > 0.45 then 9 else 10 end PRICE_RATE,
        case when PBR < 0.5 then 10 when PBR < 0.75 then 9 when PBR < 1 then 8 when PBR < 1.2 then 7 when PBR < 1.5 then 6    when PBR < 2 then 5
          when PBR < 3 then 4 when PBR < 4 then 3 when PBR < 5 then 2 when PBR < 6 then 1 else 0 end PBR_RATE,
        case when M.PER / M.INDUSTRY_PER  > 5 then 0 when M.PER / M.INDUSTRY_PER  > 4 then 4 when M.PER / M.INDUSTRY_PER  > 3 then 5 when M.PER / M.INDUSTRY_PER  > 2 then 6
          when M.PER / M.INDUSTRY_PER  > 1 then 7 when M.PER / M.INDUSTRY_PER  > 0.8 then 8 when M.PER / M.INDUSTRY_PER  > 0.6 then 9 else 10 end PER_RATE,
        case when IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10) ) > 30 then 0 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 20 then 1 
          when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 15  then 2 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 10 then 3
          when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 9 then 4 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 8 then 5 
          when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 7 then 6 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 5 then 7
          when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 3 then 8 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 1 then 9
          else 10 end PER_PROFITS_RATE,
        IFNULL(M.PER /F.PROFITS_RATE,0) as PER_PROFITS,
      m.MAX_52, m.MIN_52 ,m.* 
from ST_STOCK_MASTER m left join
	(
		 select FF.STOCK_ID, FF.PROFITS_2021, FF.PROFITS_2020, FF.PROFITS_2019
		  , case when FF.PROFITS_2021 <> 0 then FF.PROFITS_2021/FF.PROFITS_2020 else FF.PROFITS_2020 / FF.PROFITS_2019 end PROFITS_RATE
		from 
		(
			select STOCK_ID,  MAX(case when year ='2021' THEN SF.PROFITS else 0 END) as PROFITS_2021,  MAX(case when year ='2020' THEN SF.PROFITS else 0 END) PROFITS_2020,
			    MAX(case when year ='2019' THEN SF.PROFITS else 0 END) as PROFITS_2019
			from ST_STOCK_FINANCIAL SF
			group by STOCK_ID
		) FF	
	)F on M.STOCK_ID = F.STOCK_ID
where 1=1
 and m.MARKET_CAPITALIZATION > 1000000000000
-- and m.per < 15
-- and m.pbr < 1.5
-- and m.roe < 15
-- and m.DIVIDEND_RATE  > 2
-- and M.PRICE/M.TARGET_PRICE < 0.65
 and m.per <> 0
 and m.TARGET_PRICE  <> 0
 and (
   m.name not like '%우'
   and m.name not like '%우B'
 )
-- and name like '현대%'
-- order by M.PRICE/M.TARGET_PRICE 
-- order by PRICE_RATE + PBR_RATE + PER_PROFITS_RATE DESC
order by m.DIVIDEND_RATE*1.5 + PRICE_RK + ROE_RK + PER_RK + PBR_RK desc --  



select m.name, m.DIVIDEND_RATE as DR, m.per, m.pbr, m.ROE, M.INVESTMENT_OPINION as OPINION, M.PRICE, m.TARGET_PRICE as TP, 
	   m.MARKET_CAPITALIZATION  as market, M.PRICE/M.TARGET_PRICE PR, (100-ROUND(M.PRICE/M.TARGET_PRICE*100))*1.2 PRICE_RK, 
       (case when ROE < 0 or ROE = 0 then 0
            when ROE > 10 then 10-ROUND(ROE/10)
            else 10-ROUND(ROE)+10 end ) * 2 AS ROE_RK,
       (case when PER < 0 or PER >= 100 or PER = 0 then 0
            when PER > 10 then 10-ROUND(PER/10)
            else 10-ROUND(PER)+10 end )  AS PER_RK,
       (case when PBR >= 10 then 0
            when PBR < 10 and PBR >= 1 then 10-ROUND(PBR)
            else 10-ROUND(PBR*10)+10 end )  AS PBR_RK, 
       case when M.PRICE/M.TARGET_PRICE > 1 then 0 when M.TARGET_PRICE = 0 then 1 when M.PRICE/M.TARGET_PRICE > 0.9 then 1 when M.PRICE/M.TARGET_PRICE > 0.8 then 2
         when M.PRICE/M.TARGET_PRICE > 0.75 then 3 when M.PRICE/M.TARGET_PRICE > 0.7 then 4 when M.PRICE/M.TARGET_PRICE > 0.65 then 5 when M.PRICE/M.TARGET_PRICE > 0.6 then 6
         when M.PRICE/M.TARGET_PRICE > 0.55 then 7 when M.PRICE/M.TARGET_PRICE > 0.5 then 8 when M.PRICE/M.TARGET_PRICE > 0.45 then 9 else 10 end PRICE_RATE,
        case when PBR < 0.5 then 10 when PBR < 0.75 then 9 when PBR < 1 then 8 when PBR < 1.2 then 7 when PBR < 1.5 then 6    when PBR < 2 then 5
          when PBR < 3 then 4 when PBR < 4 then 3 when PBR < 5 then 2 when PBR < 6 then 1 else 0 end PBR_RATE,
        case when M.PER / M.INDUSTRY_PER  > 5 then 0 when M.PER / M.INDUSTRY_PER  > 4 then 4 when M.PER / M.INDUSTRY_PER  > 3 then 5 when M.PER / M.INDUSTRY_PER  > 2 then 6
          when M.PER / M.INDUSTRY_PER  > 1 then 7 when M.PER / M.INDUSTRY_PER  > 0.8 then 8 when M.PER / M.INDUSTRY_PER  > 0.6 then 9 else 10 end PER_RATE,
        case when IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10) ) > 30 then 0 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 20 then 1 
          when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 15  then 2 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 10 then 3
          when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 9 then 4 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 8 then 5 
          when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 7 then 6 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 5 then 7
          when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 3 then 8 when  IFNULL(M.PER /F.PROFITS_RATE, M.PER / (M.INDUSTRY_PER/10))  > 1 then 9
          else 10 end PER_PROFITS_RATE,
        IFNULL(M.PER /F.PROFITS_RATE,0) as PER_PROFITS,
      m.MAX_52, m.MIN_52 ,m.* 
from ST_STOCK_MASTER m left join
	(
		 select FF.STOCK_ID, FF.PROFITS_2021, FF.PROFITS_2020, FF.PROFITS_2019
		  , case when FF.PROFITS_2021 <> 0 then FF.PROFITS_2021/FF.PROFITS_2020 else FF.PROFITS_2020 / FF.PROFITS_2019 end PROFITS_RATE
		from 
		(
			select STOCK_ID,  MAX(case when year ='2021' THEN SF.PROFITS else 0 END) as PROFITS_2021,  MAX(case when year ='2020' THEN SF.PROFITS else 0 END) PROFITS_2020,
			    MAX(case when year ='2019' THEN SF.PROFITS else 0 END) as PROFITS_2019
			from ST_STOCK_FINANCIAL SF
			group by STOCK_ID
		) FF	
	)F on M.STOCK_ID = F.STOCK_ID
where 1=1
-- and m.MARKET_CAPITALIZATION > 1000000000000
-- and m.per < 15
-- and m.pbr < 1.5
-- and m.roe < 15
-- and m.DIVIDEND_RATE  > 2
-- and M.PRICE/M.TARGET_PRICE < 0.65
  and m.per <> 0
--  and m.TARGET_PRICE  <> 0
 and (
   m.name not like '%우'
   and m.name not like '%우B'
 )
-- and name like '삼성%'
-- order by M.PRICE/M.TARGET_PRICE 
-- order by PRICE_RATE + PBR_RATE + PER_PROFITS_RATE DESC
order by PRICE_RK desc



select 1250*12 from dual

and  INDUSTRY_PER / per  > 1.5
and m.INVESTMENT_OPINION >= 3
order by M.PRICE/M.TARGET_PRICE  asc

select 127 * 12 *0.75, 884*12*0.75
from DUAL

select 884 * 12 * 0.75, 125*12*0.67, 125*12*0.1, 12000/9000,  11000/9000,  1250/884, 11000/15000, 12000/15000, 10000/15000, 10500/15000
from dual

select m.name, m.DIVIDEND_RATE, m.per, m.pbr, M.PRICE/M.TARGET_PRICE price_rate, m.INDUSTRY_PER, m.MARKET_CAPITALIZATION market,  m.INVESTMENT_OPINION opinion, m.price, m.TARGET_PRICE , 
      M.PRICE/M.TARGET_PRICE tt, m.MAX_52, m.MIN_52,
      (M.MAX_52-M.PRICE)/(M.MAX_52 -M.MIN_52) GUGAN
from ST_STOCK_INTEREST SI, ST_STOCK_MASTER M
where SI.STOCK_ID  = M.STOCK_ID 
and (
   m.INVESTMENT_OPINION < 3
   or M.PER > 15
   or M.PBR > 2
   or M.PRICE > M.TARGET_PRICE
   or M.PRICE > M.MAX_52
   or M.PRICE/M.TARGET_PRICE > 0.8
   )
order by DIVIDEND_RATE ASC  

select *
from ST_STOCK_INTEREST

delete from ST_STOCK_INTEREST
where NAME in ('대한항공','메리츠증권','한화솔루션','한화에어로스페이스')

insert into ST_STOCK_INTEREST
select STOCK_ID, NAME, 'KTW' STOCK_TYPE
from st_stock_master 
where NAME in ('한국자산신탁','HMM') 


SELECT RANK()OVER( ORDER BY CAL_PER )+RANK()OVER( ORDER BY CAL_ROE) RANKING,
   RANK()OVER( ORDER BY CAL_PER ) RANK_PER, RANK()OVER( ORDER BY CAL_ROE) RANK_ROE,
   AA.NAME, AA.PER, AA.ROE, AA.PRICE, AA.MARKET_CAPITALIZATION MARKET, AA.TARGET_PRICE, AA.INVESTMENT_OPINION INVESTMET,  AA.*
FROM
(
SELECT CASE WHEN S.PER < 0 THEN ABS(S.PER *10000) WHEN S.PER = 0 THEN 10000 ELSE S.PER END CAL_PER, 
	CASE WHEN S.ROE < 0 THEN ABS(S.ROE *10000) WHEN S.ROE = 0 THEN 10000 ELSE S.ROE END CAL_ROE,   S.*
FROM st_stock_master s
)AA
ORDER BY RANKING, INVESTMET DESC;


select GROUP_CONCAT(CATEGORY_NAME,'/') as CATEGORY, NAME
from 
(
select SC.CATEGORY_NAME, SM.NAME, SM.MARKET_CAPITALIZATION, RANK()OVER(partition by SC.CATEGORY_NO order by SM.MARKET_CAPITALIZATION DESC) RK
from ST_STOCK_CATEGORY SC, ST_STOCK_CATEGORY_LINK SCL, ST_STOCK_MASTER SM
where SC.CATEGORY_NO  = SCL.CATEGORY_NO 
and scl.stock_id = sm.stock_id
and SC.CATEGORY_TYPE  = 'Industry'
)AA
where AA.RK = 1
group by NAME


-- 1. 추정EPS * 추정PER
select SSM.NAME, ( SSM.ESTIMATION_EPS * SSM.ESTIMATION_PER)/SSM.PRICE  AS RK1_RATE ,SSM.ESTIMATION_EPS * SSM.ESTIMATION_PER RK1, SSM.PRICE, SSM.ESTIMATION_EPS, SSM.ESTIMATION_PER , SSM.*
from st_stock_master ssm 
order by RK1_RATE desc

-- 2. 현명한초보투자자 공식
-- 적정주가 = (사업가치 + 재산가치 - 고정부채) / 발행주식수
-- 사업가치 = 영업이익 * ((1-법인세율) / 기대수익률)  영업이익: 최근3년간 영업이익평균, 법인세율: 0.25,  기대수익률: 1)주식에 기대수익률과 기업대출금리의 중간값 2)sRim 공식의 할인율
-- 재산가치 = 유동자산 - (유동부채*1.2) + 투자자산   재산가치: 회사가 보유하고 있는 현금과 토지 등 자산, 유동자산 : 1년이내 돈으로 바꿀 수 있는 자산, 유동부채: 만기가 1년 이내 도래하는 부채, 투자자산 : 비유동자산 중에서 기업의 판매활동 이외의 장기간에 결쳐 투자이익을 얻을 목적으로 보유하고 있는 자산
-- 투자자산 구하기 

select   ssf.year, ssf.*
from st_stock_master ssm , st_stock_financial ssf 
where SSM.STOCK_ID = SSF.STOCK_ID
and SSF.year = date_format( DATE_ADD(NOW(), INTERVAL -1 YEAR) ,'%Y') 

-- 3. 적정주가 = BPS  = EPS * 10 = ROE * EPS * 100 의 중간값
select ssm.name, ssm.bps, ssm.eps * 10, ssm.roe*ssm.eps*100, ssm.price, ssm.roe, ssm.eps
	,date_format( DATE_ADD(NOW(), INTERVAL -1 YEAR) ,'%Y'), concat('2023'-3),  ssm.*
from st_stock_master ssm 

-- 5. 사경인 회계사의 S-rim
-- 기업가치 = 자산가치 + 초과이익의 현재가치
-- = 자기자본 + 자기자본*(ROE-할인율)/할인율
-- 할인율 :  https://www.kisrating.com/ratingsStatistics/statics_spread.do BBB-  5년 8.76
-- 적정주가 = 기업가치 / (유통주식수 - 자기주식수[자사주])
-- ROE - 10%  1차 매도가격
-- ROE - 20% : 매수가격 

date_format(now(),'%Y'),  date_format( DATE_ADD(NOW(), INTERVAL -1 YEAR) ,'%Y') , 

select ssm.stock_id, ssm.name, aaa.total_sales_point, aaa.profits_point,  ssm.price, ssm.dividend_rate, ssm.INVESTMENT_OPINION io, ssm.TARGET_PRICE ,ssm.market_capitalization mc, ssm.max_52, ssm.min_52
	 , aaa.profits_4, profits_3, profits_2, profits_1
from 
(
select aa.stock_name, aa.stock_id	
	, case when total_sales_3/total_sales_4 < 1 then 0 else  1 end +
	  case when total_sales_2/total_sales_3 < 1 then 0 else  10 end + 
	  case when total_sales_1/total_sales_2 < 1 then 0 else  100 end total_sales_point
	, case when profits_3/profits_4 < 1 then 0 else truncate(profits_3/profits_4, 0) * 1 end +
	  case when profits_2/profits_3 < 1 then 0 else truncate(profits_2/profits_3 , 0) * 10 end + 
	  case when profits_1/profits_2 < 1 then 0 else truncate(profits_1/profits_2 , 0) * 100 end profits_point
	, total_sales_4, total_sales_3, total_sales_2, total_sales_1,total_sales_3/total_sales_4,total_sales_2/total_sales_3,total_sales_1/total_sales_2
	, profits_4, profits_3, profits_2, profits_1
from 
(
	select  max(ssm.name) stock_name
		, ssm.STOCK_ID 
		, max(case when date_format( DATE_ADD(NOW(), INTERVAL -4 YEAR) ,'%Y') = ssf.year then ssf.total_sales else 0 end) total_sales_4
		, max(case when date_format( DATE_ADD(NOW(), INTERVAL -3 YEAR) ,'%Y') = ssf.year then ssf.total_sales else 0 end) total_sales_3
		, max(case when date_format( DATE_ADD(NOW(), INTERVAL -2 YEAR) ,'%Y') = ssf.year then ssf.total_sales else 0 end) total_sales_2
		, max(case when date_format( DATE_ADD(NOW(), INTERVAL -1 YEAR) ,'%Y') = ssf.year then ssf.total_sales else 0 end) total_sales_1
		, max(case when date_format( DATE_ADD(NOW(), INTERVAL -4 YEAR) ,'%Y') = ssf.year then ssf.PROFITS else 0 end) profits_4
		, max(case when date_format( DATE_ADD(NOW(), INTERVAL -3 YEAR) ,'%Y') = ssf.year then ssf.PROFITS else 0 end) profits_3
		, max(case when date_format( DATE_ADD(NOW(), INTERVAL -2 YEAR) ,'%Y') = ssf.year then ssf.PROFITS else 0 end) profits_2
		, max(case when date_format( DATE_ADD(NOW(), INTERVAL -1 YEAR) ,'%Y') = ssf.year then ssf.PROFITS else 0 end) profits_1
	from st_stock_master ssm  , st_stock_financial ssf 
	where SSM.STOCK_ID = SSF.STOCK_ID
	-- and SSF.year = '2021'
	-- and ssm.stock_id = '005930'
	group by ssm.stock_id
) aa
)aaa, st_stock_master ssm
where 1=1
and aaa.stock_id = ssm.stock_id
and aaa.total_sales_point = '111'
order by profits_point * total_sales_point desc


select SSDP.*
from st_stock_master SSM, st_stock_daily_price ssdp
where SSM.NAME = '삼성전자'
and SSM.STOCK_ID = SSDP.STOCK_ID
order by TRADING_DATE DESC

CREATE INDEX stock_daily_price_IDX1 ON st_stock_daily_price ( STOCK_ID, TRADING_DATE, CLOSING_PRICE );


select *
FROM(
select MAX(IF(SSDP.TRADING_DATE='2021.11.01', SSDP.CLOSING_PRICE,0)) PRICE_1101, 
	MAX(IF(SSDP.TRADING_DATE='2021.12.01', SSDP.CLOSING_PRICE,0)) PRICE_1201,
	MAX(IF(SSDP.TRADING_DATE='2022.01.04', SSDP.CLOSING_PRICE,0)) PRICE_0101,
	SSM.NAME, SSM.PRICE, SSM.DIVIDEND_RATE, SSM.MIN_52, SSM.MAX_52, SSM.MARKET_CAPITALIZATION
from st_stock_daily_price ssdp , st_stock_master ssm 
where SSM.STOCK_ID = SSDP.STOCK_ID
and SSDP.TRADING_DATE in ('2021.11.01','2021.12.01','2022.01.04')
group by SSM.NAME, SSM.PRICE, SSM.DIVIDEND_RATE, SSM.MIN_52, SSM.MAX_52, SSM.MARKET_CAPITALIZATION
)AA
where NAME != '0'
and PRICE_1101 != 0
order by (PRICE_1101 / PRICE) DESC;