<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<form:form id="masterForm" name="masterForm"  modelAttribute="masterVO" action="#" method="post">


<div id="masterInfoDiv">
<form:hidden path="id" id="masterId" />
   <div class="form-row" >
	    <div class="col-md-4 mb-3">
	      	<label for="ddCode">DD Code</label>
		  	<form:input id="ddCode" path="ddCode" class="form-control" placeholder="USER_INFO_CITY" required="true" />
		    <div id="ddCodeFeedback" class="valid-feedback">
		    </div>
		</div>
	    <div class="col-md-4 mb-3">
		     <label for="ddName">DD NAME</label>
		     <form:input id="ddName" path="ddName" class="form-control" placeholder="도시" required="true" />
	         <div id="ddNameFeedback" class="valid-feedback">
		     </div>
	    </div>
	    <div class="col-md-4 mb-3">
	       <label for="ddModule">DD Module</label>
	       <form:input id="ddModule" path="ddModule" class="form-control" placeholder="User"  />
	       <div id="ddModuleFeedback" class="invalid-feedback">
	       </div>
       </div>
  </div>
  <div class="form-row" >
	    <div class="col-md-12 mb-9">
	      	<label for="ddDesc">DD 설명</label>
		  	<form:input id="ddDesc" path="ddDesc" class="form-control" placeholder="DD 설명"  />
		    <div id="ddDescFeedback" class="valid-feedback">
		    </div>
		</div>
  </div>
</div>

	<div class="m-3 float-right">
		<a class="btn btn-outline-primary btn-sm" href="javascript:$('#itemInfoTable').addRow('templateItemInfoTr');"  role="button">추가</a>
		<a class="btn btn-outline-primary btn-sm" href="javascript:$('#itemInfoTable').deleteRow();" role="button">삭제</a>
	</div>
			<table id="itemInfoTable" class="table">
				<tr>
					<th scope="col" width="20px"/></th>
					<th scope="col" width="80px"/>DD VALUE</th>
					<th scope="col" width="150px">DD KO</th>
					<th scope="col" width="150px"/>DD EN</th>
					<th scope="col" width="150px"/>FILTER</th>
					<th scope="col" width="60px">순서</th>
					<th scope="col" width="60px">DEFAULT</th>
					<th scope="col" width="60px">사용여부</th>
				</tr>

				<tbody id="itemInfoTbody">
					<c:if test="${not empty masterVO.ddValueList}">
					   <c:forEach var="itemVO" items ="${masterVO.ddValueList}" varStatus="idx">
							<tr>
								<td>
									<input type="checkbox" name="checkBoxList" class="checkbox" />
									<input type="hidden" name="rowFlag" value="C"/>
									<input type="hidden" name="id" value="${itemVO.id}"/>
									<input type="hidden" name="ddId" value="${itemVO.ddId}"/>
					             </td>
								<td>
									<input type="text" name="ddVal" value="${itemVO.ddVal}" title="DD VALUE" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
								<td>
									<input type="text" name="ddKo" value="${itemVO.ddKo}" title="DD KO" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
								<td>
									<input type="text" name="ddEn" value="${itemVO.ddEn}" title="DD EN" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
								<td>
									<input type="text" name="ddFilter" value="${itemVO.ddFilter}" title="FILTER" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
					            <td>
					            	<input type="text" name="ddOrder" value="${itemVO.ddOrder}" title="순서" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
					             <td align="center">
									<input type="radio" id="ddDefault" name="ddDefault" value="${itemVO.ddDefault}" onChange="kangong.dd.checkedValue('ddDefault');" <c:if test="${itemVO.ddDefault eq 'Y'}">checked</c:if> class="form-check-input form-check-inline" />
					             </td>
					             <td>
					             	 <select id="useYn" name="useYn"  value="${itemVO.useYn}" class="custom-select">
								      	<option disabled value="">Choose...</option>
								        <option value="Y" <c:if test="${itemVO.useYn eq 'Y'}">selected</c:if> >Y</option>
								        <option value="N" <c:if test="${itemVO.useYn eq 'N'}">selected</c:if> >N</option>
								      </select>
								</td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${empty masterVO.ddValueList}">
					    <tr> <td colspan="8"> 데이터가 없습니다 </td></tr>
					</c:if>


				</tbody>
			</table>


  <button class="btn btn-primary" type="submit">저장</button>
</form:form>

<script type="text/x-jsrender" id="templateItemInfoTr">
<tr>
	<td>
		<input type="checkbox" name="checkBoxList" class="checkbox" />
		<input type="hidden" name="rowFlag" value="I"/>
		<input type="hidden" name="id" />
		<input type="hidden" name="ddId" />
	</td>
	<td>
		<input type="text" name="ddVal" title="DD VALUE" class="inputmax required"/>
	</td>
	<td>
		<input type="text" name="ddKo" title="DD KO" class="inputmax required"/>
	</td>
	<td>
		<input type="text" name="ddEn"  title="DD EN" class="inputmax required"/>
	</td>
	<td>
		<input type="text" name="ddFilter" title="FILTER" class="inputmax required"/>
	</td>
	<td>
	<input type="text" name="ddOrder" title="순서" class="inputmax required"/>
		</td>
	<td align="center">
			<input type="radio" id="ddDefault" name="ddDefault" value="N" onChange="kangong.dd.checkedValue('ddDefault');" class="form-check-input form-check-inline" />
	</td>
	<td>
	 <select id="useYn" name="useYn" class="custom-select">
		<option selected disabled value="">Choose...</option>
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
	console.log("ready");
	$("#itemInfoTable").find("input").each(function(){
		$(this).on("change",function(){
			var $rowFlagObj = $(this).parents("tr:first").find("input[name='rowFlag']").val("U");
		});
	});

	$("#masterForm").on("submit",function (e){
		console.log("submit");
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
				kangong.db.list();
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
	}
};
</script>