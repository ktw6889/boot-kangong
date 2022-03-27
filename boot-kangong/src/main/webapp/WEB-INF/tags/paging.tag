<%@ tag language="java" body-content="tagdependent"  pageEncoding="UTF-8"%>

<% try{ %>

<%@ attribute name="url" type="java.lang.String" %>
<%@ attribute name="page" type="java.lang.Integer" %>
<%@ attribute name="range" type="java.lang.Integer" %>
<%@ attribute name="rangeSize" type="java.lang.Integer" %>
<%@ attribute name="startPage" type="java.lang.Integer" %>
<%@ attribute name="endPage" type="java.lang.Integer" %>
<%@ attribute name="next" type="java.lang.Boolean" %>
<%@ attribute name="prev" type="java.lang.Boolean" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/bootstrap/4.5.2/css/bootstrap.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jquery/3.5.1/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jsrender/1.0.0-rc.70/jsrender.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/4.5.2/js/bootstrap.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/com/kangong.js"></script> 

<script>
var url = "${url}";
kangong.paging = {
	//이전 버튼 이벤트
	fn_prev : function(page, range, rangeSize) {
				var page = ((range - 2) * rangeSize) + 1;
				var range = range - 1;

				var paramObj = {};
				paramObj.page = page;
				paramObj.range = range;
				kangong.form.submitPost(url,paramObj);
			},
	//페이지 번호 클릭
	fn_pagination : function(page, range, rangeSize, searchType, keyword) {
			var paramObj = {};
			paramObj.page = page;
			paramObj.range = range;
			console.log("paramObj",paramObj);			
			
			kangong.form.submitPost(url,paramObj);
		},
	//다음 버튼 이벤트
	fn_next :function (page, range, rangeSize) {
			var page = parseInt((range * rangeSize)) + 1;
			var range = parseInt(range) + 1;

			var paramObj = {};
			paramObj.page = page;
			paramObj.range = range;
			
			kangong.form.submitPost(url,paramObj);
		}
}
</script>

<!-- pagination{s} -->
	<div id="paginationBox">
		<ul class="pagination">
			<c:if test="${prev}">
				<li class="page-item"> <a class="page-link" href="javascript:kangong.paging.fn_prev('${page}', '${range}', '${rangeSize}')"> Previous </a> </li>
			</c:if>

			<c:forEach begin="${startPage}" end="${endPage}" var="idx">
				<li class="page-item <c:out value="${page == idx ? 'active' : ''}"/> "> <a class="page-link" href="javascript:kangong.paging.fn_pagination('${idx}', '${range}', '${rangeSize}')" > ${idx} </a></li>
			</c:forEach>

			<c:if test="${next}">
				<li class="page-item"><a class="page-link" href="javascript:kangong.paging.fn_next('${range}', '${range}', '${rangeSize}')"  >Next</a></li>
			</c:if>
		</ul>
	</div>
	<!-- pagination{e} -->

<% }catch(Exception e){
	e.printStackTrace();
} %>