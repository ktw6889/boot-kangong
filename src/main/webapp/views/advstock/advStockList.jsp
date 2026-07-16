<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<style>
#advStockTable th.sortable { cursor: pointer; user-select: none; position: relative; padding-right: 18px; }
#advStockTable th.sortable:hover { background-color: #495057; }
#advStockTable th.sortable .sort-arrow { position: absolute; right: 4px; top: 50%; transform: translateY(-50%); font-size: 10px; color: #adb5bd; }
#advStockTable th.sortable.asc .sort-arrow,
#advStockTable th.sortable.desc .sort-arrow { color: #fff; }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

<h5>코스피 데이터 다운로드</h5>
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
            <label class="mr-2"><strong>Daily Price 기준일:</strong></label>
            <input type="text" id="tradingDate" class="form-control form-control-sm mr-3"
                   value="${today}" placeholder="YYYY-MM-DD" maxlength="10" style="width:130px;"/>
            <button type="button" class="btn btn-primary btn-sm mr-2" onclick="kangong.advstock.downloadAll();">
                전체 다운로드
            </button>
        </div>
        <div class="mt-2">
            <button type="button" class="btn btn-outline-secondary btn-sm mr-1" onclick="kangong.advstock.downloadMaster();">
                종목 Master
            </button>
            <button type="button" class="btn btn-outline-secondary btn-sm mr-1" onclick="kangong.advstock.downloadDailyPrice();">
                Daily Price
            </button>
            <button type="button" class="btn btn-outline-secondary btn-sm mr-1" onclick="kangong.advstock.downloadCategory();">
                업종 Category
            </button>
            <button type="button" class="btn btn-success btn-sm ml-2" onclick="kangong.advstock.excelDownload();">
                엑셀 다운로드
            </button>
        </div>
        <small class="text-muted mt-2 d-block">
            데이터 소스: Naver Finance API (무료, 인증 불필요)<br/>
            저장 대상: ST_STOCK_MASTER (종목+삭제플래그), ST_STOCK_DAILY_PRICE (일별시세), ST_STOCK_CATEGORY/LINK (업종분류)
        </small>
    </div>
</div>

<div class="mb-2">
    <small>총 <strong>${fn:length(stockList)}</strong>건 (타이틀 클릭시 정렬)</small>
</div>

<div class="table-responsive">
<table id="advStockTable" class="table table-sm table-bordered table-hover" style="font-size:12px;">
    <thead class="thead-dark">
        <tr>
            <th style="width:40px;">No</th>
            <th class="sortable" data-col="1" data-type="string">종목코드<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable" data-col="2" data-type="string">종목명<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="3" data-type="number">현재가<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="4" data-type="number">거래량<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="5" data-type="number">시가총액<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="6" data-type="number">PER<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable text-right" data-col="7" data-type="number">PBR<span class="sort-arrow">&#9650;&#9660;</span></th>
            <th class="sortable" data-col="8" data-type="string">수정일<span class="sort-arrow">&#9650;&#9660;</span></th>
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
            <td class="text-right" data-val="${stock.volumn}">
                <c:if test="${not empty stock.volumn}"><fmt:formatNumber value="${stock.volumn}" pattern="#,###"/></c:if>
            </td>
            <td class="text-right" data-val="${stock.marketCapitalization}">
                <c:if test="${not empty stock.marketCapitalization}"><fmt:formatNumber value="${stock.marketCapitalization}" pattern="#,###"/></c:if>
            </td>
            <td class="text-right" data-val="${stock.per}">${stock.per}</td>
            <td class="text-right" data-val="${stock.pbr}">${stock.pbr}</td>
            <td><fmt:formatDate value="${stock.updateDate}" pattern="MM-dd HH:mm"/></td>
        </tr>
        </c:forEach>
        <c:if test="${empty stockList}">
        <tr class="empty-row">
            <td colspan="9" class="text-center text-muted py-4">
                데이터가 없습니다. [전체 다운로드] 버튼을 클릭하세요.
            </td>
        </tr>
        </c:if>
    </tbody>
</table>
</div>

</div>
</div>

<c:url var="downloadAllURL" value="/advstock/downloadAll"/>
<c:url var="downloadMasterURL" value="/advstock/downloadMaster"/>
<c:url var="downloadDailyPriceURL" value="/advstock/downloadDailyPrice"/>
<c:url var="downloadCategoryURL" value="/advstock/downloadCategory"/>
<c:url var="excelURL" value="/advstock/excel"/>

<script>
kangong.advstock = {
    downloadAll: function() {
        var date = $('#tradingDate').val();
        kangong.form.submitPost('${downloadAllURL}', { tradingDate: date });
    },
    downloadMaster: function() {
        kangong.form.submitPost('${downloadMasterURL}');
    },
    downloadDailyPrice: function() {
        var date = $('#tradingDate').val();
        kangong.form.submitPost('${downloadDailyPriceURL}', { tradingDate: date });
    },
    downloadCategory: function() {
        kangong.form.submitPost('${downloadCategoryURL}');
    },
    excelDownload: function() {
        window.location.href = '${excelURL}';
    }
};

$(document).ready(function() {
    var $table = $('#advStockTable');
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
