<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.7/dist/chart.umd.min.js"></script>

<style>
.macro-table { font-size: 13px; margin-bottom: 0; table-layout: fixed; width: 100%; }
.macro-table th { background-color: #f8f9fa; text-align: center; vertical-align: middle; white-space: nowrap; }
.macro-table td { text-align: right; vertical-align: middle; overflow: hidden; text-overflow: ellipsis; }
.macro-table td:first-child { text-align: left; white-space: nowrap; }
.text-up { color: #d63031; font-weight: bold; }
.text-down { color: #0984e3; font-weight: bold; }
.text-flat { color: #636e72; }
.macro-card { margin-bottom: 15px; }
.macro-card .card-header { font-size: 14px; font-weight: bold; padding: 8px 15px; }
.macro-info { font-size: 12px; color: #6c757d; padding: 6px 15px; }
.section-row { display: flex; flex-wrap: wrap; gap: 15px; }
.section-col { flex: 1; min-width: 380px; }
.chart-wrap { position: relative; height: 380px; }
.loading-box { text-align: center; padding: 30px 0; color: #6c757d; font-size: 13px; }
.loading-box .spinner-border { width: 1.2rem; height: 1.2rem; border-width: 2px; }
.error-box { text-align: center; padding: 20px; color: #d63031; font-size: 13px; }
.nav-tabs .nav-link { font-size: 14px; }
.reload-btn { font-size: 12px; padding: 2px 10px; }
.ref-link { color: #6c757d; font-size: 11px; }
.signal-badge { font-size: 11px; padding: 3px 8px; }
.signal-desc { font-size: 11px; line-height: 1.3; display: inline-block; max-width: 200px; }
.signal-asset-card { margin-bottom: 10px; }
.signal-asset-card .card-header { font-size: 14px; padding: 10px 15px; }
.signal-text-lg { font-size: 15px; font-weight: bold; margin-bottom: 4px; }
.signal-reasons { font-size: 11px; color: #6c757d; }
.text-warning-dark { color: #e67e22; }
.alloc-bar-bg { background: #ecf0f1; border-radius: 4px; height: 18px; overflow: hidden; }
.alloc-bar { height: 100%; color: #fff; font-size: 11px; font-weight: bold; text-align: center; line-height: 18px; border-radius: 4px; min-width: 30px; transition: width 0.5s ease; }
.alloc-bar-row td { border-top: none !important; }
.action-list { margin: 0; padding-left: 20px; }
.action-list li { padding: 4px 0; font-size: 13px; }
.warning-list { margin: 0; padding-left: 20px; }
.warning-list li { padding: 3px 0; font-size: 13px; }
.badge-outline-success { color: #27ae60; border: 1px solid #27ae60; background: transparent; }
.badge-outline-danger { color: #e74c3c; border: 1px solid #e74c3c; background: transparent; }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

    <div class="d-flex justify-content-between align-items-center mb-2">
        <h5 class="mb-0">시장 매크로 지표</h5>
        <button class="btn btn-outline-secondary reload-btn" onclick="reloadAll()">새로고침</button>
    </div>

    <ul class="nav nav-tabs" id="macroTabs" role="tablist">
        <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#tab-signal" role="tab">매매 시그널</a></li>
        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#tab-allocation" role="tab">자산배분</a></li>
        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#tab-summary" role="tab">요약</a></li>
        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#tab-fear" role="tab" data-category="fearIndex" data-chart="fear">공포지수</a></li>
        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#tab-bond" role="tab" data-category="bond" data-chart="bond">금리/채권</a></li>
        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#tab-index" role="tab" data-category="index" data-chart="idx">주요지수</a></li>
        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#tab-m2" role="tab" data-category="m2" data-chart="m2">M2 통화량</a></li>
        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#tab-commodity" role="tab" data-category="commodity" data-chart="commodity">원자재</a></li>
        <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#tab-exchange" role="tab" data-category="exchange" data-chart="exchange">환율</a></li>
    </ul>

    <div class="tab-content mt-3" id="macroTabContent">

        <%-- ===== 시그널 탭 ===== --%>
        <div class="tab-pane fade show active" id="tab-signal" role="tabpanel">
            <div id="signal-area">
                <div class="loading-box"><span class="spinner-border"></span> 시그널 데이터 로딩 중...</div>
            </div>
        </div>

        <%-- ===== 자산배분 탭 ===== --%>
        <div class="tab-pane" id="tab-allocation" role="tabpanel">
            <div id="allocation-area">
                <div class="loading-box"><span class="spinner-border"></span> 자산배분 데이터 로딩 중...</div>
            </div>
        </div>

        <%-- ===== 요약 탭 ===== --%>
        <div class="tab-pane" id="tab-summary" role="tabpanel">
            <div class="section-row">
                <div class="section-col">
                    <div class="card macro-card">
                        <div class="card-header bg-danger text-white">공포지수 (Fear Index)</div>
                        <div class="card-body p-0" id="summary-fearIndex"></div>
                    </div>
                </div>
                <div class="section-col">
                    <div class="card macro-card">
                        <div class="card-header bg-info text-white">M2 통화량 (Money Supply)</div>
                        <div class="card-body p-0" id="summary-m2"></div>
                    </div>
                </div>
            </div>
            <div class="section-row">
                <div class="section-col">
                    <div class="card macro-card">
                        <div class="card-header bg-warning text-dark">원자재 (Commodities, KRW)</div>
                        <div class="card-body p-0" id="summary-commodity"></div>
                    </div>
                </div>
                <div class="section-col">
                    <div class="card macro-card">
                        <div class="card-header bg-success text-white">환율 (Exchange Rates)</div>
                        <div class="card-body p-0" id="summary-exchange"></div>
                    </div>
                </div>
            </div>
        </div>

        <%-- ===== 채권/금리 탭 ===== --%>
        <div class="tab-pane" id="tab-bond" role="tabpanel">
            <div class="card macro-card">
                <div class="card-header bg-primary text-white"><strong>금리/채권 현황</strong></div>
                <div class="card-body p-0" id="tab-bond-table"></div>
            </div>
            <div class="card"><div class="card-header bg-primary text-white"><strong>금리 월간 추이</strong> (최근 12개월)</div>
            <div class="card-body"><div id="bond-chart-area"><div class="loading-box"><span class="spinner-border"></span> 차트 탭 선택 시 로딩됩니다.</div></div></div></div>
        </div>

        <%-- ===== 주요지수 탭 ===== --%>
        <div class="tab-pane" id="tab-index" role="tabpanel">
            <div class="card macro-card">
                <div class="card-header bg-dark text-white"><strong>주요 지수 현황</strong></div>
                <div class="card-body p-0" id="tab-index-table"></div>
            </div>
            <div class="card"><div class="card-header bg-dark text-white"><strong>지수 월간 추이</strong> (최근 12개월, 변동률 %)</div>
            <div class="card-body"><div id="idx-chart-area"><div class="loading-box"><span class="spinner-border"></span> 차트 탭 선택 시 로딩됩니다.</div></div></div></div>
        </div>

        <%-- ===== 차트 탭들 ===== --%>
        <div class="tab-pane" id="tab-fear" role="tabpanel">
            <div class="card"><div class="card-header bg-danger text-white"><strong>공포지수 월간 추이</strong> (최근 12개월)</div>
            <div class="card-body"><div id="fear-chart-area"><div class="loading-box"><span class="spinner-border"></span> 차트 탭 선택 시 로딩됩니다.</div></div></div></div>
        </div>
        <div class="tab-pane" id="tab-m2" role="tabpanel">
            <div class="card macro-card">
                <div class="card-header bg-info text-white"><strong>M2 통화량 현황</strong></div>
                <div class="card-body p-0" id="tab-m2-table"></div>
            </div>
            <div class="card"><div class="card-header bg-info text-white"><strong>M2 통화량 월간 추이</strong> (최근 12개월, 변동률 %)</div>
            <div class="card-body"><div id="m2-chart-area"><div class="loading-box"><span class="spinner-border"></span> 차트 탭 선택 시 로딩됩니다.</div></div></div></div>
        </div>
        <div class="tab-pane" id="tab-commodity" role="tabpanel">
            <div class="card"><div class="card-header bg-warning text-dark"><strong>원자재 월간 추이</strong> (최근 12개월, 변동률 %)</div>
            <div class="card-body"><div id="commodity-chart-area"><div class="loading-box"><span class="spinner-border"></span> 차트 탭 선택 시 로딩됩니다.</div></div></div></div>
        </div>
        <div class="tab-pane" id="tab-exchange" role="tabpanel">
            <div class="card"><div class="card-header bg-success text-white"><strong>환율 월간 추이</strong> (최근 12개월, 변동률 %)</div>
            <div class="card-body"><div id="exchange-chart-area"><div class="loading-box"><span class="spinner-border"></span> 차트 탭 선택 시 로딩됩니다.</div></div></div></div>
        </div>

    </div>
</div>
</div>

<script>
var CTX = '${pageContext.request.contextPath}';
var chartInstances = {};
var usePercent = { commodity: true, exchange: true, fearIndex: false, m2: true, bond: false, index: true };

var SUMMARY_COLS = {
    fearIndex: { headers: ['지표명','현재가','변동','변동률(%)','52주 최고','52주 최저','시그널'],
                 info: 'VIX: 20 이하 안정, 20~30 불안, 30 이상 공포 구간' },
    m2:        { headers: ['지표명','현재값','변동','변동률(%)','이전값','통화'],
                 info: '미국 M2: FRED (연방준비은행) 월간 데이터 기준' },
    commodity: { headers: ['지표명','현재가(원)','변동','변동률(%)','52주 최고','52주 최저','시그널'],
                 info: '금: 1돈(3.75g) 기준, 구리: 1kg 기준, 비트코인: 원화 환산' },
    exchange:  { headers: ['지표명','현재가','변동','변동률(%)','52주 최고','52주 최저','시그널'],
                 info: 'Yahoo Finance 실시간 환율 기준' },
    bond:      { headers: ['지표명','현재가(%)','변동','변동률(%)','52주 최고','52주 최저','시그널'],
                 info: '미국 국채 수익률 — Yahoo Finance 기준. 장단기 금리차 역전 시 경기침체 경고' },
    index:     { headers: ['지표명','현재가','변동','변동률(%)','52주 최고','52주 최저','시그널'],
                 info: 'Yahoo Finance 실시간 지수 — 52주 대비 위치로 시그널 판단' }
};

$(function() {
    loadSignals();

    $('#macroTabs').on('click', 'a[data-toggle="tab"]', function() {
        var href = $(this).attr('href');
        if (href === '#tab-summary' && !$(this).data('summaryLoaded')) {
            $(this).data('summaryLoaded', true);
            loadAllSummary();
        }
    });

    $('#macroTabs').on('click', 'a[data-category]', function() {
        var $link = $(this);
        if ($link.data('loaded')) return;
        var category = $link.data('category');
        var chartKey = $link.data('chart');
        if (!category || !chartKey) return;
        $link.data('loaded', true);
        if (category === 'm2' || category === 'bond' || category === 'index') {
            loadTabTable(category);
        }
        setTimeout(function() { loadChart(category, chartKey); }, 300);
    });
});

function reloadAll() {
    loadSignals();
    var activeTab = $('#macroTabs a.active').attr('href');
    if (activeTab === '#tab-summary') loadAllSummary();
    $('#macroTabs a[data-category]').each(function() { $(this).removeData('loaded'); });
    $('#macroTabs a[data-toggle="tab"]').each(function() { $(this).removeData('summaryLoaded'); });
}

function loadTabTable(category) {
    var el = $('#tab-' + category + '-table');
    el.html('<div class="loading-box"><span class="spinner-border"></span> 로딩 중...</div>');
    $.ajax({
        url: CTX + '/stock2/macro/data',
        data: { category: category },
        type: 'GET', dataType: 'json', timeout: 30000,
        success: function(data) { el.html(buildSummaryTable(category, data)); },
        error: function() { el.html('<div class="error-box">데이터를 불러올 수 없습니다.</div>'); }
    });
}

function loadAllSummary() {
    ['fearIndex','m2','commodity','exchange'].forEach(function(cat) {
        loadSummary(cat);
    });
}

function loadSummary(category) {
    var el = $('#summary-' + category);
    el.html('<div class="loading-box"><span class="spinner-border"></span> 로딩 중...</div>');

    $.ajax({
        url: CTX + '/stock2/macro/data',
        data: { category: category },
        type: 'GET',
        dataType: 'json',
        timeout: 30000,
        success: function(data) { el.html(buildSummaryTable(category, data)); },
        error: function() { el.html('<div class="error-box">데이터를 불러올 수 없습니다.</div>'); }
    });
}

function buildSummaryTable(category, rows) {
    var cfg = SUMMARY_COLS[category];
    var isM2 = (category === 'm2');
    var hasSignalCol = cfg.headers.indexOf('시그널') >= 0;
    var html = '<div class="table-responsive"><table class="table table-bordered table-hover macro-table"><thead><tr>';
    cfg.headers.forEach(function(h) { html += '<th>' + h + '</th>'; });
    html += '</tr></thead><tbody>';

    if (!rows || rows.length === 0) {
        html += '<tr><td colspan="' + cfg.headers.length + '" class="text-center">데이터를 불러올 수 없습니다.</td></tr>';
    } else {
        rows.forEach(function(r) {
            var chgClass = getChangeClass(r.change);
            var pctClass = getChangeClass(r.changePercent);
            var pctDisp = (r.changePercent && r.changePercent !== '-') ? r.changePercent + '%' : '-';

            html += '<tr>';
            html += '<td><strong>' + r.nameKr + '</strong>';
            if (r.symbol === 'VKOSPI') html += '<br/><a href="https://kr.investing.com/indices/kospi-volatility" target="_blank" class="ref-link">Investing.com</a>';
            if (r.symbol === 'M2_KR') html += '<br/><a href="https://ecos.bok.or.kr/" target="_blank" class="ref-link">한국은행 ECOS</a>';
            if (r.symbol === 'M2SL' || r.symbol === 'M2SL_YOY') html += '<br/><span class="ref-link">' + r.name + '</span>';
            html += '</td>';
            html += '<td><strong>' + r.price + '</strong></td>';
            html += '<td class="' + chgClass + '">' + r.change + '</td>';
            html += '<td class="' + pctClass + '">' + pctDisp + '</td>';

            if (isM2) {
                html += '<td>' + (r.previousClose || '-') + '</td>';
                html += '<td>' + (r.currency || '-') + '</td>';
            } else {
                html += '<td>' + (r.fiftyTwoWeekHigh || '-') + '</td>';
                html += '<td>' + (r.fiftyTwoWeekLow || '-') + '</td>';
            }
            if (hasSignalCol) {
                html += '<td>' + buildSignalBadge(r.signal, r.signalText) + '</td>';
            }
            html += '</tr>';
        });
    }

    html += '</tbody></table></div>';
    html += '<div class="macro-info">' + cfg.info + '</div>';
    return html;
}

function getChangeClass(val) {
    if (!val || val === '-') return 'text-flat';
    if (val.charAt(0) === '+') return 'text-up';
    if (val.charAt(0) === '-') return 'text-down';
    return 'text-flat';
}

function loadChart(category, chartKey) {
    var area = $('#' + chartKey + '-chart-area');
    area.html('<div class="loading-box"><span class="spinner-border"></span> 차트 데이터 로딩 중...</div>');

    $.ajax({
        url: CTX + '/stock2/macro/chart',
        data: { category: category },
        type: 'GET',
        dataType: 'json',
        timeout: 60000,
        success: function(data) {
            if (!data || !data.labels || data.labels.length === 0 || !data.datasets || data.datasets.length === 0) {
                area.html('<div class="error-box">차트 데이터가 없습니다.</div>');
                return;
            }
            if (typeof Chart === 'undefined') {
                area.html('<div class="error-box">Chart.js 라이브러리를 로드할 수 없습니다.</div>');
                return;
            }
            area.html('<div class="chart-wrap"><canvas id="canvas-' + chartKey + '"></canvas></div>');
            renderChart('canvas-' + chartKey, data, usePercent[category] || false);
        },
        error: function(xhr, status) {
            var msg = status === 'timeout' ? '요청 시간이 초과되었습니다.' : '차트 데이터를 불러올 수 없습니다.';
            area.html('<div class="error-box">' + msg + '</div>');
        }
    });
}

function renderChart(canvasId, chartData, normalize) {
    var ctx = document.getElementById(canvasId);
    if (!ctx) return;

    if (chartInstances[canvasId]) chartInstances[canvasId].destroy();

    var rawDatasets = [];
    var displayDatasets = chartData.datasets.map(function(ds) {
        var raw = ds.data.slice();
        rawDatasets.push(raw);

        var display;
        if (normalize) {
            var base = null;
            for (var i = 0; i < raw.length; i++) { if (raw[i] !== null) { base = raw[i]; break; } }
            if (base && base !== 0) {
                display = raw.map(function(v) {
                    return v !== null ? Math.round(((v - base) / base) * 10000) / 100 : null;
                });
            } else {
                display = raw;
            }
        } else {
            display = raw;
        }

        return {
            label: ds.label,
            data: display,
            borderColor: ds.borderColor,
            backgroundColor: ds.borderColor + '15',
            fill: false,
            tension: 0.3,
            pointRadius: 4,
            pointHoverRadius: 6,
            borderWidth: 2
        };
    });

    chartInstances[canvasId] = new Chart(ctx, {
        type: 'line',
        data: { labels: chartData.labels, datasets: displayDatasets },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: { position: 'top', labels: { font: { size: 13 }, usePointStyle: true } },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            var raw = rawDatasets[context.datasetIndex][context.dataIndex];
                            if (raw === null || raw === undefined) return context.dataset.label + ': N/A';
                            if (normalize) {
                                var d = context.parsed.y;
                                return context.dataset.label + ': ' + (d >= 0 ? '+' : '') + d.toFixed(2) + '% (' + raw.toLocaleString() + ')';
                            }
                            return context.dataset.label + ': ' + raw.toLocaleString();
                        }
                    }
                }
            },
            scales: {
                x: { grid: { display: false }, ticks: { font: { size: 12 } } },
                y: {
                    title: { display: normalize, text: '변동률 (%)', font: { size: 13 } },
                    ticks: { font: { size: 12 }, callback: function(v) { return normalize ? v.toFixed(1) + '%' : v.toLocaleString(); } },
                    grid: { color: '#f0f0f0' }
                }
            }
        }
    });
}

// ==================== Signal Dashboard ====================

function loadSignals() {
    var area = $('#signal-area');
    area.html('<div class="loading-box"><span class="spinner-border"></span> 시그널 데이터 로딩 중... (약 10~20초 소요)</div>');

    $.ajax({
        url: CTX + '/stock2/macro/signals',
        type: 'GET', dataType: 'json', timeout: 120000,
        success: function(data) { area.html(buildSignalDashboard(data)); },
        error: function(xhr, status) {
            var msg = status === 'timeout' ? '시간 초과 — 다시 시도해주세요.' : '시그널 데이터를 불러올 수 없습니다.';
            area.html('<div class="error-box">' + msg + '</div>');
        }
    });
}

function buildSignalDashboard(data) {
    var html = '';

    // 자산배분은 별도 탭으로 렌더링
    var allocHtml = '';
    if (data.portfolioAllocation && data.portfolioAllocation.items && data.portfolioAllocation.items.length > 0) {
        allocHtml += buildPortfolioAllocationCard(data.portfolioAllocation);
    }
    if (data.allocation) {
        allocHtml += buildAllocationCard(data.allocation);
    }
    $('#allocation-area').html(allocHtml || '<div class="text-center text-muted" style="padding:30px;">자산배분 데이터가 없습니다.</div>');

    // 자산별 종합 시그널 카드
    if (data.assetSignals && data.assetSignals.length > 0) {
        html += '<h6 class="mt-3 mb-2" style="font-weight:bold;">자산별 시그널 판단</h6>';
        html += '<div class="section-row mb-3">';
        data.assetSignals.forEach(function(a) {
            var bg = signalBgClass(a.signal);
            var textColor = (a.signal === 'CAUTION') ? 'text-dark' : 'text-white';
            html += '<div class="section-col" style="min-width:220px;">';
            html += '<div class="card signal-asset-card">';
            html += '<div class="card-header ' + bg + ' ' + textColor + '">';
            html += '<strong>' + a.asset + '</strong>';
            html += '<span class="float-right">' + buildSignalBadgeWhite(a.signal) + '</span>';
            html += '</div>';
            html += '<div class="card-body p-2">';
            html += '<div class="signal-text-lg ' + signalTextClass(a.signal) + '">' + (a.text || '') + '</div>';
            if (a.reasons) {
                html += '<div class="signal-reasons">' + a.reasons + '</div>';
            }
            html += '</div></div></div>';
        });
        html += '</div>';
    }

    // 개별 지표 시그널 테이블
    if (data.indicators && data.indicators.length > 0) {
        var groups = {};
        var catOrder = ['fearIndex','bond','exchange','commodity','index','m2'];
        var catNames = {
            fearIndex: '공포/탐욕 지표', bond: '금리/채권', exchange: '환율',
            commodity: '원자재', index: '주요 지수', m2: 'M2 통화량'
        };

        data.indicators.forEach(function(ind) {
            var cat = ind.category || 'etc';
            if (!groups[cat]) groups[cat] = [];
            groups[cat].push(ind);
        });

        html += '<div class="section-row">';
        catOrder.forEach(function(cat) {
            if (!groups[cat]) return;
            html += '<div class="section-col" style="min-width:380px;">';
            html += '<div class="card macro-card">';
            html += '<div class="card-header">' + (catNames[cat] || cat) + '</div>';
            html += '<div class="card-body p-0">';
            html += '<table class="table table-bordered table-hover macro-table">';
            html += '<thead><tr><th>지표</th><th>현재가</th><th>변동률</th><th style="width:110px;">시그널</th></tr></thead><tbody>';
            groups[cat].forEach(function(r) {
                var pctClass = getChangeClass(r.changePercent);
                var pctDisp = (r.changePercent && r.changePercent !== '-') ? r.changePercent + '%' : '-';
                html += '<tr>';
                html += '<td><strong>' + (r.nameKr || r.name) + '</strong></td>';
                html += '<td><strong>' + r.price + '</strong></td>';
                html += '<td class="' + pctClass + '">' + pctDisp + '</td>';
                html += '<td>' + buildSignalBadge(r.signal, r.signalText) + '</td>';
                html += '</tr>';
            });
            html += '</tbody></table></div></div></div>';
        });
        html += '</div>';
    }

    return html;
}

function buildSignalBadge(signal, text) {
    if (!signal) return '<span class="badge badge-secondary">-</span>';
    var cls = signalBadgeClass(signal);
    var label = signalLabel(signal);
    var title = text ? ' title="' + text.replace(/"/g, '&quot;') + '"' : '';
    return '<span class="badge ' + cls + ' signal-badge"' + title + '>' + label + '</span>'
         + (text && text !== '-' ? '<br/><small class="text-muted signal-desc">' + text + '</small>' : '');
}

function buildSignalBadgeWhite(signal) {
    if (!signal) return '';
    var label = signalLabel(signal);
    return '<span class="badge badge-light">' + label + '</span>';
}

function signalBadgeClass(signal) {
    switch(signal) {
        case 'BUY': return 'badge-success';
        case 'SELL': return 'badge-danger';
        case 'CAUTION': return 'badge-warning';
        default: return 'badge-secondary';
    }
}

function signalBgClass(signal) {
    switch(signal) {
        case 'BUY': return 'bg-success';
        case 'SELL': return 'bg-danger';
        case 'CAUTION': return 'bg-warning';
        default: return 'bg-secondary';
    }
}

function signalTextClass(signal) {
    switch(signal) {
        case 'BUY': return 'text-success';
        case 'SELL': return 'text-danger';
        case 'CAUTION': return 'text-warning-dark';
        default: return 'text-muted';
    }
}

function signalLabel(signal) {
    switch(signal) {
        case 'BUY': return 'BUY 매수';
        case 'SELL': return 'SELL 매도';
        case 'CAUTION': return 'CAUTION 주의';
        default: return 'NEUTRAL 중립';
    }
}

// ==================== Portfolio-Based Allocation Card ====================

function buildPortfolioAllocationCard(pf) {
    var html = '';
    var totalActual = pf.totalActual || 0;

    html += '<div class="card mb-3" style="border-left: 5px solid #2c3e50;">';
    html += '<div class="card-header" style="background:#2c3e50; color:#fff; font-size:16px;">';
    html += '<strong>내 사계절 포트폴리오 — 매크로 시그널 반영 리밸런싱</strong>';
    html += '<span class="float-right" style="font-size:13px;">총 평가: ' + formatWon(totalActual) + '</span>';
    html += '</div>';
    html += '<div class="card-body p-0">';

    // 비중 비교 차트 (가로 누적 바)
    html += '<div style="padding:12px 15px 8px;">';
    html += '<div style="font-size:12px; color:#666; margin-bottom:4px;"><strong>현재 비중</strong></div>';
    html += '<div style="display:flex; height:28px; border-radius:4px; overflow:hidden; margin-bottom:8px;">';
    var items = pf.items || [];
    items.forEach(function(item) {
        var w = item.currentWeight || 0;
        if (w < 1) return;
        html += '<div style="width:' + w + '%; background:' + (item.color || '#bbb') + '; color:#fff; font-size:10px; display:flex; align-items:center; justify-content:center; min-width:30px;" title="' + item.category + ' ' + w.toFixed(1) + '%">';
        html += (w >= 5 ? item.category + ' ' : '') + w.toFixed(0) + '%';
        html += '</div>';
    });
    html += '</div>';
    html += '<div style="font-size:12px; color:#666; margin-bottom:4px;"><strong>추천 비중</strong></div>';
    html += '<div style="display:flex; height:28px; border-radius:4px; overflow:hidden;">';
    items.forEach(function(item) {
        var w = item.recommendedWeight || 0;
        if (w < 1) return;
        html += '<div style="width:' + w + '%; background:' + (item.color || '#bbb') + '; color:#fff; font-size:10px; display:flex; align-items:center; justify-content:center; min-width:30px; opacity:0.85;" title="' + item.category + ' ' + w.toFixed(1) + '%">';
        html += (w >= 5 ? item.category + ' ' : '') + w.toFixed(0) + '%';
        html += '</div>';
    });
    html += '</div></div>';

    // 메인 테이블
    html += '<table class="table table-bordered macro-table mb-0">';
    html += '<thead><tr>';
    html += '<th>자산 분류</th>';
    html += '<th style="width:60px;">시그널</th>';
    html += '<th style="width:65px;">현재</th>';
    html += '<th style="width:65px;">추천</th>';
    html += '<th style="width:70px;">변동</th>';
    html += '<th style="width:80px;">액션</th>';
    html += '<th style="width:100px;">현재 금액</th>';
    html += '<th style="width:100px;">조정 금액</th>';
    html += '<th>상세 판단</th>';
    html += '</tr></thead><tbody>';

    items.forEach(function(item) {
        var change = item.change || 0;
        var changeStr = (change > 0 ? '+' : '') + change.toFixed(1) + '%p';
        var changeClass = change > 0 ? 'text-up' : (change < 0 ? 'text-down' : 'text-flat');
        var adjustStr = formatWonSigned(item.adjustAmount || 0);
        var adjustClass = (item.adjustAmount || 0) > 0 ? 'text-up' : ((item.adjustAmount || 0) < 0 ? 'text-down' : 'text-flat');

        html += '<tr>';
        html += '<td><span style="display:inline-block;width:10px;height:10px;border-radius:2px;background:' + (item.color || '#bbb') + ';margin-right:5px;"></span><strong>' + item.category + '</strong></td>';
        html += '<td class="text-center">' + buildSignalBadge(item.signal, '') + '</td>';
        html += '<td class="text-center">' + (item.currentWeight || 0).toFixed(1) + '%</td>';
        html += '<td class="text-center"><strong>' + (item.recommendedWeight || 0).toFixed(1) + '%</strong></td>';
        html += '<td class="text-center ' + changeClass + '"><strong>' + changeStr + '</strong></td>';
        html += '<td class="text-center">' + buildActionBadge(item.action) + '</td>';
        html += '<td class="text-right">' + formatWon(item.actualAmount) + '</td>';
        html += '<td class="text-right ' + adjustClass + '"><strong>' + adjustStr + '</strong></td>';
        html += '<td style="font-size:11px; text-align:left; white-space:normal;">' + (item.detail || '') + '</td>';
        html += '</tr>';

        // 보유 종목 펼치기
        if (item.stocks && item.stocks.length > 0) {
            html += '<tr class="alloc-bar-row"><td colspan="9" style="padding:2px 15px 8px;">';
            html += '<details><summary style="font-size:11px; color:#6c757d; cursor:pointer;">보유 종목 (' + item.stocks.length + '건, ' + formatWon(item.actualAmount) + ')</summary>';
            html += '<table class="table table-sm mb-0 mt-1" style="font-size:11px;">';
            html += '<thead><tr><th>종목</th><th>계좌</th><th>수량</th><th>평가금액</th><th>비중</th><th>1M</th><th>3M</th><th>52주위치</th></tr></thead><tbody>';
            item.stocks.forEach(function(s) {
                var m1Class = getReturnClass(s.month1Rate);
                var m3Class = getReturnClass(s.month3Rate);
                html += '<tr>';
                html += '<td>' + s.name + '</td>';
                html += '<td style="font-size:10px;">' + (s.division || '') + '</td>';
                html += '<td class="text-right">' + (s.qty || 0).toLocaleString() + '</td>';
                html += '<td class="text-right">' + formatWon(s.totalPrice) + '</td>';
                html += '<td class="text-center">' + (s.weight || 0).toFixed(1) + '%</td>';
                html += '<td class="text-center ' + m1Class + '">' + formatRate(s.month1Rate) + '</td>';
                html += '<td class="text-center ' + m3Class + '">' + formatRate(s.month3Rate) + '</td>';
                html += '<td class="text-center">' + (s.pos52 != null ? s.pos52.toFixed(0) + '%' : '-') + '</td>';
                html += '</tr>';
            });
            html += '</tbody></table></details></td></tr>';
        }
    });

    html += '</tbody></table>';

    // 계좌별 요약
    if (pf.accounts && pf.accounts.length > 0) {
        html += '<div style="padding:8px 15px; border-top:1px solid #dee2e6;">';
        html += '<details><summary style="font-size:12px; color:#6c757d; cursor:pointer;">계좌별 평가금액</summary>';
        html += '<div class="section-row mt-2">';
        pf.accounts.forEach(function(acc) {
            html += '<div style="flex:1; min-width:150px; padding:4px 8px; font-size:12px;">';
            html += '<strong>' + acc.account + '</strong> — ' + formatWon(acc.totalAmount) + ' (' + (acc.weight || 0).toFixed(1) + '%)';
            html += '</div>';
        });
        html += '</div></details></div>';
    }

    html += '</div></div>';
    return html;
}

function formatWon(amount) {
    if (amount == null) return '-';
    if (Math.abs(amount) >= 100000000) return (amount / 100000000).toFixed(1) + '억';
    if (Math.abs(amount) >= 10000) return Math.round(amount / 10000).toLocaleString() + '만';
    return amount.toLocaleString() + '원';
}

function formatWonSigned(amount) {
    if (amount == null || amount === 0) return '-';
    var prefix = amount > 0 ? '+' : '';
    if (Math.abs(amount) >= 100000000) return prefix + (amount / 100000000).toFixed(1) + '억';
    if (Math.abs(amount) >= 10000) return prefix + Math.round(amount / 10000).toLocaleString() + '만';
    return prefix + amount.toLocaleString() + '원';
}

function formatRate(rate) {
    if (rate == null) return '-';
    return (rate > 0 ? '+' : '') + rate.toFixed(1) + '%';
}

function getReturnClass(rate) {
    if (rate == null) return '';
    return rate > 0 ? 'text-up' : (rate < 0 ? 'text-down' : '');
}

// ==================== Allocation Card ====================

function buildAllocationCard(alloc) {
    var html = '';
    var pc = alloc.phaseColor || '#7f8c8d';

    // 시장 상태 배너
    html += '<div class="card mb-3" style="border-left: 5px solid ' + pc + ';">';
    html += '<div class="card-header" style="background:' + pc + '; color:#fff; font-size:16px;">';
    html += '<strong>범용 추천 자산배분</strong> (참고용) — ' + (alloc.phaseText || '');
    html += '</div>';
    html += '<div class="card-body p-0">';

    // 배분 테이블
    html += '<table class="table table-bordered macro-table mb-0">';
    html += '<thead><tr>';
    html += '<th style="width:120px;">자산</th>';
    html += '<th style="width:75px;">기준</th>';
    html += '<th style="width:75px;">추천</th>';
    html += '<th style="width:80px;">변동</th>';
    html += '<th style="width:80px;">액션</th>';
    html += '<th>추천 상품</th>';
    html += '<th>상세 판단 근거</th>';
    html += '</tr></thead><tbody>';

    if (alloc.allocations) {
        alloc.allocations.forEach(function(a) {
            var change = a.change || 0;
            var changeStr = (change > 0 ? '+' : '') + change + '%p';
            var changeClass = change > 0 ? 'text-up' : (change < 0 ? 'text-down' : 'text-flat');
            var actionBadge = buildActionBadge(a.action);
            var barColor = getAllocBarColor(a.asset);

            html += '<tr>';
            html += '<td><strong>' + a.asset + '</strong></td>';
            html += '<td class="text-center">' + a.baseWeight + '%</td>';
            html += '<td class="text-center"><strong>' + a.recommendedWeight + '%</strong></td>';
            html += '<td class="text-center ' + changeClass + '"><strong>' + changeStr + '</strong></td>';
            html += '<td class="text-center">' + actionBadge + '</td>';
            html += '<td style="font-size:11px;">' + (a.instruments || '') + '</td>';
            html += '<td style="font-size:11px; text-align:left; white-space:normal; max-width:300px;">' + (a.detail || '') + '</td>';
            html += '</tr>';

            // 배분 비율 바
            html += '<tr class="alloc-bar-row"><td colspan="7" style="padding:0 15px 8px;">';
            html += '<div class="alloc-bar-bg">';
            html += '<div class="alloc-bar" style="width:' + a.recommendedWeight + '%; background:' + barColor + ';">';
            html += a.recommendedWeight + '%';
            html += '</div></div></td></tr>';
        });
    }
    html += '</tbody></table>';
    html += '</div></div>';

    // 구체적 액션 리스트
    if (alloc.actions && alloc.actions.length > 0) {
        html += '<div class="card mb-3">';
        html += '<div class="card-header" style="font-weight:bold; background:#f8f9fa;">실행 액션 (우선순위순)</div>';
        html += '<div class="card-body p-2">';
        html += '<ol class="action-list">';
        alloc.actions.forEach(function(act) {
            var icon = actionIcon(act.type);
            var cls = actionTextClass(act.type);
            html += '<li class="action-item">';
            html += '<span class="' + cls + '">' + icon + ' ' + act.text + '</span>';
            if (act.instruments) {
                html += '<br/><small class="text-muted">추천: ' + act.instruments + '</small>';
            }
            html += '</li>';
        });
        html += '</ol></div></div>';
    }

    // 경고 사항
    if (alloc.warnings && alloc.warnings.length > 0) {
        html += '<div class="card mb-3 border-warning">';
        html += '<div class="card-header bg-warning text-dark" style="font-weight:bold;">주요 경고/참고 사항</div>';
        html += '<div class="card-body p-2">';
        html += '<ul class="warning-list">';
        alloc.warnings.forEach(function(w) {
            html += '<li>' + w + '</li>';
        });
        html += '</ul></div></div>';
    }

    return html;
}

function buildActionBadge(action) {
    switch(action) {
        case 'BUY': return '<span class="badge badge-success">매수</span>';
        case 'REDUCE': return '<span class="badge badge-danger">축소</span>';
        case 'SLIGHT_BUY': return '<span class="badge badge-outline-success">소폭 매수</span>';
        case 'SLIGHT_REDUCE': return '<span class="badge badge-outline-danger">소폭 축소</span>';
        default: return '<span class="badge badge-secondary">유지</span>';
    }
}

function getAllocBarColor(asset) {
    var colors = {
        '현금/MMF': '#95a5a6',
        '미국 주식': '#2980b9',
        '한국 주식': '#27ae60',
        '미국 채권': '#8e44ad',
        '금': '#f39c12',
        '암호화폐': '#e67e22'
    };
    return colors[asset] || '#bdc3c7';
}

function actionIcon(type) {
    switch(type) {
        case 'REDUCE': return '&#9660;';
        case 'SLIGHT_REDUCE': return '&#9661;';
        case 'BUY': return '&#9650;';
        case 'SLIGHT_BUY': return '&#9651;';
        default: return '&#9644;';
    }
}

function actionTextClass(type) {
    switch(type) {
        case 'REDUCE': return 'text-danger font-weight-bold';
        case 'SLIGHT_REDUCE': return 'text-danger';
        case 'BUY': return 'text-success font-weight-bold';
        case 'SLIGHT_BUY': return 'text-success';
        default: return 'text-muted';
    }
}
</script>
