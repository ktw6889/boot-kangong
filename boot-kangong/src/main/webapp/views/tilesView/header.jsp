<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

  <style>
  @media (min-width: 768px) {
  .navbar-nav {
    width: 100%;
    text-align: center;
  }
  .navbar-nav > li {
    float: none;
    display: inline-block;
  }
  .navbar-nav > li.navbar-right {
    float: right !important;
  }
}
  </style>

 <div class="jumbotron text-center">
 	<h1>강공 Homepage</h1>
 	<p>블로그 개발 한 번 해봅시다!!!</p>
 </div>

 <nav class="navbar navbar-expand-sm navbar-dark bg-dark">
	 <a href="#" class="navbar-brand">강공</a>
	 <!-- Toggle Button -->
	 <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#collapsibleNavbar">
	 	<span class="navbar-toggler-icon"></span>
	 </button>
	 <div class="collapse navbar-collapse" id="collapsibleNavbar">
		 <ul class="navbar-nav">
			 <li class="nav-item"><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/board/list');" class="nav-link">게시판</a></li>
			 <li class="nav-item"><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/stock');" class="nav-link">증권</a></li>
			 <li class="nav-item"><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/calendar/list');" class="nav-link">일정</a></li>
			 <li class="nav-item"><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/calendar/test');" class="nav-link">일정테스트</a></li>
			<!--   <li class="nav-item"><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/calendar/bootstrapTest');" class="nav-link">부트스트랩테스트</a></li> -->
			 <li class="nav-item"><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/user/list');" class="nav-link">사용자등록</a></li>
			 <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
			 <li class="nav-item"><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/commontable/list');" class="nav-link">공용테이블</a></li>
			 <li class="nav-item "><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/common/dd/list');" class="nav-link">DD</a></li>
			 </sec:authorize>
		 </ul>
	 	  <sec:authorize access="isAuthenticated()">
		  <ul class="nav navbar-nav navbar-right">		  
		  	<!--  
		  	<li class="nav-item"><span class="nav-link"><sec:authentication property="principal.username"/></span>></li>
		  	-->
		   	<li class="nav-item"><a href="javascript:kangong.form.submitPost('${pageContext.request.contextPath}/security/customLogout');" class="nav-link">로그아웃</a></li>	
		 </ul>
		</sec:authorize>
	 </div>
 </nav>

