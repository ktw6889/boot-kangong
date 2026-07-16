<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

    <h5>ESG 지표</h5>
    <hr/>

    <div class="d-flex align-items-center mb-3" style="gap:8px;">
        <input type="text" id="searchKeyword" class="form-control form-control-sm" style="width:160px;" placeholder="종목코드/종목명" value="${searchKeyword}"/>
        <input type="text" id="searchYear" class="form-control form-control-sm" style="width:80px;" placeholder="년도" value="${searchYear}"/>
        <button class="btn btn-sm btn-primary" onclick="kangong.esg.search();">조회</button>
        <small class="text-muted ml-2">총 <strong>${fn:length(esgList)}</strong>건</small>
    </div>

    <div class="table-responsive">
    <table id="esgTable" class="table table-sm table-bordered table-hover" style="font-size:12px;">
        <thead class="thead-dark">
            <tr>
                <th class="sortable" data-col="0" data-type="string">종목코드<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable" data-col="1" data-type="string">종목명<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable" data-col="2" data-type="string">년도<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="3" data-type="number">온실가스<br/>배출량<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="4" data-type="number">에너지<br/>사용량<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="5" data-type="number">미세먼지<br/>배출량<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="6" data-type="number">용수<br/>재활용률<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="7" data-type="number">폐기물<br/>재활용률<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="8" data-type="number">평균연봉<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="9" data-type="number">비정규직<br/>고용률<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="10" data-type="number">기부금<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="11" data-type="number">평균<br/>근속년수<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="12" data-type="number">사외이사<br/>비율<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="13" data-type="number">최대주주<br/>지분율<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="14" data-type="number">이사회<br/>독립성<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="15" data-type="number">임원<br/>평균보수<span class="sort-arrow">&#9650;&#9660;</span></th>
                <th class="sortable text-right" data-col="16" data-type="number">임직원<br/>보수비율<span class="sort-arrow">&#9650;&#9660;</span></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="row" items="${esgList}">
            <tr>
                <td>${row.stockId}</td>
                <td><strong>${row.name}</strong></td>
                <td class="text-center">${row.year}</td>
                <td class="text-right">${row.greenHouseEmission}</td>
                <td class="text-right">${row.energyUsage}</td>
                <td class="text-right">${row.fineDustUsage}</td>
                <td class="text-right">${row.waterRecyclingRate}</td>
                <td class="text-right">${row.wasteRecyclingRate}</td>
                <td class="text-right">${row.averageAnnualSalary}</td>
                <td class="text-right">${row.nonRegularEmplymentRate}</td>
                <td class="text-right">${row.donation}</td>
                <td class="text-right">${row.continuousServiceYear}</td>
                <td class="text-right">${row.outsideDirectorRate}</td>
                <td class="text-right">${row.largestShareHolderRatio}</td>
                <td class="text-right">${row.directorateIndependence}</td>
                <td class="text-right">${row.executiveAverageAnnualSalary}</td>
                <td class="text-right">${row.salaryRatio}</td>
            </tr>
            </c:forEach>
            <c:if test="${empty esgList}">
            <tr>
                <td colspan="17" class="text-center text-muted py-4">데이터가 없습니다.</td>
            </tr>
            </c:if>
        </tbody>
    </table>
    </div>

</div>
</div>

<c:url var="esgURL" value="/stock2/esg"/>
<script>
kangong.esg = {
    search: function() {
        var keyword = $('#searchKeyword').val();
        var year = $('#searchYear').val();
        kangong.form.submitPost('${esgURL}', { keyword: keyword, year: year, searched: 'Y' });
    }
};
$(document).ready(function() {
    $('#searchKeyword, #searchYear').on('keypress', function(e) {
        if (e.which === 13) kangong.esg.search();
    });

    var $table = $('#esgTable');
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
            var valA = $(a).find('td').eq(colIdx).text().trim();
            var valB = $(b).find('td').eq(colIdx).text().trim();
            if (dataType === 'number') {
                valA = parseFloat(valA.replace(/,/g, '')) || 0;
                valB = parseFloat(valB.replace(/,/g, '')) || 0;
            }
            if (valA < valB) return -dir;
            if (valA > valB) return dir;
            return 0;
        });

        $.each($rows, function(i, row) { $tbody.append(row); });
    });
});
</script>
