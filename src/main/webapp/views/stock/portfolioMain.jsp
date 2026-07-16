<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<%-- 비중 차이 경고 임계값 (%) --%>
<c:set var="DIFF_TH" value="3" />

<style>
/* ===== 포트폴리오 자산배분 대시보드 ===== */
.pf-wrap { --buy:#28a745; --sell:#dc3545; --info:#007bff; --hold:#f59e0b; --sea:#8e5ce0;
  --t1:#333; --t2:#6c757d; --t3:#adb5bd; --bdr:#dee2e6; }
.pf-head { display:flex; align-items:baseline; gap:10px; margin-bottom:12px; flex-wrap:wrap; }
.pf-title { font-size:18px; font-weight:bold; }
.pf-sub { font-size:12px; color:var(--t2); }
.pf-date { margin-left:auto; font-size:11px; color:var(--t2); }

/* 탭 */
.pf-tabs { display:flex; gap:2px; border-bottom:2px solid var(--bdr); margin-bottom:16px; overflow-x:auto; }
.pf-tab { flex-shrink:0; padding:8px 16px; font-size:13px; font-weight:600; color:var(--t2);
  background:none; border:none; border-bottom:2px solid transparent; margin-bottom:-2px; cursor:pointer; white-space:nowrap; }
.pf-tab:hover { color:var(--t1); }
.pf-tab.active { color:var(--info); border-bottom-color:var(--info); }
.pf-panel { display:none; } .pf-panel.active { display:block; }

/* 대시보드 5패널 */
.pf-dash { display:grid; grid-template-columns:1fr 1fr; gap:14px; }
.pf-card { border:1px solid var(--bdr); border-radius:8px; overflow:hidden; background:#fff; }
.pf-card-head { padding:9px 14px; border-bottom:1px solid var(--bdr); background:#f8f9fa; font-weight:bold; font-size:13px; display:flex; align-items:center; gap:8px; }
.pf-card-head .tag { margin-left:auto; font-size:9px; font-weight:bold; padding:2px 7px; border-radius:3px; color:#fff; }
.tag-macro{background:#007bff;} .tag-sea{background:#8e5ce0;} .tag-thm{background:#17a2b8;} .tag-val{background:#e83e8c;}
.pf-card-body { padding:12px 14px; }
.mtbl { width:100%; font-size:12px; border-collapse:collapse; }
.mtbl th { font-size:10px; color:var(--t2); text-align:center; padding:3px 4px; border-bottom:1px solid var(--bdr); }
.mtbl th:first-child{text-align:left;}
.mtbl td { padding:5px 4px; border-bottom:1px solid #f1f3f5; text-align:center; }
.mtbl td:first-child{text-align:left; font-weight:500;}
.mtbl tr:last-child td{border-bottom:none;}
.chip { display:inline-block; font-size:9px; font-weight:bold; padding:1px 6px; border-radius:3px; }
.chip-buy{background:#e6f4ea;color:#28a745;} .chip-hold{background:#fff4e0;color:#f59e0b;} .chip-sell{background:#fde8e8;color:#dc3545;}
.adj-up{color:#28a745;font-weight:bold;} .adj-ne{color:var(--t2);} .adj-dn{color:#dc3545;font-weight:bold;}

.phase-bn { display:flex; align-items:center; gap:9px; padding:7px 11px; border-radius:6px; margin-bottom:10px; background:#f3edfb; border:1px solid #e1d3f5; }
.ph-dot { width:9px; height:9px; border-radius:50%; }
.ph-nm { font-size:13px; font-weight:bold; }
.ph-en { font-size:10px; color:var(--t2); }
.slist { display:flex; flex-direction:column; gap:5px; }
.srow { display:flex; align-items:center; gap:7px; }
.sa { width:90px; font-size:11px; color:var(--t2); }
.sbar { flex:1; height:6px; background:#eef0f3; border-radius:3px; overflow:hidden; }
.sfill { height:100%; border-radius:3px; }
.sp { width:48px; text-align:right; font-size:10px; font-weight:bold; }

.thermo { display:flex; flex-direction:column; gap:13px; }
.ti-head { display:flex; justify-content:space-between; align-items:baseline; margin-bottom:4px; }
.ti-lbl { font-size:11px; color:var(--t2); }
.ti-num { font-size:15px; font-weight:bold; }
.ti-tag { font-size:9px; font-weight:bold; padding:1px 6px; border-radius:3px; margin-left:5px; }
.ti-track { height:8px; border-radius:4px; position:relative; }
.ti-cur { position:absolute; top:-3px; width:3px; height:14px; background:#333; border-radius:2px; transform:translateX(-50%); }
.ti-note { font-size:9px; color:var(--t3); margin-top:3px; }

/* 계좌 페이지 */
.acct-hdr { display:grid; grid-template-columns:repeat(4,1fr); gap:10px; margin-bottom:14px; }
.acct-stat { border:1px solid var(--bdr); border-radius:7px; padding:10px 14px; background:#f8f9fa; }
.as-l { font-size:10px; color:var(--t2); font-weight:bold; }
.as-v { font-size:18px; font-weight:bold; margin-top:2px; }
.as-s { font-size:11px; color:var(--t2); }

/* 유형별 비중 요약 바 */
.type-sum { border:1px solid var(--bdr); border-radius:7px; padding:11px 14px; margin-bottom:14px; }
.type-sum-title { font-size:11px; font-weight:bold; color:var(--t2); margin-bottom:9px; }
.ts-row { display:flex; align-items:center; gap:8px; font-size:11px; margin-bottom:6px; }
.ts-row:last-child{margin-bottom:0;}
.ts-name { width:60px; color:var(--t2); }
.ts-barw { flex:1; position:relative; height:8px; }
.ts-bar { width:100%; height:100%; background:#eef0f3; border-radius:4px; overflow:hidden; }
.ts-fill { height:100%; border-radius:4px; opacity:.75; }
.ts-tgt { position:absolute; top:-2px; width:2px; height:12px; background:#666; transform:translateX(-50%); }
.ts-pct { display:flex; gap:5px; align-items:center; }
.ts-cur { font-weight:bold; width:46px; text-align:right; }
.ts-goal { width:40px; color:var(--t2); }
.ts-diff { width:54px; text-align:right; font-weight:bold; font-size:10px; }
.warn { color:#dc3545 !important; font-weight:bold; }

/* 종목 테이블 */
.sc-sect { font-size:11px; font-weight:bold; color:var(--t2); margin-bottom:8px; }
.hold-tbl { width:100%; font-size:12px; border-collapse:collapse; }
.hold-tbl th { font-size:10px; color:#fff; background:#495057; padding:6px 6px; text-align:right; white-space:nowrap; }
.hold-tbl th:nth-child(1), .hold-tbl th:nth-child(2), .hold-tbl th:nth-child(3) { text-align:left; }
.hold-tbl td { padding:6px 6px; border-bottom:1px solid #f1f3f5; text-align:right; white-space:nowrap; }
.hold-tbl td.l { text-align:left; }
.hold-tbl td.c { text-align:center; }
.hold-tbl tfoot td, .hold-tbl tr.subtot td { background:#f8f9fa; font-weight:bold; border-top:2px solid var(--bdr); }
.amt-s{color:#dc3545;font-weight:bold;} .amt-b{color:#28a745;font-weight:bold;} .amt-n{color:var(--t3);}
.badge-type { font-size:10px; padding:1px 6px; border-radius:3px; color:#fff; }
.bt-stock{background:#6c757d;} .bt-bond{background:#17a2b8;} .bt-comm{background:#f59e0b;}

/* 전체합산 */
.tot-sum { display:grid; grid-template-columns:repeat(4,1fr); gap:10px; margin-bottom:14px; }
.sumtbl { width:100%; font-size:12px; border-collapse:collapse; }
.sumtbl th { font-size:10px; color:var(--t2); padding:6px 10px; border-bottom:1px solid var(--bdr); text-align:right; }
.sumtbl th:first-child{text-align:left;}
.sumtbl td { padding:7px 10px; border-bottom:1px solid #f1f3f5; text-align:right; }
.sumtbl td:first-child{text-align:left; font-weight:bold;}
.sumtbl tfoot td { border-top:2px solid var(--bdr); font-weight:bold; }

.pf-foot { margin-top:14px; padding:9px 13px; border-radius:6px; background:#f8f9fa; border:1px solid var(--bdr); font-size:11px; color:var(--t2); }
.pf-foot li { margin-bottom:2px; }

@media(max-width:900px){ .pf-dash{grid-template-columns:1fr;} .acct-hdr,.tot-sum{grid-template-columns:1fr 1fr;} }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">
<div class="pf-wrap">

  <%-- 사계론 국면 라벨 --%>
  <c:set var="cp" value="${cycleDashboard.currentPhase}"/>
  <c:choose>
    <c:when test="${cp == 'FINANCIAL'}"><c:set var="phaseLabel" value="봄(금융장세)"/></c:when>
    <c:when test="${cp == 'EARNINGS'}"><c:set var="phaseLabel" value="여름(실적장세)"/></c:when>
    <c:when test="${cp == 'REVERSE_FINANCIAL'}"><c:set var="phaseLabel" value="가을(역금융장세)"/></c:when>
    <c:when test="${cp == 'REVERSE_EARNINGS'}"><c:set var="phaseLabel" value="겨울(역실적장세)"/></c:when>
    <c:otherwise><c:set var="phaseLabel" value="봄(금융장세)"/></c:otherwise>
  </c:choose>

  <%-- 사계론 stockType별 신호 (주식/채권/현물 × 4국면) --%>
  <c:set var="seaStockSig" value="HOLD"/><c:set var="seaStockDelta" value="${0}"/>
  <c:set var="seaBondSig"  value="HOLD"/><c:set var="seaBondDelta"  value="${0}"/>
  <c:set var="seaCommSig"  value="HOLD"/><c:set var="seaCommDelta"  value="${0}"/>
  <c:choose>
    <c:when test="${cp == 'FINANCIAL'}">
      <c:set var="seaStockSig" value="BUY"/> <c:set var="seaStockDelta" value="${8}"/>
      <c:set var="seaBondSig"  value="HOLD"/><c:set var="seaBondDelta"  value="${0}"/>
      <c:set var="seaCommSig"  value="BUY"/> <c:set var="seaCommDelta"  value="${3}"/>
    </c:when>
    <c:when test="${cp == 'EARNINGS'}">
      <c:set var="seaStockSig" value="BUY"/> <c:set var="seaStockDelta" value="${10}"/>
      <c:set var="seaBondSig"  value="SELL"/><c:set var="seaBondDelta"  value="${-8}"/>
      <c:set var="seaCommSig"  value="BUY"/> <c:set var="seaCommDelta"  value="${5}"/>
    </c:when>
    <c:when test="${cp == 'REVERSE_FINANCIAL'}">
      <c:set var="seaStockSig" value="SELL"/><c:set var="seaStockDelta" value="${-10}"/>
      <c:set var="seaBondSig"  value="SELL"/><c:set var="seaBondDelta"  value="${-5}"/>
      <c:set var="seaCommSig"  value="BUY"/> <c:set var="seaCommDelta"  value="${5}"/>
    </c:when>
    <c:when test="${cp == 'REVERSE_EARNINGS'}">
      <c:set var="seaStockSig" value="SELL"/><c:set var="seaStockDelta" value="${-15}"/>
      <c:set var="seaBondSig"  value="BUY"/> <c:set var="seaBondDelta"  value="${15}"/>
      <c:set var="seaCommSig"  value="BUY"/> <c:set var="seaCommDelta"  value="${8}"/>
    </c:when>
  </c:choose>

  <%-- 시장 매크로 신호 (일 1회 실시간 계산) --%>
  <c:set var="macroStockSig" value="${not empty macroAdjCoeff ? macroAdjCoeff.stockSig : 'HOLD'}"/>
  <c:set var="macroStockAdj" value="${not empty macroAdjCoeff ? macroAdjCoeff.stockAdj : 5}"/>
  <c:set var="macroBondSig"  value="${not empty macroAdjCoeff ? macroAdjCoeff.bondSig  : 'HOLD'}"/>
  <c:set var="macroBondAdj"  value="${not empty macroAdjCoeff ? macroAdjCoeff.bondAdj  : 0}"/>
  <c:set var="macroCommSig"  value="${not empty macroAdjCoeff ? macroAdjCoeff.commSig  : 'HOLD'}"/>
  <c:set var="macroCommAdj"  value="${not empty macroAdjCoeff ? macroAdjCoeff.commAdj  : 5}"/>

  <div class="pf-head">
    <span class="pf-title">자산배분 대시보드</span>
    <span class="pf-sub">대시보드 · 계좌별 보유종목 매수/매도 · 비중 조정</span>
    <span class="pf-date">
      <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm"/> · ${phaseLabel}
    </span>
  </div>

  <c:if test="${not empty errorMsg}">
    <div class="alert alert-danger" style="font-size:13px;">조회 오류: ${errorMsg}</div>
  </c:if>

  <%-- 탭 --%>
  <div class="pf-tabs">
    <button class="pf-tab active" onclick="pfGo(event,'dashboard')">대시보드</button>
    <c:forEach items="${divisions}" var="dv" varStatus="st">
      <button class="pf-tab" onclick="pfGo(event,'acc${st.index}')">${dv.stockDivision}</button>
    </c:forEach>
    <button class="pf-tab" onclick="pfGo(event,'total')">전체 합산</button>
  </div>

  <%-- ===== 대시보드 탭 ===== --%>
  <div id="pf-dashboard" class="pf-panel active">
    <div class="pf-dash">
      <%-- 1. 시장 매크로 --%>
      <div class="pf-card">
        <div class="pf-card-head">시장 매크로 신호 <span class="tag tag-macro">MACRO</span></div>
        <div class="pf-card-body">
          <table class="mtbl">
            <thead><tr><th>자산군</th><th>신호</th><th>조정계수</th><th>근거</th></tr></thead>
            <tbody>
              <tr><td>한국 주식</td><td><span class="chip chip-buy">BUY</span></td><td class="adj-up">×1.18</td><td style="font-size:10px;color:#888;">금리인하+이익회복</td></tr>
              <tr><td>미국 주식</td><td><span class="chip chip-buy">BUY</span></td><td class="adj-up">×1.22</td><td style="font-size:10px;color:#888;">AI 수요 견조</td></tr>
              <tr><td>채권(미국)</td><td><span class="chip chip-hold">HOLD</span></td><td class="adj-ne">×0.98</td><td style="font-size:10px;color:#888;">연준 동결</td></tr>
              <tr><td>금/원자재</td><td><span class="chip chip-buy">BUY</span></td><td class="adj-up">×1.12</td><td style="font-size:10px;color:#888;">달러약세+헤지</td></tr>
              <tr><td>단기채/현금성</td><td><span class="chip chip-hold">HOLD</span></td><td class="adj-ne">×0.96</td><td style="font-size:10px;color:#888;">단기금리 하락</td></tr>
              <tr><td>현금/MMF</td><td><span class="chip chip-sell">SELL</span></td><td class="adj-dn">×0.82</td><td style="font-size:10px;color:#888;">기회비용 상승</td></tr>
            </tbody>
          </table>
        </div>
      </div>
      <%-- 2. 증시 사계론 --%>
      <div class="pf-card">
        <div class="pf-card-head">증시 사계론 <span class="tag tag-sea">SEASONS</span></div>
        <div class="pf-card-body">
          <div class="phase-bn"><span class="ph-dot" style="background:#28a745;"></span>
            <div><div class="ph-nm">${phaseLabel}</div><div class="ph-en">국면별 자산 적합도 점수 (비중 아님)</div></div></div>
          <div class="slist">
            <div class="srow"><span class="sa">채권</span><div class="sbar"><div class="sfill" style="width:85%;background:#28a745;"></div></div><span class="sp" style="color:#28a745;">85점</span></div>
            <div class="srow"><span class="sa">금</span><div class="sbar"><div class="sfill" style="width:72%;background:#007bff;"></div></div><span class="sp" style="color:#007bff;">72점</span></div>
            <div class="srow"><span class="sa">나스닥100</span><div class="sbar"><div class="sfill" style="width:71%;background:#007bff;"></div></div><span class="sp" style="color:#007bff;">71점</span></div>
            <div class="srow"><span class="sa">S&amp;P500</span><div class="sbar"><div class="sfill" style="width:68%;background:#007bff;"></div></div><span class="sp" style="color:#007bff;">68점</span></div>
            <div class="srow"><span class="sa">KODEX200</span><div class="sbar"><div class="sfill" style="width:62%;background:#f59e0b;"></div></div><span class="sp" style="color:#f59e0b;">62점</span></div>
            <div class="srow"><span class="sa">코리아밸류업</span><div class="sbar"><div class="sfill" style="width:58%;background:#f59e0b;"></div></div><span class="sp" style="color:#f59e0b;">58점</span></div>
          </div>
        </div>
      </div>
      <%-- 3. 시장 온도계 --%>
      <div class="pf-card">
        <div class="pf-card-head">시장 온도계 <span class="tag tag-thm">THERMO</span></div>
        <div class="pf-card-body">
          <div class="thermo">
            <div>
              <div class="ti-head"><span class="ti-lbl">VIX 변동성 지수</span><span><span class="ti-num" style="color:#28a745;">17.8</span><span class="ti-tag chip-buy">안정</span></span></div>
              <div class="ti-track" style="background:linear-gradient(to right,#28a745,#f59e0b 55%,#dc3545);"><div class="ti-cur" style="left:44.5%;"></div></div>
              <div class="ti-note">10↓ 극도안정 | 20 정상 | 30↑ 공포 | 40↑ 패닉</div>
            </div>
            <div>
              <div class="ti-head"><span class="ti-lbl">10Y–2Y 금리 스프레드</span><span><span class="ti-num" style="color:#28a745;">+0.38%</span><span class="ti-tag chip-buy">정상화</span></span></div>
              <div class="ti-track" style="background:linear-gradient(to right,#dc3545,#f59e0b 40%,#28a745 60%);"><div class="ti-cur" style="left:69%;"></div></div>
              <div class="ti-note">-0.5%↓ 역전(침체) | 0 중립 | +0.5%↑ 정상</div>
            </div>
            <div>
              <div class="ti-head"><span class="ti-lbl">공포·탐욕 지수 (CNN)</span><span><span class="ti-num" style="color:#f59e0b;">58</span><span class="ti-tag chip-hold">중립~탐욕</span></span></div>
              <div class="ti-track" style="background:linear-gradient(to right,#dc3545,#f59e0b 35%,#28a745 65%,#f59e0b 88%,#dc3545);"><div class="ti-cur" style="left:58%;"></div></div>
              <div class="ti-note">0–24 극도공포 | 50–74 탐욕 | 75+ 극도탐욕</div>
            </div>
          </div>
        </div>
      </div>
      <%-- 4. 밸류에이션 --%>
      <div class="pf-card">
        <div class="pf-card-head">밸류에이션 스코어카드 <span class="tag tag-val">VALUATION</span></div>
        <div class="pf-card-body" style="overflow-x:auto;">
          <table class="mtbl">
            <thead><tr><th>종목</th><th>PER</th><th>PBR</th><th>배당</th><th>ROE</th><th>종합</th></tr></thead>
            <tbody>
              <tr><td>현대차 <span style="font-size:9px;color:#aaa;">005380</span></td><td>7.2</td><td>0.7</td><td>4.2%</td><td>11.4%</td><td><span class="chip chip-buy">9/10</span></td></tr>
              <tr><td>삼성화재 <span style="font-size:9px;color:#aaa;">000810</span></td><td>9.1</td><td>1.1</td><td>3.8%</td><td>12.6%</td><td><span class="chip chip-buy">8/10</span></td></tr>
              <tr><td>삼성전자 <span style="font-size:9px;color:#aaa;">005930</span></td><td>15.2</td><td>1.3</td><td>2.1%</td><td>8.7%</td><td><span class="chip chip-buy">7/10</span></td></tr>
              <tr><td>NAVER <span style="font-size:9px;color:#aaa;">035420</span></td><td>22.4</td><td>1.8</td><td>0.5%</td><td>8.2%</td><td><span class="chip chip-hold">5/10</span></td></tr>
            </tbody>
          </table>
        </div>
      </div>
      <%-- 5. 계절성 캘린더 --%>
      <% pageContext.setAttribute("nowMonth", new java.util.GregorianCalendar().get(java.util.Calendar.MONTH) + 1); %>
      <c:choose>
        <c:when test="${nowMonth==11||nowMonth==12||nowMonth==1||nowMonth==2||nowMonth==3||nowMonth==4}">
          <c:set var="seasonSig" value="강세"/><c:set var="seasonChip" value="buy"/>
          <c:set var="seasonMsg" value="11~4월 주식 통계적 강세 구간 (Halloween Effect)"/>
        </c:when>
        <c:when test="${nowMonth==5||nowMonth==8||nowMonth==9}">
          <c:set var="seasonSig" value="약세"/><c:set var="seasonChip" value="sell"/>
          <c:set var="seasonMsg" value="5월: Sell in May · 9월: 역사적 최약 · 8월: 변동성"/>
        </c:when>
        <c:otherwise>
          <c:set var="seasonSig" value="중립"/><c:set var="seasonChip" value="hold"/>
          <c:set var="seasonMsg" value="6·7·10월: 뚜렷한 패턴 없음 — 다른 신호 우선"/>
        </c:otherwise>
      </c:choose>
      <div class="pf-card">
        <div class="pf-card-head">계절성 캘린더 <span class="tag" style="background:#20c997;">SEASONAL</span></div>
        <div class="pf-card-body">
          <div style="display:flex;align-items:center;gap:10px;margin-bottom:10px;flex-wrap:wrap;">
            <span style="font-weight:bold;font-size:14px;">${nowMonth}월</span>
            <span class="chip chip-${seasonChip}" style="font-size:11px;padding:2px 9px;">${seasonSig}</span>
            <span style="font-size:11px;color:#666;">${seasonMsg}</span>
          </div>
          <div style="display:flex;gap:3px;flex-wrap:wrap;">
            <c:forEach var="m" begin="1" end="12">
              <c:choose>
                <c:when test="${m==11||m==12||m==1||m==2||m==3||m==4}"><c:set var="mc" value="#e6f4ea"/><c:set var="mf" value="#28a745"/></c:when>
                <c:when test="${m==5||m==8||m==9}">                    <c:set var="mc" value="#fde8e8"/><c:set var="mf" value="#dc3545"/></c:when>
                <c:otherwise>                                           <c:set var="mc" value="#fff4e0"/><c:set var="mf" value="#e67e22"/></c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${m==nowMonth}"><span style="display:inline-block;width:30px;height:28px;line-height:28px;text-align:center;font-size:10px;font-weight:900;border-radius:4px;background:${mc};color:${mf};outline:2px solid ${mf};outline-offset:-2px;">${m}</span></c:when>
                <c:otherwise>              <span style="display:inline-block;width:30px;height:28px;line-height:28px;text-align:center;font-size:10px;font-weight:bold;border-radius:4px;background:${mc};color:${mf};">${m}</span></c:otherwise>
              </c:choose>
            </c:forEach>
          </div>
          <div style="margin-top:6px;font-size:10px;color:#aaa;">■ 녹=강세(11~4월) ■ 빨=약세(5·8·9월) ■ 노=중립 / 통계적 경향, 실제와 다를 수 있음</div>
        </div>
      </div>
      <%-- 6. 200일 이동평균 추세 --%>
      <div class="pf-card">
        <div class="pf-card-head">200일 이동평균 추세 <span class="tag" style="background:#6610f2;">200MA</span></div>
        <div class="pf-card-body">
          <c:choose>
            <c:when test="${not empty trendSignal && trendSignal.spPrice > 0}">
              <table class="mtbl">
                <thead><tr><th>지수</th><th>현재가</th><th>200MA</th><th>이격도</th><th>신호</th></tr></thead>
                <tbody>
                  <tr>
                    <td>S&amp;P500</td>
                    <td><fmt:formatNumber value="${trendSignal.spPrice}" pattern="#,###"/></td>
                    <td><fmt:formatNumber value="${trendSignal.spMa200}" pattern="#,###"/></td>
                    <td class="${trendSignal.spDiffPct > 0 ? 'adj-up' : (trendSignal.spDiffPct < 0 ? 'adj-dn' : 'adj-ne')}">${trendSignal.spDiffPct >= 0 ? '+' : ''}<fmt:formatNumber value="${trendSignal.spDiffPct}" pattern="#,##0.1"/>%</td>
                    <td><span class="chip chip-${trendSignal.spSignal == 'BUY' ? 'buy' : (trendSignal.spSignal == 'SELL' ? 'sell' : 'hold')}">${trendSignal.spSignal}</span></td>
                  </tr>
                  <tr>
                    <td>KOSPI</td>
                    <td><fmt:formatNumber value="${trendSignal.ksPrice}" pattern="#,###"/></td>
                    <td><fmt:formatNumber value="${trendSignal.ksMa200}" pattern="#,###"/></td>
                    <td class="${trendSignal.ksDiffPct > 0 ? 'adj-up' : (trendSignal.ksDiffPct < 0 ? 'adj-dn' : 'adj-ne')}">${trendSignal.ksDiffPct >= 0 ? '+' : ''}<fmt:formatNumber value="${trendSignal.ksDiffPct}" pattern="#,##0.1"/>%</td>
                    <td><span class="chip chip-${trendSignal.ksSignal == 'BUY' ? 'buy' : (trendSignal.ksSignal == 'SELL' ? 'sell' : 'hold')}">${trendSignal.ksSignal}</span></td>
                  </tr>
                </tbody>
              </table>
              <div style="margin-top:6px;font-size:10px;color:#aaa;">MA200 대비 +3%↑ BUY · ±3% HOLD · -3%↓ SELL · 갱신: ${trendSignal.fetchedAt}</div>
            </c:when>
            <c:otherwise>
              <div style="color:#aaa;font-size:12px;padding:8px 0;">200MA 데이터 로딩 중... (일 1회 자동 갱신)</div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
      <%-- 7. 장기 밸류에이션 (PE) --%>
      <div class="pf-card" style="grid-column:1/-1;">
        <div class="pf-card-head">장기 밸류에이션 (PE · CAPE) <span class="tag tag-val">VALUATION2</span></div>
        <div class="pf-card-body">
          <div style="display:flex;gap:24px;flex-wrap:wrap;align-items:flex-start;">
            <div style="flex:1;min-width:180px;">
              <div style="font-size:11px;color:#888;margin-bottom:6px;">S&amp;P500 Trailing PE</div>
              <c:choose>
                <c:when test="${not empty trendSignal && trendSignal.spPE > 0}">
                  <c:choose>
                    <c:when test="${trendSignal.spPE > 30}"><c:set var="peSig" value="SELL"/><c:set var="peMsg" value="고평가 — 장기 기대수익 낮음"/></c:when>
                    <c:when test="${trendSignal.spPE > 22}"><c:set var="peSig" value="HOLD"/><c:set var="peMsg" value="다소 고평가 구간"/></c:when>
                    <c:when test="${trendSignal.spPE > 15}"><c:set var="peSig" value="HOLD"/><c:set var="peMsg" value="역사적 평균 수준"/></c:when>
                    <c:otherwise>                           <c:set var="peSig" value="BUY"/> <c:set var="peMsg" value="저평가 — 장기 매수 기회"/></c:otherwise>
                  </c:choose>
                  <div style="display:flex;align-items:center;gap:8px;margin-bottom:4px;">
                    <span style="font-size:26px;font-weight:bold;">${trendSignal.spPE}</span>
                    <span class="chip chip-${peSig == 'BUY' ? 'buy' : (peSig == 'SELL' ? 'sell' : 'hold')}" style="font-size:11px;">${peSig}</span>
                  </div>
                  <div style="font-size:11px;color:#666;">${peMsg}</div>
                  <div style="margin-top:4px;font-size:10px;color:#aaa;">갱신: ${trendSignal.fetchedAt}</div>
                </c:when>
                <c:otherwise><div style="color:#aaa;font-size:12px;">PE 데이터 로딩 중...</div></c:otherwise>
              </c:choose>
            </div>
            <div style="flex:2;min-width:280px;font-size:11px;color:#555;line-height:1.8;border-left:2px solid #f0f0f0;padding-left:16px;">
              <strong style="font-size:12px;">CAPE (Shiller PE) 해석 가이드</strong><br>
              · CAPE = 현재주가 ÷ 10년 평균 실질이익 (인플레 조정)<br>
              · 역사적 평균 CAPE ≈ 15~17 (S&amp;P500 100년 기준)<br>
              · <span style="color:#dc3545;">CAPE &gt; 30</span>: 버블 경고 (2000 닷컴:44, 2021:38)<br>
              · <span style="color:#e67e22;">CAPE &gt; 25</span>: 주의 구간 — 향후 10년 기대수익 ↓<br>
              · <span style="color:#28a745;">CAPE &lt; 15</span>: 역사적 저평가 — 강력 매수 시그널<br>
              <span style="color:#aaa;font-size:10px;">※ Trailing PE는 CAPE 근사치. 정확한 CAPE → multpl.com</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <%-- ===== 계좌별 탭 (실제 DB 데이터) ===== --%>
  <c:forEach items="${divisions}" var="dv" varStatus="st">
    <div id="pf-acc${st.index}" class="pf-panel">

      <%-- 계좌 합계 사전 계산 --%>
      <c:set var="aTotal" value="0"/><c:set var="aStd" value="0"/>
      <c:set var="aBuy" value="0"/><c:set var="aSell" value="0"/>
      <c:set var="vStock" value="0"/><c:set var="vBond" value="0"/><c:set var="vComm" value="0"/>
      <c:set var="pStock" value="0"/><c:set var="pBond" value="0"/><c:set var="pComm" value="0"/>
      <c:forEach items="${interestList}" var="row">
        <c:if test="${row.stockDivision == dv.stockDivision}">
          <c:set var="aTotal" value="${aTotal + row.totalPrice}"/>
          <c:set var="aStd" value="${aStd + row.standard}"/>
          <c:if test="${row.addPrice > 0}"><c:set var="aBuy" value="${aBuy + row.addPrice}"/></c:if>
          <c:if test="${row.addPrice < 0}"><c:set var="aSell" value="${aSell - row.addPrice}"/></c:if>
          <c:choose>
            <c:when test="${row.stockType == '채권'}"><c:set var="vBond" value="${vBond + row.totalPrice}"/><c:set var="pBond" value="${pBond + row.stockPotion}"/></c:when>
            <c:when test="${row.stockType == '현물'}"><c:set var="vComm" value="${vComm + row.totalPrice}"/><c:set var="pComm" value="${pComm + row.stockPotion}"/></c:when>
            <c:otherwise><c:set var="vStock" value="${vStock + row.totalPrice}"/><c:set var="pStock" value="${pStock + row.stockPotion}"/></c:otherwise>
          </c:choose>
        </c:if>
      </c:forEach>

      <div class="acct-hdr">
        <div class="acct-stat"><div class="as-l">평가금액 (현재)</div><div class="as-v"><fmt:formatNumber value="${aTotal}" pattern="#,###"/></div><div class="as-s">${dv.stockDivision}</div></div>
        <div class="acct-stat"><div class="as-l">기준금액 (목표)</div><div class="as-v"><fmt:formatNumber value="${aStd}" pattern="#,###"/></div><div class="as-s">목표 비중 환산</div></div>
        <div class="acct-stat"><div class="as-l">매수 필요 합계</div><div class="as-v" style="color:#28a745;">+<fmt:formatNumber value="${aBuy}" pattern="#,###"/></div><div class="as-s">부족분 매수</div></div>
        <div class="acct-stat"><div class="as-l">매도 필요 합계</div><div class="as-v" style="color:#dc3545;">-<fmt:formatNumber value="${aSell}" pattern="#,###"/></div><div class="as-s">초과분 매도</div></div>
      </div>

      <%-- 드로우다운 방어 행 (목표대비 손실률) --%>
      <c:set var="ddPct" value="${aStd > 0 ? (aTotal - aStd) / aStd * 100 : 0}"/>
      <c:choose>
        <c:when test="${ddPct <= -20}"><c:set var="ddLevel" value="DANGER"/><c:set var="ddColor" value="#dc3545"/><c:set var="ddBg" value="#fde8e8"/><c:set var="ddMsg" value="방어모드 — 주식 30% 축소 검토"/></c:when>
        <c:when test="${ddPct <= -10}"><c:set var="ddLevel" value="WARN"/><c:set var="ddColor" value="#e67e22"/><c:set var="ddBg" value="#fff4e0"/><c:set var="ddMsg" value="주의 — 주식 10% 현금 전환 검토"/></c:when>
        <c:when test="${ddPct <= -5}"> <c:set var="ddLevel" value="CAUTION"/><c:set var="ddColor" value="#f59e0b"/><c:set var="ddBg" value="#fffbea"/><c:set var="ddMsg" value="모니터링 — 추이 확인 필요"/></c:when>
        <c:when test="${ddPct >= 5}">  <c:set var="ddLevel" value="PROFIT"/><c:set var="ddColor" value="#28a745"/><c:set var="ddBg" value="#e6f4ea"/><c:set var="ddMsg" value="목표 초과 달성 — 차익실현 리밸런싱 고려"/></c:when>
        <c:otherwise>                  <c:set var="ddLevel" value="OK"/>    <c:set var="ddColor" value="#17a2b8"/><c:set var="ddBg" value="#e8f8fc"/><c:set var="ddMsg" value="목표 범위 내 정상"/></c:otherwise>
      </c:choose>
      <div style="display:flex;align-items:center;gap:12px;padding:7px 14px;border-radius:6px;margin-bottom:10px;background:${ddBg};border:1px solid ${ddColor}33;font-size:12px;flex-wrap:wrap;">
        <strong style="color:${ddColor};">드로우다운 방어</strong>
        <span style="font-size:15px;font-weight:bold;color:${ddColor};">${ddPct >= 0 ? '+' : ''}<fmt:formatNumber value="${ddPct}" pattern="#,##0.0"/>%</span>
        <span style="color:#666;">목표 <fmt:formatNumber value="${aStd}" pattern="#,###"/> 대비 현재 <fmt:formatNumber value="${aTotal}" pattern="#,###"/></span>
        <span style="margin-left:auto;font-weight:bold;color:${ddColor};">${ddMsg}</span>
        <span style="font-size:10px;color:#aaa;">※ 목표금액(기준금액) 대비 편차. 전고점 기반 MDD는 별도 추적 필요</span>
      </div>

      <%-- 유형별 비중 조정 (목표 vs 실제) --%>
      <div class="type-sum">
        <div class="type-sum-title">유형별 비중 조정 (세로선 = 목표비중)</div>
        <c:set var="acS" value="${aTotal > 0 ? vStock / aTotal * 100 : 0}"/>
        <c:set var="acB" value="${aTotal > 0 ? vBond / aTotal * 100 : 0}"/>
        <c:set var="acC" value="${aTotal > 0 ? vComm / aTotal * 100 : 0}"/>
        <c:if test="${pStock > 0 || acS > 0}">
          <c:set var="dS" value="${acS - pStock}"/>
          <div class="ts-row"><span class="ts-name">주식</span>
            <span class="ts-barw"><span class="ts-bar"><span class="ts-fill" style="width:${acS > 100 ? 100 : acS}%;background:#6c757d;"></span></span><span class="ts-tgt" style="left:${pStock > 100 ? 100 : pStock}%;"></span></span>
            <span class="ts-pct"><span class="ts-cur"><fmt:formatNumber value="${acS}" pattern="#,##0.0"/>%</span><span style="color:#bbb;">/</span><span class="ts-goal"><fmt:formatNumber value="${pStock}" pattern="#,##0"/>%</span>
              <span class="ts-diff ${dS >= DIFF_TH || dS <= -DIFF_TH ? 'warn' : ''}">${dS >= 0 ? '+' : ''}<fmt:formatNumber value="${dS}" pattern="#,##0.0"/></span></span>
          </div>
        </c:if>
        <c:if test="${pBond > 0 || acB > 0}">
          <c:set var="dB" value="${acB - pBond}"/>
          <div class="ts-row"><span class="ts-name">채권</span>
            <span class="ts-barw"><span class="ts-bar"><span class="ts-fill" style="width:${acB > 100 ? 100 : acB}%;background:#17a2b8;"></span></span><span class="ts-tgt" style="left:${pBond > 100 ? 100 : pBond}%;"></span></span>
            <span class="ts-pct"><span class="ts-cur"><fmt:formatNumber value="${acB}" pattern="#,##0.0"/>%</span><span style="color:#bbb;">/</span><span class="ts-goal"><fmt:formatNumber value="${pBond}" pattern="#,##0"/>%</span>
              <span class="ts-diff ${dB >= DIFF_TH || dB <= -DIFF_TH ? 'warn' : ''}">${dB >= 0 ? '+' : ''}<fmt:formatNumber value="${dB}" pattern="#,##0.0"/></span></span>
          </div>
        </c:if>
        <c:if test="${pComm > 0 || acC > 0}">
          <c:set var="dC" value="${acC - pComm}"/>
          <div class="ts-row"><span class="ts-name">현물</span>
            <span class="ts-barw"><span class="ts-bar"><span class="ts-fill" style="width:${acC > 100 ? 100 : acC}%;background:#f59e0b;"></span></span><span class="ts-tgt" style="left:${pComm > 100 ? 100 : pComm}%;"></span></span>
            <span class="ts-pct"><span class="ts-cur"><fmt:formatNumber value="${acC}" pattern="#,##0.0"/>%</span><span style="color:#bbb;">/</span><span class="ts-goal"><fmt:formatNumber value="${pComm}" pattern="#,##0"/>%</span>
              <span class="ts-diff ${dC >= DIFF_TH || dC <= -DIFF_TH ? 'warn' : ''}">${dC >= 0 ? '+' : ''}<fmt:formatNumber value="${dC}" pattern="#,##0.0"/></span></span>
          </div>
        </c:if>
      </div>

      <%-- 보유 종목별 매수/매도 --%>
      <div style="display:flex;align-items:center;gap:10px;padding:7px 12px;border-radius:6px;background:#f3edfb;border:1px solid #e1d3f5;margin-bottom:10px;font-size:12px;flex-wrap:wrap;">
        <strong style="color:#8e5ce0;">사계론 [${phaseLabel}]</strong>
        <span>주식 <span class="chip chip-${seaStockSig == 'BUY' ? 'buy' : (seaStockSig == 'SELL' ? 'sell' : 'hold')}">${seaStockSig}</span> <span style="color:#aaa;font-size:10px;">2~4개월 선행</span></span>
        <span>채권 <span class="chip chip-${seaBondSig == 'BUY' ? 'buy' : (seaBondSig == 'SELL' ? 'sell' : 'hold')}">${seaBondSig}</span> <span style="color:#aaa;font-size:10px;">3~6개월 선행</span></span>
        <span>현물 <span class="chip chip-${seaCommSig == 'BUY' ? 'buy' : (seaCommSig == 'SELL' ? 'sell' : 'hold')}">${seaCommSig}</span> <span style="color:#aaa;font-size:10px;">동행~2개월</span></span>
        <span style="color:#aaa;margin-left:4px;font-size:10px;">권장조정 = 보유금액 기준</span>
      </div>
      <div class="sc-sect">보유 종목별 리밸런싱 — 조정금액(목표−평가) | 사계론 권장조정</div>
      <div style="overflow-x:auto;">
        <table class="hold-tbl">
          <thead><tr>
            <th>종목명</th><th>코드</th><th>유형</th><th>수량</th>
            <th>평가금액</th><th>목표금액</th><th>조정금액</th><th>동작</th>
            <th>목표비중</th><th>실제비중</th><th>차이</th><th>사계론</th><th>권장조정</th><th>매크로</th><th>매크로조정</th><th>실행?</th>
          </tr></thead>
          <tbody>
            <c:forEach items="${interestList}" var="row">
              <c:if test="${row.stockDivision == dv.stockDivision}">
                <c:set var="pd" value="${(row.actualPotion != null ? row.actualPotion : 0) - row.stockPotion}"/>
                <tr>
                  <td class="l">${row.name}</td>
                  <td class="l" style="color:#999;font-size:11px;">${row.stockId}</td>
                  <td class="c"><span class="badge-type ${row.stockType == '채권' ? 'bt-bond' : (row.stockType == '현물' ? 'bt-comm' : 'bt-stock')}">${row.stockType}</span></td>
                  <td><fmt:formatNumber value="${row.qty}" pattern="#,###"/></td>
                  <td><fmt:formatNumber value="${row.totalPrice}" pattern="#,###"/></td>
                  <td><fmt:formatNumber value="${row.standard}" pattern="#,###"/></td>
                  <td class="${row.addPrice > 0 ? 'amt-b' : (row.addPrice < 0 ? 'amt-s' : 'amt-n')}">
                    <c:choose>
                      <c:when test="${row.addPrice > 0}">+<fmt:formatNumber value="${row.addPrice}" pattern="#,###"/></c:when>
                      <c:when test="${row.addPrice < 0}"><fmt:formatNumber value="${row.addPrice}" pattern="#,###"/></c:when>
                      <c:otherwise>—</c:otherwise>
                    </c:choose>
                  </td>
                  <td class="c">
                    <c:choose>
                      <c:when test="${row.addPrice > 0}"><span class="chip chip-buy">매수</span></c:when>
                      <c:when test="${row.addPrice < 0}"><span class="chip chip-sell">매도</span></c:when>
                      <c:otherwise><span class="amt-n" style="font-size:10px;">유지</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td><fmt:formatNumber value="${row.stockPotion}" pattern="#,##0"/>%</td>
                  <td><c:if test="${row.actualPotion != null}"><fmt:formatNumber value="${row.actualPotion}" pattern="#,##0.0"/>%</c:if><c:if test="${row.actualPotion == null}">-</c:if></td>
                  <td class="${pd >= DIFF_TH || pd <= -DIFF_TH ? 'warn' : ''}">${pd >= 0 ? '+' : ''}<fmt:formatNumber value="${pd}" pattern="#,##0.0"/></td>
                  <c:choose>
                    <c:when test="${row.stockType == '채권'}"><c:set var="seaSig" value="${seaBondSig}"/><c:set var="seaDelta" value="${seaBondDelta}"/></c:when>
                    <c:when test="${row.stockType == '현물'}"><c:set var="seaSig" value="${seaCommSig}"/><c:set var="seaDelta" value="${seaCommDelta}"/></c:when>
                    <c:otherwise><c:set var="seaSig" value="${seaStockSig}"/><c:set var="seaDelta" value="${seaStockDelta}"/></c:otherwise>
                  </c:choose>
                  <c:set var="seaAmt" value="${row.totalPrice * seaDelta / 100}"/>
                  <td class="c"><span class="chip chip-${seaSig == 'BUY' ? 'buy' : (seaSig == 'SELL' ? 'sell' : 'hold')}">${seaSig}</span></td>
                  <td class="${seaAmt > 0 ? 'amt-b' : (seaAmt < 0 ? 'amt-s' : 'amt-n')}">
                    <c:choose>
                      <c:when test="${seaAmt > 0}">+<fmt:formatNumber value="${seaAmt}" pattern="#,###"/></c:when>
                      <c:when test="${seaAmt < 0}"><fmt:formatNumber value="${seaAmt}" pattern="#,###"/></c:when>
                      <c:otherwise>—</c:otherwise>
                    </c:choose>
                  </td>
                  <c:choose>
                    <c:when test="${row.stockType == '채권'}"><c:set var="macSig" value="${macroBondSig}"/><c:set var="macAdj" value="${macroBondAdj}"/></c:when>
                    <c:when test="${row.stockType == '현물'}"><c:set var="macSig" value="${macroCommSig}"/><c:set var="macAdj" value="${macroCommAdj}"/></c:when>
                    <c:otherwise><c:set var="macSig" value="${macroStockSig}"/><c:set var="macAdj" value="${macroStockAdj}"/></c:otherwise>
                  </c:choose>
                  <c:set var="macAmt" value="${row.totalPrice * macAdj / 100}"/>
                  <td class="c"><span class="chip chip-${macSig == 'BUY' ? 'buy' : (macSig == 'SELL' ? 'sell' : 'hold')}">${macSig}</span></td>
                  <td class="${macAmt > 0 ? 'amt-b' : (macAmt < 0 ? 'amt-s' : 'amt-n')}">
                    <c:choose>
                      <c:when test="${macAmt > 0}">+<fmt:formatNumber value="${macAmt}" pattern="#,###"/></c:when>
                      <c:when test="${macAmt < 0}"><fmt:formatNumber value="${macAmt}" pattern="#,###"/></c:when>
                      <c:otherwise>—</c:otherwise>
                    </c:choose>
                  </td>
                  <td class="c">
                    <c:choose>
                      <c:when test="${pd >= 5 || pd <= -5}"><span class="chip chip-sell" style="background:#fde8e8;color:#dc3545;">실행</span></c:when>
                      <c:when test="${pd >= 3 || pd <= -3}"><span class="chip chip-hold" style="background:#fff4e0;color:#e67e22;">검토</span></c:when>
                      <c:otherwise><span style="color:#ccc;font-size:10px;">—</span></c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:if>
            </c:forEach>
          </tbody>
          <tfoot>
            <tr>
              <td class="l" colspan="4">${dv.stockDivision} 합계</td>
              <td><fmt:formatNumber value="${aTotal}" pattern="#,###"/></td>
              <td><fmt:formatNumber value="${aStd}" pattern="#,###"/></td>
              <td colspan="2" style="text-align:left;"><span class="amt-b">매수 +<fmt:formatNumber value="${aBuy}" pattern="#,###"/></span> / <span class="amt-s">매도 -<fmt:formatNumber value="${aSell}" pattern="#,###"/></span></td>
              <td colspan="8"></td>
            </tr>
          </tfoot>
        </table>
      </div>

      <%-- 매크로·사계론 컬럼 설명 --%>
      <details style="margin-top:10px;border:1px solid #e1d3f5;border-radius:6px;background:#faf8ff;">
        <summary style="padding:7px 12px;font-size:12px;font-weight:bold;color:#8e5ce0;cursor:pointer;">
          ℹ 매크로조정 · 사계론 컬럼 설명 (클릭하여 펼치기)
        </summary>
        <div style="padding:10px 14px 14px;font-size:12px;color:#495057;line-height:1.7;">

          <%-- ① 현재 계산값 --%>
          <div style="font-weight:bold;color:#333;margin-bottom:5px;">① 현재 계산된 조정계수 (일 1회 자동 갱신)</div>
          <div style="background:#f8f9fa;border:1px solid #dee2e6;border-radius:5px;padding:8px 12px;margin-bottom:12px;">
            <div style="display:flex;gap:16px;flex-wrap:wrap;margin-bottom:4px;">
              <span>주식 <b style="color:${macroStockSig=='BUY'?'#28a745':(macroStockSig=='SELL'?'#dc3545':'#888')};">${macroStockAdj >= 0 ? '+' : ''}${macroStockAdj}%</b> <span class="chip chip-${macroStockSig=='BUY'?'buy':(macroStockSig=='SELL'?'sell':'hold')}">${macroStockSig}</span></span>
              <span>채권 <b style="color:${macroBondSig=='BUY'?'#28a745':(macroBondSig=='SELL'?'#dc3545':'#888')};">${macroBondAdj >= 0 ? '+' : ''}${macroBondAdj}%</b> <span class="chip chip-${macroBondSig=='BUY'?'buy':(macroBondSig=='SELL'?'sell':'hold')}">${macroBondSig}</span></span>
              <span>현물 <b style="color:${macroCommSig=='BUY'?'#28a745':(macroCommSig=='SELL'?'#dc3545':'#888')};">${macroCommAdj >= 0 ? '+' : ''}${macroCommAdj}%</b> <span class="chip chip-${macroCommSig=='BUY'?'buy':(macroCommSig=='SELL'?'sell':'hold')}">${macroCommSig}</span></span>
            </div>
            <c:if test="${not empty macroAdjCoeff}">
              <div style="font-size:11px;color:#666;">
                참조: VIX <b>${macroAdjCoeff.vix}</b> &nbsp;·&nbsp;
                10Y금리 <b>${macroAdjCoeff.tnx}%</b> &nbsp;·&nbsp;
                스프레드(10Y-3M) <b>${macroAdjCoeff.spread}%p</b> &nbsp;·&nbsp;
                DXY <b>${macroAdjCoeff.dxy}</b>
                &nbsp;&nbsp;<span style="color:#aaa;">갱신: ${macroAdjCoeff.fetchedAt}</span>
              </div>
            </c:if>
            <c:if test="${empty macroAdjCoeff}">
              <span style="color:#dc3545;font-size:11px;">⚠ 실시간 조회 실패 — 기본값 사용 중</span>
            </c:if>
          </div>

          <%-- ② 계산 로직 테이블 3종 --%>
          <div style="font-weight:bold;color:#333;margin-bottom:6px;">② 조정계수 계산 로직</div>
          <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:12px;">

            <%-- 주식 --%>
            <div style="flex:1;min-width:200px;">
              <div style="font-size:11px;font-weight:bold;background:#e8f4ea;padding:3px 7px;border-radius:3px 3px 0 0;border:1px solid #c3e6cb;">주식 조정% &nbsp;<span style="color:#888;font-weight:normal;">범위 −25 ~ +25</span></div>
              <table style="width:100%;border-collapse:collapse;font-size:11px;border:1px solid #dee2e6;">
                <thead><tr style="background:#f8f9fa;"><th style="padding:3px 6px;border-bottom:1px solid #dee2e6;text-align:left;">지표·조건</th><th style="padding:3px 6px;border-bottom:1px solid #dee2e6;text-align:right;">영향</th></tr></thead>
                <tbody>
                  <tr><td style="padding:3px 6px;border-bottom:1px solid #f1f3f5;color:#888;">VIX</td><td></td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 40 (극도공포)</td><td style="padding:2px 6px;text-align:right;color:#28a745;font-weight:bold;">+20%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 30 (공포)</td><td style="padding:2px 6px;text-align:right;color:#28a745;font-weight:bold;">+12%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 20 (불안)</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+5%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 15 (보통)</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+3%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 15 (과열주의)</td><td style="padding:2px 6px;text-align:right;color:#dc3545;">−3%</td></tr>
                  <tr><td style="padding:3px 6px;border-bottom:1px solid #f1f3f5;color:#888;">10Y 금리</td><td></td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 5.0%</td><td style="padding:2px 6px;text-align:right;color:#dc3545;font-weight:bold;">−12%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 4.5%</td><td style="padding:2px 6px;text-align:right;color:#dc3545;">−5%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">3.5 ~ 4.5%</td><td style="padding:2px 6px;text-align:right;color:#888;">0%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 3.5%</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+5%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 3.0%</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+8%</td></tr>
                  <tr><td style="padding:3px 6px;border-bottom:1px solid #f1f3f5;color:#888;">스프레드(10Y−3M)</td><td></td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 1.0%p (정상)</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+3%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">0 ~ 1.0%p</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+1%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;">&lt; 0%p (역전)</td><td style="padding:2px 6px;text-align:right;color:#dc3545;font-weight:bold;">−5%</td></tr>
                </tbody>
              </table>
            </div>

            <%-- 채권 --%>
            <div style="flex:1;min-width:200px;">
              <div style="font-size:11px;font-weight:bold;background:#e8edf8;padding:3px 7px;border-radius:3px 3px 0 0;border:1px solid #b8cce4;">채권 조정% &nbsp;<span style="color:#888;font-weight:normal;">범위 −20 ~ +20</span></div>
              <table style="width:100%;border-collapse:collapse;font-size:11px;border:1px solid #dee2e6;">
                <thead><tr style="background:#f8f9fa;"><th style="padding:3px 6px;border-bottom:1px solid #dee2e6;text-align:left;">지표·조건</th><th style="padding:3px 6px;border-bottom:1px solid #dee2e6;text-align:right;">영향</th></tr></thead>
                <tbody>
                  <tr><td style="padding:3px 6px;border-bottom:1px solid #f1f3f5;color:#888;">10Y 금리 (높을수록 채권 저점)</td><td></td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 5.0% (저점 매수)</td><td style="padding:2px 6px;text-align:right;color:#28a745;font-weight:bold;">+15%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 4.5%</td><td style="padding:2px 6px;text-align:right;color:#28a745;font-weight:bold;">+10%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 4.0%</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+5%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">3.5 ~ 4.0%</td><td style="padding:2px 6px;text-align:right;color:#888;">0%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 3.5%</td><td style="padding:2px 6px;text-align:right;color:#dc3545;">−5%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 3.0% (가격 고점)</td><td style="padding:2px 6px;text-align:right;color:#dc3545;font-weight:bold;">−12%</td></tr>
                  <tr><td style="padding:3px 6px;border-bottom:1px solid #f1f3f5;color:#888;">스프레드(10Y−3M)</td><td></td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; −0.3%p (역전 심화)</td><td style="padding:2px 6px;text-align:right;color:#28a745;font-weight:bold;">+5%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 0%p (역전)</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+3%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;">&gt; 1.5%p (정상)</td><td style="padding:2px 6px;text-align:right;color:#dc3545;">−3%</td></tr>
                </tbody>
              </table>
            </div>

            <%-- 현물(금) --%>
            <div style="flex:1;min-width:200px;">
              <div style="font-size:11px;font-weight:bold;background:#fef9e7;padding:3px 7px;border-radius:3px 3px 0 0;border:1px solid #f9e79f;">현물(금·원자재) 조정% &nbsp;<span style="color:#888;font-weight:normal;">범위 −15 ~ +15</span></div>
              <table style="width:100%;border-collapse:collapse;font-size:11px;border:1px solid #dee2e6;">
                <thead><tr style="background:#f8f9fa;"><th style="padding:3px 6px;border-bottom:1px solid #dee2e6;text-align:left;">지표·조건</th><th style="padding:3px 6px;border-bottom:1px solid #dee2e6;text-align:right;">영향</th></tr></thead>
                <tbody>
                  <tr><td style="padding:3px 6px;border-bottom:1px solid #f1f3f5;color:#888;">DXY 달러 인덱스 (약세 = 금↑)</td><td></td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 95 (달러 약세)</td><td style="padding:2px 6px;text-align:right;color:#28a745;font-weight:bold;">+10%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 100</td><td style="padding:2px 6px;text-align:right;color:#28a745;font-weight:bold;">+7%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 105</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+3%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 108 (달러 강세)</td><td style="padding:2px 6px;text-align:right;color:#dc3545;">−5%</td></tr>
                  <tr><td style="padding:3px 6px;border-bottom:1px solid #f1f3f5;color:#888;">VIX (위험회피 = 금↑)</td><td></td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 30</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+5%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 20</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+2%</td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&lt; 15 (안일)</td><td style="padding:2px 6px;text-align:right;color:#dc3545;">−2%</td></tr>
                  <tr><td style="padding:3px 6px;border-bottom:1px solid #f1f3f5;color:#888;">10Y 금리 (기회비용)</td><td></td></tr>
                  <tr style="background:#fafafa;"><td style="padding:2px 6px 2px 14px;border-bottom:1px solid #f1f3f5;">&gt; 5.0%</td><td style="padding:2px 6px;text-align:right;color:#dc3545;">−5%</td></tr>
                  <tr><td style="padding:2px 6px 2px 14px;">&lt; 3.5%</td><td style="padding:2px 6px;text-align:right;color:#28a745;">+3%</td></tr>
                </tbody>
              </table>
            </div>

          </div><%-- end flex --%>

          <%-- ③ 지표 선행성 --%>
          <div style="font-weight:bold;color:#333;margin-bottom:6px;">③ 지표별 업데이트 주기 및 선행성</div>
          <table style="width:100%;border-collapse:collapse;font-size:11px;margin-bottom:10px;">
            <thead>
              <tr style="background:#f1eeff;">
                <th style="padding:4px 8px;border:1px solid #ddd;text-align:left;">지표</th>
                <th style="padding:4px 8px;border:1px solid #ddd;text-align:center;">업데이트</th>
                <th style="padding:4px 8px;border:1px solid #ddd;text-align:center;">선행</th>
                <th style="padding:4px 8px;border:1px solid #ddd;text-align:left;">해석</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td style="padding:4px 8px;border:1px solid #eee;font-weight:bold;">VIX</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;">실시간</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;color:#e67e22;font-weight:bold;">역행</td>
                <td style="padding:4px 8px;border:1px solid #eee;">VIX 급등 후 1~4주 내 저점. 40↑= 역발상 매수 타이밍</td>
              </tr>
              <tr style="background:#fafafa;">
                <td style="padding:4px 8px;border:1px solid #eee;font-weight:bold;">10Y 금리 (TNX)</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;">일 단위</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;color:#e67e22;">동행~단기</td>
                <td style="padding:4px 8px;border:1px solid #eee;">금리 방향이 채권·주식 밸류에이션 즉각 반영</td>
              </tr>
              <tr>
                <td style="padding:4px 8px;border:1px solid #eee;font-weight:bold;">스프레드(10Y−3M)</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;">일 단위</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;color:#28a745;font-weight:bold;">6~18개월</td>
                <td style="padding:4px 8px;border:1px solid #eee;">역전 후 6~18개월 뒤 침체 시작. 가장 강력한 선행 지표</td>
              </tr>
              <tr style="background:#fafafa;">
                <td style="padding:4px 8px;border:1px solid #eee;font-weight:bold;">DXY (달러 인덱스)</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;">실시간</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;color:#e67e22;">동행</td>
                <td style="padding:4px 8px;border:1px solid #eee;">달러 약세 시 금·신흥국 동시 상승. 거의 즉각 반영</td>
              </tr>
              <tr>
                <td style="padding:4px 8px;border:1px solid #eee;font-weight:bold;">이 컬럼 조정계수</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;color:#28a745;font-weight:bold;">일 1회 자동</td>
                <td style="padding:4px 8px;border:1px solid #eee;text-align:center;">—</td>
                <td style="padding:4px 8px;border:1px solid #eee;">위 4개 지표 실시간 값 기반으로 매일 자동 재계산</td>
              </tr>
            </tbody>
          </table>

          <%-- ④ 사계론 선행 순서 --%>
          <div style="font-weight:bold;color:#333;margin-bottom:4px;">④ 사계론 국면 전환 선행 순서</div>
          <div style="background:#f3edfb;border:1px solid #e1d3f5;border-radius:5px;padding:7px 12px;color:#555;font-size:11px;">
            채권 <b style="color:#17a2b8;">3~6개월 선행</b> →
            성장주 <b style="color:#28a745;">2~4개월 선행</b> →
            경기민감주 <b style="color:#f59e0b;">1~3개월 선행</b> →
            방어주·현물 <b style="color:#888;">동행~2개월</b><br>
            현재 국면 <b style="color:#8e5ce0;">[${phaseLabel}]</b> 이
            공식 전환되기 전에 이미 자산가격이 움직입니다.
            <b>조정 신호 발생 후 수개월 이내 선제적 포지션 조정을 권장합니다.</b>
          </div>

        </div>
      </details>
    </div>
  </c:forEach>

  <%-- ===== 전체 합산 탭 ===== --%>
  <div id="pf-total" class="pf-panel">
    <c:set var="gTotal" value="0"/><c:set var="gStd" value="0"/>
    <c:set var="gBuy" value="0"/><c:set var="gSell" value="0"/>
    <c:forEach items="${interestList}" var="row">
      <c:set var="gTotal" value="${gTotal + row.totalPrice}"/>
      <c:set var="gStd" value="${gStd + row.standard}"/>
      <c:if test="${row.addPrice > 0}"><c:set var="gBuy" value="${gBuy + row.addPrice}"/></c:if>
      <c:if test="${row.addPrice < 0}"><c:set var="gSell" value="${gSell - row.addPrice}"/></c:if>
    </c:forEach>

    <div class="tot-sum">
      <div class="acct-stat"><div class="as-l">총 평가금액</div><div class="as-v"><fmt:formatNumber value="${gTotal}" pattern="#,###"/></div><div class="as-s">${fn:length(divisions)}개 계좌</div></div>
      <div class="acct-stat"><div class="as-l">총 기준금액</div><div class="as-v"><fmt:formatNumber value="${gStd}" pattern="#,###"/></div><div class="as-s">목표 합계</div></div>
      <div class="acct-stat"><div class="as-l">매수 필요 합계</div><div class="as-v" style="color:#28a745;">+<fmt:formatNumber value="${gBuy}" pattern="#,###"/></div><div class="as-s">전체 부족분</div></div>
      <div class="acct-stat"><div class="as-l">매도 필요 합계</div><div class="as-v" style="color:#dc3545;">-<fmt:formatNumber value="${gSell}" pattern="#,###"/></div><div class="as-s">전체 초과분</div></div>
    </div>

    <div style="border:1px solid var(--bdr);border-radius:8px;padding:14px;">
      <div class="sc-sect" style="margin-bottom:12px;">계좌별 리밸런싱 요약</div>
      <div style="overflow-x:auto;">
        <table class="sumtbl">
          <thead><tr><th>계좌</th><th>평가금액</th><th>기준금액</th><th>매수 필요</th><th>매도 필요</th></tr></thead>
          <tbody>
            <c:forEach items="${divisions}" var="dv">
              <c:set var="rT" value="0"/><c:set var="rS" value="0"/><c:set var="rB" value="0"/><c:set var="rSell" value="0"/>
              <c:forEach items="${interestList}" var="row">
                <c:if test="${row.stockDivision == dv.stockDivision}">
                  <c:set var="rT" value="${rT + row.totalPrice}"/>
                  <c:set var="rS" value="${rS + row.standard}"/>
                  <c:if test="${row.addPrice > 0}"><c:set var="rB" value="${rB + row.addPrice}"/></c:if>
                  <c:if test="${row.addPrice < 0}"><c:set var="rSell" value="${rSell - row.addPrice}"/></c:if>
                </c:if>
              </c:forEach>
              <tr>
                <td>${dv.stockDivision}</td>
                <td><fmt:formatNumber value="${rT}" pattern="#,###"/></td>
                <td><fmt:formatNumber value="${rS}" pattern="#,###"/></td>
                <td class="amt-b">+<fmt:formatNumber value="${rB}" pattern="#,###"/></td>
                <td class="amt-s">-<fmt:formatNumber value="${rSell}" pattern="#,###"/></td>
              </tr>
            </c:forEach>
          </tbody>
          <tfoot>
            <tr>
              <td>전체 합계</td>
              <td><fmt:formatNumber value="${gTotal}" pattern="#,###"/></td>
              <td><fmt:formatNumber value="${gStd}" pattern="#,###"/></td>
              <td class="amt-b">+<fmt:formatNumber value="${gBuy}" pattern="#,###"/></td>
              <td class="amt-s">-<fmt:formatNumber value="${gSell}" pattern="#,###"/></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
  </div>

  <ul class="pf-foot">
    <li><b>조정금액 = 목표금액(기준금액) − 평가금액.</b> 양수(초록)=매수 필요, 음수(빨강)=매도 필요.</li>
    <li><b>실제비중</b>은 계좌 내 평가금액 기준, <b>목표비중</b>은 설정값. 차이가 ${DIFF_TH}%p 이상이면 빨간색 강조.</li>
    <li>대시보드 사계론 점수는 비중이 아니라 국면별 자산 적합도(0~100점)입니다.</li>
    <li>데이터 출처: 포트폴리오 현황(ST_STOCK_INTEREST). 종목/목표비중 변경은 "포트폴리오 수정"에서.</li>
  </ul>

</div>
</div>
</div>

<script>
function pfGo(e, id) {
  document.querySelectorAll('.pf-panel').forEach(function(p){ p.classList.remove('active'); });
  document.querySelectorAll('.pf-tab').forEach(function(b){ b.classList.remove('active'); });
  document.getElementById('pf-' + id).classList.add('active');
  e.currentTarget.classList.add('active');
}
</script>
