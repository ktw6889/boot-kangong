<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<style>
.dm-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 16px; }
.dm-card { border: 1px solid #dee2e6; border-radius: 8px; background: #fff; overflow: hidden; }
.dm-card-header { padding: 10px 16px; font-size: 14px; font-weight: 700; color: #fff; display: flex; align-items: center; gap: 8px; }
.dm-card-body { padding: 14px 16px; display: flex; flex-direction: column; gap: 8px; }
.dm-btn { display: flex; align-items: center; justify-content: space-between; padding: 8px 14px; border: 1px solid #dee2e6; border-radius: 6px; background: #f8f9fa; cursor: pointer; font-size: 13px; transition: all .15s; text-decoration: none; color: #333; }
.dm-btn:hover { background: #e9ecef; border-color: #adb5bd; text-decoration: none; color: #333; }
.dm-btn .dm-label { font-weight: 500; }
.dm-btn .dm-badge { font-size: 10px; padding: 2px 8px; border-radius: 10px; color: #fff; font-weight: 600; }
.dm-badge-single { background: #17a2b8; }
.dm-badge-bulk { background: #fd7e14; }
.hdr-daily { background: linear-gradient(135deg, #2b8a3e, #37b24d); }
.hdr-stock { background: linear-gradient(135deg, #1864ab, #228be6); }
.hdr-price { background: linear-gradient(135deg, #862e9c, #9c36b5); }
.hdr-finance { background: linear-gradient(135deg, #c92a2a, #e03131); }
.hdr-etc { background: linear-gradient(135deg, #495057, #868e96); }
.dm-status { display: none; margin-top: 12px; padding: 10px 14px; border-radius: 6px; font-size: 13px; }
.dm-status.running { display: block; background: #fff3cd; color: #856404; border: 1px solid #ffc107; }
.dm-status.done { display: block; background: #d4edda; color: #155724; border: 1px solid #28a745; }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

<h5>데이터 관리</h5>
<p class="text-muted" style="font-size:12px;">크롤링 및 데이터 수집 작업을 실행합니다.</p>

<div id="dmStatus" class="dm-status"></div>

<div class="dm-grid">

    <div class="dm-card">
        <div class="dm-card-header hdr-daily">일괄 업데이트</div>
        <div class="dm-card-body">
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('updateDaily');">
                <span class="dm-label">Daily Update</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveAllStockId');">
                <span class="dm-label">전체 종목코드 저장</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>

        </div>
    </div>

    <div class="dm-card">
        <div class="dm-card-header hdr-stock">종목 데이터</div>
        <div class="dm-card-body">
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('save', '005930');">
                <span class="dm-label">삼성전자 크롤링</span>
                <span class="dm-badge dm-badge-single">단건</span>
            </a>
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveSimpleList');">
                <span class="dm-label">SimpleList 저장</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveAll');">
                <span class="dm-label">전체 List 저장</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>
        </div>
    </div>

    <div class="dm-card">
        <div class="dm-card-header hdr-price">가격 데이터</div>
        <div class="dm-card-body">
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveDailyPrice', '005930');">
                <span class="dm-label">삼성전자 Daily Price</span>
                <span class="dm-badge dm-badge-single">단건</span>
            </a>
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveDailyPriceMig');">
                <span class="dm-label">Daily Price 전체 Migration</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveStockMarketIndex');">
                <span class="dm-label">시장 지표(Market Index)</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>
        </div>
    </div>

    <div class="dm-card">
        <div class="dm-card-header hdr-finance">재무 데이터</div>
        <div class="dm-card-body">
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveStockFinancial', '005930');">
                <span class="dm-label">삼성전자 Financial</span>
                <span class="dm-badge dm-badge-single">단건</span>
            </a>
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveStockFinancialAll');">
                <span class="dm-label">Financial 전체 저장</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>
        </div>
    </div>

    <div class="dm-card">
        <div class="dm-card-header hdr-etc">기타 데이터</div>
        <div class="dm-card-body">
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveStockCategory');">
                <span class="dm-label">업종 카테고리 저장</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveStockEsg', '005930');">
                <span class="dm-label">삼성전자 ESG</span>
                <span class="dm-badge dm-badge-single">단건</span>
            </a>
            <a class="dm-btn" href="javascript:kangong.stockMobile.runTask('saveStockEsgAll');">
                <span class="dm-label">ESG 전체 저장</span>
                <span class="dm-badge dm-badge-bulk">전체</span>
            </a>
        </div>
    </div>

</div>

</div>
</div>

<c:url var="saveAllStockIdURL" value="/stockMobile/saveAllStockId"></c:url>
<c:url var="updateDailyURL" value="/stockMobile/updateDailyStock"></c:url>
<c:url var="saveSimpleListURL" value="/stockMobile/saveSimpleList"></c:url>
<c:url var="saveURL" value="/stockMobile/save"></c:url>
<c:url var="saveAllURL" value="/stockMobile/saveAll"></c:url>
<c:url var="saveDaillyPriceURL" value="/stockMobile/saveDaillyPrice"></c:url>
<c:url var="saveDaillyPriceMigURL" value="/stockMobile/saveDaillyPriceMig"></c:url>
<c:url var="saveStockFinancialURL" value="/stockMobile/saveStockFinancial"></c:url>
<c:url var="saveStockFinancialAllURL" value="/stockMobile/saveStockFinancialAll"></c:url>
<c:url var="saveStockCategoryURL" value="/stockMobile/saveStockCategory"></c:url>
<c:url var="saveStockEsgAllURL" value="/stockMobile/saveStockEsgAll"></c:url>
<c:url var="saveStockEsgURL" value="/stockMobile/saveStockEsg"></c:url>
<c:url var="saveStockMarketIndexURL" value="/stockMobile/saveStockMarketIndex"></c:url>

<script>
kangong.stockMobile = {
    urlMap: {
        updateDaily:            '${updateDailyURL}',
        saveAllStockId:         '${saveAllStockIdURL}',
        save:                   '${saveURL}',
        saveSimpleList:         '${saveSimpleListURL}',
        saveAll:                '${saveAllURL}',
        saveDailyPrice:         '${saveDaillyPriceURL}',
        saveDailyPriceMig:      '${saveDaillyPriceMigURL}',
        saveStockFinancial:     '${saveStockFinancialURL}',
        saveStockFinancialAll:  '${saveStockFinancialAllURL}',
        saveStockCategory:      '${saveStockCategoryURL}',
        saveStockEsgAll:        '${saveStockEsgAllURL}',
        saveStockEsg:           '${saveStockEsgURL}',
        saveStockMarketIndex:   '${saveStockMarketIndexURL}'
    },
    runTask: function(task, stockId) {
        var url = this.urlMap[task];
        if (!url) return;
        var paramObj = {};
        if (stockId) paramObj.stockId = stockId;
        var $st = $('#dmStatus');
        $st.removeClass('done').addClass('running').text(task + ' 실행 중...').show();
        kangong.form.submitPost(url, paramObj);
    }
}
</script>
