<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jquery/3.5.1/jquery.js"></script>

<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Main</title>

</head>
<body>
  Hello World!!   <br>
  
  <kang:kangspan color="yellow" iterNum="5">
       ktw
  </kang:kangspan>
  <br>  
  
  <kangong:tospan color="blueviolet" iterNum="5">
      hello
  </kangong:tospan>
  
  ${kangfunc:out("abc")}; <br> 
  <br>
  ${kangfunc:getTest("ktw")}
  
  <script>
  $( document ).ready(function() {
	    console.log( "ready!" );
	});
  </script>
</body>
</html>