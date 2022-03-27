<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script src="https://www.jsviews.com/download/jsviews.min.js"></script>


<form:form id="userForm" name="userForm"  modelAttribute="userVo" action="#" method="post">

<div id="userInfoDiv" >

 <form:hidden path="id" />

  <div class="form-row" >
    <div class="col-md-4 mb-3">
      <label for="userId">사용자 ID</label>
	  <form:input id="userId" path="userId" class="form-control" placeholder="abcd@naver.com" required="true" />
    </div>
    <div id="userIdFeedback" class="valid-feedback">
    </div>
    <div class="col-md-4 mb-3">
      <label for="passwordId">Password</label>
      <input type="password" class="form-control" id="passwordId" name="password" value="1234" required>
      <div id="passwordIdFeedback" class="valid-feedback">
      </div>
    </div>
	    <div class="col-md-4 mb-3">
	      <label for="passwordConfirmId">Password Confirm</label>
	      <input type="password" class="form-control" id="passwordIdConfirm" value="1234" required>
	      <div id="passwordIdConfirmFeedback" class="invalid-feedback">
	      </div>
    	</div>
  </div>

  <div class="form-row">
    <div class="col-md-4 mb-3">
      <label for="userNameId">이름</label>
      <form:input path="userName" id="userNameId" class="form-control" maxlength="5" placeholder="홍길동" required="true" />
    </div>
    <div class="col-md-4 mb-3">
      <label for="ageId">Age</label>
      <form:input type="number" id="ageId" path="age" class="form-control" max="100" aria-describedby="ageId" required="true" />
    </div>
    <div class="col-md-4 mb-3">
      <label for="birthDayId">생일</label>
      <form:input type="date" id="birthDayId" path="birthDate" class="form-control" min = "1950-07-01" max = "2020-08-15" required="true" />
    </div>
  </div>

  <div class="form-row">
	    <div class="col-md-4 mb-3">
	      <label for="cityId">도시</label>
	      <form:select id="cityId" path="city" class="custom-select" aria-describedby="cityIdFeedback" required="true">
	      	<option selected disabled value="">Choose...</option>
	        <form:option value="seoul">서울</form:option>
	        <form:option value="keongki">경기</form:option>
	        <form:option value="busan">부산</form:option>
	        <form:option value="suwon">수원</form:option>
	      </form:select>
	    </div>
		<div class="col-md-4 mb-3">
		    <fieldset class="form-group">
			    <div class="row">
			      	<legend class="col-form-label col-sm-2 pt-0">성별</legend>
				      <div class="col-sm-20">
					        <div class="form-check">
					        <form:radiobutton id="genderId1" path="gender" value="male" class="form-check-input form-check-inline" />
					          <label class="form-check-label" for="gender1">
					            	남
					          </label>
					        </div>
					        <div class="form-check">
					        <form:radiobutton id="genderId2" path="gender" class="form-check-input form-check-inline" value="female" />
					          <label class="form-check-label" for="gender2">
					            	여
					          </label>
					        </div>
				      </div>
			    </div>
		  </fieldset>
		</div>
	    <div class="col-md-4 mb-3">
			     <label for="hobbyId">취미</label>
			      <div class="form-check form-check-inline">
				      <form:checkbox  id="inlineCheckbox1" path="strsHobby" class="form-check-input" value="book" />
					  <label class="form-check-label" for="inlineCheckbox1">독서</label>
				 </div>
				<div class="form-check form-check-inline">
				  <form:checkbox  id="inlineCheckbox2" path="strsHobby" class="form-check-input" value="music" />
				  <label class="form-check-label" for="inlineCheckbox2">음악감상</label>
				</div>
				<div class="form-check form-check-inline">
				  <form:checkbox  id="inlineCheckbox3" path="strsHobby" class="form-check-input" value="game" />
				  <label class="form-check-label" for="inlineCheckbox3">게임</label>
				</div>
	    </div>
  </div>

	  <div class="form-row">
	    <div class="col-md-12 mb-3">
	        <label for="commentId">Comment</label>
	       <textarea class="form-control" id="commentId" name="comment" cols = "50" rows = "5" placeholder="가입인사를 남겨주세요.">안녕하세요</textarea>
	    </div>
	  </div>
