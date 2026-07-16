<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />

<div class="board-container-wide">

	<div class="board-header">
		<h2>DD 목록</h2>
	</div>

	<table class="board-table">
		<colgroup>
			<col style="width:60px;" />
			<col style="width:20%;" />
			<col style="width:15%;" />
			<col style="width:12%;" />
			<col />
		</colgroup>
		<thead>
			<tr>
				<th>NO</th>
				<th>DD Code</th>
				<th>DD Name</th>
				<th>DD 모듈</th>
				<th>DD 설명</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${empty masterList}">
					<tr><td colspan="5" class="td-empty">데이터가 없습니다.</td></tr>
				</c:when>
				<c:when test="${!empty masterList}">
					<c:forEach var="list" items="${masterList}">
						<tr>
							<td class="td-no"><c:out value="${list.id}"/></td>
							<td class="td-title"><a href="javascript:kangong.dd.view('${list.id}');"><c:out value="${list.ddCode}"/></a></td>
							<td><c:out value="${list.ddName}"/></td>
							<td><c:out value="${list.ddModule}"/></td>
							<td style="text-align:left;color:#495057;"><c:out value="${list.ddDesc}"/></td>
						</tr>
					</c:forEach>
				</c:when>
			</c:choose>
		</tbody>
	</table>

	<div class="board-toolbar">
		<div></div>
		<button type="button" class="btn btn-board-primary" id="ddEditBtn">DD 생성</button>
	</div>

	<kangong:paging url="${pageContext.request.contextPath}/dd/list.do" page="${pagination.page}" range="${pagination.range}" rangeSize="${pagination.rangeSize}" startPage="${pagination.startPage}" endPage="${pagination.endPage}" next="${pagination.next}" prev="${pagination.next}" />

	<div class="board-search">
		<select class="form-control form-control-sm" name="searchType" id="searchType">
			<option value="ddName">DD명</option>
			<option value="ddModule">DD모듈</option>
			<option value="ddDesc">DD설명</option>
		</select>
		<input type="text" class="form-control form-control-sm" name="keyword" id="keyword">
		<button class="btn btn-board-outline" id="btnSearch">검색</button>
	</div>

</div>

<c:url var="listURL" value="/common/dd/list"></c:url>
<c:url var="editURL" value="/common/dd/edit"></c:url>
<c:url var="viewURL" value="/common/dd/view"></c:url>

<script>
$( document ).ready(function() {
	$("#ddEditBtn").on('click', function(e){
		e.preventDefault();
		kangong.form.submitPost('${editURL}');
	});

	$('#btnSearch').on('click', function(e){
		e.preventDefault();
		var paramObj ={};
		 paramObj[$('#searchType').val()] =  $('#keyword').val();
		kangong.form.submitPost('${listURL}',paramObj);
	});

	$('#keyword').on('keypress', function(e){
		if(e.which === 13){
			e.preventDefault();
			$('#btnSearch').click();
		}
	});
});

kangong.dd = {
	view :function (id){
			var paramObj ={};
			paramObj.id =  id;
			kangong.form.submitPost('${viewURL}',paramObj);
		}
}
</script>
