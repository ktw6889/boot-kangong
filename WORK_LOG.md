# WORK_LOG

> **토큰 관리 주의**: 컨텍스트가 압축(compaction)되거나, 응답이 느려지거나, 이전 대화 내용을 못 찾는 증상이 나타나면 세션을 바꿀 시점입니다. Claude가 판단하여 알려줄 것.

---

## 완료 — Git/GitHub 정리 (2026-07-17)

- [x] GitHub `boot-kangong` 저장소 기본 브랜치를 `master` → `main`으로 변경
- [x] 예전 `master`(Spring Boot 2.4.3 구버전, init 커밋 1개) 삭제 완료 → 원격에 `main`만 존재

## 완료된 작업

### Git 초기화 + 민감정보 분리 + GitHub 연결 (2026-07-17)
- 로컬에 git 저장소 없어서 `git init`, 신규 `.gitignore` 작성(`target/`, `.idea/`, `logs/`, `application-secret.properties`, `.claude/settings.local.json` 제외)
- `application.properties`의 DB 계정/비밀번호, ECOS·FRED API-Key, chromedriver 로컬 경로를 `application-secret.properties`(신규, gitignore 대상)로 분리, `spring.config.import=optional:application-secret.properties`로 로드
- git 미포함 대신 값 없는 템플릿 `application-secret.properties.example` 추가
- git 계정은 이 저장소 로컬 한정으로 설정: `user.name=ktw6889`, `user.email=ktw6889@gmail.com`
- 원격 `https://github.com/ktw6889/boot-kangong.git` 확인 → 예전 구버전 init 커밋 1개만 존재 → 사용자 확인 후 force push로 `main` 브랜치에 최신 소스 반영 완료
- 예전 `master`는 GitHub 기본 브랜치라 삭제 거부됨 → 위 "다음 할 일" 참고

### Git 반영 2차 점검 — 개인정보 잔존 제거 + 로그인 정상 동작 확인 (2026-07-17)
- 재점검 중 발견: `SQL/backup.sql`에 실사용자 개인정보(이름/이메일/생년월일/평문비밀번호 등)가 통째로 커밋되어 있어 git 추적 제거 + `.gitignore` 추가 (로컬 파일은 유지), 커밋 amend 후 force push로 히스토리에서 완전히 제거
- `application.properties`에 죽은 주석 블록으로 남아있던 실제 DB 비밀번호(`1234`) 삭제
- 소스 하드코딩된 실제 개인정보 더미로 교체: `customLogin.jsp`/`MenuUrlTests.java`의 실제 이메일(`orktw@naver.com`) → `admin@kangong.local`, `userView.jsp`/`commonInputEdit.jsp`의 실제 이름·회원정보 → 가상 샘플 데이터
- DB(SECKIMDB)에 `admin@kangong.local`/`1234` 계정 신규 생성(ST_USER_INFO, ST_USER_AUTHORITIES: ROLE_ADMIN/ROLE_MEMBER) — 소스에서 개인정보 제거해도 로그인 정상 동작하도록 실제 로그인 정보 대체
- 별도 이슈 발견 및 수정: `application-secret.properties`에 오타(`4spring.datasource...`)로 DB 계정 인식 실패 → 수정. `application.properties` datasource URL에 `allowPublicKeyRetrieval=true` 추가(MySQL8 caching_sha2_password 공개키 조회 오류 해결)
- `mvn spring-boot:run` + curl로 `admin@kangong.local` 로그인 → `/stock` 200 OK 확인, 세션 쿠키로 인증 필요 페이지 정상 접근 검증 완료

### QQQM 유령 행 삭제 (2026-07-12)
- ST_STOCK_INTEREST에서 STOCK_DIVISION이 깨진 문자(과거 인코딩 오류로 U+FFFD 포함)로 저장된 QQQM 중복 행 1건을 DB에서 직접 삭제
- 조건: `STOCK_DIVISION NOT IN (SELECT STOCK_DIVISION FROM ST_STOCK_INTEREST_PARAM)` — 전체 테이블에 해당 1건만 존재, 삭제 후 QQQM 정상 행 1건만 남음 확인

### QQQM 평가금액 오표시 버그 수정 (2026-07-12) — BUGS.md [ID 64]
- 증상: 수정화면 QQQM 평가금액 3,487원(정상 522만원). daily-update.log에 `PRICE=298.72원`(USD 원가 그대로 저장)
- 원인: 환율(USDKRW=X) 조회 실패 시에도 USD 가격을 그대로 저장 → 연속 저장 시 정상 KRW 값을 덮어씀
- 수정: `AdvStockYahooService` — 환율 조회 실패/가격 파싱 실패 시 저장 건너뛰도록 가드 추가 (syncSingleUsStock, syncInterestUsStocks)
- 검증: `/stock2/interest/save` 실제 호출로 QQQM 재동기화 → 447,742원 정상 변환, 평가금액 5,221,980원 확인
- 참고: 수정화면에 계좌명 깨진 QQQM 유령 행 발견(포트폴리오 총액엔 영향 없음, 별도 이슈로 보류)