</div>

	<div class="mb-3 float-right">
			<a class="btn btn-outline-primary btn-sm" href="javascript:$('#userInfoItemTable').addRow('templateUserInfoItemTr');"  role="button">추가</a>
			<a class="btn btn-outline-primary btn-sm" href="javascript:$('#userInfoItemTable').deleteRow();" role="button">삭제</a>
	</div>

			<table id="userInfoItemTable" class="table">
				<tr>
					<th scope="col" width="20px"/></th>
					<th scope="col" width="60px"/>순서</th>
					<th scope="col" width="80px">회사</th>
					<th scope="col" width="250px"/>프로젝트명</th>
					<th scope="col" width="80px">년도</th>
					<th scope="col" width="300px">모듈</th>
					<th scope="col" >설명</th>
				</tr>

				<tbody id="userInfoItemTbody">
					<c:if test="${not empty userVo.userItemList}">
					   <c:forEach var="routeVo" items ="${userVo.userItemList}" varStatus="idx">
							<tr>
								<td>
									<input type="checkbox" name="checkBoxList" class="checkbox" />
									<input type="hidden" name="rowFlag" value="C"/>
									<input type="hidden" name="id" value="${routeVo.id}"/>
									<input type="hidden" name="userInfoId" value="${routeVo.userInfoId}"/>
					             </td>
								<td>
									<input type="text" name="displayOrder" value="${routeVo.displayOrder}" title="순서" class="form-control required" />
								</td>
								<td>
									<select name="projectCompany" class="form-control" value="${routeVo.projectCompany}"  onfocus="selectRowObj=this;">
										<option value=""></option>
										<option value="삼성" <c:if test="${'삼성' eq routeVo.projectCompany}">selected</c:if> > 삼성 </option>
										<option value="엘지" <c:if test="${'엘지' eq routeVo.projectCompany}">selected</c:if>>엘지</option>
										<option value="현대" <c:if test="${'현대' eq routeVo.projectCompany}">selected</c:if>>현대</option>
									</select>
								</td>
								<td>
									<input type="text" name="projectName" value="${routeVo.projectName}" title="프로젝트명" class="form-control required"/>
								</td>
					            <td>
					            	<input type="text" name="projectYear" value="${routeVo.projectYear}" title="년도" class="form-control required"/>
								</td>
					             <td align="center">
									<div class="form-check"><input type="checkbox" name="strsApplyModule" class="form-check-input" value="project"  <c:if test="${fn:contains(routeVo.applyModule, 'project')}">checked</c:if> /> 과제</div>
									<div class="form-check"><input type="checkbox" name="strsApplyModule" class="form-check-input" value="bom"  <c:if test="${fn:contains(routeVo.applyModule, 'bom')}">checked</c:if> /> BOM</div>
					             </td>
					             <td>
					             	<input type="text" name="description" value="${routeVo.description}" title="설명" class="form-control required" />
								</td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${empty userVo.userItemList}">
					    <tr> <td colspan="7"> 데이터가 없습니다 </td></tr>
					</c:if>


				</tbody>
			</table>


  <button class="btn btn-primary" type="submit">저장</button>
 </form:form>



<script type="text/x-jsrender" id="templateUserInfoItemTr">
<tr>
	<td>
		<input type="checkbox" name="checkBoxList" class="checkbox" />
		<input type="hidden" name="rowFlag" value="I"/>
		<input type="hidden" name="id"/>
		<input type="hidden" name="userInfoId"/>
      </td>
	<td>
		<input type="text" name="displayOrder" title="순서" class="form-control required" />
	</td>
	<td>
		<select name="projectCompany" class="form-control"  onfocus="selectRowObj=this;">
			<option value=""></option>
			<option value="삼성"> 삼성 </option>
			<option value="엘지">엘지</option>
			<option value="현대">현대</option>
		</select>
	</td>
	<td>
		<input type="text" name="projectName" title="프로젝트명" class="form-control required" />
	</td>
    <td>
	<input type="text" name="projectYear" title="년도" class="form-control required"/>
		</td>
     <td>
			<div class="form-check"><input type="checkbox" name="strsApplyModule" value="project" class="form-check-input" /> <label class="form-check-label">과제</label></div>
			<div class="form-check"><input type="checkbox" name="strsApplyModule" value="bom" class="form-check-input" /> <label class="form-check-label">BOM</label></div>
     </td>
     <td>
		<input type="text" name="description" title="설명" class="form-control required" />
	</td>
</tr>
</script>


<c:url var="listURL" value="/user/list"></c:url>
<c:url var="editURL" value="/user/edit"></c:url>
<c:url var="viewURL" value="/user/view"></c:url>

<script>
$( document ).ready(function() {
	//getData();

	$("#userInfoItemTable").find("input").each(function(){
		$(this).on("change",function(){
			var $rowFlagObj = $(this).parents("tr:first").find("input[name='rowFlag']").val("U");
		});
	});

	$("#userForm").on("submit",function (e){
	    e.preventDefault();
	    kangong.user.saveData();
	});

	$("#userId").on("blur",function (e){
	    checkUser();
	});

	$("#passwordId").on("change",function (e){
	    scriptCheck.checkPassword();
	});

	$("#passwordIdConfirm").on("change",function (e){
	    scriptCheck.checkPasswordConfirm();
	});

});

