<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />

<div class="board-container">

	<div class="board-header">
		<h2>게시판</h2>
		<span class="badge badge-dark">${pagination.listCnt}건</span>
	</div>

	<table class="board-table">
		<colgroup>
			<col class="td-no" />
			<col />
			<col class="td-author" />
			<col class="td-count" />
			<col class="td-date" />
		</colgroup>
		<thead>
			<tr>
				<th>NO</th>
				<th>제목</th>
				<th>작성자</th>
				<th>조회</th>
				<th>작성일</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${empty boardList}">
					<tr><td colspan="5" class="td-empty">등록된 게시글이 없습니다.</td></tr>
				</c:when>
				<c:when test="${!empty boardList}">
					<c:forEach var="list" items="${boardList}" varStatus="status">
						<tr>
							<td class="td-no">${kangfunc:getIndex(pagination.page, pagination.rangeSize, status.count)}</td>
							<td class="td-title"><a href="javascript:kangong.board.openBoard('${list.id}');"><c:out value="${list.title}"/></a></td>
							<td class="td-author"><kang:userinfo id="${list.createUser}" paramType="username"/></td>
							<td class="td-count"><c:out value="${list.viewCnt}"/></td>
							<td class="td-date"><fmt:formatDate pattern="yyyy-MM-dd" value="${list.createDate}"/></td>
						</tr>
					</c:forEach>
				</c:when>
			</c:choose>
		</tbody>
	</table>

	<div class="board-toolbar">
		<div></div>
		<button type="button" class="btn btn-board-primary" id="btnWriteForm">글쓰기</button>
	</div>

	<kangong:paging url="${pageContext.request.contextPath}/board/list" page="${pagination.page}" range="${pagination.range}" rangeSize="${pagination.rangeSize}" startPage="${pagination.startPage}" endPage="${pagination.endPage}" next="${pagination.next}" prev="${pagination.next}" />

	<form:form modelAttribute="pagination">
		<div class="board-search">
			<form:select class="form-control form-control-sm" path="searchType" id="searchType">
				<form:option value="title">제목</form:option>
				<form:option value="content">본문</form:option>
				<form:option value="createUser">작성자</form:option>
			</form:select>
			<form:input class="form-control form-control-sm" path="keyword" id="keyword"/>
			<button class="btn btn-board-outline" id="btnSearch">검색</button>
		</div>
	</form:form>

</div>

<c:url var="listURL" value="/board/list"></c:url>
<c:url var="editURL" value="/board/edit"></c:url>
<c:url var="viewURL" value="/board/view"></c:url>

<script>
$( document ).ready(function() {
	$("#btnWriteForm").on('click', function(e){
		e.preventDefault();
		kangong.form.submitPost('${editURL}');
	});

	$('#btnSearch').on('click', function(e){
		e.preventDefault();
		var paramObj ={};
		 paramObj[$('#searchType').val()] =  $('#keyword').val();
		 paramObj.searchType = $('#searchType').val();
		 paramObj.keyword = $('#keyword').val();

		kangong.form.submitPost('${listURL}',paramObj);
	});

	$('#keyword').on('keypress', function(e){
		if(e.which === 13){
			e.preventDefault();
			$('#btnSearch').click();
		}
	});
});

kangong.board = {
	openBoard :function (id){
			var paramObj ={};
			paramObj.id =  id;
			kangong.form.submitPost('${viewURL}',paramObj);
		}
}
</script>
