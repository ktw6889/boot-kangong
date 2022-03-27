<%@ tag body-content="empty"  pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
<%@ attribute name="attrName" required="true" rtexprvalue="false"  %>
<%@ attribute name="num1" required="true"  type="java.lang.Integer" %>
<%@ attribute name="num2" required="true"  type="java.lang.Integer" %>
<%@ variable name-from-attribute="attrName" alias="mySumResult"  variable-class="java.lang.Long" scope="AT_END" %>
 
<c:set var="mySumResult" value="${num1 + num2}"/>
