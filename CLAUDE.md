# Kangong Project

## Quick Context
Spring Boot 3.4.5 / Java 17 / Maven WAR 프로젝트.
MySQL(SECKIMDB) + MyBatis/JPA(QueryDSL) 혼용. JSP + Thymeleaf 뷰.

## Modules
- `board` — 게시판 (MyBatis + JSP)
- `user` — 사용자 관리 (MyBatis + JSP)
- `stock` — 주식 크롤링/조회 (Jsoup + Selenium, 가장 큰 모듈)
- `advstock` — Yahoo Finance 고급 주식
- `guestbook` — 방명록 (JPA + Thymeleaf)
- `bootboard` — JPA/QueryDSL 게시판
- `common` — 보안(Spring Security), 유틸, AOP, 태그라이브러리, 공통코드(dd), 범용테이블

## Build & Run
```bash
mvn spring-boot:run
# http://localhost:8080/kangong
```

## Conventions — 토큰 절약 원칙

### 응답 스타일
- 응답은 항상 간결하게. 작업 완료 후 요약은 1~2문장 이내.
- 코드 변경 시 변경된 부분만 보여주고, 변경하지 않은 코드는 생략.
- 설명이 필요한 경우에도 핵심만 짧게. 장황한 배경 설명 금지.

### 탐색 최소화
- 프로젝트 구조/아키텍처 질문 → memory/project_overview.md 먼저 참조. Glob/Grep으로 재탐색하지 않기.
- 파일 Read 시 필요한 라인 범위만 읽기 (offset/limit 활용).
- 이미 읽은 파일 내용을 재확인 목적으로 다시 읽지 않기.

### 도구 사용
- 독립적인 도구 호출은 반드시 병렬 실행.
- 단순 확인용 Glob/Grep은 가능한 한 번에 해결 (패턴을 정확하게).
- Bash보다 전용 도구(Read, Edit, Grep, Glob) 우선 사용.
