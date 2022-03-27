<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jsrender-1.0.7.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script src="https://www.jsviews.com/download/jsviews.min.js"></script>

<form:form id="masterForm" name="masterForm"  modelAttribute="masterVO" action="#" method="post">


<div id="masterInfoDiv">
<form:hidden path="id" id="masterId" />
   <div class="form-row" >
	    <div class="col-md-4 mb-3">
	      	<label for="tableCode">Table Code</label>
		  	<form:input id="tableCode" path="tableCode" class="form-control" value="ST_USER_INFO" placeholder="ST_USER_INFO" required="true" />
		    <div id="tableCodeFeedback" class="valid-feedback">
		    </div>
		</div>
	    <div class="col-md-4 mb-3">
		     <label for="tableName">Table NAME</label>
		     <form:input id="tableName" path="tableName" class="form-control" placeholder="테이블 한글명" required="true" />
	    </div>
	    <div class="col-md-4 mb-3">
	       <label for="tableDesc">Table 설명</label>
	       <form:input id="tableDesc" path="tableDesc" class="form-control" placeholder="테이블 설명"  />
       </div>
  </div>
</div>

	<div class="m-3 float-right">
		<a class="btn btn-outline-primary btn-sm" href="javascript:$('#itemInfoTable').addRow('templateItemInfoTr');"  role="button">추가</a>
		<a class="btn btn-outline-primary btn-sm" href="javascript:$('#itemInfoTable').deleteRow();" role="button">삭제</a>
	</div>

<table id="itemInfoTable" class="table">
	<tr>
		<th scope="col" width="5%"/></th>
		<th scope="col" width="20%"/>컬럼명</th>
		<th scope="col" width="15%"/>컬럼설명</th>
		<th scope="col" width="10%">컬럼타입</th>
		<th scope="col" width="10%">Input타입</th>
		<th scope="col" width="10%"/>컬럼사이즈</th>
		<th scope="col" width="10%">컬럼 Max</th>
		<th scope="col" width="15%">패턴</th>
		<th scope="col" width="5%">순서</th>
	</tr>

	<tbody id="itemInfoTbody">
		<c:if test="${not empty masterVO.commonTalbeColumnList}">
		   <c:forEach var="itemVO" items ="${masterVO.commonTalbeColumnList}" varStatus="idx">
				<tr>
					<td>
						<input type="checkbox" name="checkBoxList" class="checkbox" />
						<input type="hidden" name="rowFlag" value="C"/>
						<input type="hidden" name="id" value="${itemVO.id}"/>
						<input type="hidden" name="masterId" value="${itemVO.masterId}"/>
		             </td>
					<td>
						<input type="text" name="columnCode" value="${itemVO.columnCode}" title=컬럼명" class="form-control required" onchange="javascript:updateTableRow(this);"/>
					</td>
					<td>
						<input type="text" name="columnComment" value="${itemVO.columnComment}" title="컬럼설명" class="form-control" onchange="javascript:updateTableRow(this);"/>
					</td>
					<td>
						<input type="text" name="columnType" value="${itemVO.columnType}" title="컬럼타입" class="form-control required" onchange="javascript:updateTableRow(this);"/>
					</td>
					<td>
						<input type="text" name="inputType" value="${itemVO.inputType}" title="INPUT 타입" class="form-control required" onchange="javascript:updateTableRow(this);"/>
					</td>
					<td>
						<input type="text" name="columnSize" value="${itemVO.columnSize}" title="컬럼사이즈" class="form-control" onchange="javascript:updateTableRow(this);"/>
					</td>
		            <td>
		            	<input type="text" name="columnMax" value="${itemVO.columnMax}" title="컬럼Max" class="form-control" onchange="javascript:updateTableRow(this);"/>
					</td>
		             <td>
		            	<input type="text" name="columnPattern" value="${itemVO.columnPattern}" title="패턴" class="form-control" onchange="javascript:updateTableRow(this);"/>
					</td>
		             <td>
		            	<input type="text" name="columnOrder" value="${itemVO.columnOrder}" title="순서" class="form-control" onchange="javascript:updateTableRow(this);"/>
					</td>
				</tr>
			</c:forEach>
		</c:if>
		<c:if test="${empty masterVO.commonTalbeColumnList}">
		    <tr> <td colspan="9"> 데이터가 없습니다 </td></tr>
		</c:if>


	</tbody>
</table>

 <button class="btn btn-info" id="importBtn">불러오기</button>
  <button class="btn btn-primary" type="submit">저장</button>
</form:form>

<script type="text/x-jsrender" id="templateImportData">
	{{if commonTalbeColumnList}}
			{{for commonTalbeColumnList}}
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
		<input type="hidden" name="id"  value="{{:id}}" />
		<input type="hidden" name="masterId"  value="{{:masterId}}" />
	</td>
	<td>
		<input type="text" name="columnCode" value="{{:columnCode}}" title=컬럼명" class="form-control required"/>
	</td>
	<td>
		<input type="text" name="columnComment" value="{{:columnComment}}" title="컬럼설명" class="form-control"/>
	</td>
	<td>
		<input type="text" name="columnType" value="{{:columnType}}" title="컬럼타입" class="form-control required"/>
	</td>
	<td>
		<input type="text" name="inputType" value="{{:inputType}}" title="INPUT 타입" class="form-control required"/>
	</td>
	<td>
		<input type="text" name="columnSize" value="{{:columnSize}}" title="컬럼사이즈" class="form-control"/>
	</td>
	<td>
		<input type="text" name="columnMax" value="{{:columnMax}}" title="컬럼Max" class="form-control"/>
	</td>
	<td>
		<input type="text" name="columnPattern" value="{{:columnPattern}}" title="패턴" class="form-control"/>
	</td>
	<td>
		<input type="text" name="columnOrder" value="{{:columnOrder}}"  title="순서" class="form-control"/>
	</td>
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
	console.log("ready");
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
		console.log("submit");
	    e.preventDefault();
	    kangong.commontable.save();
	});

});

kangong.commontable = {
	save : function(){

					var formObj = $("#masterInfoDiv").inputToObject({});
					options = {"loopTagName":"tr"};
					formObj.commonTalbeColumnList = $("#itemInfoTable").inputToArray(options);
					console.log("formObj:"+JSON.stringify(formObj));

				    $.ajax(
				    {
				        type:'post',
				        url:"${saveURL}",
				        contentType: "application/json",
				        data: JSON.stringify(formObj),
				        beforeSend:function()
				        {
				            //launchpreloader();
				        },
				        complete:function()
				        {
				            //stopPreloader();
				        },
				        success:function(result)
				        {
				        	// kangong.form.submitPost("${listURL}", {});
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
			url: "${deleteURL}"
			, data : paramData
			, type : 'POST'
			, dataType : 'text'
			, success: function(result){
				kangong.commontable.list();
			}

			, error: function(error){
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
		console.log(paramData.tableCode);
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