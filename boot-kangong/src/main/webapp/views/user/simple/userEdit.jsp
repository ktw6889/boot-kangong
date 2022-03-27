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
    </div>
    <div class="col-md-4 mb-3">      
    </div>
  </div>

</div>


	

  <button class="btn btn-primary" type="submit">저장</button>
 </form:form>





<c:url var="listURL" value="/user/list"></c:url>
<c:url var="editURL" value="/user/edit"></c:url>
<c:url var="viewURL" value="/user/view"></c:url>

<script>
$( document ).ready(function() {
	//getData();
	
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
					var options = {};
					var userInfoObj = $("#userInfoDiv").inputToObject(options);
					console.log("userInfoObj:"+JSON.stringify(userInfoObj));
			
				    $.ajax(
				    {
				        type:'post',
				        url:'${pageContext.request.contextPath}/user/simple/saveJson',
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