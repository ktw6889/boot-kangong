<%@ page pageEncoding="utf-8" %>

<style>
.stock-layout { display: flex; min-height: calc(100vh - 250px); }
.stock-left-menu { width: 200px; min-width: 200px; background-color: #f8f9fa; border-right: 1px solid #dee2e6; padding: 0; }
.stock-left-menu .menu-title { background-color: #343a40; color: #fff; padding: 12px 15px; font-weight: bold; font-size: 15px; margin: 0; }
.stock-left-menu .list-group-item { border-radius: 0; border-left: none; border-right: none; padding: 10px 15px; font-size: 13px; cursor: pointer; }
.stock-left-menu .list-group-item:hover { background-color: #e9ecef; }
.stock-left-menu .list-group-item.active { background-color: #007bff; border-color: #007bff; color: #fff; }
.stock-left-menu .menu-section { font-size: 11px; color: #6c757d; padding: 8px 15px 4px; text-transform: uppercase; font-weight: bold; background-color: #f1f3f5; }
.stock-content { flex: 1; padding: 15px; overflow-x: auto; }
</style>

<div class="stock-left-menu">
    <div class="menu-title">증권</div>
    <div class="list-group list-group-flush">
        <div class="menu-section">지표</div>
        <a href="${pageContext.request.contextPath}/marketcycle"
           class="list-group-item list-group-item-action ${activeMenu == 'marketcycle' ? 'active' : ''}">증시 사계론</a>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stock2/macro'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'macroIndicator' ? 'active' : ''}">시장 매크로 지표</a>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stock2/esg'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'esgList' ? 'active' : ''}">ESG 지표</a>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stockMobile/index/list'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'indexList' ? 'active' : ''}">시장지표</a>
        <div class="menu-section">포트폴리오</div>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stock2/portfolio'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'portfolio' ? 'active' : ''}">자산배분 대시보드</a>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stock2'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'interestList' ? 'active' : ''}">포트폴리오 현황</a>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stock2/edit'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'interestEdit' ? 'active' : ''}">포트폴리오 수정</a>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stock2/recommend'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'recommend' ? 'active' : ''}">리밸런싱 추천</a>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stock2/value'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'valueScreen' ? 'active' : ''}">가치주 스크리닝</a>
        <div class="menu-section">데이터</div>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/stock'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'stockList' ? 'active' : ''}">데이터 관리</a>
        <div class="menu-section">KRX 코스피</div>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/advstock'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'advStockList' ? 'active' : ''}">코스피 다운로드</a>
        <a href="#" onclick="kangong.form.submitPost('${pageContext.request.contextPath}/advstock/yahoo'); return false;"
           class="list-group-item list-group-item-action ${activeMenu == 'advStockYahooList' ? 'active' : ''}">Yahoo Finance 다운로드</a>
        <div class="menu-section">자산관리</div>
        <a href="${pageContext.request.contextPath}/retire"
           class="list-group-item list-group-item-action ${activeMenu == 'retire' ? 'active' : ''}">노후자산 시뮬레이터</a>
    </div>
</div>
