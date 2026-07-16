<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<style>
.table-rec { font-size: 13px; }
.table-rec th { text-align: center; white-space: nowrap; vertical-align: middle !important; }
.table-rec td { text-align: right; white-space: nowrap; vertical-align: middle !important; }
.table-rec td.text-left { text-align: left; }
.table-rec td.text-center { text-align: center; }
.section-buy { border-left: 4px solid #dc3545; padding-left: 12px; margin-bottom: 25px; }
.section-sell { border-left: 4px solid #007bff; padding-left: 12px; margin-bottom: 25px; }
.section-buy h6 { color: #dc3545; font-weight: bold; }
.section-sell h6 { color: #007bff; font-weight: bold; }
.badge-score { display: inline-block; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: bold; color: #fff; }
.score-high { background-color: #dc3545; }
.score-mid { background-color: #fd7e14; }
.score-low { background-color: #6c757d; }
.dev-positive { color: #dc3545; font-weight: bold; }
.dev-negative { color: #007bff; font-weight: bold; }
.reason-text { font-size: 11px; color: #555; white-space: normal; max-width: 200px; }
.filter-bar { display: flex; align-items: center; gap: 10px; margin-bottom: 15px; }
.filter-bar select { width: 200px; }
.summary-card { display: flex; gap: 15px; margin-bottom: 20px; flex-wrap: wrap; }
.summary-card .card { min-width: 180px; }
.summary-card .card-body { padding: 12px 16px; }
.summary-card .card-title { font-size: 12px; color: #6c757d; margin-bottom: 4px; }
.summary-card .card-value { font-size: 20px; font-weight: bold; }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

    <h5>리밸런싱 추천</h5>
    <p class="text-muted" style="font-size:12px;">조회일시: <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm"/></p>

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
        <button class="btn btn-sm btn-primary" onclick="kangong.recommend.search();">조회</button>
    </div>

    <%-- 요약 카드 --%>
    <div class="summary-card">
        <div class="card border-danger">
            <div class="card-body">
                <div class="card-title">매수 추천</div>
                <div class="card-value text-danger">${fn:length(buyList)}건</div>
            </div>
        </div>
        <div class="card border-primary">
            <div class="card-body">
                <div class="card-title">매도 추천</div>
                <div class="card-value text-primary">${fn:length(sellList)}건</div>
            </div>
        </div>
        <c:set var="totalBuyAmt" value="0" />
        <c:forEach items="${buyList}" var="row">
            <c:set var="totalBuyAmt" value="${totalBuyAmt + row.recommendAmount}" />
        </c:forEach>
        <div class="card border-warning">
            <div class="card-body">
                <div class="card-title">매수 필요금액</div>
                <div class="card-value text-danger"><fmt:formatNumber value="${totalBuyAmt}" pattern="#,###"/>원</div>
            </div>
        </div>
        <c:set var="totalSellAmt" value="0" />
        <c:forEach items="${sellList}" var="row">
            <c:set var="totalSellAmt" value="${totalSellAmt + row.recommendAmount}" />
        </c:forEach>
        <div class="card border-info">
            <div class="card-body">
                <div class="card-title">매도 예상금액</div>
                <div class="card-value text-primary"><fmt:formatNumber value="${totalSellAmt}" pattern="#,###"/>원</div>
            </div>
        </div>
    </div>

    <%-- 매수 추천 --%>
    <div class="section-buy">
        <h6>매수 추천 종목</h6>
        <table class="table table-bordered table-hover table-sm table-rec">
            <thead class="thead-dark">
                <tr>
                    <th>우선순위</th>
                    <th>계좌</th>
                    <th>종목명</th>
                    <th>유형</th>
                    <th>현재가</th>
                    <th>목표비중</th>
                    <th>실제비중</th>
                    <th>괴리</th>
                    <th>추천수량</th>
                    <th>추천금액</th>
                    <th>점수</th>
                    <th>추천사유</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${fn:length(buyList) > 0}">
                        <c:forEach items="${buyList}" var="row" varStatus="st">
                            <tr>
                                <td class="text-center">${st.index + 1}</td>
                                <td class="text-left">${row.stockDivision}</td>
                                <td class="text-left">${row.name}</td>
                                <td class="text-center"><span class="badge badge-${row.stockType == '채권' ? 'info' : row.stockType == '현물' ? 'warning' : 'secondary'}" style="font-size:11px;">${row.stockType}</span></td>
                                <td><fmt:formatNumber value="${row.price}" pattern="#,###"/></td>
                                <td><fmt:formatNumber value="${row.stockPotion}" pattern="#,##0"/>%</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${row.actualPotion != null}"><fmt:formatNumber value="${row.actualPotion}" pattern="#,##0.0"/>%</c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="dev-negative"><fmt:formatNumber value="${row.deviation}" pattern="#,##0.0"/>%</td>
                                <td class="text-danger"><fmt:formatNumber value="${row.recommendQty}" pattern="#,###"/>주</td>
                                <td class="text-danger"><fmt:formatNumber value="${row.recommendAmount}" pattern="#,###"/></td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${row.score >= 5}"><span class="badge-score score-high">${row.score}</span></c:when>
                                        <c:when test="${row.score >= 3}"><span class="badge-score score-mid">${row.score}</span></c:when>
                                        <c:otherwise><span class="badge-score score-low">${row.score}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-left reason-text">${row.reason}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr><td colspan="12" class="text-center">매수 추천 종목이 없습니다.</td></tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <%-- 매도 추천 --%>
    <div class="section-sell">
        <h6>매도 추천 종목</h6>
        <table class="table table-bordered table-hover table-sm table-rec">
            <thead class="thead-dark">
                <tr>
                    <th>우선순위</th>
                    <th>계좌</th>
                    <th>종목명</th>
                    <th>유형</th>
                    <th>현재가</th>
                    <th>목표비중</th>
                    <th>실제비중</th>
                    <th>괴리</th>
                    <th>추천수량</th>
                    <th>추천금액</th>
                    <th>점수</th>
                    <th>추천사유</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${fn:length(sellList) > 0}">
                        <c:forEach items="${sellList}" var="row" varStatus="st">
                            <tr>
                                <td class="text-center">${st.index + 1}</td>
                                <td class="text-left">${row.stockDivision}</td>
                                <td class="text-left">${row.name}</td>
                                <td class="text-center"><span class="badge badge-${row.stockType == '채권' ? 'info' : row.stockType == '현물' ? 'warning' : 'secondary'}" style="font-size:11px;">${row.stockType}</span></td>
                                <td><fmt:formatNumber value="${row.price}" pattern="#,###"/></td>
                                <td><fmt:formatNumber value="${row.stockPotion}" pattern="#,##0"/>%</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${row.actualPotion != null}"><fmt:formatNumber value="${row.actualPotion}" pattern="#,##0.0"/>%</c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="dev-positive">+<fmt:formatNumber value="${row.deviation}" pattern="#,##0.0"/>%</td>
                                <td class="text-primary"><fmt:formatNumber value="${row.recommendQty}" pattern="#,###"/>주</td>
                                <td class="text-primary"><fmt:formatNumber value="${row.recommendAmount}" pattern="#,###"/></td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${row.score >= 5}"><span class="badge-score score-high">${row.score}</span></c:when>
                                        <c:when test="${row.score >= 3}"><span class="badge-score score-mid">${row.score}</span></c:when>
                                        <c:otherwise><span class="badge-score score-low">${row.score}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-left reason-text">${row.reason}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr><td colspan="12" class="text-center">매도 추천 종목이 없습니다.</td></tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

</div>
</div>

<c:url var="recommendURL" value="/stock2/recommend"></c:url>
<script>
kangong.recommend = {
    search: function() {
        var paramObj = {};
        var div = $('#divisionFilter').val();
        if (div) paramObj.stockDivision = div;
        kangong.form.submitPost('${recommendURL}', paramObj);
    }
}
</script>