kangong.user = {
	saveData : function(){
					var options = {"strArray":["strsHobby"]};
					var userInfoObj = $("#userInfoDiv").inputToObject(options);
					options = {"loopTagName":"tr", "strArray":["strsApplyModule"]};
					userInfoObj.userItemList = $("#userInfoItemTable").inputToArray(options);
					console.log("userInfoObj:"+JSON.stringify(userInfoObj));
			
				    $.ajax(
				    {
				        type:'post',
				        url:'${pageContext.request.contextPath}/user/saveJson',
				        contentType: "application/json",
				        data: JSON.stringify(userInfoObj),
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
				        	 kangong.form.submitPost("${listURL}", {});
				             //alert(result);
				        }
				    });
			
	},
	checkUser : function() {
					var userId = $("#userId").val();
					$.ajax({
						url: "${pageContext.request.contextPath}/user/checkUserId?userId="+userId,
						type: "post",
						//data: formData,
						dataType: "json",
						contentType: "application/json",
						success: function(userData) {
								console.log("userData: "+JSON.stringify(userData));
								var $userIdObj = $("#userId");
								var $userIdFeedbackObj = $("#userIdFeedback");

								if(userData != "success"){
									$userIdObj.removeClass("is-invalid").addClass("is-valid");
									$userIdFeedbackObj.removeClass("invalid-feedback").addClass("valid-feedback");
									$userIdFeedbackObj.html("");
								}else{
									$userIdObj.removeClass("is-valid").addClass("is-invalid");
									$userIdFeedbackObj.removeClass("valid-feedback").addClass("invalid-feedback");
									$userIdFeedbackObj.html("이미 존재하는 UserId 입니다");
								}
							},
						error: function(errorThrown) {
							alert(errorThrown.statusText);
							}
						});
				}
};






$.views.helpers({
	format: function (val, format) {
		switch (format) {
		case "timestamp":
			return moment(val).format("YYYY-MM-DD hh:mm:ss");
		case "date":
			return moment(val).format("YYYY-MM-DD");
		}
	}
});


var scriptCheck = {
	checkPassword : function(passwordId){
		if(kangong.check.isNull(passwordId)) passwordId = "passwordId";
		 var $passwordObj = $("#"+passwordId);
		 var $passwordFeedbackObj = $("#"+passwordId+"Feedback");
		 var $passwordConfirmObj = $("#"+passwordId+"Confirm");
		 var $passwordConfirmFeedbackObj = $("#"+passwordId+"ConfirmFeedback");

		 var pw = $passwordObj.val();
		 var num = pw.search(/[0-9]/g);
		 var eng = pw.search(/[a-z]/ig);
		 var spe = pw.search(/[`~!@@#$%^&*|₩₩₩'₩";:₩/?]/gi);

		 var checkFlag = true;
		 var resultMessage = "";
		 if(pw.length < 10 || pw.length > 20){
			  resultMessage = "10자리 ~ 20자리 이내로 입력해주세요."
			  checkFlag = false;
		 }else if(pw.search(/\s/) != -1){
			  resultMessage = "비밀번호는 공백 없이 입력해주세요."
			  checkFlag = false;
		 }else if( (num < 0 && eng < 0) || (eng < 0 && spe < 0) || (spe < 0 && num < 0) ){
			  resultMessage = "영문,숫자, 특수문자 중 2가지 이상을 혼합하여 입력해주세요."
			  checkFlag = false;
		 }

		 if(checkFlag){
			 $passwordObj.removeClass("is-invalid").addClass("is-valid");
			 $passwordFeedbackObj.removeClass("invalid-feedback").addClass("valid-feedback");
			 $passwordFeedbackObj.html("");
		 }else{
			 $passwordObj.removeClass("is-valid").addClass("is-invalid");
			 $passwordFeedbackObj.removeClass("valid-feedback").addClass("invalid-feedback");
			 $passwordFeedbackObj.html(resultMessage);
		 }

		 //password confirm 초기화
		 $passwordConfirmObj.val("");
		 $passwordConfirmObj.removeClass("is-invalid").removeClass("is-valid");
		 $passwordConfirmFeedbackObj.attr("class","");
		 $passwordConfirmFeedbackObj.html("");
	},
   checkPasswordConfirm: function(passwordId)	{
	   if(kangong.check.isNull(passwordId)) passwordId = "passwordId";
	   var $passwordObj = $("#"+passwordId);
	   var $passwordConfirmObj = $("#"+passwordId+"Confirm");
	   var $passwordConfirmFeedbackObj = $("#"+passwordId+"ConfirmFeedback");

	   if($passwordObj.val() == $passwordConfirmObj.val()){
		    $passwordConfirmObj.removeClass("is-invalid").addClass("is-valid");
			$passwordConfirmFeedbackObj.removeClass("invalid-feedback").addClass("valid-feedback");
			$passwordConfirmFeedbackObj.html("");
	   }else{
		   $passwordConfirmObj.removeClass("is-valid").addClass("is-invalid");
			$passwordConfirmFeedbackObj.removeClass("valid-feedback").addClass("invalid-feedback");
			$passwordConfirmFeedbackObj.html("패스워드가 일치하지 않습니다");
	   }
   }
};




</script>