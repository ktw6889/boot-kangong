SELECT count(AA.id)
FROM  st_stock_master AA
where NOT EXISTS ( select * from ST_STOCK_FINANCIAL sf where sf.STOCK_ID = aa.stock_id) 




ALTER TABLE ST_STOCK_MASTER MODIFY COLUMN MARKET_CAPITALIZATION  BIGINT(30) unsigned COMMENT '시가총액';

select count(*) from ST_STOCK_FINANCIAL;


select FF.STOCK_ID, FF.PROFITS_2021, FF.PROFITS_2020, FF.PROFITS_2019
  , case when FF.PROFITS_2021 <> 0 then FF.PROFITS_2021/FF.PROFITS_2020 else FF.PROFITS_2020 / FF.PROFITS_2019 end PROFITS_RATE
from 
(
	select STOCK_ID,  MAX(case when year ='2021' THEN SF.PROFITS else 0 END) as PROFITS_2021,  MAX(case when year ='2020' THEN SF.PROFITS else 0 END) PROFITS_2020,
	    MAX(case when year ='2019' THEN SF.PROFITS else 0 END) as PROFITS_2019
	from ST_STOCK_FINANCIAL SF
	group by STOCK_ID
) FF	

select 1930 * 0.9
from dual
 

 SELECT SI.*
from ST_STOCK_INTEREST si
where SI.NAME like '%'

insert into ST_STOCK_INTEREST(stock_id, name, stock_type)
select sm.stock_id, sm.name, 'KTW' as stock_type
from st_stock_master sm
where sm.name in ('카카오')

select *
from st_stock_master ssm
where ssm.name LIKE '%'
   
SELECT *
FROM ST_STOCK_FINANCIAL f
where f.STOCK_ID = '035720'

delete FROM ST_STOCK_FINANCIAL F
where f.STOCK_ID = '005930'

ALTER TABLE ST_STOCK_FINANCIAL MODIFY COLUMN liquid_Asset  DECIMAL(20,2)  COMMENT '유동자산';
ALTER TABLE ST_STOCK_FINANCIAL ADD COLUMN liquid_Dept  DECIMAL(20,2)  COMMENT '유동부채';
ALTER TABLE ST_STOCK_FINANCIAL ADD COLUMN total_Stock_Qty  DECIMAL(20,2)  COMMENT '발행주식수';
ALTER TABLE ST_STOCK_FINANCIAL ADD COLUMN common_Stock_Qty  DECIMAL(20,2)  COMMENT '보통주';
ALTER TABLE ST_STOCK_FINANCIAL ADD COLUMN preferred_Stock_Qty  DECIMAL(20,2)  COMMENT '우선주';



select *
from ST_STOCK_CATEGORY;

select *
from ST_STOCK_CATEGORY_LINK;



select *
from ST_STOCK_MASTER

INSERT INTO seq_mysql
VALUES (0, 'ST_STOCK_DAILY_PRICE');

drop table ST_STOCK_DAILY_PRICE;

 
 
 delete from ST_STOCK_DAILY_PRICE where trading_date = '';
 
 select * from ST_STOCK_DAILY_PRICE
 order by trading_date asc
 ;