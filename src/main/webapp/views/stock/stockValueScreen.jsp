<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<style>
.table-value { font-size: 13px; }
.table-value th { text-align: center; white-space: nowrap; vertical-align: middle !important; }
.table-value td { text-align: right; white-space: nowrap; vertical-align: middle !important; }
.table-value td.text-left { text-align: left; }
.table-value td.text-center { text-align: center; }

.filter-panel { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 6px; padding: 15px; margin-bottom: 15px; }
.filter-panel .filter-row { display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end; }
.filter-panel .filter-item { display: flex; flex-direction: column; gap: 3px; }
.filter-panel .filter-item label { font-size: 11px; font-weight: bold; color: #495057; margin: 0; }
.filter-panel .filter-item input { width: 90px; font-size: 13px; }
.filter-panel .filter-item .filter-desc { font-size: 10px; color: #868e96; }

.pass-badge { display: inline-block; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: bold; color: #fff; min-width: 40px; text-align: center; }
.pass-10 { background: #000000; }
.pass-9 { background: #050f08; }
.pass-8 { background: #0a2e15; }
.pass-7 { background: #155724; }
.pass-6 { background: #28a745; }
.pass-5 { background: #20c997; }
.pass-4 { background: #ffc107; color: #333; }
.pass-3 { background: #fd7e14; color: #fff; }
.pass-low { background: #dc3545; }

.criteria-pass { color: #28a745; font-weight: bold; }
.criteria-fail { color: #adb5bd; }

.criteria-icon { display: inline-block; width: 16px; text-align: center; }

.info-panel { background: #e7f5ff; border: 1px solid #74c0fc; border-radius: 6px; padding: 12px 15px; margin-bottom: 15px; font-size: 12px; }
.info-panel h6 { margin: 0 0 8px; font-size: 13px; color: #1864ab; }
.info-panel table { width: 100%; border-collapse: collapse; }
.info-panel table th { text-align: left; padding: 3px 8px; color: #495057; font-weight: bold; width: 120px; }
.info-panel table td { padding: 3px 8px; color: #495057; }

.sort-link { cursor: pointer; text-decoration: none; color: inherit; }
.sort-link:hover { color: #007bff; }
.sort-active { color: #007bff !important; }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

    <h5>가치주 스크리닝</h5>
    <p class="text-muted" style="font-size:12px;">한국시장 특성을 반영한 가치주 추천 스크리닝</p>

    <!-- 기준 설명 패널 (접기/펼치기) -->
    <div style="margin-bottom:10px;">
        <a href="#" onclick="$('#criteriaInfo').toggle(); return false;" style="font-size:12px; color:#495057;">
            스크리닝 기준 설명 <span style="font-size:10px;">[접기/펼치기]</span>
        </a>
    </div>
    <div id="criteriaInfo" class="info-panel" style="display:none;">
        <h6>스크리닝 기준 및 한국시장 특성 반영</h6>
        <table>
            <tr><th>PER</th><td>10배 이하 (또는 업종 평균의 60% 이하). 경기민감주가 많은 국장 특성상 3개년 평균 PER도 함께 참고.</td></tr>
            <tr><th>PBR</th><td>0.8배 이하. 밸류업 정책으로 시장 평균 PBR이 1배를 넘어선 상황에서 강력한 자산 가치 지지선.</td></tr>
            <tr><th>ROE</th><td>8% 이상 (최근 3년 연속). 자본비용(WACC 6~7%) 이상 수익 창출 능력 확인으로 가치 함정 회피.</td></tr>
            <tr><th>배당수익률</th><td>3.5% 이상 (또는 배당성향 25% 이상). 분기배당/자사주 소각 기업 우대.</td></tr>
            <tr><th>부채비율</th><td>100% 이하 (금융업 제외). 금리 변동성 대비 재무 안전성 확인.</td></tr>
            <tr><th>배당 연속성</th><td>최근 3년 이상 연속 배당. 주주 환원 의지 확인, 지배구조 리스크 간접 평가.</td></tr>
            <tr><th>영업이익 연속</th><td>최근 3년 이상 연속 흑자. 본업 수익 창출력 확인, 청산가치 착시 방지.</td></tr>
            <tr><th>유동비율</th><td>100% 이상. 유동자산이 유동부채를 초과해 단기 상환 능력 확보. 금융업 제외 적용.</td></tr>
            <tr><th>영업활동현금흐름</th><td>0 이상 (양수). 실제 영업에서 현금이 창출되는지 확인. 이익은 있으나 현금이 없는 가치 함정 방지.</td></tr>
            <tr><th>코리아밸류업지수</th><td>코리아밸류업지수 편입 종목 (KRX). 정부 주주환원 정책 수혜 기업, 저PBR 개선 의지 확인. /stock/valueup/saveAll로 갱신.</td></tr>
        </table>
    </div>

    <!-- 매수/매도 확률 공식 설명 -->
    <div style="margin-bottom:10px;">
        <a href="#" onclick="$('#formulaInfo').toggle(); return false;" style="font-size:12px; color:#495057;">
            매수/매도 확률 산출 공식 <span style="font-size:10px;">[접기/펼치기]</span>
        </a>
    </div>
    <div id="formulaInfo" class="info-panel" style="display:none; background:#f0f7ff; border-color:#74a0fc;">
        <h6 style="color:#1864ab;">매수/매도 확률 = 5개 팩터 가중 합산 (Multi-Factor Scoring)</h6>
        <table>
            <tr>
                <th style="width:150px; vertical-align:top;">F1. 기본 충족도<br/><span style="color:#007bff; font-size:11px;">가중치 25%</span></th>
                <td>스크리닝 기준 통과 비율. <code>충족 갯수 / 전체 기준 수</code><br/>7개 중 7개 통과 → 1.0, 4개 통과 → 0.57</td>
            </tr>
            <tr>
                <th style="vertical-align:top;">F2. 밸류에이션 매력도<br/><span style="color:#007bff; font-size:11px;">가중치 25%</span></th>
                <td>PER·PBR이 필터 기준 대비 얼마나 저렴한지의 평균.<br/>
                    <code>(기준PER - 현재PER) / 기준PER</code> + <code>(기준PBR - 현재PBR) / 기준PBR</code> ÷ 2<br/>
                    예) PER 기준 10배, 현재 5배 → (10-5)/10 = 0.5</td>
            </tr>
            <tr>
                <th style="vertical-align:top;">F3. 수익성<br/><span style="color:#007bff; font-size:11px;">가중치 20%</span></th>
                <td>ROE 수준(60%) + ROE 연속성(40%).<br/>
                    ROE 수준: <code>min(1, 3년평균ROE / 20%)</code> — ROE 20%면 만점<br/>
                    ROE 연속: <code>min(1, ROE기준초과연수 / 3년)</code> — 3년 연속이면 만점</td>
            </tr>
            <tr>
                <th style="vertical-align:top;">F4. 주주환원<br/><span style="color:#007bff; font-size:11px;">가중치 15%</span></th>
                <td>배당수익률(50%) + 배당연속성(50%).<br/>
                    배당률: <code>min(1, 배당수익률 / 7%)</code> — 7%면 만점<br/>
                    배당연속: <code>min(1, 연속배당연수 / 5년)</code> — 5년 연속이면 만점</td>
            </tr>
            <tr>
                <th style="vertical-align:top;">F5. 재무안전성<br/><span style="color:#007bff; font-size:11px;">가중치 15%</span></th>
                <td>부채비율 안전도(50%) + 영업이익 연속성(50%).<br/>
                    부채: <code>(기준부채비율 - 현재부채비율) / 기준부채비율</code><br/>
                    영익연속: <code>min(1, 연속흑자연수 / 5년)</code> — 5년 연속이면 만점</td>
            </tr>
        </table>
        <div style="margin-top:10px; padding:8px 12px; background:#e7f5ff; border-radius:4px; font-size:12px;">
            <strong>최종 산출:</strong>
            매수% = <code>가중합산 × 90 + 5</code> (5~95% 범위),
            매도% = <code>(1 - 가중합산) × 80 + 5</code> (5~85% 범위)<br/>
            <span style="color:#6c757d;">※ 단순 충족 갯수가 아닌, 각 지표의 <strong>수준(얼마나 좋은지)</strong>과 <strong>지속성(얼마나 오래됐는지)</strong>을 함께 반영합니다.</span>
        </div>
    </div>

    <!-- 필터 패널 -->
    <div class="filter-panel">
        <div class="filter-row">
            <div class="filter-item">
                <label>PER (최대)</label>
                <input type="number" id="filterPerMax" class="form-control form-control-sm" step="0.5" value="${filter.filterPerMax}" />
                <span class="filter-desc">배</span>
            </div>
            <div class="filter-item">
                <label>PBR (최대)</label>
                <input type="number" id="filterPbrMax" class="form-control form-control-sm" step="0.1" value="${filter.filterPbrMax}" />
                <span class="filter-desc">배</span>
            </div>
            <div class="filter-item">
                <label>ROE (최소, 3년연속)</label>
                <input type="number" id="filterRoeMin" class="form-control form-control-sm" step="0.5" value="${filter.filterRoeMin}" />
                <span class="filter-desc">%</span>
            </div>
            <div class="filter-item">
                <label>배당수익률 (최소)</label>
                <input type="number" id="filterDividendMin" class="form-control form-control-sm" step="0.5" value="${filter.filterDividendMin}" />
                <span class="filter-desc">%</span>
            </div>
            <div class="filter-item">
                <label>부채비율 (최대)</label>
                <input type="number" id="filterDebtMax" class="form-control form-control-sm" step="10" value="${filter.filterDebtMax}" />
                <span class="filter-desc">%</span>
            </div>
            <div class="filter-item">
                <label>시가총액 (최소)</label>
                <input type="number" id="filterMarketCapMin" class="form-control form-control-sm" step="100" value="${filter.filterMarketCapMin}" />
                <span class="filter-desc">억</span>
            </div>
            <div class="filter-item">
                <label>충족 갯수 (최소)</label>
                <select id="filterPassCountMin" class="form-control form-control-sm" style="width:90px;">
                    <c:forEach begin="1" end="10" var="i">
                        <option value="${i}" <c:if test="${filter.filterPassCountMin == i}">selected</c:if>>${i}개</option>
                    </c:forEach>
                </select>
                <span class="filter-desc">/10</span>
            </div>
            <div class="filter-item">
                <button class="btn btn-sm btn-primary" onclick="kangong.valueScreen.search();">스크리닝</button>
                <span class="filter-desc">&nbsp;</span>
            </div>
            <div class="filter-item">
                <button class="btn btn-sm btn-outline-secondary" onclick="kangong.valueScreen.reset();">초기화</button>
                <span class="filter-desc">&nbsp;</span>
            </div>
        </div>
    </div>

    <!-- 결과 요약 -->
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
        <span style="font-size:13px; font-weight:bold;">
            검색 결과: <span style="color:#007bff;">${fn:length(screenList)}</span>개 종목 (${filter.filterPassCountMin}개 이상 충족)
        </span>
        <div>
            <button class="btn btn-sm btn-outline-success" onclick="kangong.valueScreen.toggleFilter(8);">8개 이상</button>
            <button class="btn btn-sm btn-outline-success" onclick="kangong.valueScreen.toggleFilter(7);">7개 이상</button>
            <button class="btn btn-sm btn-outline-success" onclick="kangong.valueScreen.toggleFilter(6);">6개 이상</button>
            <button class="btn btn-sm btn-outline-info" onclick="kangong.valueScreen.toggleFilter(5);">5개 이상</button>
            <button class="btn btn-sm btn-outline-warning" onclick="kangong.valueScreen.toggleFilter(4);">4개 이상</button>
            <button class="btn btn-sm btn-outline-secondary" onclick="kangong.valueScreen.toggleFilter(0);">전체</button>
            <button class="btn btn-sm btn-success" onclick="kangong.valueScreen.downloadExcel();" style="margin-left:8px;">Excel 다운로드</button>
        </div>
    </div>

    <!-- 결과 테이블 -->
    <table class="table table-bordered table-hover table-sm table-value" id="valueTable">
        <thead class="thead-dark">
            <tr>
                <th>No</th>
                <th class="sort-link" data-col="name">종목명</th>
                <th class="sort-link" data-col="passCount">충족</th>
                <th class="sort-link" data-col="buyProbability">매수%</th>
                <th class="sort-link" data-col="sellProbability">매도%</th>
                <th class="sort-link" data-col="currentPer">PER(배)</th>
                <th>업종PER(배)</th>
                <th>업종비율(%)</th>
                <th>3Y평균PER(배)</th>
                <th class="sort-link" data-col="currentPbr">PBR(배)</th>
                <th class="sort-link" data-col="avgRoe3y">ROE 3Y(%)</th>
                <th>ROE연속</th>
                <th class="sort-link" data-col="currentDividendRate">배당률(%)</th>
                <th>배당성향(%)</th>
                <th class="sort-link" data-col="currentDebtRatio">부채비율(%)</th>
                <th class="sort-link" data-col="divConsecutive">배당연속</th>
                <th class="sort-link" data-col="profitConsecutive">영익연속</th>
                <th class="sort-link" data-col="currentRatio">유동비율(%)</th>
                <th class="sort-link" data-col="operatingCashFlow">영업현금흐름(억)</th>
                <th>밸류업지수</th>
                <th>현재가(원)</th>
                <th class="sort-link" data-col="marketCapitalization">시가총액(억)</th>
                <th>업종</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${screenList}" var="item" varStatus="st">
            <tr data-pass="${item.passCount}">
                <td class="text-center">${st.count}</td>
                <td class="text-left">
                    <a href="https://finance.naver.com/item/main.nhn?code=${item.stockId}" target="_blank"
                       style="color:#333; text-decoration:none;">${item.name}</a>
                </td>
                <td class="text-center">
                    <span class="pass-badge
                        <c:choose>
                            <c:when test="${item.passCount >= 10}">pass-10</c:when>
                            <c:when test="${item.passCount == 9}">pass-9</c:when>
                            <c:when test="${item.passCount == 8}">pass-8</c:when>
                            <c:when test="${item.passCount == 7}">pass-7</c:when>
                            <c:when test="${item.passCount == 6}">pass-6</c:when>
                            <c:when test="${item.passCount == 5}">pass-5</c:when>
                            <c:when test="${item.passCount == 4}">pass-4</c:when>
                            <c:when test="${item.passCount == 3}">pass-3</c:when>
                            <c:otherwise>pass-low</c:otherwise>
                        </c:choose>
                    ">${item.passCount}/${item.totalCriteria}</span>
                </td>
                <!-- 매수% -->
                <td class="text-center">
                    <div style="display:flex; align-items:center; gap:3px; justify-content:center;">
                        <div style="background:#e9ecef; border-radius:3px; height:14px; width:50px;">
                            <div style="background:<c:choose><c:when test='${item.buyProbability >= 60}'>#007bff</c:when><c:otherwise>#adb5bd</c:otherwise></c:choose>; height:100%; border-radius:3px; width:${item.buyProbability}%;"></div>
                        </div>
                        <span style="font-weight:<c:if test='${item.buyProbability >= 60}'>700</c:if>; color:<c:choose><c:when test='${item.buyProbability >= 60}'>#007bff</c:when><c:otherwise>#495057</c:otherwise></c:choose>; font-size:12px;">${item.buyProbability}%</span>
                    </div>
                </td>
                <!-- 매도% -->
                <td class="text-center">
                    <div style="display:flex; align-items:center; gap:3px; justify-content:center;">
                        <div style="background:#e9ecef; border-radius:3px; height:14px; width:50px;">
                            <div style="background:<c:choose><c:when test='${item.sellProbability >= 50}'>#dc3545</c:when><c:otherwise>#adb5bd</c:otherwise></c:choose>; height:100%; border-radius:3px; width:${item.sellProbability}%;"></div>
                        </div>
                        <span style="font-weight:<c:if test='${item.sellProbability >= 50}'>700</c:if>; color:<c:choose><c:when test='${item.sellProbability >= 50}'>#dc3545</c:when><c:otherwise>#495057</c:otherwise></c:choose>; font-size:12px;">${item.sellProbability}%</span>
                    </div>
                </td>
                <!-- PER -->
                <td class="<c:if test='${item.currentPer > 0 && item.currentPer <= filter.filterPerMax}'>criteria-pass</c:if><c:if test='${item.currentPer <= 0 || item.currentPer > filter.filterPerMax}'>criteria-fail</c:if>">
                    <fmt:formatNumber value="${item.currentPer}" pattern="#,##0.0" />
                </td>
                <td><c:if test="${item.industryPer != null && item.industryPer != ''}">${item.industryPer}</c:if></td>
                <td class="text-center">
                    <c:if test="${item.industryPerRatio != null}">
                        <span class="<c:if test='${item.industryPerRatio <= 60}'>criteria-pass</c:if>">${item.industryPerRatio}%</span>
                    </c:if>
                </td>
                <td>
                    <c:if test="${item.avgPer3y != null}"><fmt:formatNumber value="${item.avgPer3y}" pattern="#,##0.0" /></c:if>
                </td>
                <!-- PBR -->
                <td class="<c:if test='${item.currentPbr > 0 && item.currentPbr <= filter.filterPbrMax}'>criteria-pass</c:if><c:if test='${item.currentPbr <= 0 || item.currentPbr > filter.filterPbrMax}'>criteria-fail</c:if>">
                    <fmt:formatNumber value="${item.currentPbr}" pattern="#,##0.00" />
                </td>
                <!-- ROE 3Y -->
                <td class="<c:if test='${item.avgRoe3y != null && item.avgRoe3y >= filter.filterRoeMin && item.roePassYears >= 3}'>criteria-pass</c:if><c:if test='${item.avgRoe3y == null || item.avgRoe3y < filter.filterRoeMin || item.roePassYears < 3}'>criteria-fail</c:if>">
                    <c:if test="${item.avgRoe3y != null}"><fmt:formatNumber value="${item.avgRoe3y}" pattern="#,##0.1" />%</c:if>
                </td>
                <td class="text-center">
                    <c:choose>
                        <c:when test="${item.roePassYears >= 3}"><span class="criteria-pass">${item.roePassYears}Y</span></c:when>
                        <c:otherwise><span class="criteria-fail">${item.roePassYears}Y</span></c:otherwise>
                    </c:choose>
                </td>
                <!-- 배당률 -->
                <td class="<c:if test='${item.currentDividendRate != null && (item.currentDividendRate >= filter.filterDividendMin || item.dividendTendency >= 25)}'>criteria-pass</c:if><c:if test='${item.currentDividendRate == null || (item.currentDividendRate < filter.filterDividendMin && item.dividendTendency < 25)}'>criteria-fail</c:if>">
                    <c:if test="${item.currentDividendRate != null}"><fmt:formatNumber value="${item.currentDividendRate}" pattern="#,##0.0" />%</c:if>
                </td>
                <td>
                    <c:if test="${item.dividendTendency != null && item.dividendTendency > 0}">
                        <fmt:formatNumber value="${item.dividendTendency}" pattern="#,##0.0" />%
                    </c:if>
                </td>
                <!-- 부채비율 -->
                <td class="<c:if test='${item.currentDebtRatio >= 0 && item.currentDebtRatio <= filter.filterDebtMax}'>criteria-pass</c:if><c:if test='${item.currentDebtRatio < 0 || item.currentDebtRatio > filter.filterDebtMax}'>criteria-fail</c:if>">
                    <c:if test="${item.currentDebtRatio >= 0}"><fmt:formatNumber value="${item.currentDebtRatio}" pattern="#,##0.0" />%</c:if>
                    <c:if test="${item.currentDebtRatio < 0}">-</c:if>
                </td>
                <!-- 배당 연속성 -->
                <td class="text-center <c:if test='${item.divConsecutive >= 3}'>criteria-pass</c:if><c:if test='${item.divConsecutive < 3}'>criteria-fail</c:if>">
                    ${item.divConsecutive}Y<span style="font-size:10px;color:#868e96;"> (${item.divPayYears}/5)</span>
                </td>
                <!-- 영업이익 연속 -->
                <td class="text-center <c:if test='${item.profitConsecutive >= 3}'>criteria-pass</c:if><c:if test='${item.profitConsecutive < 3}'>criteria-fail</c:if>">
                    ${item.profitConsecutive}Y
                </td>
                <!-- 유동비율 -->
                <td class="text-center <c:if test='${item.currentRatio >= 100}'>criteria-pass</c:if><c:if test='${item.currentRatio < 100}'>criteria-fail</c:if>">
                    <c:choose>
                        <c:when test="${item.currentRatio >= 0}"><fmt:formatNumber value="${item.currentRatio}" pattern="#,##0.0" />%</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
                <!-- 영업활동현금흐름 -->
                <td class="text-center <c:if test='${item.operatingCashFlow != null && item.operatingCashFlow > 0}'>criteria-pass</c:if><c:if test='${item.operatingCashFlow == null || item.operatingCashFlow <= 0}'>criteria-fail</c:if>">
                    <c:choose>
                        <c:when test="${item.operatingCashFlow != null && item.operatingCashFlow != 0}">
                            <fmt:formatNumber value="${item.operatingCashFlow}" pattern="#,###" />
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
                <!-- 밸류업지수 편입 -->
                <td class="text-center <c:if test='${item.valueUpYn == "Y"}'>criteria-pass</c:if><c:if test='${item.valueUpYn != "Y"}'>criteria-fail</c:if>">
                    <c:choose>
                        <c:when test="${item.valueUpYn == 'Y'}">✓</c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
                <!-- 현재가 -->
                <td><c:if test="${not empty item.price}"><fmt:formatNumber value="${item.price}" pattern="#,###" /></c:if></td>
                <!-- 시가총액 -->
                <td><c:if test="${not empty item.marketCapitalization}"><fmt:formatNumber value="${item.marketCapitalization}" pattern="#,###" /></c:if></td>
                <!-- 업종 -->
                <td class="text-left" style="font-size:11px;">${item.sectorName}</td>
            </tr>
            </c:forEach>
            <c:if test="${empty screenList}">
                <tr><td colspan="23" class="text-center" style="padding:30px; color:#868e96;">조건에 맞는 종목이 없습니다. 필터를 완화해 보세요.</td></tr>
            </c:if>
        </tbody>
    </table>

</div>
</div>

<script>
kangong.valueScreen = {
    search: function() {
        var params = '?perMax=' + $('#filterPerMax').val()
                   + '&pbrMax=' + $('#filterPbrMax').val()
                   + '&roeMin=' + $('#filterRoeMin').val()
                   + '&dividendMin=' + $('#filterDividendMin').val()
                   + '&debtMax=' + $('#filterDebtMax').val()
                   + '&passCountMin=' + $('#filterPassCountMin').val();
        var capMin = $('#filterMarketCapMin').val();
        if (capMin) params += '&marketCapMin=' + capMin;
        location.href = '${pageContext.request.contextPath}/stock2/value' + params;
    },
    reset: function() {
        $('#filterPerMax').val(10);
        $('#filterPbrMax').val(0.8);
        $('#filterRoeMin').val(8);
        $('#filterDividendMin').val(3.5);
        $('#filterDebtMax').val(100);
        $('#filterMarketCapMin').val('');
        $('#filterPassCountMin').val(3);
    },
    downloadExcel: function() {
        var params = '?perMax=' + $('#filterPerMax').val()
                   + '&pbrMax=' + $('#filterPbrMax').val()
                   + '&roeMin=' + $('#filterRoeMin').val()
                   + '&dividendMin=' + $('#filterDividendMin').val()
                   + '&debtMax=' + $('#filterDebtMax').val()
                   + '&passCountMin=' + $('#filterPassCountMin').val();
        var capMin = $('#filterMarketCapMin').val();
        if (capMin) params += '&marketCapMin=' + capMin;
        location.href = '${pageContext.request.contextPath}/stock2/value/excel' + params;
    },
    toggleFilter: function(minPass) {
        $('#valueTable tbody tr').each(function() {
            var pass = parseInt($(this).data('pass')) || 0;
            if (minPass === 0 || pass >= minPass) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
        // 번호 재정렬
        var idx = 1;
        $('#valueTable tbody tr:visible').each(function() {
            $(this).find('td:first').text(idx++);
        });
    }
};

// 컬럼 정렬
$(function() {
    var sortDir = {};
    $('.sort-link').css('cursor', 'pointer').on('click', function() {
        var col = $(this).data('col');
        var colIdx = $(this).index();
        sortDir[col] = !sortDir[col];
        var rows = $('#valueTable tbody tr').get();
        rows.sort(function(a, b) {
            var aVal = parseFloat($(a).find('td').eq(colIdx).text().replace(/[,%]/g, '')) || 0;
            var bVal = parseFloat($(b).find('td').eq(colIdx).text().replace(/[,%]/g, '')) || 0;
            if (col === 'name') {
                aVal = $(a).find('td').eq(colIdx).text().trim();
                bVal = $(b).find('td').eq(colIdx).text().trim();
                return sortDir[col] ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal);
            }
            return sortDir[col] ? aVal - bVal : bVal - aVal;
        });
        $.each(rows, function(i, row) {
            $('#valueTable tbody').append(row);
        });
        // 번호 재정렬
        var idx = 1;
        $('#valueTable tbody tr:visible').each(function() {
            $(this).find('td:first').text(idx++);
        });
        $('.sort-link').removeClass('sort-active');
        $(this).addClass('sort-active');
    });
});
</script>
