# Kangong — DB 스키마

> DB: MySQL / SECKIMDB  
> 최종 갱신: 2026-06-21

---

## 주식 관련 테이블

### ST_STOCK_MASTER — 주식 마스터
```sql
STOCK_ID            VARCHAR(20)  PK/UNIQUE  -- 종목코드 (한국: 6자리, US: AAPL/BRK.B 등)
ID                  BIGINT       UNIQUE      -- 자동 시퀀스
CREATE_DATE         TIMESTAMP
UPDATE_DATE         TIMESTAMP
DELETE_YN           VARCHAR(1)  DEFAULT 'N'
NAME                VARCHAR(50)
PRICE               BIGINT                  -- 현재가(KRW). US주식도 원화 변환 후 저장
PRICE_BEFOREDAY     BIGINT
FACE_VALUE          BIGINT
MARKET_CAPITALIZATION BIGINT               -- 시가총액(억원, US는 달러→억원 변환)
STOCK_QTY           BIGINT                  -- 상장주식수
FOREIGNER_RATIO     DECIMAL(10,2)
VOLUMN              BIGINT
PER                 DECIMAL(10,2)
ESTIMATION_PER      DECIMAL(10,2)
ROE                 DECIMAL(10,2)
PBR                 DECIMAL(10,2)
BPS                 DECIMAL(10,2)
EPS                 DECIMAL(10,2)
ESTIMATION_EPS      DECIMAL(10,2)
INDUSTRY_PER        DECIMAL(10,2)
INDUSTRY_BAISSE     DECIMAL(10,2)
INVESTMENT_OPINION  VARCHAR(10)
TARGET_PRICE        BIGINT
MAX_52              BIGINT                  -- 52주 최고가
MIN_52              BIGINT                  -- 52주 최저가
DIVIDEND_RATE       DECIMAL(10,2)
NATIONAL            VARCHAR(20)             -- 'KR', 'US' 등
FUND_PAY            (추가 컬럼)
```
**인덱스**: PK(ID), UNIQUE(STOCK_ID)  
**국내·US 공용** — saveFromNaver, saveFromYahoo 모두 이 테이블에 UPSERT

---

### ST_STOCK_INTEREST — 포트폴리오 관심종목
```sql
STOCK_ID            VARCHAR(20)  PK (복합)
STOCK_DIVISION      VARCHAR(50)  PK (복합)  -- 계좌명
STOCK_QTY           INT                     -- 보유수량
STOCK_POTION        DECIMAL                 -- 목표비중
STOCK_TYPE          VARCHAR(20)             -- '주식', 'ETF' 등 (IFNULL 기본값 '주식')
```
**인덱스**: PK(STOCK_ID, STOCK_DIVISION)

---

### ST_STOCK_INTEREST_PARAM — 계좌(Division) 설정
```sql
STOCK_DIVISION      VARCHAR(50)  PK
STANDARD_PRICE      BIGINT                  -- 계좌 기준금액
ORDER_NUM           INT                     -- 정렬순서
```

---

### ST_STOCK_FINANCIAL — 재무 데이터 (연도별)
```sql
ID                  BIGINT       PK
STOCK_ID            VARCHAR(20)
YEAR                VARCHAR(10)
TOTAL_SALES         BIGINT       -- 매출액(억원)
PROFITS             BIGINT       -- 영업이익(억원)
EARNINGS            BIGINT       -- 당기순이익(억원)
PROFITS_RATIO       DECIMAL(10,2) -- 영업이익률
NET_PROFIT_RATIO    DECIMAL(10,2)
ROE                 DECIMAL(10,2)
DEPT_RATIO          DECIMAL(10,2) -- 부채비율
RESERVE_RATIO       DECIMAL(10,2) -- 유보율
EPS / PER / BPS / PBR  DECIMAL(10,2)
DIVIDENDS_PER_SHARE BIGINT
DIVIDENDS_RATE      DECIMAL(10,2)
DIVIDENDS_TENDENCY  DECIMAL(10,2)
STOCK_MASTER_ID     BIGINT       FK → ST_STOCK_MASTER.ID
SHARES_OUTSTANDING  BIGINT       -- 발행주식수
TOTAL_ASSETS / TOTAL_DEPT / TOTAL_CAPITAL / CAPITAL  BIGINT
LIQUID_ASSET / LIQUID_DEPT / TOTAL_STOCK_QTY / COMMON_STOCK_QTY / PREFERRED_STOCK_QTY
DELETE_YN           VARCHAR(1)
```
**인덱스**: UNIQUE(STOCK_ID, YEAR)

---

### ST_STOCK_DAILY_PRICE — 일별 주가
```sql
ID                  BIGINT       PK
STOCK_ID            VARCHAR(20)
TRADING_DATE        VARCHAR(20)             -- 'YYYYMMDD'
CLOSING_PRICE       DECIMAL(10,2)
PREVIOUS_DAY_RATE   DECIMAL(10,2)           -- 전일비
FLUCTUATION_RATE    DECIMAL(10,2)           -- 등락률
VOLUMN              DECIMAL(10,2)
ORGAN_TRADING_VOLUMN      DECIMAL(10,2)
FOREIGN_TRADING_VOLUMN    DECIMAL(20,2)
FOREIGN_HOLDING_VOLUMN    DECIMAL(20,2)
FOREIGN_HOLDING_RATE      DECIMAL(10,2)
```
**인덱스**: UNIQUE(STOCK_ID, TRADING_DATE)

