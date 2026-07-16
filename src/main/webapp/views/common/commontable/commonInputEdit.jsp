<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jsrender-1.0.7.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script src="https://www.jsviews.com/download/jsviews.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/underscore@1.11.0/underscore-min.js"></script>

<div class="board-container-wide">

	<div class="board-header">
		<h2>공용테이블 데이터</h2>
	</div>

	<form:form name="form" id="form" method="post" modelAttribute="masterVO" action="#">
	<form:hidden id="tableCode" path="tableCode" />

	<div class="grid-row-actions">
		<a class="btn-grid-action" href="javascript:$('#table').addRow('templateItemAddTr');" role="button">+ 추가</a>
		<a class="btn-grid-action" href="javascript:$('#table').deleteRow();" role="button">- 삭제</a>
	</div>

	<table id="table" class="admin-grid">
	</table>

	</form:form>

	<div class="board-form-actions">
		<button type="button" class="btn btn-board-primary" id="btnSave">저장</button>
		<button type="button" class="btn btn-board-outline" id="btnList">목록</button>
	</div>

</div>

<script type="text/x-jsrender" id="templateHeadTr">
<tr>
	{{if columnList}}
		 {{for columnList}}
			{{if columnCode == 'ID' }}
			{{else}}
				<td>
					{{:columnComment}}
				</td>
			{{/if}}
		 {{/for}}
	{{/if}}
</tr>
</script>

<script type="text/x-jsrender" id="templateItemTr">
	{{if trList}}
		 {{for trList}}
			<tr>
				{{if tdList}}
		 			{{for tdList}}
						{{if columnName == 'ID' }}
							<input type="hidden" name="ID" value="{{:columnValue}}"/>
							<input type="hidden" name="rowFlag" value="C"/>
						{{else}}
							<td>
								<input type="{{:inputType}}" name="{{:columnName}}" max="{{:columnMax}}" value="{{:columnValue}}" />
							</td>
						{{/if}}
		 			{{/for}}
				{{/if}}
			</tr>
    	{{/for}}
	{{/if}}
</script>

<script type="text/x-jsrender" id="templateTempItemAddTr">
<tr>
	{{if columnList}}
		 {{for columnList}}
			{{if columnCode == 'ID' }}
				<input type="hidden" name="ID" />
				<input type="hidden" name="rowFlag" value="I"/>
			{{else}}
				<td>
					<input type="{{:inputType}}" name="{{:columnCode}}" max="{{:columnMax}}" />
				</td>
			{{/if}}
		 {{/for}}
	{{/if}}
</tr>
</script>

<script type="text/x-jsrender" id="templateItemAddTr">
</script>

<c:url var="listURL" value="/commontable/list"></c:url>
<c:url var="saveInputURL" value="/commontable/saveInput"></c:url>
<c:url var="viewURL" value="/commontable/edit"></c:url>

<script>
var columnData = { columnList : ${ masterVO.jsonColumnList} };
var resultData = { resultList : ${masterVO.jsonDataList} };

var columnData2 = {
		columnList : [
			{ columnName:"userName", inputType:"text", columnMax:"10", columnCommnet:"사용자명", orderNo:1},
			{ columnName:"age", inputType:"number", columnMax:"10", columnCommnet:"나이", orderNo:2},
			{ columnName:"city", inputType:"text", columnMax:"5", columnCommnet:"도시", orderNo:3}
		]
};

var resultData2 = {
		resultList : [
			{userName:"홍길동",age:"30",city:"서울",orderNo:1},
			{userName:"김철수",age:"28",city:"부산",orderNo:2},
			{userName:"이영희",age:"25",city:"대구",orderNo:3},
			{userName:"박민수",age:"22",city:"인천",orderNo:4}
		]
};
</script>

<script>
$(document).ready(function(){
	kangong.commontable.generatePage();

	$("#table").find("input").each(function(){
		$(this).on("change",function(){
			var $rowFlagObj = $(this).parents("tr:first").find("input[name='rowFlag']").val("U");
		});
	});
	$('#btnSave').on('click',function(e){
		e.preventDefault();
		kangong.commontable.save();
	});

	$('#btnList').on('click', function(e){
		e.preventDefault();
		kangong.form.submitPost("${listURL}");
	});
});

kangong.commontable = {
	generatePage : function(){
		var templateHeadTr = $.templates("#templateHeadTr");
		var htmlHeadTr = templateHeadTr.render(columnData);
		$("#table").append(htmlHeadTr);

		var combineData = kangong.commontable.getCombineJson(resultData.resultList, columnData.columnList);

		var templateItemTr = $.templates("#templateItemTr");
		var htmlItemTr = templateItemTr.render(combineData);
		$("#table").append(htmlItemTr);

		var templateTempItemTr = $.templates("#templateTempItemAddTr");
		var htmlTempItemTr = templateTempItemTr.render(columnData);
		$("#templateItemAddTr").html(htmlTempItemTr);
	},
	save : function(){
		var options = {};
		var dataInfoObj ={ tableCode : $("#tableCode").val() };
		options = {"loopTagName":"tr"};
		dataInfoObj.dataList = $("#table").inputToArray(options);

		var params = $("#form").serialize();
		$.ajax({
			type:'post',
			url:'${saveInputURL}',
			contentType: "application/json",
			data: JSON.stringify(dataInfoObj),
			success:function(result){
			}
		});
	},
	getCombineJson : function(dataList, columnList){
		var tdDataList =  _.map(dataList, function(udata){
			 return $.map(Object.keys(udata), function(data){
				return {columnName : data, columnValue : udata[data]};
			});
		});

		var tdColumnData = _.map(tdDataList, function(tdDataRow){
			var tdArray= _.map(tdDataRow, function(tdData){
				return $.extend({}, _.filter(columnList,function(columnData){
					return columnData.columnCode == tdData.columnName;
				})[0], tdData) ;
			});
			tdArray = _.chain(tdArray).sortBy('columnOrder').value();
			$.each(tdArray, function(ind, tdObj){
				if(tdObj.inputType == "date"){
					var date = new Date();
					date.setTime(tdObj.columnValue);
					tdObj.columnValue = kangong.commontable.formatDate(date);
				}
			});
			return {tdList : tdArray};
		});

		return{trList :tdColumnData };
	},
	formatDate : function(date) {
		var d = new Date(date);
		month = '' + (d.getMonth() + 1);
		day = '' + d.getDate();
		year = d.getFullYear();

		if (month.length < 2) month = '0' + month;
		if (day.length < 2) day = '0' + day;

		return [year, month, day].join('-');
	}
}
</script>
