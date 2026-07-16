# Kangong — 화면 흐름 및 라우팅

> Context path: `/kangong`  
> 최종 갱신: 2026-06-21

---

## URL 구조 요약

| URL 패턴 | 모듈 | 뷰 엔진 |
|----------|------|---------|
| `/stock`, `/stock2/**` | 국내+US 주식 | JSP + Tiles |
| `/advstock/**` | Yahoo Finance | JSP + Tiles |
| `/board/**` | 게시판 | JSP + Tiles |
| `/user/**` | 사용자 | JSP + Tiles |
| `/thymeleaf/guestbook/**` | 방명록 | Thymeleaf |
| `/marketcycle/**` | 마켓 사이클 | Thymeleaf |
| `/retire/**` | 은퇴 시뮬레이션 | Thymeleaf |
| `/calendar/**` | 캘린더 | JSP + Tiles |
| `/dd/**`, `/commontable/**` | 공통코드·범용테이블 | JSP + Tiles |
| `/customLogin`, `/customLogout` | 보안 | JSP (loginLayout) |

---

## 주식 모듈 (`/stock`, `/stock2`)

```
/stock                          stockList.jsp           국내 주식 목록·크롤링
/stock2                         stockInterestList.jsp   포트폴리오 현황 (계좌별)
  └─ ?stockDivision=XXX                                 계좌 필터
/stock2/edit                    stockInterestEdit.jsp   포트폴리오 수정
  └─ ?stockDivision=XXX
/stock2/recommend               stockRecommend.jsp      리밸런싱 추천 (매수/매도)
/stock2/value                   stockValueScreen.jsp    가치주 스크리닝
/stock2/esg                     stockEsgList.jsp        ESG 데이터
/stock2/macro                   stockMacroIndicator.jsp 거시경제 지표 (사계론)
/stock2/mobile                  stockMobileList.jsp     모바일 뷰
/stock2/excel                   (파일 다운로드)          포트폴리오 Excel 내보내기
```

### REST API (JSON 응답)
```
POST /stock2/interest/save      포트폴리오 종목 저장 → "OK"
POST /stock2/interest/delete    포트폴리오 종목 삭제 → "OK"
POST /stock2/param/save         계좌 설정 저장 → "OK"
POST /stock2/param/delete       계좌 삭제 → "OK"
GET  /stock2/searchStock?keyword= 종목 검색 → List<StockVO> (JSON)
GET  /stock2/macro/data?category= 거시지표 데이터 → List<MacroIndicatorVO>
GET  /stock2/macro/chart?category= 차트용 데이터 → Map<String,Object>
GET  /stock2/macro/signals      사계론 신호 + 포트폴리오 배분 → Map<String,Object>
```

---

## 미국 주식 모듈 (`/advstock`)

```
/advstock/list                  advStockList.jsp        네이버 기반 미국 주식 목록
/advstock/yahoo/list            advStockYahooList.jsp   Yahoo Finance 검색·저장
```

---

## 포트폴리오 화면 흐름

```
[메뉴] 포트폴리오 현황
  └─ GET /stock2
       └─ 계좌 선택 드롭다운
            └─ GET /stock2?stockDivision={계좌명}

[메뉴] 포트폴리오 수정
  └─ GET /stock2/edit
       ├─ 계좌 선택 드롭다운
       ├─ 종목 입력 (자동완성: /stock2/searchStock)
       ├─ 수량·비중 입력
       ├─ 저장 버튼 → POST /stock2/interest/save
       │    └─ US 주식이면 자동으로 Yahoo 가격 동기화
       └─ 삭제 버튼 → POST /stock2/interest/delete
```

---

## 게시판 모듈 (`/board`)

```
GET  /board                     boardList.jsp           목록 (페이지네이션, 검색)
GET  /board/view?boardNo=       boardView.jsp           글 상세 + 댓글
GET  /board/edit                boardEdit.jsp           글 등록·수정
POST /board/save                                        글 저장
POST /board/delete                                      글 삭제
/board/rest/**                  (JSON REST)             댓글 CRUD
```

---

## 사용자 모듈 (`/user`)

```
GET  /user                      userList.jsp            사용자 목록
GET  /user/view?userId=         userView.jsp            상세
GET  /user/edit                 userEdit.jsp            수정 폼
GET  /user/editInput            userEditInput.jsp       Input 방식 수정
GET  /user/editJson             userEditJson.jsp        Ajax 방식 수정
```

---

## 보안·인증 (`/customLogin`, `/security`)

```
GET  /customLogin               customLogin.jsp         로그인 폼
     └─ POST /login                                     Spring Security 처리
GET  /customLogout              customLogout.jsp        로그아웃 확인
     └─ POST /logout                                    Spring Security 처리
GET  /security/all              all.jsp                 전체 공개
GET  /security/member           member.jsp              USER 이상
GET  /security/admin            admin.jsp               ADMIN 전용
GET  /accessError               accessError.jsp         권한 없음
```

---

## 방명록 (`/thymeleaf/guestbook`)

```
GET  /thymeleaf/guestbook/list              list.html       목록 (페이징)
GET  /thymeleaf/guestbook/register          register.html   등록
POST /thymeleaf/guestbook/register                          저장
GET  /thymeleaf/guestbook/read?gno=         read.html       상세
GET  /thymeleaf/guestbook/modify?gno=       modify.html     수정
POST /thymeleaf/guestbook/modify                            수정 저장
POST /thymeleaf/guestbook/remove                            삭제
```

---

## 마켓 사이클 (`/marketcycle`)

```
GET  /marketcycle/dashboard     dashboard.html          사이클 대시보드
                                                        경기 국면, 섹터 추천
```

---

## 은퇴 시뮬레이션 (`/retire`)

```
GET  /retire/input              input.html              입력 폼 (은퇴 파라미터)
POST /retire/simulate           result.html             시뮬레이션 결과 (연도별)
```

---

## 캘린더 (`/calendar`)

```
GET  /calendar                  calendarList.jsp        월별 캘린더
GET  /calendar/google/callback                          Google OAuth2 콜백
```

---

## 공통코드 (`/dd`)

```
GET  /dd                        ddList.jsp              공통코드 목록
GET  /dd/view                   ddView.jsp              상세
GET  /dd/edit                   ddEdit.jsp              등록·수정
```

---

## 범용 테이블 (`/commontable`)

```
GET  /commontable               commontableList.jsp     테이블 목록
GET  /commontable/edit          commontableEdit.jsp     테이블 편집
GET  /commontable/input/edit    commonInputEdit.jsp     데이터 입력
```

---

## 뷰 리졸버 우선순위

1. `thymeleaf/*` 뷰명 → Thymeleaf (`classpath:/templates/thymeleaf/...html`)
2. `kims:/...` 뷰명 → Tiles 정의 (`tiles-def.xml`)
3. 그 외 → JSP (`/views/...jsp`)
