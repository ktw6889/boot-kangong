<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />

<div class="board-container-wide">

	<div class="board-header">
		<h2>DD 조회</h2>
	</div>

	<div class="detail-card">
		<div class="detail-row">
			<div class="detail-label">DD Code</div>
			<div class="detail-value"><c:out value="${masterVO.ddCode}"/></div>
			<div class="detail-label">DD명</div>
			<div class="detail-value"><c:out value="${masterVO.ddName}"/></div>
			<div class="detail-label">DD모듈</div>
			<div class="detail-value"><c:out value="${masterVO.ddModule}"/></div>
		</div>
		<div class="detail-row">
			<div class="detail-label">DD설명</div>
			<div class="detail-value" style="flex:1"><c:out value="${masterVO.ddDesc}"/></div>
		</div>
	</div>

	<div class="section-title">DD 값</div>

	<table class="admin-grid">
		<thead>
		<tr>
			<th style="width:40px"></th>
			<th style="width:15%">Val</th>
			<th style="width:20%">Ko</th>
			<th style="width:20%">En</th>
			<th style="width:15%">Filter</th>
			<th style="width:60px">순서</th>
			<th style="width:70px">Default</th>
			<th style="width:80px">사용여부</th>
		</tr>
		</thead>
		<tbody>
			<c:if test="${not empty masterVO.ddValueList}">
			   <c:forEach var="itemVO" items="${masterVO.ddValueList}" varStatus="idx">
					<tr>
						<td>
							<input type="hidden" name="id" value="${itemVO.id}"/>
							<input type="hidden" name="ddId" value="${itemVO.ddId}"/>
						</td>
						<td><c:out value="${itemVO.ddVal}"/></td>
						<td><c:out value="${itemVO.ddKo}"/></td>
						<td><c:out value="${itemVO.ddEn}"/></td>
						<td><c:out value="${itemVO.ddFilter}"/></td>
						<td><c:out value="${itemVO.ddOrder}"/></td>
						<td><c:out value="${itemVO.ddDefault}"/></td>
						<td><c:out value="${itemVO.useYn}"/></td>
					</tr>
				</c:forEach>
			</c:if>
			<c:if test="${empty masterVO.ddValueList}">
			    <tr><td colspan="8" class="td-empty">데이터가 없습니다</td></tr>
			</c:if>
		</tbody>
	</table>

	<div class="board-view-actions">
		<button type="button" class="btn btn-board-primary" id="btnUpdate">수정</button>
		<button type="button" class="btn btn-board-danger" id="btnDelete">삭제</button>
		<button type="button" class="btn btn-board-outline" id="btnList">목록</button>
	</div>

</div>

<c:url var="listURL" value="/common/dd/list"></c:url>
<c:url var="editURL" value="/common/dd/edit"></c:url>
<c:url var="deleteURL" value="/common/dd/delete"></c:url>

<script>
$( document ).ready(function() {
	$('#btnList').on('click', function(){
		kangong.form.submitPost("${listURL}");
	});

	$('#btnUpdate').on('click', function(){
		var paramObj = {};
        paramObj.id = ${masterVO.id};
		kangong.form.submitPost("${editURL}",paramObj);
	});

	$('#btnDelete').on('click',function(){
		if(!confirm('정말 삭제하시겠습니까?')) return;
	    var paramObj = {};
	    paramObj.id = ${masterVO.id};
	    kangong.form.submitPost("${deleteURL}",paramObj);
	});
});
</script>
