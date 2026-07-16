<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<%-- ========================================================
  비중 차이 경고 임계값 (%)
  이 값을 변경하면 소계/합계의 빨간색 강조 기준이 바뀝니다.
  예) 3 → 3% 이상 차이 시 빨간색, 5 → 5% 이상
========================================================= --%>
<c:set var="POTION_DIFF_THRESHOLD" value="3" />

<style>
.table-stock { font-size: 13px; }
.table-stock th { text-align: center; white-space: nowrap; vertical-align: middle !important; }
.table-stock td { text-align: right; white-space: nowrap; vertical-align: middle !important; }
.table-stock td.text-left { text-align: left; }
.table-stock td.text-center { text-align: center; }
.rate-positive { color: #dc3545; }
.rate-negative { color: #007bff; }
.add-positive { color: #dc3545; font-weight: bold; }
.add-negative { color: #007bff; }
.warn-highlight { color: #dc3545; font-weight: bold; }
.badge-rank { display: inline-block; width: 22px; height: 22px; line-height: 22px; text-align: center; border-radius: 50%; color: #fff; font-size: 11px; font-weight: bold; }
.badge-rank-1 { background-color: #dc3545; }
.badge-rank-2 { background-color: #fd7e14; }
.badge-rank-3 { background-color: #ffc107; color: #333; }
.badge-rank-other { background-color: #6c757d; }
.summary-row td { background-color: #f8f9fa; font-weight: bold; border-top: 2px solid #dee2e6; }
.filter-bar { display: flex; align-items: center; gap: 10px; margin-bottom: 15px; }
.filter-bar select { width: 200px; }
.range52-container { min-width: 140px; padding: 2px 0; }
.range52-row { display: flex; align-items: center; gap: 4px; }
.range52-track { position: relative; flex: 1; height: 6px; background: #e9ecef; border-radius: 3px; }
.range52-pos { position: absolute; top: 50%; width: 10px; height: 10px; background: #dc3545; border-radius: 50%; border: 2px solid #fff; box-shadow: 0 0 3px rgba(0,0,0,.3); transform: translate(-50%,-50%); z-index: 1; }
.range52-label { font-size: 10px; color: #999; white-space: nowrap; }
.range52-price { text-align: center; font-size: 11px; font-weight: bold; color: #333; margin-top: 1px; }
.type-breakdown { white-space: normal !important; min-width: 110px; }
.type-row { display: flex; justify-content: space-between; align-items: center; gap: 6px; line-height: 1.7; font-size: 12px; }
.type-label { color: #555; font-weight: normal; min-width: 26px; }
.type-val { text-align: right; }
.type-diff { font-size: 10px; color: #999; }
.type-warn .type-val, .type-warn .type-diff { color: #dc3545; font-weight: bold; }
.macro-badge { display:inline-block; padding:2px 6px; border-radius:3px; font-size:10px; font-weight:bold; color:#fff; }
.macro-badge-buy { background:#28a745; }
.macro-badge-sell { background:#dc3545; }
.macro-badge-caution { background:#ffc107; color:#333; }
.macro-badge-neutral { background:#6c757d; }
.macro-adjust-positive { color:#dc3545; font-weight:bold; }
.macro-adjust-negative { color:#007bff; font-weight:bold; }
.table-stock th:nth-child(11), .table-stock td:nth-child(11),
.table-stock th:nth-child(12), .table-stock td:nth-child(12) { max-width:50px; font-size:12px; }
/* 사계론 배너 */
.cycle-banner { display:flex; align-items:center; gap:10px; padding:8px 14px; border-radius:6px; margin-bottom:14px; font-size:13px; border:1px solid rgba(0,0,0,.08); }
.cycle-phase-tag { display:inline-block; padding:3px 10px; border-radius:4px; font-size:12px; font-weight:bold; color:#fff; }
.cycle-tag-FINANCIAL { background:#4CAF50; }
.cycle-tag-EARNINGS { background:#2196F3; }
.cycle-tag-REVERSE_FINANCIAL { background:#FF9800; }
.cycle-tag-REVERSE_EARNINGS { background:#f44336; }
.cycle-progress-wrap { display:inline-flex; align-items:center; gap:5px; font-size:11px; color:#666; }
.cycle-progress-bar { width:80px; height:7px; background:#e9ecef; border-radius:4px; overflow:hidden; display:inline-block; vertical-align:middle; }
.cycle-progress-fill { height:100%; border-radius:4px; }
/* 조정 우선순위 */
.priority-panel { margin-bottom:16px; }
.priority-panel .panel-title { font-size:13px; font-weight:bold; margin-bottom:6px; color:#333; }
.priority-table { width:100%; font-size:12px; border-collapse:collapse; }
.priority-table th { background:#f1f3f5; text-align:center; padding:4px 8px; border:1px solid #dee2e6; white-space:nowrap; font-weight:600; }
.priority-table td { padding:4px 8px; border:1px solid #dee2e6; text-align:right; white-space:nowrap; }
.priority-table td.text-left { text-align:left; }
.priority-table td.text-center { text-align:center; }
.action-buy { color:#28a745; font-weight:bold; }
.action-reduce { color:#dc3545; font-weight:bold; }
.action-slight-buy { color:#5cb85c; }
.action-slight-reduce { color:#e07b7b; }
.cycle-prob { display:inline-block; padding:1px 5px; border-radius:3px; font-size:10px; font-weight:bold; color:#fff; }
.cycle-prob-buy { background:#28a745; }
.cycle-prob-sell { background:#dc3545; }
.cycle-prob-hold { background:#6c757d; }
/* 기간 수익률 셀 */
.rate-cell { text-align:center !important; min-width:60px; }
.rate-badge { display:inline-block; padding:2px 7px; border-radius:12px; font-size:11px; font-weight:bold; white-space:nowrap; }
.rate-badge-up { background:#fff0f0; color:#dc3545; }
.rate-badge-dn { background:#f0f4ff; color:#1a6fb5; }
.rate-badge-zero { background:#f5f5f5; color:#999; }
.rate-badge-up.rate-hot { background:#dc3545; color:#fff; }
.rate-badge-dn.rate-cold { background:#1a6fb5; color:#fff; }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

    <h5>포트폴리오 현황</h5>
    <p class="text-muted" style="font-size:12px;">조회일시: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm"/></p>

    <%-- 사계론 국면 배너 --%>
    <c:if test="${not empty cycleDashboard}">
        <c:set var="cp" value="${cycleDashboard.currentPhase}"/>
        <c:choose>
            <c:when test="${cp == 'FINANCIAL'}"><c:set var="phaseLabel" value="금융장세 (봄)"/><c:set var="phaseBg" value="#e8f5e9"/></c:when>
            <c:when test="${cp == 'EARNINGS'}"><c:set var="phaseLabel" value="실적장세 (여름)"/><c:set var="phaseBg" value="#e3f2fd"/></c:when>
            <c:when test="${cp == 'REVERSE_FINANCIAL'}"><c:set var="phaseLabel" value="역금융장세 (가을)"/><c:set var="phaseBg" value="#fff3e0"/></c:when>
            <c:when test="${cp == 'REVERSE_EARNINGS'}"><c:set var="phaseLabel" value="역실적장세 (겨울)"/><c:set var="phaseBg" value="#ffebee"/></c:when>
            <c:otherwise><c:set var="phaseLabel" value="${cp}"/><c:set var="phaseBg" value="#f5f5f5"/></c:otherwise>
        </c:choose>
        <div class="cycle-banner" style="background:${phaseBg};">
            <span class="cycle-phase-tag cycle-tag-${cp}">${phaseLabel}</span>
            <span class="cycle-progress-wrap">
                진행률
                <span class="cycle-progress-bar">
                    <span class="cycle-progress-fill" style="width:${cycleDashboard.progressPercent}%; background:${cp == 'FINANCIAL' ? '#4CAF50' : cp == 'EARNINGS' ? '#2196F3' : cp == 'REVERSE_FINANCIAL' ? '#FF9800' : '#f44336'};"></span>
                </span>
                ${cycleDashboard.progressPercent}%
            </span>
            <span style="color:#555; flex:1;">${cycleDashboard.diagnosisSummary}</span>
            <a href="<c:url value='/marketcycle/dashboard'/>" style="font-size:11px; color:#888;">사계론 상세 →</a>
        </div>
    </c:if>

    <%-- 조정 우선순위 패널 (AJAX 로드) --%>
    <div class="priority-panel" id="priority-panel" style="display:none;">
        <div class="panel-title">📋 실시간 포트폴리오 조정 우선순위</div>
        <table class="priority-table">
            <thead>
                <tr>
                    <th>자산분류</th>
                    <th>매크로</th>
                    <th>사계론</th>
                    <th>현재비중</th>
                    <th>추천비중</th>
                    <th>변화</th>
                    <th>조정금액(천원)</th>
                    <th>액션</th>
                </tr>
            </thead>
            <tbody id="priority-tbody"></tbody>
        </table>
    </div>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger" style="font-size:13px;">
            조회 오류: ${errorMsg}
        </div>
    </c:if>

    <div class="filter-bar">
        <label for="divisionFilter" style="margin-bottom:0; font-size:13px; font-weight:bold;">계좌:</label>
        <select id="divisionFilter" class="form-control form-control-sm">
            <option value="">전체</option>
            <c:forEach items="${divisions}" var="item">
                <option value="${item.stockDivision}" ${item.stockDivision == selectedDivision ? 'selected' : ''}>${item.stockDivision}</option>
            </c:forEach>
        </select>
        <button class="btn btn-sm btn-primary" onclick="kangong.stock2.search();">조회</button>
        <button class="btn btn-sm btn-success ml-2" onclick="kangong.stock2.excelDownload();">엑셀 다운로드</button>
    </div>

    <table class="table table-bordered table-hover table-sm table-stock">
        <thead class="thead-dark">
            <tr>
                <th>계좌</th>
                <th>종목명</th>
                <th>종목코드</th>
                <th>유형</th>
                <th>수량</th>
                <th>평가금액</th>
                <th>기준금액</th>
                <th>추가필요</th>
                <th>매크로</th>
                <th>매크로 조정</th>
                <th>비중</th>
                <th>실제비중</th>
                <th>순위</th>
                <th>52주 구간</th>
                <th>1개월</th>
                <th>3개월</th>
                <th>6개월</th>
                <th>12개월</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${fn:length(interestList) > 0}">
                    <c:set var="prevDivision" value="" />
                    <c:set var="subTotalPrice" value="0" />
                    <c:set var="subStandard" value="0" />
                    <c:set var="subAddPrice" value="0" />
                    <c:set var="subPotion" value="0" />
                    <c:set var="subActualPotion" value="0" />
                    <%-- 유형별 소계 --%>
                    <c:set var="subPotionStock" value="0" />
                    <c:set var="subPotionBond" value="0" />
                    <c:set var="subPotionComm" value="0" />
                    <c:set var="subValStock" value="0" />
                    <c:set var="subValBond" value="0" />
                    <c:set var="subValComm" value="0" />
                    <%-- 전체 합계 --%>
                    <c:set var="grandTotalPrice" value="0" />
                    <c:set var="grandStandard" value="0" />
                    <c:set var="grandAddPrice" value="0" />
                    <c:set var="grandPotion" value="0" />
                    <c:set var="grandActualPotion" value="0" />
                    <c:set var="grandPotionStock" value="0" />
                    <c:set var="grandPotionBond" value="0" />
                    <c:set var="grandPotionComm" value="0" />
                    <c:set var="grandActualStock" value="0" />
                    <c:set var="grandActualBond" value="0" />
                    <c:set var="grandActualComm" value="0" />
                    <%-- 전체 합계 금액 기반 비율 계산용 --%>
                    <c:set var="grandStdStock" value="0" />
                    <c:set var="grandStdBond" value="0" />
                    <c:set var="grandStdComm" value="0" />
                    <c:set var="grandValStock" value="0" />
                    <c:set var="grandValBond" value="0" />
                    <c:set var="grandValComm" value="0" />

                    <c:forEach items="${interestList}" var="row" varStatus="status">
                        <%-- === 소계 출력 (계좌 변경 시) === --%>
                        <c:if test="${prevDivision != '' && prevDivision != row.stockDivision}">
                            <tr class="summary-row">
                                <td class="text-center" colspan="5">${prevDivision} 소계</td>
                                <td><fmt:formatNumber value="${subTotalPrice}" pattern="#,###"/></td>
                                <td><fmt:formatNumber value="${subStandard}" pattern="#,###"/></td>
                                <td class="${subAddPrice > 0 ? 'add-positive' : 'add-negative'}"><fmt:formatNumber value="${subAddPrice}" pattern="#,###"/></td>
                                <td></td><td></td>
                                <td class="type-breakdown">
                                    <c:if test="${subPotionStock > 0}"><div class="type-row"><span class="type-label">주식</span> <span class="type-val"><fmt:formatNumber value="${subPotionStock}" pattern="#,##0.0"/>%</span></div></c:if>
                                    <c:if test="${subPotionBond > 0}"><div class="type-row"><span class="type-label">채권</span> <span class="type-val"><fmt:formatNumber value="${subPotionBond}" pattern="#,##0.0"/>%</span></div></c:if>
                                    <c:if test="${subPotionComm > 0}"><div class="type-row"><span class="type-label">현물</span> <span class="type-val"><fmt:formatNumber value="${subPotionComm}" pattern="#,##0.0"/>%</span></div></c:if>
                                </td>
                                <td class="type-breakdown">
                                    <c:set var="subActS" value="${subTotalPrice > 0 ? subValStock / subTotalPrice * 100 : 0}" />
                                    <c:set var="subActB" value="${subTotalPrice > 0 ? subValBond / subTotalPrice * 100 : 0}" />
                                    <c:set var="subActC" value="${subTotalPrice > 0 ? subValComm / subTotalPrice * 100 : 0}" />
                                    <c:if test="${subPotionStock > 0 || subActS > 0}">
                                        <c:set var="dS" value="${subActS - subPotionStock}" />
                                        <div class="type-row ${dS >= POTION_DIFF_THRESHOLD || dS <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                            <span class="type-label">주식</span>
                                            <span class="type-val"><fmt:formatNumber value="${subActS}" pattern="#,##0.0"/>%</span>
                                            <span class="type-diff">(${dS >= 0 ? '+' : ''}<fmt:formatNumber value="${dS}" pattern="#,##0.0"/>)</span>
                                        </div>
                                    </c:if>
                                    <c:if test="${subPotionBond > 0 || subActB > 0}">
                                        <c:set var="dB" value="${subActB - subPotionBond}" />
                                        <div class="type-row ${dB >= POTION_DIFF_THRESHOLD || dB <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                            <span class="type-label">채권</span>
                                            <span class="type-val"><fmt:formatNumber value="${subActB}" pattern="#,##0.0"/>%</span>
                                            <span class="type-diff">(${dB >= 0 ? '+' : ''}<fmt:formatNumber value="${dB}" pattern="#,##0.0"/>)</span>
                                        </div>
                                    </c:if>
                                    <c:if test="${subPotionComm > 0 || subActC > 0}">
                                        <c:set var="dC" value="${subActC - subPotionComm}" />
                                        <div class="type-row ${dC >= POTION_DIFF_THRESHOLD || dC <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                            <span class="type-label">현물</span>
                                            <span class="type-val"><fmt:formatNumber value="${subActC}" pattern="#,##0.0"/>%</span>
                                            <span class="type-diff">(${dC >= 0 ? '+' : ''}<fmt:formatNumber value="${dC}" pattern="#,##0.0"/>)</span>
                                        </div>
                                    </c:if>
                                </td>
                                <td colspan="8"></td>
                            </tr>
                            <c:set var="subTotalPrice" value="0" />
                            <c:set var="subStandard" value="0" />
                            <c:set var="subAddPrice" value="0" />
                            <c:set var="subPotion" value="0" />
                            <c:set var="subActualPotion" value="0" />
                            <c:set var="subPotionStock" value="0" />
                            <c:set var="subPotionBond" value="0" />
                            <c:set var="subPotionComm" value="0" />
                            <c:set var="subValStock" value="0" />
                            <c:set var="subValBond" value="0" />
                            <c:set var="subValComm" value="0" />
                        </c:if>

                        <%-- === 누적 === --%>
                        <c:set var="subTotalPrice" value="${subTotalPrice + row.totalPrice}" />
                        <c:set var="subStandard" value="${subStandard + row.standard}" />
                        <c:set var="subAddPrice" value="${subAddPrice + row.addPrice}" />
                        <c:set var="subPotion" value="${subPotion + row.stockPotion}" />
                        <c:set var="subActualPotion" value="${subActualPotion + (row.actualPotion != null ? row.actualPotion : 0)}" />
                        <c:choose>
                            <c:when test="${row.stockType == '채권'}">
                                <c:set var="subPotionBond" value="${subPotionBond + row.stockPotion}" />
                                <c:set var="subValBond" value="${subValBond + row.totalPrice}" />
                            </c:when>
                            <c:when test="${row.stockType == '현물'}">
                                <c:set var="subPotionComm" value="${subPotionComm + row.stockPotion}" />
                                <c:set var="subValComm" value="${subValComm + row.totalPrice}" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="subPotionStock" value="${subPotionStock + row.stockPotion}" />
                                <c:set var="subValStock" value="${subValStock + row.totalPrice}" />
                            </c:otherwise>
                        </c:choose>
                        <c:set var="grandTotalPrice" value="${grandTotalPrice + row.totalPrice}" />
                        <c:set var="grandStandard" value="${grandStandard + row.standard}" />
                        <c:set var="grandAddPrice" value="${grandAddPrice + row.addPrice}" />
                        <c:set var="grandPotion" value="${grandPotion + row.stockPotion}" />
                        <c:set var="rowActual" value="${row.actualPotion != null ? row.actualPotion : 0}" />
                        <c:set var="grandActualPotion" value="${grandActualPotion + rowActual}" />
                        <c:choose>
                            <c:when test="${row.stockType == '채권'}">
                                <c:set var="grandPotionBond" value="${grandPotionBond + row.stockPotion}" />
                                <c:set var="grandActualBond" value="${grandActualBond + rowActual}" />
                                <c:set var="grandStdBond" value="${grandStdBond + row.standard}" />
                                <c:set var="grandValBond" value="${grandValBond + row.totalPrice}" />
                            </c:when>
                            <c:when test="${row.stockType == '현물'}">
                                <c:set var="grandPotionComm" value="${grandPotionComm + row.stockPotion}" />
                                <c:set var="grandActualComm" value="${grandActualComm + rowActual}" />
                                <c:set var="grandStdComm" value="${grandStdComm + row.standard}" />
                                <c:set var="grandValComm" value="${grandValComm + row.totalPrice}" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="grandPotionStock" value="${grandPotionStock + row.stockPotion}" />
                                <c:set var="grandActualStock" value="${grandActualStock + rowActual}" />
                                <c:set var="grandStdStock" value="${grandStdStock + row.standard}" />
                                <c:set var="grandValStock" value="${grandValStock + row.totalPrice}" />
                            </c:otherwise>
                        </c:choose>

                        <%-- === 데이터 행 === --%>
                        <tr>
                            <td class="text-left">${row.stockDivision}</td>
                            <td class="text-left">${row.name}</td>
                            <td class="text-center">${row.stockId}</td>
                            <td class="text-center"><span class="badge badge-${row.stockType == '채권' ? 'info' : row.stockType == '현물' ? 'warning' : 'secondary'}" style="font-size:11px;">${row.stockType}</span></td>
                            <td><fmt:formatNumber value="${row.qty}" pattern="#,###"/></td>
                            <td><fmt:formatNumber value="${row.totalPrice}" pattern="#,###"/></td>
                            <td><fmt:formatNumber value="${row.standard}" pattern="#,###"/></td>
                            <td class="${row.addPrice > 0 ? 'add-positive' : 'add-negative'}">
                                <fmt:formatNumber value="${row.addPrice}" pattern="#,###"/>
                            </td>
                            <td class="text-center macro-signal-cell" data-stock-name="${row.name}" data-stock-div="${row.stockDivision}"><span class="text-muted" style="font-size:10px;">-</span></td>
                            <td class="macro-adjust-cell" data-stock-name="${row.name}" data-stock-div="${row.stockDivision}"><span class="text-muted" style="font-size:10px;">-</span></td>
                            <td><fmt:formatNumber value="${row.stockPotion}" pattern="#,##0"/>%</td>
                            <c:set var="potionDiff" value="${row.actualPotion - row.stockPotion}" />
                            <td class="${potionDiff >= POTION_DIFF_THRESHOLD || potionDiff <= -POTION_DIFF_THRESHOLD ? 'warn-highlight' : ''}">
                                <c:if test="${row.actualPotion != null}"><fmt:formatNumber value="${row.actualPotion}" pattern="#,##0.0"/>%</c:if>
                                <c:if test="${row.actualPotion == null}">-</c:if>
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${row.rk == 1}"><span class="badge-rank badge-rank-1">${row.rk}</span></c:when>
                                    <c:when test="${row.rk == 2}"><span class="badge-rank badge-rank-2">${row.rk}</span></c:when>
                                    <c:when test="${row.rk == 3}"><span class="badge-rank badge-rank-3">${row.rk}</span></c:when>
                                    <c:otherwise><span class="badge-rank badge-rank-other">${row.rk}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center" ${row.rate52 != null && row.rate52 > 10 ? 'style="background-color:#fff3cd;"' : ''}>
                                <c:if test="${row.max52 != null && row.max52 > 0 && row.min52 != null}">
                                    <div class="range52-container">
                                        <div class="range52-row">
                                            <span class="range52-label"><fmt:formatNumber value="${row.min52}" pattern="#,###"/></span>
                                            <div class="range52-track">
                                                <c:if test="${row.pos52 != null}">
                                                    <div class="range52-pos" style="left:${row.pos52}%"></div>
                                                </c:if>
                                            </div>
                                            <span class="range52-label"><fmt:formatNumber value="${row.max52}" pattern="#,###"/></span>
                                        </div>
                                        <div class="range52-price">
                                            <fmt:formatNumber value="${row.price}" pattern="#,###"/>
                                            <c:if test="${row.rate52 != null}">
                                                <span class="${row.rate52 > 10 ? 'warn-highlight' : ''}" style="font-size:10px;">
                                                    (<fmt:formatNumber value="${row.rate52}" pattern="#,##0.0"/>%)
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${row.max52 == null || row.max52 == 0}">-</c:if>
                            </td>
                            <td class="rate-cell">
                                <c:choose>
                                    <c:when test="${row.month1Rate != null}">
                                        <span class="rate-badge ${row.month1Rate > 0 ? 'rate-badge-up' : (row.month1Rate < 0 ? 'rate-badge-dn' : 'rate-badge-zero')} ${row.month1Rate >= 10 ? 'rate-hot' : ''} ${row.month1Rate <= -10 ? 'rate-cold' : ''}">${row.month1Rate > 0 ? '▲' : (row.month1Rate < 0 ? '▼' : '—')}<fmt:formatNumber value="${row.month1Rate < 0 ? -row.month1Rate : row.month1Rate}" pattern="#,##0.1"/>%</span>
                                    </c:when>
                                    <c:otherwise><span style="color:#ccc;font-size:11px;">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="rate-cell">
                                <c:choose>
                                    <c:when test="${row.month3Rate != null}">
                                        <span class="rate-badge ${row.month3Rate > 0 ? 'rate-badge-up' : (row.month3Rate < 0 ? 'rate-badge-dn' : 'rate-badge-zero')} ${row.month3Rate >= 15 ? 'rate-hot' : ''} ${row.month3Rate <= -15 ? 'rate-cold' : ''}">${row.month3Rate > 0 ? '▲' : (row.month3Rate < 0 ? '▼' : '—')}<fmt:formatNumber value="${row.month3Rate < 0 ? -row.month3Rate : row.month3Rate}" pattern="#,##0.1"/>%</span>
                                    </c:when>
                                    <c:otherwise><span style="color:#ccc;font-size:11px;">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="rate-cell">
                                <c:choose>
                                    <c:when test="${row.month6Rate != null}">
                                        <span class="rate-badge ${row.month6Rate > 0 ? 'rate-badge-up' : (row.month6Rate < 0 ? 'rate-badge-dn' : 'rate-badge-zero')} ${row.month6Rate >= 20 ? 'rate-hot' : ''} ${row.month6Rate <= -20 ? 'rate-cold' : ''}">${row.month6Rate > 0 ? '▲' : (row.month6Rate < 0 ? '▼' : '—')}<fmt:formatNumber value="${row.month6Rate < 0 ? -row.month6Rate : row.month6Rate}" pattern="#,##0.1"/>%</span>
                                    </c:when>
                                    <c:otherwise><span style="color:#ccc;font-size:11px;">—</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="rate-cell">
                                <c:choose>
                                    <c:when test="${row.month12Rate != null}">
                                        <span class="rate-badge ${row.month12Rate > 0 ? 'rate-badge-up' : (row.month12Rate < 0 ? 'rate-badge-dn' : 'rate-badge-zero')} ${row.month12Rate >= 30 ? 'rate-hot' : ''} ${row.month12Rate <= -30 ? 'rate-cold' : ''}">${row.month12Rate > 0 ? '▲' : (row.month12Rate < 0 ? '▼' : '—')}<fmt:formatNumber value="${row.month12Rate < 0 ? -row.month12Rate : row.month12Rate}" pattern="#,##0.1"/>%</span>
                                    </c:when>
                                    <c:otherwise><span style="color:#ccc;font-size:11px;">—</span></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <c:set var="prevDivision" value="${row.stockDivision}" />

                        <%-- === 마지막 항목 소계 === --%>
                        <c:if test="${status.last}">
                            <tr class="summary-row">
                                <td class="text-center" colspan="5">${prevDivision} 소계</td>
                                <td><fmt:formatNumber value="${subTotalPrice}" pattern="#,###"/></td>
                                <td><fmt:formatNumber value="${subStandard}" pattern="#,###"/></td>
                                <td class="${subAddPrice > 0 ? 'add-positive' : 'add-negative'}"><fmt:formatNumber value="${subAddPrice}" pattern="#,###"/></td>
                                <td></td><td></td>
                                <td class="type-breakdown">
                                    <c:if test="${subPotionStock > 0}"><div class="type-row"><span class="type-label">주식</span> <span class="type-val"><fmt:formatNumber value="${subPotionStock}" pattern="#,##0.0"/>%</span></div></c:if>
                                    <c:if test="${subPotionBond > 0}"><div class="type-row"><span class="type-label">채권</span> <span class="type-val"><fmt:formatNumber value="${subPotionBond}" pattern="#,##0.0"/>%</span></div></c:if>
                                    <c:if test="${subPotionComm > 0}"><div class="type-row"><span class="type-label">현물</span> <span class="type-val"><fmt:formatNumber value="${subPotionComm}" pattern="#,##0.0"/>%</span></div></c:if>
                                </td>
                                <td class="type-breakdown">
                                    <c:set var="subActS" value="${subTotalPrice > 0 ? subValStock / subTotalPrice * 100 : 0}" />
                                    <c:set var="subActB" value="${subTotalPrice > 0 ? subValBond / subTotalPrice * 100 : 0}" />
                                    <c:set var="subActC" value="${subTotalPrice > 0 ? subValComm / subTotalPrice * 100 : 0}" />
                                    <c:if test="${subPotionStock > 0 || subActS > 0}">
                                        <c:set var="dS" value="${subActS - subPotionStock}" />
                                        <div class="type-row ${dS >= POTION_DIFF_THRESHOLD || dS <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                            <span class="type-label">주식</span>
                                            <span class="type-val"><fmt:formatNumber value="${subActS}" pattern="#,##0.0"/>%</span>
                                            <span class="type-diff">(${dS >= 0 ? '+' : ''}<fmt:formatNumber value="${dS}" pattern="#,##0.0"/>)</span>
                                        </div>
                                    </c:if>
                                    <c:if test="${subPotionBond > 0 || subActB > 0}">
                                        <c:set var="dB" value="${subActB - subPotionBond}" />
                                        <div class="type-row ${dB >= POTION_DIFF_THRESHOLD || dB <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                            <span class="type-label">채권</span>
                                            <span class="type-val"><fmt:formatNumber value="${subActB}" pattern="#,##0.0"/>%</span>
                                            <span class="type-diff">(${dB >= 0 ? '+' : ''}<fmt:formatNumber value="${dB}" pattern="#,##0.0"/>)</span>
                                        </div>
                                    </c:if>
                                    <c:if test="${subPotionComm > 0 || subActC > 0}">
                                        <c:set var="dC" value="${subActC - subPotionComm}" />
                                        <div class="type-row ${dC >= POTION_DIFF_THRESHOLD || dC <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                            <span class="type-label">현물</span>
                                            <span class="type-val"><fmt:formatNumber value="${subActC}" pattern="#,##0.0"/>%</span>
                                            <span class="type-diff">(${dC >= 0 ? '+' : ''}<fmt:formatNumber value="${dC}" pattern="#,##0.0"/>)</span>
                                        </div>
                                    </c:if>
                                </td>
                                <td colspan="8"></td>
                            </tr>
                        </c:if>
                    </c:forEach>

                    <%-- === 전체 합계 === --%>
                    <c:set var="gPctStock" value="${grandStandard > 0 ? grandStdStock / grandStandard * 100 : 0}" />
                    <c:set var="gPctBond" value="${grandStandard > 0 ? grandStdBond / grandStandard * 100 : 0}" />
                    <c:set var="gPctComm" value="${grandStandard > 0 ? grandStdComm / grandStandard * 100 : 0}" />
                    <c:set var="gActStock" value="${grandTotalPrice > 0 ? grandValStock / grandTotalPrice * 100 : 0}" />
                    <c:set var="gActBond" value="${grandTotalPrice > 0 ? grandValBond / grandTotalPrice * 100 : 0}" />
                    <c:set var="gActComm" value="${grandTotalPrice > 0 ? grandValComm / grandTotalPrice * 100 : 0}" />
                    <c:set var="gs" value="background-color:#343a40; color:#fff; font-weight:bold;" />
                    <tr style="border-top: 3px double #333;">
                        <td class="text-center" colspan="5" style="${gs}">전체 합계</td>
                        <td style="${gs}"><fmt:formatNumber value="${grandTotalPrice}" pattern="#,###"/></td>
                        <td style="${gs}"><fmt:formatNumber value="${grandStandard}" pattern="#,###"/></td>
                        <td style="${gs}"><fmt:formatNumber value="${grandAddPrice}" pattern="#,###"/></td>
                        <td style="${gs}"></td><td style="${gs}"></td>
                        <td class="type-breakdown" style="${gs}">
                            <c:if test="${gPctStock > 0}"><div class="type-row"><span class="type-label" style="color:#ccc;">주식</span> <span class="type-val"><fmt:formatNumber value="${gPctStock}" pattern="#,##0.0"/>%</span></div></c:if>
                            <c:if test="${gPctBond > 0}"><div class="type-row"><span class="type-label" style="color:#ccc;">채권</span> <span class="type-val"><fmt:formatNumber value="${gPctBond}" pattern="#,##0.0"/>%</span></div></c:if>
                            <c:if test="${gPctComm > 0}"><div class="type-row"><span class="type-label" style="color:#ccc;">현물</span> <span class="type-val"><fmt:formatNumber value="${gPctComm}" pattern="#,##0.0"/>%</span></div></c:if>
                        </td>
                        <td class="type-breakdown" style="${gs}">
                            <c:if test="${gPctStock > 0 || gActStock > 0}">
                                <c:set var="gdS" value="${gActStock - gPctStock}" />
                                <div class="type-row ${gdS >= POTION_DIFF_THRESHOLD || gdS <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                    <span class="type-label" style="color:#ccc;">주식</span>
                                    <span class="type-val"><fmt:formatNumber value="${gActStock}" pattern="#,##0.0"/>%</span>
                                    <span class="type-diff" style="${gdS >= POTION_DIFF_THRESHOLD || gdS <= -POTION_DIFF_THRESHOLD ? 'color:#ff6b6b;' : 'color:#aaa;'}">(${gdS >= 0 ? '+' : ''}<fmt:formatNumber value="${gdS}" pattern="#,##0.0"/>)</span>
                                </div>
                            </c:if>
                            <c:if test="${gPctBond > 0 || gActBond > 0}">
                                <c:set var="gdB" value="${gActBond - gPctBond}" />
                                <div class="type-row ${gdB >= POTION_DIFF_THRESHOLD || gdB <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                    <span class="type-label" style="color:#ccc;">채권</span>
                                    <span class="type-val"><fmt:formatNumber value="${gActBond}" pattern="#,##0.0"/>%</span>
                                    <span class="type-diff" style="${gdB >= POTION_DIFF_THRESHOLD || gdB <= -POTION_DIFF_THRESHOLD ? 'color:#ff6b6b;' : 'color:#aaa;'}">(${gdB >= 0 ? '+' : ''}<fmt:formatNumber value="${gdB}" pattern="#,##0.0"/>)</span>
                                </div>
                            </c:if>
                            <c:if test="${gPctComm > 0 || gActComm > 0}">
                                <c:set var="gdC" value="${gActComm - gPctComm}" />
                                <div class="type-row ${gdC >= POTION_DIFF_THRESHOLD || gdC <= -POTION_DIFF_THRESHOLD ? 'type-warn' : ''}">
                                    <span class="type-label" style="color:#ccc;">현물</span>
                                    <span class="type-val"><fmt:formatNumber value="${gActComm}" pattern="#,##0.0"/>%</span>
                                    <span class="type-diff" style="${gdC >= POTION_DIFF_THRESHOLD || gdC <= -POTION_DIFF_THRESHOLD ? 'color:#ff6b6b;' : 'color:#aaa;'}">(${gdC >= 0 ? '+' : ''}<fmt:formatNumber value="${gdC}" pattern="#,##0.0"/>)</span>
                                </div>
                            </c:if>
                        </td>
                        <td colspan="8" style="background-color:#343a40;"></td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="18" class="text-center">조회된 결과가 없습니다.</td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

</div>
</div>

<c:url var="stock2URL" value="/stock2"></c:url>
<c:url var="stock2ExcelURL" value="/stock2/excel"></c:url>
<script>
kangong.stock2 = {
    search: function() {
        var paramObj = {};
        var div = $('#divisionFilter').val();
        if (div) paramObj.stockDivision = div;
        kangong.form.submitPost('${stock2URL}', paramObj);
    },
    excelDownload: function() {
        var div = $('#divisionFilter').val();
        var url = '${stock2ExcelURL}';
        if (div) url += '?stockDivision=' + encodeURIComponent(div);
        window.location.href = url;
    }
}
$(function() {
    $('.type-val').each(function() {
        var t = $(this).text().trim();
        if (t.indexOf('%') > -1) {
            var n = parseFloat(t.replace('%',''));
            if (!isNaN(n)) $(this).text(n.toFixed(1) + '%');
        }
    });
    $('.type-diff').each(function() {
        var t = $(this).text().trim();
        var m = t.match(/\(([+\-]?)([0-9.]+)\)/);
        if (m) {
            var n = parseFloat(m[2]);
            if (!isNaN(n)) $(this).text('(' + m[1] + n.toFixed(1) + ')');
        }
    });
    $('td').not('.type-breakdown').each(function() {
        var t = $(this).text().trim();
        if (/^-?\d{4,}$/.test(t)) {
            var n = parseInt(t);
            $(this).text(n.toLocaleString());
        }
    });

    // 매크로 시그널 로드
    $.getJSON('<c:url value="/stock2/macro/signals"/>', function(data) {
        if (!data || !data.portfolioAllocation) return;
        var items = data.portfolioAllocation.items || [];

        // 종목별 시그널 맵
        var stockMap = {};
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            var adjustAmount = item.adjustAmount || 0;
            var stocks = item.stocks || [];
            var catTotal = 0;
            for (var j = 0; j < stocks.length; j++) catTotal += (stocks[j].totalPrice || 0);
            for (var j = 0; j < stocks.length; j++) {
                var s = stocks[j];
                var ratio = catTotal > 0 ? (s.totalPrice || 0) / catTotal : 0;
                stockMap[s.name] = {
                    signal: item.signal || 'NEUTRAL',
                    action: item.action || 'HOLD',
                    adjustAmt: Math.round(adjustAmount * ratio)
                };
            }
        }
        $('.macro-signal-cell').each(function() {
            var name = $(this).data('stock-name');
            var info = stockMap[name];
            if (!info) return;
            var sig = info.signal.toUpperCase();
            var cls = sig === 'BUY' ? 'macro-badge-buy' : sig === 'SELL' ? 'macro-badge-sell' : sig === 'CAUTION' ? 'macro-badge-caution' : 'macro-badge-neutral';
            var label = sig === 'BUY' ? '매수' : sig === 'SELL' ? '매도' : sig === 'CAUTION' ? '주의' : '중립';
            $(this).html('<span class="macro-badge ' + cls + '">' + label + '</span>');
        });
        $('.macro-adjust-cell').each(function() {
            var name = $(this).data('stock-name');
            var info = stockMap[name];
            if (!info || info.adjustAmt === 0) return;
            var amt = info.adjustAmt;
            var cls = amt > 0 ? 'macro-adjust-positive' : 'macro-adjust-negative';
            var prefix = amt > 0 ? '+' : '';
            $(this).html('<span class="' + cls + '">' + prefix + amt.toLocaleString() + '</span>');
        });

        // 조정 우선순위 패널 렌더링
        var actionItems = items.filter(function(it) { return it.action && it.action !== 'HOLD'; });
        actionItems.sort(function(a, b) { return Math.abs(b.adjustAmount || 0) - Math.abs(a.adjustAmount || 0); });
        if (actionItems.length > 0) {
            var rows = '';
            for (var i = 0; i < actionItems.length; i++) {
                var it = actionItems[i];
                var sig = (it.signal || 'NEUTRAL').toUpperCase();
                var macroSigCls = sig === 'BUY' ? 'macro-badge-buy' : sig === 'SELL' ? 'macro-badge-sell' : sig === 'CAUTION' ? 'macro-badge-caution' : 'macro-badge-neutral';
                var macroSigLabel = sig === 'BUY' ? '매수' : sig === 'SELL' ? '매도' : sig === 'CAUTION' ? '주의' : '중립';
                var cySig = (it.cycleSignal || 'HOLD').toUpperCase();
                var cyProb = it.cycleBuyProb || 50;
                var cyCls = cySig === 'BUY' ? 'cycle-prob-buy' : cySig === 'SELL' ? 'cycle-prob-sell' : 'cycle-prob-hold';
                var act = it.action || 'HOLD';
                var actCls = act === 'BUY' ? 'action-buy' : act === 'REDUCE' ? 'action-reduce' : act === 'SLIGHT_BUY' ? 'action-slight-buy' : act === 'SLIGHT_REDUCE' ? 'action-slight-reduce' : '';
                var actLabel = act === 'BUY' ? '▲ 매수' : act === 'REDUCE' ? '▼ 축소' : act === 'SLIGHT_BUY' ? '△ 소폭매수' : act === 'SLIGHT_REDUCE' ? '▽ 소폭축소' : '유지';
                var adjAmt = Math.round((it.adjustAmount || 0) / 1000);
                var adjStr = adjAmt === 0 ? '-' : (adjAmt > 0 ? '+' : '') + adjAmt.toLocaleString();
                var adjCls = adjAmt > 0 ? 'macro-adjust-positive' : adjAmt < 0 ? 'macro-adjust-negative' : '';
                var chg = it.change || 0;
                var chgStr = (chg >= 0 ? '+' : '') + chg.toFixed(1) + '%';
                var chgCls = chg > 0 ? 'macro-adjust-positive' : chg < 0 ? 'macro-adjust-negative' : '';
                rows += '<tr>';
                rows += '<td class="text-left"><span style="display:inline-block;width:8px;height:8px;border-radius:50%;background:' + (it.color || '#999') + ';margin-right:5px;"></span>' + (it.category || '') + '</td>';
                rows += '<td class="text-center"><span class="macro-badge ' + macroSigCls + '">' + macroSigLabel + '</span></td>';
                rows += '<td class="text-center"><span class="cycle-prob ' + cyCls + '">' + cyProb + '%</span></td>';
                rows += '<td>' + (it.currentWeight || 0).toFixed(1) + '%</td>';
                rows += '<td>' + (it.recommendedWeight || 0).toFixed(1) + '%</td>';
                rows += '<td class="' + chgCls + '">' + chgStr + '</td>';
                rows += '<td class="' + adjCls + '">' + adjStr + '</td>';
                rows += '<td class="text-center ' + actCls + '">' + actLabel + '</td>';
                rows += '</tr>';
            }
            $('#priority-tbody').html(rows);
            $('#priority-panel').show();
        }
    });
});
</script>
