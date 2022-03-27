<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>


<div id="masterInfoDiv">
     <div class="row border" >
     	<div class="col-2 p-1 bg-primary">DD Code </div>
     	<div class="col-2"><c:out value="${masterVO.ddCode}"/> </div>
     	<div class="col-2 p-1 bg-primary">DD명 </div>
     	<div class="col-2"><c:out value="${masterVO.ddName}"/> </div>
     	<div class="col-2 p-1 bg-primary">DD모듈 </div>
     	<div class="col-2"><c:out value="${masterVO.ddModule}"/> </div>
     </div>
     <div class="row border" >
     	<div class="col-2 p-1 bg-primary">DD설명</div>
     	<div class="col-10"><c:out value="${masterVO.ddDesc}"/> </div>
     </div>
</div>

<div class="page-header m-3">
	DD 값
</div>

<table id="itemInfoTable" class="table">
	<tr>
		<th scope="col" width="5%"/></th>
		<th scope="col" width="15%"/>Val</th>
		<th scope="col" width="20%">Ko</th>
		<th scope="col" width="20%"/>En</th>
		<th scope="col" width="15%">Filter</th>
		<th scope="col" width="5%">순서</th>
		<th scope="col" width="10%">Default</th>
		<th scope="col" width="10%">사용여부</th>
	</tr>

	<tbody id="itemInfoTbody">
		<c:if test="${not empty masterVO.ddValueList}">
		   <c:forEach var="itemVO" items ="${masterVO.ddValueList}" varStatus="idx">
				<tr>
					<td>
						<input type="hidden" name="id" value="${itemVO.id}"/>
						<input type="hidden" name="ddId" value="${itemVO.ddId}"/>
		             </td>
					<td>
						<c:out value="${itemVO.ddVal}"/>
					</td>
					<td>
						<c:out value="${itemVO.ddKo}"/>
					</td>
					<td>
						<c:out value="${itemVO.ddEn}"/>
					</td>
					<td>
						<c:out value="${itemVO.ddFilter}"/>
					</td>
		            <td>
		            	<c:out value="${itemVO.ddOrder}"/>
					</td>
		             <td align="center">
						<c:out value="${itemVO.ddDefault}"/>
		             </td>
		             <td>
		             	 <c:out value="${itemVO.useYn}"/>
					</td>
				</tr>
			</c:forEach>
		</c:if>
		<c:if test="${empty masterVO.ddValueList}">
		    <tr> <td colspan="8"> 데이터가 없습니다 </td></tr>
		</c:if>
</table>


<div style="margin-top : 20px">
	<button type="button" class="btn btn-sm btn-primary" id="btnUpdate">수정</button>
	<button type="button" class="btn btn-sm btn-primary" id="btnDelete">삭제</button>
	<button type="button" class="btn btn-sm btn-primary" id="btnList">목록</button>
</div>

<c:url var="listURL" value="/common/dd/list"></c:url>
<c:url var="editURL" value="/common/dd/edit"></c:url>
<c:url var="deleteURL" value="/common/dd/delete"></c:url>

<script>
$( document ).ready(function() {
	$('#btnList').on('click', function(){
		kangong.form.submitPost("${listURL}");
	});

	//수정 버튼 클릭 이벤트
	$('#btnUpdate').on('click', function(){
		var paramObj = {};
        paramObj.id = ${masterVO.id};
		kangong.form.submitPost("${editURL}",paramObj);
	});

	//삭제 버튼 클릭 이벤트
	$('#btnDelete').on('click',function(){
	    var paramObj = {};
	    paramObj.id = ${masterVO.id};

	    kangong.form.submitPost( "${deleteURL}",paramObj);
	});

});
</script>