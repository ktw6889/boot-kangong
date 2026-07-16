<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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

        <c:import url="/views/tilesView/header.jsp"/>
        <div class="m-3">
        <c:import url="${bodyPath}"/>
        </div>
        <c:import url="/views/tilesView/footer.jsp"/>
</body>
</html>
