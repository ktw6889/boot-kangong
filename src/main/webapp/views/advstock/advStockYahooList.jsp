<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<style>
#yahooTable th.sortable { cursor: pointer; user-select: none; position: relative; padding-right: 18px; }
#yahooTable th.sortable:hover { background-color: #495057; }
#yahooTable th.sortable .sort-arrow { position: absolute; right: 4px; top: 50%; transform: translateY(-50%); font-size: 10px; color: #adb5bd; }
#yahooTable th.sortable.asc .sort-arrow,
#yahooTable th.sortable.desc .sort-arrow { color: #fff; }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

<h5>Yahoo Finance KOSPI 데이터 다운로드</h5>
<hr/>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger py-2" style="font-size:13px;">${errorMessage}</div>
</c:if>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success py-2" style="font-size:13px;">${successMessage}</div>
</c:if>

<div class="card mb-3">
    <div class="card-body">
        <div class="form-inline mb-2">
            <label class="mr-2"><strong>검색:</strong></label>
            <input type="text" id="yahooKeyword" class="form-control form-control-sm mr-2"
                   value="${keyword}" placeholder="KODEX, TIGER, 069500 등" style="width:200px;"/>
            <button type="button" class="btn btn-primary btn-sm mr-2" onclick="kangong.advYahoo.search();">
                조회
            </button>
            <label class="mr-2 ml-3"><strong>기준일:</strong></label>
            <input type="text" id="yahooTradingDate" class="form-control form-control-sm mr-2"
                   value="${today}" placeholder="YYYY-MM-DD" maxlength="10" style="width:130px;"/>
        </div>
        <div class="mt-2">
            <button type="button" class="btn btn-outline-secondary btn-sm mr-1" onclick="kangong.advYahoo.downloadMaster();">
                Master 저장
            </button>
            <button type="button" class="btn btn-outline-secondary btn-sm mr-1" onclick="kangong.advYahoo.downloadDailyPrice();">
                Daily Price 저장
            </button>
        </div>
        <small class="text-muted mt-2 d-block">
            데이터 소스: Yahoo Finance API<br/>
            저장 대상: ST_STOCK_MASTER (종목Master), ST_STOCK_DAILY_PRICE (일별시세)
        </small>
    </div>
</div>

<div class="alert alert-info py-2" style="font-size:12px;">
    <strong>Yahoo Finance API 제공 항목:</strong>
    종목코드, 종목명, 현재가, 전일대비, 등락률, NAV, 거래량, 시가총액
</div>

<div class="mb-2">
    <small>총 <strong>${fn:length(stockList)}</strong>건 (타이틀 클릭시 정렬)</small>
</div>

