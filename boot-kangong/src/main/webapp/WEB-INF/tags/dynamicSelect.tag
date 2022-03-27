<%@tag import="java.util.Iterator"%>
<%@tag import="java.util.Set"%>
<%@tag import="java.util.Map"%>
<%@ tag dynamic-attributes="optionMap" body-content="empty"   pageEncoding="UTF-8"%>


<%
    Map optionMap = (Map)jspContext.getAttribute("optionMap");
    Set keySet = optionMap.keySet();
    Iterator<String> keys = keySet.iterator();
%>
    <select>

<%
    while (keys.hasNext()) {
        String key = keys.next();
%>
        <option value="<%= key %>"><%= optionMap.get(key)  %></option>
<%
    }
%>
    </select>


