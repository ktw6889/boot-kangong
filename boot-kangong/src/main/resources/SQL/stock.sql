drop table ST_STOCK_MASTER;

create table ST_STOCK_MASTER(
    ID bigint(20) unsigned NOT NULL COMMENT 'ID',
    CREATE_DATE TIMESTAMP DEFAULT NOW() COMMENT '생성일',
    UPDATE_DATE TIMESTAMP COMMENT '수정일',
    DELETE_YN VARCHAR(1) DEFAULT 'N' COMMENT '삭제여부',
    STOCK_ID  VARCHAR(20) NOT NULL PRIMARY KEY  COMMENT '종목_ID',
	NAME  VARCHAR(50) COMMENT '종목명',
	PRICE  BIGINT(20) unsigned COMMENT '현재가',
	PRICE_BEFOREDAY  BIGINT(20) unsigned COMMENT '전일가',
	FACE_VALUE  BIGINT(10) unsigned COMMENT '액면가',
	MARKET_CAPITALIZATION  BIGINT(20) unsigned COMMENT '시가총액',
	STOCK_QTY  BIGINT(20) unsigned COMMENT '상장주식수',
	FOREIGNER_RATIO  DECIMAL(10,2) unsigned COMMENT '외국인비율',
	VOLUMN  BIGINT(20) unsigned COMMENT '거래량',
	PER  DECIMAL(10,2)  COMMENT 'PER',
	ESTIMATION_PER  DECIMAL(10,2)  COMMENT '추정PER',
	ROE  DECIMAL(10,2)  COMMENT 'ROE',
	PBR  DECIMAL(10,2)  COMMENT 'PBR',
	BPS  DECIMAL(10,2)  COMMENT 'BPS',
	INDUSTRY_PER  DECIMAL(10,2)  COMMENT '동일업종 PER',
	INDUSTRY_BAISSE  DECIMAL(10,2)  COMMENT '동일업종 등락률',
	INVESTMENT_OPINION  VARCHAR(10)  COMMENT '투자의견',
	TARGET_PRICE  BIGINT(20) unsigned COMMENT '목표주가',
	MAX_52  BIGINT(20) unsigned COMMENT '52주 최고',
	MIN_52  BIGINT(20) unsigned COMMENT '52주 최저',
	DIVIDEND_RATE  DECIMAL(10,2) unsigned COMMENT '배당수익률',
	NATIONAL  VARCHAR(20) COMMENT '종목 국가' 
)COMMENT='주식 마스터 테이블' ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

ALTER TABLE ST_STOCK_MASTER ADD COLUMN EPS DECIMAL(10,2)  COMMENT 'EPS';
ALTER TABLE ST_STOCK_MASTER ADD COLUMN ESTIMATION_EPS DECIMAL(10,2)  COMMENT '추정EPS',




SELECT AA.INVESTMENT_OPINION, AA.NAME, AA.PER, AA.ROE, AA.PBR, AA.PRICE, AA.TARGET_PRICE, AA.MARKET_CAPITALIZATION AS MARKET, AA.STOCK_QTY,
   AA.FOREIGNER_RATIO AS FOREIGNER, AA.TARGET_PRICE / AA.PRICE AS INCR, AA.*
FROM ST_STOCK_MASTER AA
ORDER BY INCR DESC, INVESTMENT_OPINION DESC;

SELECT RANK()OVER( ORDER BY CAL_PER )+RANK()OVER( ORDER BY CAL_ROE) RANKING,
   RANK()OVER( ORDER BY CAL_PER ) RANK_PER, RANK()OVER( ORDER BY CAL_ROE) RANK_ROE,
   AA.NAME, AA.PER, AA.ROE, AA.PRICE, AA.MARKET_CAPITALIZATION MARKET, AA.TARGET_PRICE, AA.INVESTMENT_OPINION INVESTMET,  AA.*
FROM
(
SELECT CASE WHEN S.PER < 0 THEN ABS(S.PER *10000) WHEN S.PER = 0 THEN 10000 ELSE S.PER END CAL_PER, 
	CASE WHEN S.ROE < 0 THEN ABS(S.ROE *10000) WHEN S.ROE = 0 THEN 10000 ELSE S.ROE END CAL_ROE,   S.*
FROM st_stock_master s
)AA
ORDER BY INVESTMET DESC, RANKING;


