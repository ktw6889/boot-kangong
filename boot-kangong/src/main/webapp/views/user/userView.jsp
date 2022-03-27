<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>


<form:form id="userForm" name="userForm"  modelAttribute="userVo" action="#" method="post">

<div id="userInfoDiv" >

 <form:hidden path="id" />

  <div class="form-row" >
    <div class="col-md-4 mb-3">
      <label for="userId">사용자 ID</label>
      <c:out value="${userVo.userId}"/>
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

	<script>

	var userInfoObj = {"userId":"abcd1@naver.com","password":"123456789!","userName":"김예린","age":"15","birthDate":"2020-08-13","city":"busan","gender":"female","strsHobby":"music"}


	function saveData(){
		console.dir(userInfoObj);
	    $.ajax(
	    {
	        type:'post',
	        url:'${pageContext.request.contextPath}/user/saveJson',
	        dataType: "json",
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
	}


	$( document ).ready(function() {
		//htmlToObject();
		//htmlToObject2();
		//htmlToObject3();
		//htmlToObject4();

		var options = {"strArray":["hobby"]}

		var userInfoObj = $("#userInfoDiv").inputToObject(options);
		console.log(userInfoObj);

		var options2 = {"loopTagName":"tr"};
		userInfoObj.itemList = $("#userInfoItemDiv").inputToArray(options2);
		console.log("userInfoObj:"+userInfoObj);
		console.log("userInfoObj stringify:"+JSON.stringify(userInfoObj));
		//console.log("userInfoObj stringify parse:"+JSON.parse(JSON.stringify(userInfoObj)));

		//var resultObj = $("#userInfoDiv").inputToObject();
		//console.log(resultObj);
		//var resultArray = $("#userInfoItemDiv").inputToArray("tr");
		//console.log(resultArray);

		$("#userForm").on("submit",function (e){
		    e.preventDefault();
		    saveData();
		});

	});



	(function($) {
		$.fn.inputToObject = function(options) {
			var obj = null;
			var arr = $(this).find(":input").checkSelectValue();
			obj = toObject(arr,options);
			return obj;
		};

		$.fn.inputToArray = function(options) {
			var arrayObj = new Array();

			$(this).find(options.loopTagName).each( function(index) {
				var arr =  $(this).find(":input").checkSelectValue();
				var obj = toObject(arr);
				arrayObj.push(obj);
			});

			return arrayObj;
		};

		function toObject(arr, options){
			var obj = {};
			if(arr){
				jQuery.each(arr, function() {
					if(!kangong.check.isNullObject(options) && options.hasOwnProperty("strArray") && options.strArray.indexOf(this.name) > -1 )
					{
						if(obj.hasOwnProperty(this.name)){
							obj[this.name].push(this.value);
						}else{
							obj[this.name] = [this.value];
						}
					}else{
						obj[this.name] = this.value;
					}

				});
			}
			return obj;
		};

		$.fn.checkSelectValue = function(){
			var resultArray = new Array();
			var self = this;
			$(self).each( function(index,item) {
				if($(this).attr("type")=="checkbox" && !$(this).is(":checked") ){
				}else{
					resultArray.push(this);
				}
			});
			return resultArray;
		}
	 }(jQuery));




	function toArray(tableId){
		var arrayObj = new Array();
		var $trObj = $("#"+tableId+" tr");

		$("#"+tableId+" tr").each( function(index) {
			var arr =  $(this).find(":input").serializeArray();
			if(arr){
				obj = {};
				jQuery.each(arr, function() {
					obj[this.name] = this.value;
				});
			}
			arrayObj.push(obj);
		});

		return arrayObj;
	}

	function toObject(paramId){
		var obj = null;
		var arr = $("#"+paramId+" :input").serializeArray();

		if(arr){
			obj = {};
			jQuery.each(arr, function() {
				obj[this.name] = this.value;
			});
		}
		return obj;
	}

	function renameForModelAttribute() {
	    $("#form").each( function (index) {
	        $(this).find("input[name=targetId]").attr("name", "targets[" + index + "].targetId");
	        $(this).find("input[name=targetName]").attr("name", "targets[" + index + "].targetName");
	    });
	}

		function htmlToObject(){
			var aJson = new Object();

			aJson.name = "삽잡이";
			aJson.profile = {engName : "shovelMan", sex : "남", bloodType : "B"};

			var sJson = JSON.stringify(aJson);
			console.log("sJson: "+sJson);
		}

		function htmlToObject2(){

			var aTotalJson = new Object();
			var aSapArray = new Array();
			var aJson = new Object();

			aJson.korName = "삽잡이";
			aJson.engName = "shovelMan";

			aSapArray.push(aJson);

			console.log("aJson:"+JSON.stringify(aJson));
			var aZapArray = new Array();
			aJson = new Object();

			aJson.korName = "김모군";
			aJson.engName = "Kim";

			aZapArray.push(aJson);
			console.log("aZapArray:"+JSON.stringify(aZapArray));

			aTotalJson.sap = aSapArray;
			aTotalJson.zap = aZapArray;


			console.log("aTotalJson: "+JSON.stringify(aTotalJson));
		}

		function htmlToObject3(){
			var aJson = new Object();

			aJson.korName = "삽잡이";
			aJson.engName = "shovelMan";
			aJson.sex = "남";
			aJson.bloodType = "B";

			console.log("html3: "+ JSON.stringify(aJson));
		}

		function htmlToObject4(){
			var aJsonArray = new Array();
			var aJson = new Object();

			aJson.korName = "삽잡이";
			aJson.engName = "shovelMan";
			aJson.sex = "남";
			aJson.bloodType = "B";

			aJsonArray.push(aJson);

			aJson.kroName = "삽돌이";
			aJson.engName = "sapMan";
			aJson.sex = "여";
			aJson.bloodType ="A";

			aJsonArray.push(aJson);

			var sJson = JSON.stringify(aJsonArray);
			console.log("html4: "+ JSON.stringify(sJson));


		}

	</script>
