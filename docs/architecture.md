# Kangong — 아키텍처

> 최종 갱신: 2026-06-21

---

## 기술 스택

| 항목 | 기술 |
|------|------|
| 프레임워크 | Spring Boot 3.4.5 / Java 17 |
| 빌드 | Maven, WAR 패키징 |
| DB | MySQL (SECKIMDB, 127.0.0.1:3306) |
| ORM | MyBatis 혼용 + Spring Data JPA + QueryDSL |
| 뷰 | JSP (Apache Tiles) + Thymeleaf (일부 모듈) |
| 보안 | Spring Security (JDBC 기반, Remember-me) |
| 크롤링 | Jsoup (HTML 파싱) + Selenium (ChromeDriver) |
| 외부 API | Yahoo Finance v7, Naver Finance, ECOS, FRED, Google Calendar |
| 유틸 | Lombok, Log4j2, Apache POI (Excel) |
| 커넥션풀 | HikariCP (max 20) |

---

## 모듈 구조

```
com.kangong
├── KangongApplication.java              # Spring Boot 진입점 (WAR)
│
├── common/                              # 공통 인프라
│   ├── aop/          LogAdvice          # @LogExecutionTime AOP
│   ├── commontable/                     # 범용 동적 테이블
│   ├── config/       TilesConfiguration # Apache Tiles 설정
│   ├── dd/                              # 공통코드(Domain Data)
│   ├── model/        CommonVO, Pagination, MessageVo
│   ├── security/                        # Spring Security 설정·핸들러
│   ├── taglib/       CustomFunction, CustomTag, UserInfoTag
│   └── util/         CommonUtil, SecurityContextUtil, SqlBuilder...
│
├── board/                               # 게시판 (MyBatis + JSP)
│   ├── controller/   BoardController, RestBoardController
│   ├── model/        BoardVO, BoardReplyVO
│   └── service/      BoardService
│
├── user/                                # 사용자 관리 (MyBatis + JSP)
│
├── stock/                               # 국내 주식 (Jsoup+Selenium+MyBatis)
│   ├── controller/   StockController
│   ├── model/        StockVO, StockInterestVO, StockInterestParamVO,
│   │                 StockFinancialVO, StockEsgVO, StockDailyPriceVO,
│   │                 MacroIndicatorVO, StockValueScreenVO, StockRecommendVO
│   └── service/      StockService, MacroIndicatorService
│
├── advstock/                            # 미국 주식 (Yahoo Finance + MyBatis)
│   ├── controller/   AdvStockController, AdvStockYahooController
│   ├── model/        YahooStockVO (currency 필드 포함)
│   ├── parser/       AdvStockParser (Jsoup), AdvStockYahooParser (Yahoo API)
│   └── service/      AdvStockService, AdvStockYahooService, AdvStockRepository
│
├── guestbook/                           # 방명록 (JPA + Thymeleaf)
│   ├── controller/   GuestbookController
│   ├── dto/          GuestbookDTO, PageRequestDTO, PageResultDTO
│   ├── entity/       Guestbook, BaseEntity
│   ├── repository/   GuestbookRepository
│   └── service/      GuestbookServiceImpl
│
├── bootboard/                           # JPA 게시판 (QueryDSL)
│   ├── entity/       Board, Member, Reply
│   └── repository/   BoardRepository, SearchBoardRepositoryImpl
│
├── calendar/                            # 캘린더 (Google Calendar API)
│   ├── controller/   CalendarController
│   ├── google/       GoogleCalendarConfig, GoogleCalendarService
│   └── service/      CalendarService
│
├── marketcycle/                         # 증시 사계론 (ECOS + FRED)
│   ├── controller/   MarketCycleController
│   ├── dto/          CycleDeterminationResult, CycleIndicator, CyclePhase,
│   │                 MacroIndicatorData, SectorOverview, SectorRecommendation
│   └── service/      CycleDeterminator, MacroDataFetcher, MarketCycleService
│
├── retire/                              # 은퇴 시뮬레이션
│   ├── controller/   RetireController
│   ├── dto/          RetireInputDto, RetireYearResult, RealEstateItem
│   └── service/      RetireSimulationService
│
└── sample/                              # 샘플/테스트용
    └── test/jpa/     Memo, MemoRepository
```

---

## 레이어 흐름

```
브라우저
  │
  ▼
Spring Security (인증·권한 필터)
  │
  ▼
Controller (JSP 뷰 반환 or @ResponseBody JSON)
  │
  ├── Service (비즈니스 로직)
  │     ├── MyBatis Mapper XML → MySQL
  │     ├── JPA Repository → MySQL
  │     └── 외부 API (Yahoo, Naver, ECOS, FRED, Google)
  │
  └── 뷰
        ├── JSP + Apache Tiles (대부분 모듈)
        └── Thymeleaf (guestbook, marketcycle, retire)
```

---

## 뷰 레이아웃 구조

### JSP + Tiles
```
baseLayout.jsp
├── header.jsp   (include-header.jsp)
├── menu.jsp     (left nav)
└── [content]   (각 모듈의 JSP)

loginLayout.jsp
└── [content]   (로그인 화면)
```

### Thymeleaf
- `templates/thymeleaf/layout/basic.html` — 기본 레이아웃
- `templates/thymeleaf/guestbook/` — 방명록
- `templates/thymeleaf/marketcycle/` — 증시 사계론
- `templates/thymeleaf/retire/` — 은퇴 시뮬레이션

---

## 주요 설계 결정

### MyBatis vs JPA 혼용 이유
- `stock`, `board`, `user` 등 복잡한 집계 쿼리(윈도우 함수, RANK, SUM OVER PARTITION) → MyBatis
- `guestbook`, `bootboard` 단순 CRUD → JPA

### US 주식 가격 동기화 방식
- **저장 방식**: 실시간 조회가 아닌 DB 저장 (ST_STOCK_MASTER.PRICE)
- `totalPrice = PRICE × STOCK_QTY` — SQL 계산
- 동기화 시점: 포트폴리오 저장 시 즉시 + `updateDaily` 호출 시 일괄

### Yahoo Finance 세션 관리
- Cookie + Crumb 기반 인증 → `AdvStockYahooParser`
- 10분 TTL 세션 캐싱으로 rate-limit 방지
- 심볼 정규화: `BRK.B` → `BRK-B` (Yahoo 형식) → 저장 시 원본 복원

### 포트폴리오 계좌(Division) 구조
- `ST_STOCK_INTEREST_PARAM`: 계좌 정의 (division + 기준금액 + 순서)
- `ST_STOCK_INTEREST`: 계좌별 종목 (division + stockId + qty + potion)
- `ST_STOCK_MASTER`: 공통 가격 저장소 (국내/US 모두)

---

## 외부 의존성

| 서비스 | 목적 | 인증 |
|--------|------|------|
| Yahoo Finance v7 | US 주식 가격, USD/KRW 환율 | Cookie + Crumb (무료) |
| Naver Finance | 국내 주식 크롤링 | 없음 (Jsoup/Selenium) |
| ECOS (한국은행) | 거시경제 지표 | API Key |
| FRED (미국 연준) | 거시경제 지표 | API Key |
| Google Calendar | 일정 연동 | OAuth2 Client |
| ChromeDriver | Selenium 크롤링 | 로컬 설치 필요 |
