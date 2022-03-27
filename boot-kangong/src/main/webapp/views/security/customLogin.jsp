<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    

<style>
.input-group {
    margin-top: 1em;
    margin-bottom: 1em;
}

 

.login-box2 {
    line-height: 2.3em;
    font-size: 1em;
    color: #aaa;
    margin-top: 1em;
    margin-bottom: 1em;
    padding-top: 0.5em;
    padding-bottom: 0.5em;
}

</style>

  <!-- 
  <h1>Login Page</h1>
  <h2><c:out value="${error}"/></h2>
  <h2><c:out value="${logout}"/></h2>
  
  <form method='post' action="/login">
  
  <div>
    <input type='text' name='username' value='member00'>
  </div>
  <div>
    <input type='password' name='password' value='pw00'>
  </div>
  <div>
  <div>
    <input type='checkbox' name='remember-me'> Remember Me
  </div>

  <div>
    <input type='submit'>
  </div>
    <input type="hidden" name="${_csrf.parameterName}"
    value="${_csrf.token}" />
  
  </form>
   -->
  
    <h2><c:out value="${error}"/></h2>
  <h2><c:out value="${logout}"/></h2>
     <div class="container">
            <div class="row">
                <div class="col-sm-3">

                    <div class="login-box well">
                        <form method="post" action="/login">
                            <legend>로그인</legend>
                            <div class="input-group">
                                <span class="input-group-addon"><i class="fa fa-user"></i></span>
                                <input type="text" name="username" id="username" placeholder="E-mail을 입력하세요" class="form-control" value='orktw@naver.com'/>
                            </div>
                            <div class="input-group">
                                <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                                <input type="password" name="password" id="password" placeholder="비밀번호를 입력하세요" class="form-control" value='1234'/>
                            </div>
                             <div>
							    <input type='checkbox' name='remember-me'> Remember Me
							  </div>
                            <div class="d-grid gap-2">
                            <a class="btn btn-primary" id="login" role="button">로그인</a> 
                            <a class="btn btn-primary" id="btnRegiste" role="button">회원가입</a>
                            </div>
                            <span class='text-center'><a href="" class="text-sm">비밀번호 찾기</a></span>
                                <input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}" />
                        </form>
                    </div>

                </div>
            </div>
        </div>
        
<c:url var="registeURL" value="/user/simple/edit"></c:url>
<c:url var="loginURL" value="/login"></c:url>
<script>
$( document ).ready(function() {
	$('#login').on('click',function(e){
		e.preventDefault();
		 kangong.form.submitPost("${loginURL}", {username:$("#username").val(),password:$("#password").val()});
	});
	
	$('#btnRegiste').on('click',function(e){
		e.preventDefault();
		 kangong.form.submitPost("${registeURL}", {});
	});
});

</script>

