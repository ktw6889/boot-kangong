<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jsrender-1.0.7.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
<script src="https://www.jsviews.com/download/jsviews.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/com/kangong.js"></script>


<script type="text/x-jsrender" id="templateUserInfo">
	<div class="form-row">
		<div class="col-md-4 mb-3">
		  <label for="userId">사용자 ID</label>
		  <input type="text" class="form-control" id="userId" name="userId" value="{{:userId}}" placeholder="abcd@naver.com" required>
		   <div id="userIdFeedback" class="valid-feedback">
		  </div>
		</div>
		<div class="col-md-4 mb-3">
      		<label for="passwordId">Password</label>
      		<input type="password" class="form-control" id="passwordId" name="password" value="" required>
      		<div id="passwordIdFeedback" class="valid-feedback">
      		</div>
    	</div>
    	<div class="col-md-4 mb-3">
      		<label for="passwordConfirmId">Password Confirm</label>
      		<input type="password" class="form-control" id="passwordConfirmId" value="" required>
      		<div id="passwordConfirmIdFeedback" class="invalid-feedback">
      		</div>
    	</div>
	</div>

	<div class="form-row">
    <div class="col-md-4 mb-3">
      <label for="userNameId">이름</label>
      <input type="text" class="form-control" id="userNameId" name="userName" maxlength="5" placeholder="홍길동" value="{{:userName}}"  required>
      <!-- pattern = "[0-9]{2}-[0-9]{2}"  -->
      <div id="ageIdFeedback" class="invalid-feedback">
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <label for="ageId">Age</label>
      <input type="number" class="form-control" id="ageId" name="age" aria-describedby="ageId" max="100" value="{{:age}}" required>
      <div id="ageIdFeedback" class="invalid-feedback">
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <label for="birthDateId">생일</label>
      <input type="date" class="form-control" id="birthDateId" name="birthDate" min = "1950-07-01" max = "2020-08-15" value="{{>~format(birthDate, "date")}}" required>
      <div id="birthDateIdFeedback" class="invalid-feedback">
      </div>
    </div>
  </div>

  <div class="form-row">
    <div class="col-md-4 mb-3">
      <label for="cityId">도시</label>
      <select class="custom-select is-invalid" id="cityId" name="city"  data-link="city" aria-describedby="cityIdFeedback" required>
        <option selected disabled value="">Choose...</option>
        <option value="seoul">서울</option>
        <option value="keongki">경기</option>
        <option value="busan">부산</option>
        <option value="suwon">수원</option>
      </select>
      <div id="cityIdFeedback" class="invalid-feedback">
        Please select a valid state.  {{:city}}
      </div>
    </div>
	<div class="col-md-4 mb-3">
	    <fieldset class="form-group">
	    <div class="row">
	      <legend class="col-form-label col-sm-2 pt-0">성별</legend>
	      <div class="col-sm-20">
			{^{radiogroup gender}}
	        <div class="form-check">
	          <input class="form-check-input form-check-inline" type="radio" name="gender" id="genderId1" value="male">
	          <label class="form-check-label" for="gender1">
	            	남
	          </label>
	        </div>
	        <div class="form-check">
	          <input class="form-check-input form-check-inline" type="radio" name="gender" id="genderId2" value="female">
	          <label class="form-check-label" for="gender2">
	            	여
	          </label>
	        </div>
			 {{/radiogroup}}
	      </div>
	    </div>
	  </fieldset>
	</div>

     <div class="col-md-4 mb-3">
	 {^{checkboxgroup strsHobby}}
     <label for="hobbyId">취미</label>
      <div class="form-check form-check-inline">
	  <input class="form-check-input" type="checkbox" id="inlineCheckbox1" name="hobby" value="book">
	  <label class="form-check-label" for="inlineCheckbox1">독서</label>
	</div>
	<div class="form-check form-check-inline">
	  <input class="form-check-input" type="checkbox" id="inlineCheckbox2" name="hobby" value="music">
	  <label class="form-check-label" for="inlineCheckbox2">음악감상</label>
	</div>
	<div class="form-check form-check-inline">
	  <input class="form-check-input" type="checkbox" id="inlineCheckbox3" name="hobby" value="game">
	  <label class="form-check-label" for="inlineCheckbox3">게임</label>
	</div>
      <div id="hobbyIdFeedback" class="invalid-feedback">
      </div>
	 {{/checkboxgroup}}
    </div>

  </div>

  <div class="form-row">
    <div class="col-md-12 mb-3">
        <label for="commentId">Comment</label>
       <textarea class="form-control" id="commentId" name="comment" cols = "50" rows = "5" placeholder="가입인사를 남겨주세요."> {{:comment}}  </textarea>
    </div>
  </div>