### 포트폴리오 수정화면 계좌 변경 기능 (2026-07-12)
- `/stock2/edit` 종목 관리 테이블의 계좌 컬럼을 텍스트→select로 변경, 다른 계좌로 이동 가능
- StockInterestVO에 oldDivision(transient) 추가, saveStockInterest에서 division 변경 시 기존 행 delete 후 재저장
- ST_STOCK_INTEREST 복합키(STOCK_ID+STOCK_DIVISION) 특성상 UPDATE 대신 delete+save 방식 사용

### 포트폴리오 수정화면 저장 버그 수정 (2026-07-05) — BUGS.md [ID 43]
- 증상: `/stock2/edit`에서 기존 종목 저장 시 종목명=종목코드, 평가금액 0
- 원인: `getStockVO`(DELETE_YN='N') null 반환 → else 분기 `saveSimple`이 ON DUPLICATE KEY로 name=코드/price=0 덮어씀
- 수정: `saveSimpleIfAbsent`(비파괴, DELETE_YN 복구만) 신규 → `saveStockMasterMin`이 호출. `saveFromYahoo`/`saveFromNaver` 빈 값·0 덮어쓰기 가드. `selectStockInterestRaw` 종목명 IFNULL 처리

### 노후자산 시뮬레이터 (2026-05-18)
- `/retire` 모듈: Controller, Service, DTO, Thymeleaf 뷰, retireLayout.html
- input.html: 인적정보/월간수입지출/금융자산(본인·배우자)/부동산(복수)/수익률가정/보험/대출
- RetireSimulationService: 본인/배우자 별도 추적, 부동산 복수 매도, 인출순서, 투자분배, 세금, 건보료
- result.html: 요약 카드 4개, Chart.js 3탭, 연도별 상세 테이블

### 가치주 스크리닝 버그 수정 (2026-05-31)
- 원인: 2026년 컨센서스 데이터(943종목)가 MAX(YEAR)로 잡혀 부채비율=0 반환 + 배당성향 미계산(4,996건)
- 수정: selectValueScreen 쿼리에 `YEAR < YEAR(CURDATE())` 조건 추가, 배당성향 인라인 계산(DPS/EPS) 추가
- saveStockFinancial(단일종목)에서도 updateDividendsTendency() 호출 추가

### 가치주 스크리닝 영업활동현금흐름 추가 (2026-06-27)
- 9번째 스크리닝 기준: 영업활동현금흐름 > 0 (현금 창출력 확인)
- wisereport cF3002.aspx rpt=3 (현금흐름표) API 신규 파싱
- StockFinancialParser.getStockCashFlow() 추가 (rpt=3 JSON 파싱)
- StockFieldMappingRegistry.CASH_FLOW_API 추가 ("영업활동현금흐름" 등 4개 레이블 매핑)
- StockService.saveStockCashFlow() / saveCashFlowList() 추가
- StockController: /stock/cashflow/save, /stock/cashflow/saveAll 엔드포인트 추가
- DB 컬럼 필요: ALTER TABLE ST_STOCK_FINANCIAL ADD COLUMN OPERATING_CASH_FLOW VARCHAR(50) NULL
- mapping-query-stock.xml: saveCashFlow SQL 추가, selectValueScreen 9기준 반영
- JSP: 영업현금흐름 컬럼, pass-9 뱃지, /9 필터 반영

### 가치주 스크리닝 유동비율 추가 (2026-06-27)
- 8번째 스크리닝 기준: 유동비율 >= 100% (유동자산/유동부채 × 100)
- 기존 ST_STOCK_FINANCIAL.LIQUID_ASSET, LIQUID_DEPT 컬럼 활용 (새 크롤링 없음)
- FL 서브쿼리에 currentRatio 계산 추가, passCount 8기준·totalCriteria 8로 변경
- StockValueScreenVO에 currentRatio 필드 추가
- JSP: 유동비율 컬럼, 기준설명, pass-8 뱃지, 필터옵션 /8 반영

### 우라가미 사계론 대시보드 (2026-06-05)
- `/marketcycle` 모듈: Controller, Service, DTO(CyclePhase, SectorRecommendation, CycleIndicator, MarketCycleDashboard), Thymeleaf 뷰
- dashboard.html: SVG 사이클 다이어그램, 매수 추천 업종/대장주 카드, 매도 업종, 핵심 지표 테이블
- marketcycleLayout.html: retireLayout과 동일 패턴의 좌측 메뉴 레이아웃
- retireLayout.html에 "증시 사계론" 메뉴 링크 추가

---

### 포트폴리오 사계론 연동 (2026-06-27)
- macroSignals API에 사계론 국면 데이터 추가 (StockController.buildMarketCycleResponse)
- MacroIndicatorService.computePortfolioAllocation 4번째 파라미터 MarketCycleDashboard 추가
- combinedAdjustFactor: macroFactor×0.6 + cycleFactor×0.4 (cycleFactor = 0.7 + buyProb/100×0.6)
- item맵에 cycleBuyProb, cycleSignal 추가
- stockInterestList.jsp: 사계론 국면 배너 (서버사이드), 조정 우선순위 패널 (AJAX)

