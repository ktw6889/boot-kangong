<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />
<script src="https://cdn.ckeditor.com/ckeditor5/12.0.0/classic/ckeditor.js"></script>

<div class="board-container">

	<div class="board-header">
		<h2><c:choose><c:when test="${not empty boardVO.id}">게시글 수정</c:when><c:otherwise>게시글 등록</c:otherwise></c:choose></h2>
	</div>

	<div class="board-form">
		<form:form name="form" id="form" role="form" method="post" modelAttribute="boardVO" action="${pageContext.request.contextPath}/board/save.do">
		<form:hidden path="id" />
		<input type="hidden" name="mode" />

		<div class="form-group">
			<label for="title">제목</label>
			<form:input path="title" id="title" class="form-control" placeholder="제목을 입력해 주세요" />
		</div>

		<div class="form-group">
			<label>작성자</label>
			<input type="text" readonly class="form-control-plaintext" name="createUserReadonly" value='<sec:authentication property="principal.member.userName"/>'/>
		</div>

		<div class="form-group">
			<label for="content">내용</label>
			<form:textarea path="content" id="content" class="form-control" rows="5" placeholder="내용을 입력해 주세요" />
		</div>

		<div class="form-group">
			<label for="tag">TAG</label>
			<form:input path="tag" id="tag" class="form-control" placeholder="태그를 입력해 주세요" />
		</div>

		</form:form>

		<div class="board-form-actions">
			<button type="button" class="btn btn-board-primary" id="btnSave">저장</button>
			<button type="button" class="btn btn-board-outline" id="btnList">목록</button>
		</div>
	</div>

</div>

<c:url var="listURL" value="/board/list"></c:url>
<c:url var="viewURL" value="/board/view"></c:url>
<c:url var="saveURL" value="/board/save"></c:url>

<script>
var editor;
$( document ).ready(function() {
	ClassicEditor
    .create( document.querySelector( '#content' ) )
    .then( newEditor => {
        editor = newEditor;
    } )
    .catch( error => {
        console.error( error );
    } );

	$('#btnSave').on('click',function(e){
		e.preventDefault();
		kangong.board.save();
	});

	$('#btnList').on('click', function(e){
		e.preventDefault();
		kangong.form.submitPost("${listURL}");
	});
});

kangong.board = {
	save : function(){
		$("#content").val(editor.getData());
		var params = $("#form").serialize();
		$.ajax({
			type:'post',
			url:'${saveURL}',
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			data: params,
			success:function(result){
				kangong.form.submitPost("${listURL}", {});
			}
		});
	}
};
</script>
