<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ include file="/views/include/include-header.jsp" %>

<!DOCTYPE html>
<html>
<head>
	<title>강공 :: Home</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <base href="${pageContext.request.contextPath}/">
</head>
<body class="wrapper">

        <tiles:insertAttribute name="header"/>
        <!-- tiles:insertAttribute name="menu" / -->
        <div class="m-3">
        <tiles:insertAttribute name="body" />
        </div>
        <tiles:insertAttribute name="footer" />
</body>
</html>