create table ST_STOCK_FINANCIAL(
    ID bigint(20) unsigned NOT NULL COMMENT 'ID',
    CREATE_DATE TIMESTAMP DEFAULT NOW() COMMENT '생성일',
    UPDATE_DATE TIMESTAMP COMMENT '수정일',
    DELETE_YN VARCHAR(1) DEFAULT 'N' COMMENT '삭제여부',
    TOTAL_SALES  BIGINT(20) COMMENT '매출액(억원)',
	PROFITS  BIGINT(20) COMMENT '영업이익(억원)',
	EARNINGS  BIGINT(20) COMMENT '당기순이익(억원)',
	PROFITS_RATIO  DECIMAL(10, 2) COMMENT '영업이익률(%)',
	NET_PROFIT_RATIO  DECIMAL(10, 2) COMMENT '순이익률(%)',
	ROE  DECIMAL(10, 2) COMMENT 'ROE(%)',
	DEPT_RATIO DECIMAL(5, 2) COMMENT '부채비율(%)',
	RESERVE_RATIO  DECIMAL(10, 2) COMMENT '유보율(%)',
	EPS  DECIMAL(10, 2) COMMENT 'EPS(원)',
	PER  DECIMAL(10, 2) COMMENT 'PER(배)',
	BPS  DECIMAL(10, 2) COMMENT 'BPS(배)',
	PBR  DECIMAL(10, 2) COMMENT 'PBR(배)',
	DIVIDENDS_PER_SHARE  BIGINT(20) COMMENT '주당배당금(원)',
	DIVIDENDS_RATE  DECIMAL(10, 2) COMMENT '시가배당률(%)',
	DIVIDENDS_TENDENCY  DECIMAL(10, 2) COMMENT '배당성향(%)',
	YEAR  VARCHAR(10) COMMENT '년도',
	STOCK_ID  VARCHAR(20) COMMENT '종목ID',
	STOCK_MASTER_ID  BIGINT(20) COMMENT 'MASTER_ID',
	SHARES_OUTSTANDING  BIGINT(20) COMMENT '발행주식수',
	TOTAL_ASSETS  BIGINT(20) COMMENT '자산총계',
	TOTAL_DEPT  BIGINT(20) COMMENT '부채총계',
	TOTAL_CAPITAL  BIGINT(20) COMMENT '자본총계',
	CAPITAL  BIGINT(20) COMMENT '자본금',
	PRIMARY KEY(STOCK_ID, YEAR)
)COMMENT='주식 재무 테이블' ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


create table ST_STOCK_INTEREST(
    STOCK_ID  VARCHAR(20) COMMENT '종목_ID',
	NAME  VARCHAR(50) COMMENT '종목명',
	STOCK_TYPE  VARCHAR(50) COMMENT '보유TYPE'
)
 
  create table ST_STOCK_CATEGORY(
    ID bigint(20) unsigned NOT NULL COMMENT 'ID',
    CATEGORY_TYPE  VARCHAR(50) COMMENT '업종종류',
    CATEGORY_NO  VARCHAR(50) PRIMARY KEY COMMENT '업종NO'
    CATEGORY_NAME  VARCHAR(50) COMMENT '업종명'
  )  
 
create table ST_STOCK_CATEGORY_LINK(
    ID bigint(20) unsigned NOT NULL COMMENT 'ID',
    CATEGORY_NO  VARCHAR(50) COMMENT '업종NO',
    STOCK_ID  VARCHAR(20) COMMENT '종목ID',
    PRIMARY KEY(CATEGORY_NO, STOCK_ID)
 )
 

 create table ST_STOCK_DAILY_PRICE(
    ID bigint(20) unsigned NOT NULL COMMENT 'ID',
    STOCK_ID  VARCHAR(20) COMMENT '종목ID',
 	TRADING_DATE  VARCHAR(20) COMMENT '날짜',
	CLOSING_PRICE  DECIMAL(10, 2) COMMENT '종가',
	PREVIOUS_DAY_RATE  DECIMAL(10, 2) COMMENT '전일비',
	FLUCTUATION_RATE  DECIMAL(10, 2) COMMENT '등락률',
	VOLUMN  DECIMAL(10, 2) COMMENT '거래량',
	ORGAN_TRADING_VOLUMN  DECIMAL(10, 2) COMMENT '기관_매매량',
	FOREIGN_TRADING_VOLUMN  DECIMAL(20, 2) COMMENT '외국인_매매량',
	FOREIGN_HOLDING_VOLUMN  DECIMAL(20, 2) COMMENT '외국인_보유주수',
	FOREIGN_HOLDING_RATE  DECIMAL(10, 2) COMMENT '외국인_보유율',
	PRIMARY KEY(STOCK_ID, TRADING_DATE)
 )