<%@ tag language="java" body-content="tagdependent"  pageEncoding="UTF-8"%>
<%@ attribute name="color" required="true" %>
<%@ attribute name="iterNum" required="true"  type="java.lang.Integer" %>

<%
    for(int i=0; i < iterNum; i++){
%>
    <span style="background-color: ${color}">
      ♥<jsp:doBody/>♥
    </span>
<%
    }
%>

