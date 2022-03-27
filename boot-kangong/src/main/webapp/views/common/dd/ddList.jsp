<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>


<h2>DD 목록</h2>
<article>
<div class="container">
<div class="table-responsive">
<table class="table table-striped table-sm">
		<colgroup>
			<col style="width:5%;" />
			<col style="width:auto;" />
			<col style="width:15%;" />
			<col style="width:10%;" />
			<col style="width:10%;" />
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
				<c:when test="${empty masterList }" >
					<tr><td colspan="5" align="center">데이터가 없습니다.</td></tr>
				</c:when>
				<c:when test="${!empty masterList}">
					<c:forEach var="list" items="${masterList}">
						<tr>
							<td><c:out value="${list.id}"/></td>
							<td> <a href="javascript:kangong.dd.view('${list.id}');"> <c:out value="${list.ddCode}"/> </a></td>
							<td><c:out value="${list.ddName}"/></td>
							<td><c:out value="${list.ddModule}"/></td>
							<td><c:out value="${list.ddDesc}"/></td>
						</tr>
					</c:forEach>
				</c:when>
			</c:choose>
		</tbody>
	</table>
	</div>

	<div >
		<button type="button" class="btn btn-sm btn-primary" id="ddEditBtn">DD 생성</button>
   </div>

   <kangong:paging url="${pageContext.request.contextPath}/dd/list.do" page="${pagination.page}" range="${pagination.range}" rangeSize="${pagination.rangeSize}" startPage="${pagination.startPage}" endPage="${pagination.endPage}" next="${pagination.next}" prev="${pagination.next}" />


   <!-- search{s} -->
		<div class="form-group row justify-content-center">
			<div class="w100" style="padding-right:10px">
				<select class="form-control form-control-sm" name="searchType" id="searchType">
					<option value="ddName">DD명</option>
					<option value="ddModule">DD모듈</option>
					<option value="ddDesc">DD설명</option>
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

});

kangong.dd = {
	view :function (id){
			var paramObj ={};
			paramObj.id =  id;
			kangong.form.submitPost('${viewURL}',paramObj);
		}
}

</script>