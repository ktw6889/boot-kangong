<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />

<div class="board-container-wide">

	<div class="board-header">
		<h2><c:choose><c:when test="${not empty masterVO.id}">DD 수정</c:when><c:otherwise>DD 생성</c:otherwise></c:choose></h2>
	</div>

	<form:form id="masterForm" name="masterForm" modelAttribute="masterVO" action="#" method="post">

	<div class="admin-form-card" id="masterInfoDiv">
		<form:hidden path="id" id="masterId" />
		<div class="form-row">
			<div class="col-md-4 mb-3">
				<label for="ddCode">DD Code</label>
				<form:input id="ddCode" path="ddCode" class="form-control" placeholder="USER_INFO_CITY" required="true" />
			</div>
			<div class="col-md-4 mb-3">
				<label for="ddName">DD NAME</label>
				<form:input id="ddName" path="ddName" class="form-control" placeholder="도시" required="true" />
			</div>
			<div class="col-md-4 mb-3">
				<label for="ddModule">DD Module</label>
				<form:input id="ddModule" path="ddModule" class="form-control" placeholder="User" />
			</div>
		</div>
		<div class="form-row">
			<div class="col-md-12 mb-3">
				<label for="ddDesc">DD 설명</label>
				<form:input id="ddDesc" path="ddDesc" class="form-control" placeholder="DD 설명" />
			</div>
		</div>
	</div>

	<div class="section-title">DD 값 목록</div>

	<div class="grid-row-actions">
		<a class="btn-grid-action" href="javascript:$('#itemInfoTable').addRow('templateItemInfoTr');" role="button">+ 추가</a>
		<a class="btn-grid-action" href="javascript:$('#itemInfoTable').deleteRow();" role="button">- 삭제</a>
	</div>

	<table id="itemInfoTable" class="admin-grid">
		<thead>
		<tr>
			<th style="width:30px"></th>
			<th>DD VALUE</th>
			<th>DD KO</th>
			<th>DD EN</th>
			<th>FILTER</th>
			<th style="width:70px">순서</th>
			<th style="width:70px">DEFAULT</th>
			<th style="width:90px">사용여부</th>
		</tr>
		</thead>
		<tbody id="itemInfoTbody">
			<c:if test="${not empty masterVO.ddValueList}">
			   <c:forEach var="itemVO" items="${masterVO.ddValueList}" varStatus="idx">
					<tr>
						<td>
							<input type="checkbox" name="checkBoxList" class="checkbox" />
							<input type="hidden" name="rowFlag" value="C"/>
							<input type="hidden" name="id" value="${itemVO.id}"/>
							<input type="hidden" name="ddId" value="${itemVO.ddId}"/>
						</td>
						<td><input type="text" name="ddVal" value="${itemVO.ddVal}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="ddKo" value="${itemVO.ddKo}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="ddEn" value="${itemVO.ddEn}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="ddFilter" value="${itemVO.ddFilter}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="ddOrder" value="${itemVO.ddOrder}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td style="text-align:center">
							<input type="radio" id="ddDefault" name="ddDefault" value="${itemVO.ddDefault}" onChange="kangong.dd.checkedValue('ddDefault');" <c:if test="${itemVO.ddDefault eq 'Y'}">checked</c:if> class="form-check-input" style="margin:0 auto;position:relative;" />
						</td>
						<td>
							<select id="useYn" name="useYn" value="${itemVO.useYn}" class="custom-select">
								<option disabled value="">선택</option>
								<option value="Y" <c:if test="${itemVO.useYn eq 'Y'}">selected</c:if>>Y</option>
								<option value="N" <c:if test="${itemVO.useYn eq 'N'}">selected</c:if>>N</option>
							</select>
						</td>
					</tr>
				</c:forEach>
			</c:if>
			<c:if test="${empty masterVO.ddValueList}">
			    <tr><td colspan="8" class="td-empty">데이터가 없습니다</td></tr>
			</c:if>
		</tbody>
	</table>

	<div class="board-form-actions">
		<button class="btn btn-board-primary" type="submit">저장</button>
	</div>

	</form:form>

</div>

<script type="text/x-jsrender" id="templateItemInfoTr">
<tr>
	<td>
		<input type="checkbox" name="checkBoxList" class="checkbox" />
		<input type="hidden" name="rowFlag" value="I"/>
		<input type="hidden" name="id" />
		<input type="hidden" name="ddId" />
	</td>
	<td><input type="text" name="ddVal" class="form-control"/></td>
	<td><input type="text" name="ddKo" class="form-control"/></td>
	<td><input type="text" name="ddEn" class="form-control"/></td>
	<td><input type="text" name="ddFilter" class="form-control"/></td>
	<td><input type="text" name="ddOrder" class="form-control"/></td>
	<td style="text-align:center">
		<input type="radio" id="ddDefault" name="ddDefault" value="N" onChange="kangong.dd.checkedValue('ddDefault');" class="form-check-input" style="margin:0 auto;position:relative;" />
	</td>
	<td>
		<select id="useYn" name="useYn" class="custom-select">
			<option selected disabled value="">선택</option>
			<option value="Y">Y</option>
			<option value="N">N</option>
		</select>
	</td>
</tr>
</script>

<c:url var="listURL" value="/common/dd/list"></c:url>
<c:url var="viewURL" value="/common/dd/view"></c:url>
<c:url var="editURL" value="/common/dd/edit"></c:url>
<c:url var="saveURL" value="/common/dd/save"></c:url>
<c:url var="deleteURL" value="/common/dd/delete"></c:url>

<script>
$( document ).ready(function() {
	$("#itemInfoTable").find("input").each(function(){
		$(this).on("change",function(){
			var $rowFlagObj = $(this).parents("tr:first").find("input[name='rowFlag']").val("U");
		});
	});

	$("#masterForm").on("submit",function (e){
	    e.preventDefault();
	    kangong.dd.save();
	});
});

kangong.dd = {
	save  : function(){
		var formObj = $("#masterInfoDiv").inputToObject({});
		options = {"loopTagName":"tr"};
		formObj.ddValueList = $("#itemInfoTable").inputToArray(options);
		console.log("formObj:"+JSON.stringify(formObj));
	},
	save2 : function(){
		var formObj = $("#masterInfoDiv").inputToObject({});
		options = {"loopTagName":"tr"};
		formObj.ddValueList = $("#itemInfoTable").inputToArray(options);

		$.ajax({
			type:'post',
			url:"${saveURL}",
			contentType: "application/json",
			data: JSON.stringify(formObj),
			success:function(result){
			}
		});
	},
	list : function (){
		kangong.form.submitPost("${listURL}");
	},
	view: function (){
		kangong.form.submitPost("${viewURL}",{id: "${masterVO.id}" });
	},
	deleteData : function (){
		var paramData = {id: "${masterVO.id}" };
		$.ajax({
			url: "${deleteURL}",
			data : paramData,
			type : 'POST',
			dataType : 'text',
			success: function(result){
				kangong.dd.list();
			},
			error: function(error){
				console.log("에러 : " + error);
			}
		});
	},
	checkedValue : function(idName){
		$("#"+idName).each(function(){
			if($(this).is(":checked")){
				$(this).val("Y");
			}else{
				$(this).val("N");
			}
		});
		$("#rowFlag").each(function(){
			if($(this).val("C"))
				$(this).val("U");
		});
	}
};
</script>