<div class="table-responsive">
<table id="yahooTable" class="table table-sm table-bordered table-hover" style="font-size:12px;">
    <thead class="thead-dark">
        <tr>
            <th style="width:40px;">No</th>
            <th class="sortable" data-col="1" data-type="string">종목코드<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable" data-col="2" data-type="string">���목명<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="3" data-type="number">현재가<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="4" data-type="number">전일대비<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="5" data-type="number">등락률(%)<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="6" data-type="number">NAV<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="7" data-type="number">거래량<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="8" data-type="number">시가총액<span class="sort-arrow">&#9650;&#9660;</span></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="stock" items="${stockList}" varStatus="status">
        <tr>
            <td class="text-center">${status.index + 1}</td>
            <td>${stock.stockId}</td>
            <td><strong>${stock.name}</strong></td>
            <td class="text-right" data-val="${stock.price}">
                <c:if test="${not empty stock.price}"><fmt:formatNumber value="${stock.price}" pattern="#,###"/></c:if>
            </td>
            <td class="text-right<c:if test="${not empty stock.priceChange and stock.priceChange > 0.0}"> text-danger</c:if><c:if test="${not empty stock.priceChange and stock.priceChange < 0.0}"> text-primary</c:if>" data-val="${stock.priceChange}">
                <c:if test="${not empty stock.priceChange}"><c:if test="${stock.priceChange > 0.0}">+</c:if><fmt:formatNumber value="${stock.priceChange}" pattern="#,###"/></c:if>
            </td>
            <td class="text-right<c:if test="${not empty stock.fluctuationRate and stock.fluctuationRate > 0.0}"> text-danger</c:if><c:if test="${not empty stock.fluctuationRate and stock.fluctuationRate < 0.0}"> text-primary</c:if>" data-val="${stock.fluctuationRate}">
                <c:if test="${not empty stock.fluctuationRate}">${stock.fluctuationRate}%</c:if>
            </td>
            <td class="text-right" data-val="${stock.nav}">
                <c:if test="${not empty stock.nav}"><fmt:formatNumber value="${stock.nav}" pattern="#,##0.0"/></c:if>
            </td>
            <td class="text-right" data-val="${stock.volumn}">
                <c:if test="${not empty stock.volumn}"><fmt:formatNumber value="${stock.volumn}" pattern="#,###"/></c:if>
            </td>
            <td class="text-right" data-val="${stock.marketCapitalization}">
                <c:if test="${not empty stock.marketCapitalization}"><fmt:formatNumber value="${stock.marketCapitalization}" pattern="#,###"/></c:if>
            </td>
        </tr>
        </c:forEach>
        <c:if test="${empty stockList}">
        <tr class="empty-row">
            <td colspan="9" class="text-center text-muted py-4">
                데이터가 없습니다. [조회] 버튼�� 클릭하세요.
            </td>
        </tr>
        </c:if>
    </tbody>
</table>
</div>

</div>
</div>

<c:url var="yahooListURL" value="/advstock/yahoo"/>
<c:url var="yahooDownloadMasterURL" value="/advstock/yahoo/downloadMaster"/>
<c:url var="yahooDownloadDailyPriceURL" value="/advstock/yahoo/downloadDailyPrice"/>

<script>
kangong.advYahoo = {
    search: function() {
        var keyword = $('#yahooKeyword').val();
        kangong.form.submitPost('${yahooListURL}', { keyword: keyword });
    },
    downloadMaster: function() {
        var keyword = $('#yahooKeyword').val();
        kangong.form.submitPost('${yahooDownloadMasterURL}', { keyword: keyword });
    },
    downloadDailyPrice: function() {
        var keyword = $('#yahooKeyword').val();
        var date = $('#yahooTradingDate').val();
        kangong.form.submitPost('${yahooDownloadDailyPriceURL}', { tradingDate: date, keyword: keyword });
    }
};

$(document).ready(function() {
    var $table = $('#yahooTable');
    var $headers = $table.find('th.sortable');

    $headers.on('click', function() {
        var $th = $(this);
        var colIdx = parseInt($th.data('col'));
        var dataType = $th.data('type');
        var isAsc = $th.hasClass('asc');

        $headers.removeClass('asc desc');
        $th.addClass(isAsc ? 'desc' : 'asc');

        var dir = isAsc ? -1 : 1;
        var $tbody = $table.find('tbody');
        var $rows = $tbody.find('tr').not('.empty-row').get();

        $rows.sort(function(a, b) {
            var $cellA = $(a).find('td').eq(colIdx);
            var $cellB = $(b).find('td').eq(colIdx);
            var valA, valB;

            if (dataType === 'number') {
                valA = parseFloat(($cellA.data('val') + '').replace(/,/g, '')) || 0;
                valB = parseFloat(($cellB.data('val') + '').replace(/,/g, '')) || 0;
            } else {
                valA = ($cellA.text() || '').trim();
                valB = ($cellB.text() || '').trim();
            }

            if (valA < valB) return -1 * dir;
            if (valA > valB) return 1 * dir;
            return 0;
        });

        $.each($rows, function(i, row) {
            $tbody.append(row);
            $(row).find('td').eq(0).text(i + 1);
        });
    });
});
</script>
