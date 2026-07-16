# Kangong — 구현 기능 목록

> 최종 갱신: 2026-06-21

---

## 1. 주식 (stock 모듈) — 가장 큰 모듈

### 1-1. 국내 주식 마스터 관리
- **주식 목록** `GET /stock` → `stockList.jsp`
  - Jsoup + Selenium으로 네이버 금융 크롤링
  - 종목 검색 (종목명/종목코드)
  - 일괄 저장 / 배치 업데이트
- **일별 주가 저장** `updateDaily` — 네이버 일별 시세 크롤링 후 `ST_STOCK_DAILY_PRICE` 저장
- **재무 데이터 저장** — 네이버 재무분석 탭 크롤링 → `ST_STOCK_FINANCIAL`
- **ESG 데이터 조회** `GET /stock2/esg` → `stockEsgList.jsp`
  - 년도/종목코드로 검색
  - 환경(온실가스·에너지·미세먼지), 사회(급여·외국인고용), 지배구조(외부이사비율) 항목

### 1-2. 포트폴리오 관리
- **포트폴리오 현황** `GET /stock2` → `stockInterestList.jsp`
  - 계좌(division)별 종목 목록
  - 평가금액(`PRICE × QTY`) / 기준금액 / 추가필요금액 / 실제비중
  - 52주 최고·최저·비율·위치(pos52) 표시
  - 계좌 필터 드롭다운
- **포트폴리오 수정** `GET /stock2/edit` → `stockInterestEdit.jsp`
  - 종목코드 입력 시 네이버/DB 자동 완성
  - 미국 주식 입력 시 Yahoo Finance 자동 가격 동기화 (저장 즉시)
  - 수량·비중 수정 / 행 추가·삭제
- **종목 저장/삭제 API**
  - `POST /stock2/interest/save` — 저장 + US 주식이면 `syncSingleUsStock` 호출
  - `POST /stock2/interest/delete`
- **계좌(Param) 저장/삭제**
  - `POST /stock2/param/save` / `POST /stock2/param/delete`
- **종목 검색 API** `GET /stock2/searchStock?keyword=` — 네이버 크롤링 → DB fallback
- **Excel 내보내기** `GET /stock2/excel` — 포트폴리오 xlsx 다운로드

### 1-3. 리밸런싱 추천
- `GET /stock2/recommend` → `stockRecommend.jsp`
  - 추가매수 / 추가매도 목록 (기준금액 대비 정렬)
  - Score 기반 우선순위 정렬

### 1-4. 가치주 스크리닝
- `GET /stock2/value` → `stockValueScreen.jsp`
  - 파라미터 필터: PER·PBR 최대, ROE 최소, 배당률 최소, 부채비율 최대, 시가총액 최소, 통과조건 수
  - 매수/매도 확률 계산 (`calculateBuySellProbability`)

### 1-5. 거시경제 지표 (증시 사계론)
- `GET /stock2/macro` → `stockMacroIndicator.jsp`
  - ECOS(한국은행) + FRED(미국 연준) API 연동
  - 지표 카테고리별 테이블/차트
  - `GET /stock2/macro/signals` — 사계론 신호 + 포트폴리오 권장 배분 계산
  - `GET /stock2/macro/chart` — 차트 데이터 (JSON)
  - 6시간 캐시 (`macro.cache.ttl-ms`)

### 1-6. 모바일 주식 목록
- `GET /stock2/mobile` → `stockMobileList.jsp` (별도 mapper: `mapping-query-mobilestock.xml`)

---

## 2. 미국 주식 (advstock 모듈)

### 2-1. Yahoo Finance 연동
- **Yahoo Finance 목록** `GET /advstock/yahoo/list` → `advStockYahooList.jsp`
  - 키워드 검색 → 종목 저장
- **단가 동기화 (포트폴리오 저장 시)** `syncSingleUsStock(stockId)`
  - Yahoo Finance `/v7/finance/quote` API
  - `USDKRW=X` + 해당 티커 1번 API 호출로 환율·단가 동시 조회
  - USD→KRW 변환 후 `ST_STOCK_MASTER.PRICE` 저장
  - 점(`.`) 포함 티커 지원: `BRK.B` → 쿼리 시 `BRK-B` 변환, 저장 시 원본 ID 복원
  - 세션 캐싱 (10분 TTL) — 쿠키+crumb 재사용