## 다음 할 일 — 가치주 스크리닝 고도화

### 완료
- ~~Phase 1 (배당연속성, 영익연속성, 7기준)~~ → 이미 완료
- ~~Phase 2 (유동비율 8번째 기준 추가)~~ → 2026-06-27 완료

### Phase 3: 추가 데이터 소스
- ~~영업활동현금흐름 수집~~ → 2026-06-27 완료 (DB 컬럼 추가 후 /stock/cashflow/saveAll 실행 필요)
- ~~밸류업 공시/지수 편입 체크~~ → 2026-06-27 완료 (DB 컬럼 추가 후 /stock/valueup/saveAll 실행 필요)
  - StockValueUpParser: KRX POST API (data.krx.co.kr/comm/bldAttendant/getJsonData.cmd) 파싱
  - ST_STOCK_MASTER.VALUE_UP_YN CHAR(1) 컬럼 추가 필요
  - 10번째 스크리닝 기준 추가, totalCriteria=10, pass-10 뱃지

### 노후자산 시뮬레이터 엑셀 내보내기 (2026-06-27)
- RetireController.exportExcel(): POST /retire/result/excel
- Sheet1(요약): 입력값 + 초기총자산/100세잔여/소진시점
- Sheet2(연도별상세): 21개 컬럼 전체
- result.html: hidden form + 엑셀 다운로드 버튼 추가

### 노후자산 시뮬레이터 보험/대출 초기값 표시 (2026-06-27)
- result.html: 초기 자산 구성 상세 패널 (접기/펼치기) 추가
- 예금입력 + 보험해약환급금 - 대출잔액 = 시뮬레이션 반영 예금 수식을 본인/배우자/합계로 표시

### 노후자산 시뮬레이터 정합성 수정 (2026-06-27)
- CURRENT_YEAR 하드코딩 2026 → Year.now().getValue() 동적 처리
- 연금/IRP 만 55세 미만 중도인출 세율 누락 수정: 5.5% → 16.5% (기타소득세)

### 자산배분 대시보드 신규 개발 (2026-06-27)
- StockController: GET /stock2/portfolio → kims:/stock/portfolioMain
  - 실제 DB 조회: getStockDivisions() + getStockInterestList() (ST_STOCK_INTEREST)
- portfolioMain.jsp: 탭(대시보드 / 계좌별 동적 / 전체합산)
  - 대시보드 5패널: 시장매크로·증시사계론(점수)·시장온도계·밸류에이션 (현재 정적 샘플)
  - 계좌탭: 계좌별로 동적 생성. 헤더(평가/기준/매수합계/매도합계)
    + 유형별 비중조정 바(목표vs실제) + 보유종목별 매수/매도 테이블
    + 조정금액 = addPrice(목표금액-평가금액), 양수=매수/음수=매도
  - 전체합산: 계좌별 요약 + 전체 매수/매도 합계
- stockLeftMenu.jsp: "자산배분 대시보드" 메뉴 추가
- 가짜 com.kangong.portfolio 패키지 삭제 (실제 데이터로 전환)
- 컴파일 통과

### 보류 — 자산배분 대시보드 고도화
- 대시보드 5패널 실데이터 연동 (매크로/사계론점수/온도계 VIX·FRED·CNN/밸류에이션)

---

## 자산배분 대시보드 투자신호 고도화 (2026-06-28 진행중)

### 배경
- portfolioMain.jsp 계좌별 탭에 사계론·매크로 신호 컬럼 추가 완료
- 매크로 조정계수 실시간화 완료 (MacroAdjCoeff, 일 1회 캐시)
- 추가 투자 로직 5종 화면 반영 결정 (Option A+C 혼합)

### 완료 (2026-06-28)
- [x] Task 1: 밴드 리밸런싱 룰 — 테이블 "실행?" 컬럼 (|차이| ≥5%p→실행, ≥3%p→검토) JSP만
- [x] Task 2: 드로우다운 방어 — 계좌 탭 헤더 목표대비 편차% 행 (5단계 색상, DANGER~PROFIT)
- [x] Task 3: 계절성 캘린더 — 대시보드 카드 (JSP 스크립틀릿 현재월, 월별 미니달력)
- [x] Task 4: 200MA 추세 — TrendSignalVO + MacroIndicatorService.computeTrendSignal() + Controller + 카드
- [x] Task 5: PE 밸류에이션 — 대시보드 카드 (S&P500 Trailing PE Yahoo v10, CAPE 가이드)
- 컴파일 통과
- 사계론 적합도 점수를 실제 marketcycle 점수와 연동

### 보류 — 노후자산 시뮬레이터
- 부동산 매도 시나리오 비교, 연금 인출한도
