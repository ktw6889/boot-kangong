<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jsrender-1.0.7.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script src="https://www.jsviews.com/download/jsviews.min.js"></script>

<div class="board-container-wide">

	<div class="board-header">
		<h2><c:choose><c:when test="${not empty masterVO.id}">공용테이블 수정</c:when><c:otherwise>공용테이블 생성</c:otherwise></c:choose></h2>
	</div>

	<form:form id="masterForm" name="masterForm" modelAttribute="masterVO" action="#" method="post">

	<div class="admin-form-card" id="masterInfoDiv">
		<form:hidden path="id" id="masterId" />
		<div class="form-row">
			<div class="col-md-4 mb-3">
				<label for="tableCode">Table Code</label>
				<form:input id="tableCode" path="tableCode" class="form-control" value="ST_USER_INFO" placeholder="ST_USER_INFO" required="true" />
			</div>
			<div class="col-md-4 mb-3">
				<label for="tableName">Table NAME</label>
				<form:input id="tableName" path="tableName" class="form-control" placeholder="테이블 한글명" required="true" />
			</div>
			<div class="col-md-4 mb-3">
				<label for="tableDesc">Table 설명</label>
				<form:input id="tableDesc" path="tableDesc" class="form-control" placeholder="테이블 설명" />
			</div>
		</div>
	</div>

	<div class="section-title">컬럼 정의</div>

	<div class="grid-row-actions">
		<a class="btn-grid-action" href="javascript:$('#itemInfoTable').addRow('templateItemInfoTr');" role="button">+ 추가</a>
		<a class="btn-grid-action" href="javascript:$('#itemInfoTable').deleteRow();" role="button">- 삭제</a>
	</div>

	<table id="itemInfoTable" class="admin-grid">
		<thead>
		<tr>
			<th style="width:40px"></th>
			<th style="width:18%">컬럼명</th>
			<th style="width:14%">컬럼설명</th>
			<th style="width:10%">컬럼타입</th>
			<th style="width:10%">Input타입</th>
			<th style="width:10%">컬럼사이즈</th>
			<th style="width:10%">컬럼 Max</th>
			<th style="width:14%">패턴</th>
			<th style="width:60px">순서</th>
		</tr>
		</thead>
		<tbody id="itemInfoTbody">
			<c:if test="${not empty masterVO.commonTableColumnList}">
			   <c:forEach var="itemVO" items="${masterVO.commonTableColumnList}" varStatus="idx">
					<tr>
						<td>
							<input type="checkbox" name="checkBoxList" class="checkbox" />
							<input type="hidden" name="rowFlag" value="C"/>
							<input type="hidden" name="id" value="${itemVO.id}"/>
							<input type="hidden" name="masterId" value="${itemVO.masterId}"/>
						</td>
						<td><input type="text" name="columnCode" value="${itemVO.columnCode}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="columnComment" value="${itemVO.columnComment}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="columnType" value="${itemVO.columnType}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="inputType" value="${itemVO.inputType}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="columnSize" value="${itemVO.columnSize}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="columnMax" value="${itemVO.columnMax}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="columnPattern" value="${itemVO.columnPattern}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
						<td><input type="text" name="columnOrder" value="${itemVO.columnOrder}" class="form-control" onchange="javascript:updateTableRow(this);"/></td>
					</tr>
				</c:forEach>
			</c:if>
			<c:if test="${empty masterVO.commonTableColumnList}">
			    <tr><td colspan="9" class="td-empty">데이터가 없습니다</td></tr>
			</c:if>
		</tbody>
	</table>

	<div class="board-form-actions">
		<button class="btn btn-board-outline" id="importBtn" type="button">불러오기</button>
		<button class="btn btn-board-primary" type="submit">저장</button>
	</div>

	</form:form>

</div>

<script type="text/x-jsrender" id="templateImportData">
	{{if commonTableColumnList}}
			{{for commonTableColumnList}}
				{{include tmpl="#templateItemInfoTr"/}}
			{{/for}}
	{{else}}
			{{include tmpl="#templateItemInfoTr"/}}
	{{/if}}
</script>

<script type="text/x-jsrender" id="templateItemInfoTr">
<tr>
	<td>
		<input type="checkbox" name="checkBoxList" class="checkbox" />
		<input type="hidden" name="rowFlag" value="I"/>
		<input type="hidden" name="id" value="{{:id}}" />
		<input type="hidden" name="masterId" value="{{:masterId}}" />
	</td>
	<td><input type="text" name="columnCode" value="{{:columnCode}}" class="form-control"/></td>
	<td><input type="text" name="columnComment" value="{{:columnComment}}" class="form-control"/></td>
	<td><input type="text" name="columnType" value="{{:columnType}}" class="form-control"/></td>
	<td><input type="text" name="inputType" value="{{:inputType}}" class="form-control"/></td>
	<td><input type="text" name="columnSize" value="{{:columnSize}}" class="form-control"/></td>
	<td><input type="text" name="columnMax" value="{{:columnMax}}" class="form-control"/></td>
	<td><input type="text" name="columnPattern" value="{{:columnPattern}}" class="form-control"/></td>
	<td><input type="text" name="columnOrder" value="{{:columnOrder}}" class="form-control"/></td>
</tr>
</script>

<c:url var="listURL" value="/commontable/list"></c:url>
<c:url var="viewURL" value="/commontable/view"></c:url>
<c:url var="editURL" value="/commontable/edit"></c:url>
<c:url var="saveURL" value="/commontable/save"></c:url>
<c:url var="deleteURL" value="/commontable/delete"></c:url>
<c:url var="importURL" value="/commontable/importData"></c:url>

<script>
$( document ).ready(function() {
	$("#itemInfoTable").find("input").each(function(){
		$(this).on("change",function(){
			var $rowFlagObj = $(this).parents("tr:first").find("input[name='rowFlag']").val("U");
		});
	});

	$("#importBtn").on("click",function(e){
		e.preventDefault();
	    kangong.commontable.importData();
	});

	$("#masterForm").on("submit",function (e){
	    e.preventDefault();
	    kangong.commontable.save();
	});
});

kangong.commontable = {
	save : function(){
		var formObj = $("#masterInfoDiv").inputToObject({});
		options = {"loopTagName":"tr"};
		formObj.commonTableColumnList = $("#itemInfoTable").inputToArray(options);

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
				kangong.commontable.list();
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
	},
	importData : function() {
		var paramData = {};
		paramData.tableCode = $("#tableCode").val();
		if(kangong.check.isNull(paramData.tableCode)) {
			alert('테이블 코드를 입력하세요');
			retrun;
		};

		$.ajax({
			url: "${importURL}",
			type: "post",
			data: JSON.stringify(paramData),
			dataType: "json",
			contentType: "application/json",
			success: function(resultData) {
				$( "#itemInfoTbody").empty();
				var templateImportData = $.templates("#templateImportData");
				var htmlTemplateImportData = templateImportData.render(resultData);
				$("#itemInfoTbody").append(htmlTemplateImportData);
				htmlTemplateImportData.link("#itemInfoTbody", resultData);
			},
			error: function(errorThrown) {
				alert(errorThrown.statusText);
			}
		});
	}
};
</script>