</script>


<script type="text/x-jsrender" id="templateUserInfoItem">

<table id="add_table" class="table">
				<tr>
					<th scope="col"/></th>
					<th scope="col" width="40px"/>순서</th>
					<th scope="col" width="80px">회사</th>
					<th scope="col" width="250px"/>프로젝트명</th>
					<th scope="col" width="80px">년도</th>
					<th scope="col" width="300px">모듈</th>
					<th scope="col" width="300px">설명</th>
				</tr>

				<tbody id="blockTbody">
					{{if userItemList}}
					   {{for userItemList}}
								{{include tmpl="#templateUserInfoItemTr"/}}
						{{/for}}
					{{else}}
						{{include tmpl="#templateUserInfoItemTr"/}}
					{{/if}}
				</tbody>
</table>
</script>

<script type="text/x-jsrender" id="templateUserInfoItemTr">
<tr>
	<td>
		<input type="checkbox" name="checkBoxList" class="checkbox" />
		<input type="hidden" name="rowFlag" value="C"/>
		<input type="hidden" name="id" value="{{:id}}"/>
		<input type="hidden" name="userInfoId" value="{{:userInfoId}}"/>
      </td>
	<td>
		<input type="text" name="displayOrder" value="{{:displayOrder}}" title="순서" class="inputmax required" />
	</td>
	<td>
		<select name="projectCompany" class="selectmax" data-link="projectCompany"  onfocus="selectRowObj=this;">
			<option value=""></option>
			<option value="삼성"> 삼성 </option>
			<option value="엘지">엘지</option>
			<option value="현대">현대</option>
		</select>
	</td>
	<td>
		<input type="text" name="projectName" value="{{:projectName}}" title="프로젝트명" class="inputmax required" />
	</td>
    <td>
	<input type="text" name="projectYear" value="{{:projectYear}}" title="년도" class="inputmax required"/>
		</td>
     <td align="center">
			<input type="checkbox" name="applyModule" class="checkbox" {{if applyModule }}checked{{/if}} /> 과제
     </td>
     <td>
	<input type="text" name="description" value="{{:description}}" title="설명" class="inputmax required" />
		</td>
</tr>
</script>

<form id="userForm" name="userForm"  action="#" method="post">
<div id="userInfo"></div>

<div id="userInfoItem"></div>

<button class="btn btn-primary" type="submit">저장</button>
</form>
<script>

$( document ).ready(function() {

	getData();

	$("#userForm").on("submit",function (e)
			{
			    e.preventDefault();

			    //var formData = $(this).serialize();

			    var userInfoObj = $("#userInfoDiv").inputToObject();
				userInfoObj.userItemList = $("#userInfoItemDiv").inputToArray("tr");

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
			             //alert(result);
			        }
			    });
			});

});


function getData() {
	$.ajax({
		url: "${pageContext.request.contextPath}/user/userInfoJson?id=9",
		type: "post",
		//data: formData,
		dataType: "json",
		contentType: "application/json",
		success: function(userData) {
				var templateUserInfo = $.templates("#templateUserInfo");
				var htmlUserInfo = templateUserInfo.render(userData);
				$("#userInfo").html(htmlUserInfo);
				templateUserInfo.link("#userInfo", userData);

				var templateUserInfoItem = $.templates("#templateUserInfoItem");
				var htmlUserInfoItem = templateUserInfoItem.render(userData);
				$("#userInfoItem").html(htmlUserInfoItem);
				htmlUserInfoItem.link("#userInfoItem", userData);
			},
		error: function(errorThrown) {
			alert(errorThrown.statusText);
			}
		});
}

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




</script>