<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />

<div class="board-container-wide">

	<div class="board-header">
		<h2>공용테이블 목록</h2>
	</div>

	<table class="board-table">
		<colgroup>
			<col style="width:60px;" />
			<col style="width:18%;" />
			<col style="width:18%;" />
			<col style="width:10%;" />
			<col />
		</colgroup>
		<thead>
			<tr>
				<th>NO</th>
				<th>Table Code</th>
				<th>Table Name</th>
				<th>데이터 수</th>
				<th>Table 설명</th>
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
							<td class="td-title"><a href="javascript:kangong.commontable.view('${list.id}');"><c:out value="${list.tableCode}"/></a></td>
							<td class="td-title"><a href="javascript:kangong.commontable.inputEdit('${list.tableCode}');"><c:out value="${list.tableName}"/></a></td>
							<td></td>
							<td style="text-align:left;color:#495057;"><c:out value="${list.tableDesc}"/></td>
						</tr>
					</c:forEach>
				</c:when>
			</c:choose>
		</tbody>
	</table>

	<div class="board-toolbar">
		<div></div>
		<button type="button" class="btn btn-board-primary" id="editBtn">공용테이블 생성</button>
	</div>

	<kangong:paging url="${pageContext.request.contextPath}/commontable/list" page="${pagination.page}" range="${pagination.range}" rangeSize="${pagination.rangeSize}" startPage="${pagination.startPage}" endPage="${pagination.endPage}" next="${pagination.next}" prev="${pagination.next}" />

	<div class="board-search">
		<select class="form-control form-control-sm" name="searchType" id="searchType">
			<option value="tableCode">Table Code</option>
			<option value="tableName">Table명</option>
			<option value="tableDesc">Table설명</option>
		</select>
		<input type="text" class="form-control form-control-sm" name="keyword" id="keyword">
		<button class="btn btn-board-outline" id="btnSearch">검색</button>
	</div>

</div>

<c:url var="listURL" value="/commontable/list"></c:url>
<c:url var="editURL" value="/commontable/edit"></c:url>
<c:url var="viewURL" value="/commontable/edit"></c:url>
<c:url var="inputEditURL" value="/commontable/commonInputEdit"></c:url>

<script>
$( document ).ready(function() {
	$("#editBtn").on('click', function(e){
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

kangong.commontable = {
	view :function (id){
			var paramObj ={};
			paramObj.id =  id;
			kangong.form.submitPost('${viewURL}',paramObj);
		},
	inputEdit :function (tableCode){
			var paramObj ={};
			paramObj.tableCode =  tableCode;
			kangong.form.submitPost('${inputEditURL}',paramObj);
		}
}
</script>