---

### ST_STOCK_ESG — ESG 데이터
```sql
ID                  BIGINT       PK
STOCK_ID            VARCHAR(20)
YEAR                VARCHAR(10)
GREEN_HOUSE_EMISSION        -- 온실가스
ENERGY_USAGE                -- 에너지
FINE_DUST_USAGE             -- 미세먼지
WATER_RECYCLING_RATE        -- 폐수재활용률
WASTE_RECYCLING_RATE        -- 폐기물재활용률
AVERAGE_ANNUAL_SALARY       -- 평균연봉
NON_REGULAR_EMPLYMENT_RATE  -- 비정규직비율
DONATION                    -- 사회공헌
CONTINUOUS_SERVICE_YEAR     -- 평균근속년수
OUTSIDE_DIRECTOR_RATE       -- 사외이사비율
LARGEST_SHARE_HOLDER_RATIO  -- 최대주주비율
DIRECTORATE_INDEPENDENCE    -- 이사회독립성
EXECUTIVE_AVERAGE_ANNUAL_SALARY
SALARY_RATIO                -- 급여비율
UPDATE_DATE
```
**인덱스**: UNIQUE(STOCK_ID, YEAR)

---

### ST_STOCK_MARKET_INDEX — 거시경제 시장지수
```sql
ID          BIGINT   PK
YYYYMMDD    VARCHAR
-- 금리: DOMESTIC_INTEREST_CALL/CD/COFIX_MANF/COFIX_OUTB/NCOFIX_OUTB
-- 기준금리: STANDARD_INTEREST_US/KR/EU/GB/JP
-- 금속: METAL_GC(금)/CMDT(구리)/SI(은)/HG/PL
-- 운임: TRANSPORT_CCF/SCF/BADI/BACK/BPNI/BSIS/BHSI/BAID/BAIT
-- 채권: BOND_US10YT/KR10YT/JP10YT/DE10YT/CN10YT
-- 에너지: ENERGY_CL(WTI)/LCO(브렌트)/RB/HO/DCB
```
**인덱스**: UNIQUE(YYYYMMDD)

---

### ST_STOCK_CATEGORY / ST_STOCK_CATEGORY_LINK — 카테고리
```sql
ST_STOCK_CATEGORY:   ID, CATEGORY_TYPE, CATEGORY_NO(PK), CATEGORY_NAME
ST_STOCK_CATEGORY_LINK: ID, CATEGORY_NO, STOCK_ID  (UNIQUE: CATEGORY_NO+STOCK_ID)
```

---

## 게시판 테이블

### 게시판 (MyBatis — mapping-query-board.xml)
```
주요 테이블: board 관련 테이블 (BoardVO, BoardReplyVO)
```

---

## 사용자·보안 테이블

```sql
-- Spring Security JDBC 인증
MEMBER_TABLE    -- 사용자 (username, password, enabled)
AUTH_TABLE      -- 권한 (username, authority: ROLE_ADMIN/ROLE_MEMBER)
persistent_logins -- Remember-me 토큰 (Spring Security 표준)
```

---

## 공통 테이블

### COM_DD / COM_DD_VALUE — 공통코드
```
공통 도메인 코드 (ComDdVO, ComDdValueVO)
```

### COMMON_TABLE / COMMON_TABLE_COLUMN — 범용 동적 테이블
```
동적 컬럼 정의 및 입력 폼 생성용
```

---

## 시퀀스

```sql
get_seq('테이블명') -- MySQL 함수로 채번
-- 예: get_seq('ST_STOCK_MASTER'), get_seq('ST_STOCK_FINANCIAL')
```

---

## 핵심 쿼리 패턴

### 포트폴리오 평가금액 계산
```sql
SELECT
  IFNULL(M.PRICE, 0) * I.STOCK_QTY totalPrice,
  ROUND(P.STANDARD_PRICE * I.STOCK_POTION) standard,
  RANK() OVER(PARTITION BY I.STOCK_DIVISION ORDER BY ... DESC) rk,
  SUM(IFNULL(M.PRICE, 0) * I.STOCK_QTY) OVER (PARTITION BY I.STOCK_DIVISION) ...
FROM ST_STOCK_INTEREST I
INNER JOIN ST_STOCK_INTEREST_PARAM P ON P.STOCK_DIVISION = I.STOCK_DIVISION
LEFT JOIN ST_STOCK_MASTER M ON M.STOCK_ID = I.STOCK_ID
```
- `PRICE`는 저장된 값 (US 주식은 USD→KRW 변환 후 저장)
- `totalPrice`는 SQL에서 `PRICE × QTY`로 계산 (실시간 API 호출 아님)

### US 주식 Upsert
```sql
INSERT INTO ST_STOCK_MASTER (... STOCK_ID, NAME, PRICE ...)
VALUES (...)
ON DUPLICATE KEY UPDATE
  NAME = #{name}, PRICE = #{price}, MARKET_CAPITALIZATION = #{marketCapitalization}
-- mapping-query-advstock.xml: saveFromYahoo
```
