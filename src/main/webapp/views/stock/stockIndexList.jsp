<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<div class="stock-layout">
<%@ include file="/views/stock/include/stockLeftMenu.jsp" %>
<div class="stock-content">

    <h5>시장지표</h5>
    <hr/>

    <form:form id="userForm" name="userForm" modelAttribute="userList" action="#" method="post">
    <table class="table table-bordered table-hover table-sm" style="font-size:13px;">
      <thead class="thead-dark">
        <tr>
          <th>Type</th>
          <th>YMD</th>
          <th>금리(한국)</th>
          <th>금리(미국)</th>
          <th>금</th>
          <th>은</th>
          <th>구리</th>
          <th>중국컨테이너운임</th>
          <th>미국국채10년</th>
          <th>한국국채10년</th>
          <th>WTI원유</th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
            <c:when test="${fn:length(indexList) > 0}">
                <c:forEach items="${indexList}" var="row">
                    <tr>
                        <td>${row.indexType}</td>
                        <td>${row.yyyymmdd}</td>
                        <td>${row.standardInterestKr}</td>
                        <td>${row.standardInterestUs}</td>
                        <td>${row.metalGc}</td>
                        <td>${row.metalSi}</td>
                        <td>${row.metalHg}</td>
                        <td>${row.transportCcf}</td>
                        <td>${row.bondUs10yt}</td>
                        <td>${row.bondKr10yt}</td>
                        <td>${row.energyCl}</td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr>
                    <td colspan="11" class="text-center">조회된 결과가 없습니다.</td>
                </tr>
            </c:otherwise>
        </c:choose>
      </tbody>
    </table>
    </form:form>

</div>
</div>
