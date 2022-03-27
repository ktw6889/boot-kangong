<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jsrender-1.0.7.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script src="https://www.jsviews.com/download/jsviews.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/underscore@1.11.0/underscore-min.js"></script>

<form:form name="form" id="form" method="post" modelAttribute="masterVO" action="#">
<form:hidden id="tableCode" path="tableCode"  />

	<div class="m-3 float-right">
		<a class="btn btn-outline-primary btn-sm" href="javascript:$('#table').addRow('templateItemAddTr');"  role="button">추가</a>
		<a class="btn btn-outline-primary btn-sm" href="javascript:$('#table').deleteRow();" role="button">삭제</a>
	</div>

	<table id="table" class="table">
	</table>

</form:form>

	<div >
		<button type="button" class="btn btn-sm btn-primary" id="btnSave">저장</button>
		<button type="button" class="btn btn-sm btn-primary" id="btnList">목록</button>
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
			{userName:"김태우",age:"48",city:"부산",orderNo:1},
			{userName:"김송윤",age:"46",city:"부산",orderNo:2},
			{userName:"김예똥",age:"15",city:"서울",orderNo:3},
			{userName:"김예뽕",age:"14",city:"수원",orderNo:4}
		]
};
</script>

<script>
$(document).ready(function(){
	console.log("tablecode",$("#tableCode").val());

	//html 구성
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
		//헤더 생성
		var templateHeadTr = $.templates("#templateHeadTr");
		var htmlHeadTr = templateHeadTr.render(columnData);
		$("#table").append(htmlHeadTr);

		//Json 데이터 합치기
		var combineData = kangong.commontable.getCombineJson(resultData.resultList, columnData.columnList);
		/*
		console.log(combineData);
		$.each(combineData.trList, function(index, trObj){
			console.log("trObj",trObj);
			$.each(trObj.tdList, function(ind, tdObj){
				if(tdObj.inputType == "date"){
					var date = new Date();
					date.setTime(tdObj.columnValue);
					tdObj.columnValue = kangong.commontable.formatDate(date);
					console.log(tdObj.columnValue);
				}
			});
		});
		*/

		//Column정보로 Tr템플릿 만들기
		var templateItemTr = $.templates("#templateItemTr");
		var htmlItemTr = templateItemTr.render(combineData);

		$("#table").append(htmlItemTr);

		//AddTr 만들기
		var templateTempItemTr = $.templates("#templateTempItemAddTr");
		var htmlTempItemTr = templateTempItemTr.render(columnData);
		console.log(htmlTempItemTr);
		$("#templateItemAddTr").html(htmlTempItemTr);
	},
	save : function(){
		var options = {};

		var dataInfoObj ={ tableCode : $("#tableCode").val() };
		options = {"loopTagName":"tr"};
		dataInfoObj.dataList = $("#table").inputToArray(options);
		console.log("dataInfoObj:"+JSON.stringify(dataInfoObj));

		var params = $("#form").serialize(); // serialize() : 입력된 모든Element(을)를 문자열의 데이터에 serialize 한다.
		$.ajax(
			    {
			        type:'post',
			        url:'${saveInputURL}',
			        contentType: "application/json",
			        data: JSON.stringify(dataInfoObj),
			        success:function(result)
			        {
			            // kangong.form.submitPost("${listURL}", {});
			        }
			    });
	},
	getCombineJson : function(dataList, columnList){
						var tdDataList =  _.map(dataList, function(udata){
							 return $.map(Object.keys(udata), function(data){
								//return {[data] :udata[data]};
								return {columnName : data, columnValue : udata[data]};
							});
						});

						var tdColumnData = _.map(tdDataList, function(tdDataRow){
							//console.log("tdDataRow",tdDataRow);
							var tdArray= _.map(tdDataRow, function(tdData){
								return $.extend({}, _.filter(columnList,function(columnData){
									return columnData.columnCode == tdData.columnName;
								})[0], tdData) ;
							});
							//coolumn순으로 sorting - columnOrder
							tdArray = _.chain(tdArray).sortBy('columnOrder').value();
							//date 값 셋팅
							$.each(tdArray, function(ind, tdObj){
								if(tdObj.inputType == "date"){
									var date = new Date();
									date.setTime(tdObj.columnValue);
									tdObj.columnValue = kangong.commontable.formatDate(date);
									console.log(tdObj.columnValue);
								}
							});

							return {tdList : tdArray};
						});

						return{trList :tdColumnData };
					},
	formatDate :	function(date) {
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