<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>


<h2>공용테이블 목록</h2>
<article>
<div class="container">
<div class="table-responsive">
<table class="table table-striped table-sm">
		<colgroup>
			<col style="width:5%;" />
			<col style="width:15%;" />
			<col style="width:15%;" />
			<col style="width:10%;" />
			<col style="width:auto;" />
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
				<c:when test="${empty masterList }" >
					<tr><td colspan="5" align="center">데이터가 없습니다.</td></tr>
				</c:when>
				<c:when test="${!empty masterList}">
					<c:forEach var="list" items="${masterList}">
						<tr>
							<td><c:out value="${list.id}"/></td>
							<td> <a href="javascript:kangong.commontable.view('${list.id}');"> <c:out value="${list.tableCode}"/> </a></td>
							<td> <a href="javascript:kangong.commontable.inputEdit('${list.tableCode}');"> <c:out value="${list.tableName}"/></a></td>
							<td> </td>
							<td><c:out value="${list.tableDesc}"/></td>
						</tr>
					</c:forEach>
				</c:when>
			</c:choose>
		</tbody>
	</table>
	</div>

	<div >
		<button type="button" class="btn btn-sm btn-primary" id="editBtn">공용테이블 생성</button>
   </div>

   <kangong:paging url="${pageContext.request.contextPath}/commontable/list" page="${pagination.page}" range="${pagination.range}" rangeSize="${pagination.rangeSize}" startPage="${pagination.startPage}" endPage="${pagination.endPage}" next="${pagination.next}" prev="${pagination.next}" />


   <!-- search{s} -->
		<div class="form-group row justify-content-center">
			<div class="w100" style="padding-right:10px">
				<select class="form-control form-control-sm" name="searchType" id="searchType">
					<option value="tableCode">Table Code</option>
					<option value="tableName">Table명</option>
					<option value="tableDesc">Table설명</option>
				</select>
			</div>

			<div class="w300" style="padding-right:10px">
				<input type="text" class="form-control form-control-sm" name="keyword" id="keyword">
			</div>

			<div>
				<button class="btn btn-sm btn-primary" name="btnSearch" id="btnSearch">검색</button>
			</div>
		</div>
 <!-- search{e} -->



</div>
</article>

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