- **일일 전체 동기화** `syncInterestUsStocks()` — `updateDaily` 시 자동 호출
  - 포트폴리오의 US 종목(알파벳+점+대시 시작 티커) 전체 일괄 갱신

### 2-2. 네이버 기반 advstock
- `GET /advstock/list` → `advStockList.jsp`

### US 주식 판별 기준
- 패턴: `[A-Za-z][A-Za-z0-9.\-]*` (순수 영문자·점·대시)
- 한국 6자리 숫자 코드는 제외됨

---

## 3. 게시판 (board 모듈)

- **목록** `GET /board` → `boardList.jsp` (페이지네이션, 키워드 검색)
- **조회** `GET /board/view` → `boardView.jsp` (댓글 포함)
- **등록/수정** `GET /board/edit` → `boardEdit.jsp`
- **REST API** `/board/rest/**` — 댓글(BoardReply) CRUD

---

## 4. 사용자 (user 모듈)

- **사용자 목록** `GET /user` → `userList.jsp`
- **상세/수정** `GET /user/view`, `GET /user/edit` → `userView.jsp`, `userEdit.jsp`
- **JSON 수정** `GET /user/editJson` — Ajax 방식
- **Input 수정** `GET /user/editInput`

---

## 5. 보안 (common/security)

- Spring Security 기반 로그인/로그아웃
- **로그인** `GET /customLogin` → `customLogin.jsp`
- **로그아웃** `GET /customLogout` → `customLogout.jsp`
- **접근 거부** → `accessError.jsp`
- Remember-me (JDBC 토큰, 7일)
- 커스텀 핸들러: 로그인 성공/실패, 로그아웃, 접근 거부
- 권한별 페이지: `/security/admin` (ADMIN), `/security/member` (USER+), `/security/all`

---

## 6. 방명록 (guestbook 모듈)

- JPA + Thymeleaf + QueryDSL
- **목록** `GET /thymeleaf/guestbook/list`
- **등록** `GET /thymeleaf/guestbook/register`
- **상세/수정** `GET /thymeleaf/guestbook/read`, `GET /thymeleaf/guestbook/modify`
- 페이지네이션 (`PageRequestDTO`, `PageResultDTO`)

---

## 7. JPA 게시판 (bootboard 모듈)

- Spring Data JPA + QueryDSL
- `Board`, `Member`, `Reply` 엔티티
- `SearchBoardRepository` (QueryDSL 동적 검색)

---

## 8. 캘린더 (calendar 모듈)

- **캘린더 화면** `GET /calendar` → `calendarList.jsp`
- Google Calendar API 연동 (`GoogleCalendarService`)
- BootstrapDatepicker 사용

---

## 9. 마켓 사이클 (marketcycle 모듈)

- `GET /marketcycle/dashboard` → `dashboard.html` (Thymeleaf)
- 경기 사이클 국면 판별 (`CycleDeterminator`)
- 섹터별 추천 (`SectorRecommendation`)
- ECOS + FRED 지표 데이터 활용

---

## 10. 은퇴 시뮬레이션 (retire 모듈)

- `GET /retire/input` → `input.html` (Thymeleaf)
- `POST /retire/simulate` → `result.html`
- 연도별 시뮬레이션 (`RetireYearResult`)
- 부동산 항목 (`RealEstateItem`) 지원

---

## 11. 공통 기능 (common 모듈)

- **공통코드(DD)** `GET /dd/**` → `ddList.jsp`, `ddEdit.jsp`
- **범용 테이블** `GET /commontable/**` → 동적 컬럼 관리
- **AOP 실행시간 로깅** `@LogExecutionTime`
- **MyBatis 인터셉터** (`MybatisLogInterceptor`)
- **커스텀 태그라이브러리** (paging, sum, dynamicSelect, tospan)
- **Tiles 레이아웃** (baseLayout, loginLayout)
- **DB 메시지소스** (`DatabaseMessageSource`)
