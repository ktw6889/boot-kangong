<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<style>
.edit-section { margin-bottom: 25px; }
.edit-section h6 { border-bottom: 2px solid #343a40; padding-bottom: 8px; margin-bottom: 12px; font-weight: bold; }
.table-edit { font-size: 13px; }
.table-edit th { text-align: center; vertical-align: middle !important; white-space: nowrap; }
.table-edit td { vertical-align: middle !important; }
.table-edit input { font-size: 13px; }
.btn-xs { padding: 2px 8px; font-size: 12px; }
.param-table input { text-align: right; }
.add-row { background-color: #f0f8ff; }
.stock-search-wrap { position: relative; }
.stock-search-list {
    position: absolute; top: 100%; left: 0; right: 0; z-index: 100;
    max-height: 200px; overflow-y: auto;
    background: #fff; border: 1px solid #ccc; border-top: none;
    list-style: none; margin: 0; padding: 0; font-size: 13px;
    display: none;
}
.stock-search-list li {
    padding: 5px 10px; cursor: pointer;
}
.stock-search-list li:hover, .stock-search-list li.active {
    background: #e9ecef;
}

.toast-container {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 9999;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    pointer-events: none;
}
.toast-msg {
    padding: 16px 36px;
    border-radius: 10px;
    font-size: 1rem;
    font-weight: 600;
    color: #fff;
    box-shadow: 0 6px 24px rgba(0,0,0,0.25);
    opacity: 0;
    transform: scale(0.85);
    transition: opacity 0.3s ease, transform 0.3s ease;
}
.toast-msg.show {
    opacity: 1;
    transform: scale(1);
}
.toast-msg.success { background: #2b8a3e; }
.toast-msg.error { background: #c92a2a; }
.toast-msg.warn { background: #e67700; }
</style>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

    <h5>포트폴리오 수정</h5>
    <hr/>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger" style="font-size:13px;">
            조회 오류: ${errorMsg}
        </div>
    </c:if>

    <%-- 기준금액 설정 --%>
    <div class="edit-section">
        <h6>계좌별 기준금액 설정</h6>
        <table class="table table-bordered table-sm param-table" style="width:auto;">
            <thead class="thead-light">
                <tr>
                    <th>계좌명</th>
                    <th style="width:150px;">기준금액</th>
                    <th style="width:80px;">순서</th>
                    <th style="width:120px;"></th>
                </tr>
            </thead>
            <tbody id="paramBody">
                <c:forEach items="${divisions}" var="item">
                    <tr>
                        <td><input type="text" class="form-control form-control-sm" value="${item.stockDivision}" readonly style="background:#f8f9fa;"/></td>
                        <td><input type="text" class="form-control form-control-sm param-price" value="${item.standardPrice}" data-division="${item.stockDivision}"/></td>
                        <td><input type="text" class="form-control form-control-sm param-order" value="${item.orderNum}" data-division="${item.stockDivision}" style="text-align:center;"/></td>
                        <td>
                            <button class="btn btn-sm btn-success btn-xs" onclick="kangong.stock2Edit.saveParam('${item.stockDivision}', this);">저장</button>
                            <button class="btn btn-sm btn-danger btn-xs" onclick="kangong.stock2Edit.deleteParam('${item.stockDivision}');">삭제</button>
                        </td>
                    </tr>
                </c:forEach>
                <tr class="add-row">
                    <td><input type="text" class="form-control form-control-sm" id="newParamDivision" placeholder="새 계좌명"/></td>
                    <td><input type="text" class="form-control form-control-sm" id="newParamPrice" placeholder="기준금액"/></td>
                    <td><input type="text" class="form-control form-control-sm" id="newParamOrder" placeholder="순서" style="text-align:center;"/></td>
                    <td><button class="btn btn-sm btn-primary btn-xs" onclick="kangong.stock2Edit.addParam();">추가</button></td>
                </tr>
            </tbody>
        </table>
    </div>

    <%-- 종목 관리 --%>
    <div class="edit-section">
        <h6>종목 관리</h6>
        <div class="d-flex align-items-center mb-2" style="gap:10px;">
            <label style="margin-bottom:0; font-size:13px; font-weight:bold;">계좌:</label>
            <select id="editDivisionFilter" class="form-control form-control-sm" style="width:200px;">
                <option value="">전체</option>
                <c:forEach items="${divisions}" var="item">
                    <option value="${item.stockDivision}" ${item.stockDivision == selectedDivision ? 'selected' : ''}>${item.stockDivision}</option>
                </c:forEach>
            </select>
            <button class="btn btn-sm btn-primary" onclick="kangong.stock2Edit.search();">조회</button>
        </div>

        <table class="table table-bordered table-hover table-sm table-edit">
            <thead class="thead-dark">
                <tr>
                    <th style="width:180px;">계좌</th>
                    <th style="width:100px;">종목코드</th>
                    <th>종목명</th>
                    <th style="width:100px;">수량</th>
                    <th style="width:100px;">비중</th>
                    <th style="width:120px;">평가금액</th>
                    <th style="width:150px;"></th>
                </tr>
            </thead>
            <tbody id="interestBody">
                <c:choose>
                    <c:when test="${fn:length(rawList) > 0}">
                        <c:forEach items="${rawList}" var="row">
                            <tr data-stock-id="${row.stockId}" data-orig-division="${row.stockDivision}">
                                <td>
                                    <select class="form-control form-control-sm division-select">
                                        <c:forEach items="${divisions}" var="d">
                                            <option value="${d.stockDivision}" ${d.stockDivision == row.stockDivision ? 'selected' : ''}>${d.stockDivision}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td class="text-center">${row.stockId}</td>
                                <td>${row.name}</td>
                                <td><input type="number" step="any" class="form-control form-control-sm" value="${row.qty}"
                                           data-stock-id="${row.stockId}" data-division="${row.stockDivision}" data-field="qty"/></td>
                                <td><fmt:formatNumber value="${row.stockPotion}" pattern="0" var="potionInt"/>
                                    <input type="number" step="1" class="form-control form-control-sm" value="${potionInt}"
                                           data-stock-id="${row.stockId}" data-division="${row.stockDivision}" data-field="potion"/></td>
                                <td class="text-right"><fmt:formatNumber value="${row.totalPrice}" pattern="#,###"/></td>
                                <td class="text-center">
                                    <button class="btn btn-sm btn-success btn-xs"
                                            onclick="kangong.stock2Edit.saveInterest(this);">저장</button>
                                    <button class="btn btn-sm btn-danger btn-xs"
                                            onclick="kangong.stock2Edit.deleteInterest(this);">삭제</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr id="emptyRow">
                            <td colspan="7" class="text-center">조회된 종목이 없습니다.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                <tr class="add-row">
                    <td>
                        <select id="newDivision" class="form-control form-control-sm">
                            <c:forEach items="${divisions}" var="item">
                                <option value="${item.stockDivision}" ${item.stockDivision == selectedDivision ? 'selected' : ''}>${item.stockDivision}</option>
                            </c:forEach>
                        </select>
                    </td>
                    <td colspan="2">
                        <div class="stock-search-wrap">
                            <input type="text" class="form-control form-control-sm" id="newStockKeyword" placeholder="종목명 검색" autocomplete="off"/>
                            <ul class="stock-search-list" id="stockSearchList"></ul>
                        </div>
                        <input type="hidden" id="newStockId"/>
                        <small id="newStockInfo" class="text-muted"></small>
                    </td>
                    <td><input type="number" step="any" class="form-control form-control-sm" id="newQty" placeholder="수량" value="0"/></td>
                    <td><input type="number" step="1" class="form-control form-control-sm" id="newPotion" placeholder="비중(%)" value="0"/></td>
                    <td></td>
                    <td class="text-center"><button class="btn btn-sm btn-primary btn-xs" onclick="kangong.stock2Edit.addInterest();">추가</button></td>
                </tr>
            </tbody>
        </table>
    </div>

</div>
</div>

<div class="toast-container" id="toastContainer"></div>

<c:url var="editURL" value="/stock2/edit"></c:url>
<c:url var="saveInterestURL" value="/stock2/interest/save"></c:url>
<c:url var="deleteInterestURL" value="/stock2/interest/delete"></c:url>
<c:url var="saveParamURL" value="/stock2/param/save"></c:url>
<c:url var="deleteParamURL" value="/stock2/param/delete"></c:url>
<c:url var="searchStockURL" value="/stock2/searchStock"></c:url>

<script>
function showToast(message, type) {
    type = type || 'success';
    var el = $('<div class="toast-msg ' + type + '">' + message + '</div>');
    $('#toastContainer').append(el);
    setTimeout(function(){ el.addClass('show'); }, 10);
    setTimeout(function(){
        el.removeClass('show');
        setTimeout(function(){ el.remove(); }, 300);
    }, 2500);
}

function reserveToast(message, type) {
    sessionStorage.setItem('toastMsg', message);
    sessionStorage.setItem('toastType', type || 'success');
}

$(function(){
    var msg = sessionStorage.getItem('toastMsg');
    if (msg) {
        var type = sessionStorage.getItem('toastType') || 'success';
        sessionStorage.removeItem('toastMsg');
        sessionStorage.removeItem('toastType');
        showToast(msg, type);
    }
});

var searchTimer = null;
$('#newStockKeyword').on('input', function() {
    var keyword = $(this).val().trim();
    $('#newStockId').val('');
    $('#newStockInfo').text('');
    if (keyword.length < 1) { $('#stockSearchList').hide(); return; }
    clearTimeout(searchTimer);
    searchTimer = setTimeout(function() {
        $.getJSON('${searchStockURL}', { keyword: keyword }, function(list) {
            var $ul = $('#stockSearchList').empty();
            if (!list || list.length === 0) {
                $ul.append('<li class="text-muted">검색 결과 없음 (종목코드 직접 입력 가능)</li>');
            } else {
                $.each(list, function(i, item) {
                    $ul.append('<li data-stock-id="' + item.stockId + '" data-name="' + item.name + '">'
                        + item.name + ' <span class="text-muted">(' + item.stockId + ')</span></li>');
                });
            }
            $ul.show();
        });
    }, 300);
});
$(document).on('click', '#stockSearchList li[data-stock-id]', function() {
    var stockId = $(this).data('stock-id');
    var name = $(this).data('name');
    $('#newStockId').val(stockId);
    $('#newStockKeyword').val(name);
    $('#newStockInfo').text(stockId);
    $('#stockSearchList').hide();
});
$(document).on('click', function(e) {
    if (!$(e.target).closest('.stock-search-wrap').length) {
        $('#stockSearchList').hide();
    }
});
$('#newStockKeyword').on('keydown', function(e) {
    var $list = $('#stockSearchList');
    var $items = $list.find('li[data-stock-id]');
    var $active = $items.filter('.active');
    if (e.keyCode === 40) { // down
        e.preventDefault();
        if ($active.length) { $active.removeClass('active').next('li[data-stock-id]').addClass('active'); }
        else { $items.first().addClass('active'); }
    } else if (e.keyCode === 38) { // up
        e.preventDefault();
        if ($active.length) { $active.removeClass('active').prev('li[data-stock-id]').addClass('active'); }
    } else if (e.keyCode === 13) { // enter
        e.preventDefault();
        if ($active.length) { $active.click(); }
    }
});

kangong.stock2Edit = {
    search: function() {
        var paramObj = {};
        var div = $('#editDivisionFilter').val();
        if (div) paramObj.stockDivision = div;
        kangong.form.submitPost('${editURL}', paramObj);
    },

    saveInterest: function(btn) {
        var tr = $(btn).closest('tr');
        var stockId = tr.data('stock-id');
        var origDivision = tr.data('orig-division');
        var newDivision = tr.find('.division-select').val();
        var qty = tr.find('input[data-field="qty"]').val();
        var potion = tr.find('input[data-field="potion"]').val();
        $.post('${saveInterestURL}', {
            stockId: stockId,
            stockDivision: newDivision,
            oldDivision: origDivision,
            qty: qty,
            stockPotion: potion
        }, function(result) {
            if (result === 'OK') {
                reserveToast('저장되었습니다.');
                kangong.stock2Edit.search();
            }
        }).fail(function(xhr) {
            showToast('저장 실패: ' + xhr.status + ' ' + xhr.statusText, 'error');
        });
    },

    deleteInterest: function(btn) {
        var tr = $(btn).closest('tr');
        var stockId = tr.data('stock-id');
        var division = tr.data('orig-division');
        if (!confirm('[' + stockId + '] 종목을 삭제하시겠습니까?')) return;
        $.post('${deleteInterestURL}', {
            stockId: stockId,
            stockDivision: division
        }, function(result) {
            if (result === 'OK') {
                reserveToast('삭제되었습니다.', 'error');
                kangong.stock2Edit.search();
            }
        });
    },

    addInterest: function() {
        var division = $('#newDivision').val();
        var stockId = $('#newStockId').val() || $('#newStockKeyword').val().trim();
        var qty = $('#newQty').val();
        var potion = $('#newPotion').val();
        if (!stockId) { showToast('종목을 검색하거나 종목코드를 입력해주세요.', 'warn'); return; }
        if (!division) { showToast('계좌를 선택해주세요.', 'warn'); return; }
        $.post('${saveInterestURL}', {
            stockId: stockId,
            stockDivision: division,
            qty: qty,
            stockPotion: potion
        }, function(result) {
            if (result === 'OK') {
                reserveToast('추가되었습니다.');
                kangong.stock2Edit.search();
            }
        }).fail(function(xhr) {
            showToast('저장 실패: ' + xhr.status + ' ' + xhr.statusText, 'error');
        });
    },

    saveParam: function(division, btn) {
        var tr = $(btn).closest('tr');
        var price = tr.find('.param-price').val();
        var order = tr.find('.param-order').val();
        $.post('${saveParamURL}', {
            stockDivision: division,
            standardPrice: price,
            orderNum: order
        }, function(result) {
            if (result === 'OK') {
                reserveToast('저장되었습니다.');
                kangong.stock2Edit.reload();
            }
        });
    },

    deleteParam: function(division) {
        if (!confirm('[' + division + '] 계좌를 삭제하시겠습니까?')) return;
        $.post('${deleteParamURL}', {
            stockDivision: division
        }, function(result) {
            if (result === 'OK') {
                reserveToast('삭제되었습니다.', 'error');
                kangong.stock2Edit.reload();
            }
        });
    },

    addParam: function() {
        var division = $('#newParamDivision').val();
        var price = $('#newParamPrice').val();
        var order = $('#newParamOrder').val();
        if (!division) { showToast('계좌명을 입력해주세요.', 'warn'); return; }
        if (!price) { showToast('기준금액을 입력해주세요.', 'warn'); return; }
        $.post('${saveParamURL}', {
            stockDivision: division,
            standardPrice: price,
            orderNum: order || 99
        }, function(result) {
            if (result === 'OK') {
                reserveToast('추가되었습니다.');
                kangong.stock2Edit.reload();
            }
        });
    },

    reload: function() {
        var paramObj = {};
        var div = $('#editDivisionFilter').val();
        if (div) paramObj.stockDivision = div;
        kangong.form.submitPost('${editURL}', paramObj);
    }
}
</script>
