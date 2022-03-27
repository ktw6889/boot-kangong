<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/com/AddTable.js"></script>

<script>

var addTableRowLength = ${fn:length(userVo.userItemList)};

$( document ).ready(function() {
	if($("#blockTbody tr").val() != ""){
		addTableRowList('add_table',addTableRowLength++);
	}

	$("#userForm").on("submit",function (e)
			{
			    e.preventDefault();
			    $("#templateTbody").remove();
			    var formData = $(this).serialize();
			    $.ajax(
			    {
			        type:'post',
			        url:'${pageContext.request.contextPath}/user/save',
			        data:formData,
			        beforeSend:function()
			        {
			            launchpreloader();
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

function launchpreloader(){
	console.log($("#userId").val());
}
</script>

  <form:form id="userForm" name="userForm"  modelAttribute="userVo" action="#" method="post">

  <form:hidden path="id" />

  <div class="form-row">
    <div class="col-md-4 mb-3">
      <label for="userId">사용자 ID</label>

	  <form:input id="userId" path="userId" class="form-control" placeholder="abcd@naver.com" required="true" />
	  <!--
      <input type="text" class="form-control" id="userId" name="userId" value="<c:out value='${userVo.userId}'/>" placeholder="abcd@naver.com" required>
       -->
      <div id="userIdFeedback" class="valid-feedback">
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <label for="passwordId">Password</label>
      <input type="password" class="form-control" id="passwordId" name="password" value="1234" required>
      <div id="passwordIdFeedback" class="valid-feedback">
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <label for="passwordConfirmId">Password Confirm</label>
      <input type="password" class="form-control" id="passwordConfirmId" value="1234" required>
      <div id="passwordConfirmIdFeedback" class="invalid-feedback">
      </div>
    </div>
  </div>
  <div class="form-row">
    <div class="col-md-4 mb-3">
      <label for="userNameId">이름</label>
      <form:input path="userName" id="userNameId" class="form-control" maxlength="5" placeholder="홍길동" required="true" />
      <!--
      <input type="text" class="form-control" id="userNameId" name="userName" maxlength="5" placeholder="홍길동" value="<c:out value='${userVo.userName}'/>"  required>
      --> <!-- pattern = "[0-9]{2}-[0-9]{2}"  -->
      <div id="ageIdFeedback" class="invalid-feedback">
      </div>
    </div>

    <div class="col-md-4 mb-3">
      <label for="ageId">Age</label>
      <form:input type="number" id="ageId" path="age" class="form-control" max="100" aria-describedby="ageId" required="true" />
      <!--
      <input type="number" class="form-control" id="ageId" name="age" aria-describedby="ageId" max="100" value="<c:out value='${userVo.age}'/>" required>
      -->
      <div id="ageIdFeedback" class="invalid-feedback">
      </div>
    </div>
    <div class="col-md-4 mb-3">
      <label for="birthDayId">생일</label>
      <form:input type="date" id="birthDayId" path="birthDate" class="form-control" min = "1950-07-01" max = "2020-08-15" required="true" />
      <!--
      <input type="date" class="form-control" id="birthDayId" name="birthDate" min = "1950-07-01" max = "2020-08-15" value="1973-12-01" required>
      -->
      <div id="birthDayIdFeedback" class="invalid-feedback">

      </div>
    </div>
  </div>

  <div class="form-row">
    <div class="col-md-4 mb-3">
      <label for="cityId">도시</label>
      <form:select id="cityId" path="city" class="custom-select is-invalid" aria-describedby="cityIdFeedback" required="true">
      	<option selected disabled value="">Choose...</option>
        <form:option value="seoul">서울</form:option>
        <form:option value="keongki">경기</form:option>
        <form:option value="busan">부산</form:option>
        <form:option value="suwon">수원</form:option>
      </form:select>
      <!--
      <select class="custom-select is-invalid" id="cityId" name="city" aria-describedby="cityIdFeedback" required>
        <option selected disabled value="">Choose...</option>
        <option value="seoul">서울</option>
        <option value="keongki">경기</option>
        <option value="busan">부산</option>
        <option value="suwon">수원</option>
      </select>
      -->
      <div id="cityIdFeedback" class="invalid-feedback">
        Please select a valid state.
      </div>
    </div>
	<div class="col-md-4 mb-3">
	    <fieldset class="form-group">
	    <div class="row">
	      <legend class="col-form-label col-sm-2 pt-0">성별</legend>
	      <div class="col-sm-20">
	        <div class="form-check">
	        <form:radiobutton id="genderId1" path="gender" value="male" class="form-check-input form-check-inline" />
	        <!--
	          <input class="form-check-input form-check-inline" type="radio" name="gender" id="genderId1" value="male">
	        -->
	          <label class="form-check-label" for="gender1">
	            	남
	          </label>
	        </div>
	        <div class="form-check">
	        <form:radiobutton id="genderId2" path="gender" class="form-check-input form-check-inline" value="female" />
	        <!--
	          <input class="form-check-input form-check-inline" type="radio" name="gender" id="genderId2" value="female">
	        -->
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
      <!--
	  <input class="form-check-input" type="checkbox" id="inlineCheckbox1" name="hobby" value="book">
	  -->
	  <label class="form-check-label" for="inlineCheckbox1">독서</label>
	</div>
	<div class="form-check form-check-inline">
	  <form:checkbox  id="inlineCheckbox1" path="strsHobby" class="form-check-input" value="music" />
	  <!--
	  <input class="form-check-input" type="checkbox" id="inlineCheckbox2" name="hobby" value="music" checked>
	  -->
	  <label class="form-check-label" for="inlineCheckbox2">음악감상</label>
	</div>
	<div class="form-check form-check-inline">
	  <form:checkbox  id="inlineCheckbox1" path="strsHobby" class="form-check-input" value="game" />
	  <!--
	  <input class="form-check-input" type="checkbox" id="inlineCheckbox3" name="hobby" value="game" checked>
	  -->
	  <label class="form-check-label" for="inlineCheckbox3">게임</label>
	</div>
      <div id="hobbyIdFeedback" class="invalid-feedback">
      </div>
    </div>
  </div>

  <div class="form-row">
    <div class="col-md-12 mb-3">
        <label for="commentId">Comment</label>
       <textarea class="form-control" id="commentId" name="comment" cols = "50" rows = "5" placeholder="가입인사를 남겨주세요.">안녕하세요</textarea>
    </div>
  </div>

  <!--
  <div class="form-group">
    <div class="form-check">
      <input class="form-check-input is-invalid" type="checkbox" value="" id="invalidCheck3" aria-describedby="invalidCheck3Feedback" required>
      <label class="form-check-label" for="invalidCheck3">
        Agree to terms and conditions
      </label>
      <div  id="invalidCheck3Feedback" class="invalid-feedback">
        You must agree before submitting.
      </div>
    </div>
  </div>
  -->



			<a class="btn btn-outline-primary btn-sm" href="javascript:addTableRowList('add_table', addTableRowLength++);" role="button">추가</a>
			<a class="btn btn-outline-primary btn-sm" href="javascript:deleteTableRow('add_table');" role="button">삭제</a>

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

				<tbody id="templateTbody" style="display:none;" disabled >
					<tr>
						<td>
							<input type="checkbox" name="checkBoxList" class="checkbox" />
			                <input type="hidden" name="userItemList[TEMPLATE_TR_NUM].rowFlag" value="I" />
			                <input type="hidden" name="userItemList[TEMPLATE_TR_NUM].id" />
			                <input type="hidden" name="userItemList[TEMPLATE_TR_NUM].userInfoId" />
			             </td>
						<td>
							<input type="text" name="userItemList[TEMPLATE_TR_NUM].displayOrder" title="순서" class="inputmax required"/>
						</td>
						<td>
							<select name="userItemList[TEMPLATE_TR_NUM].projectCompany" class="selectmax" >
								<option value=""></option>
								<option value="삼성">삼성</option>
								<option value="엘지">엘지</option>
								<option value="현대">현대</option>
							</select>
						</td>
						<td>
							<input type="text" name="userItemList[TEMPLATE_TR_NUM].projectName" title="프로젝트명" class="inputmax required"/>
						</td>
			            <td>
							<input type="text" name="userItemList[TEMPLATE_TR_NUM].projectYear" title="년도" class="inputmax required" />
						</td>
			             <td align="center">
							<input type="checkbox" name="userItemList[TEMPLATE_TR_NUM].applyModule" class="checkbox"/>과제
			             </td>
			             <td>
							<input type="text" name="userItemList[TEMPLATE_TR_NUM].description" cssClass="inputmax readOnly"/>
						</td>
					</tr>
				</tbody>

				<tbody id="blockTbody">
					<c:if test="${not empty userVo.userItemList}">
					   <c:forEach var="routeVo" items ="${userVo.userItemList}" varStatus="idx">
							<tr>
								<td>
									<input type="checkbox" name="checkBoxList" class="checkbox" />
									<input type="hidden" name="userItemList[${idx.index}].rowFlag" value="C"/>
									<input type="hidden" name="userItemList[${idx.index}].id" value="${routeVo.id}"/>
									<input type="hidden" name="userItemList[${idx.index}].userInfoId" value="${routeVo.userInfoId}"/>
					             </td>
								<td>
									<input type="text" name="userItemList[${idx.index}].displayOrder" value="${routeVo.displayOrder}" title="순서" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
								<td>
									<select name="userItemList[${idx.index}].projectCompany" class="selectmax" value="${routeVo.projectCompany}" onchange="javascript:updateTableRow(this);"  onfocus="selectRowObj=this;">
										<option value=""></option>
										<option value="삼성" <c:if test="${'삼성' eq routeVo.projectCompany}">selected</c:if> > 삼성 </option>
										<option value="엘지" <c:if test="${'엘지' eq routeVo.projectCompany}">selected</c:if>>엘지</option>
										<option value="현대" <c:if test="${'현대' eq routeVo.projectCompany}">selected</c:if>>현대</option>
									</select>
								</td>
								<td>
									<input type="text" name="userItemList[${idx.index}].projectName" value="${routeVo.projectName}" title="프로젝트명" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
					            <td>
					            	<input type="text" name="userItemList[${idx.index}].projectYear" value="${routeVo.projectYear}" title="년도" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
					             <td align="center">
									<input type="checkbox" name="userItemList[${idx.index}].applyModule" class="checkbox" onchange="javascript:updateTableRow(this);" <c:if test="${'on' eq routeVo.applyModule}">checked</c:if> /> 과제
					             </td>
					             <td>
					             	<input type="text" name="userItemList[${idx.index}].description" value="${routeVo.description}" title="설명" class="inputmax required" onchange="javascript:updateTableRow(this);"/>
								</td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>


  <button class="btn btn-primary" type="submit">저장</button>
  </form:form>
