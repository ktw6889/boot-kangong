<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

    <form:form id="userForm" name="userForm"  modelAttribute="userList" action="#" method="post">


	<table class="table">
	  <thead class="thead-light">
	    <tr>
	      <th scope="col">#</th>
	      <th scope="col">사용자명</th>
	      <th scope="col">ID</th>
	      <th scope="col">나이</th>
	      <th scope="col">성별</th>
	      <th scope="col">생일</th>
	      <th scope="col">도시</th>
	      <th scope="col">취미</th>
	      <th scope="col">생성일</th>
	    </tr>
	  </thead>
	  <tbody>
	  	<c:choose>
			<c:when test="${fn:length(userList) > 0}">
				<c:forEach items="${userList }" var="row">
					<tr>
						<td></td>
						<td>${row.userName }</td>
						<td><a href="javascript:kangong.user.open('${row.id}');"> ${row.userId }</a></td>
						<td>${row.age }</td>
						<td>${row.gender }</td>
						<td>${row.birthDate }</td>
						<td>${row.city }</td>
						<td>${row.hobby }</td>
						<td>${row.createDate }</td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="9">조회된 결과가 없습니다.</td>
				</tr>
			</c:otherwise>
		</c:choose>

	  </tbody>
	</table>

</form:form>

<c:url var="listURL" value="/user/list"></c:url>
<c:url var="editURL" value="/user/edit"></c:url>
<c:url var="viewURL" value="/user/view"></c:url>
<script>
kangong.user = {
		open :function (id){
				var paramObj ={};
				paramObj.id =  id;

				kangong.form.submitPost('${editURL}',paramObj);
			}
	}
</script>