<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />

<div class="board-container">

	<div class="board-header">
		<h2>게시글 조회</h2>
	</div>

	<div class="board-view-card">
		<div class="board-view-title"><c:out value="${boardContent.title}"/></div>
		<div class="board-view-meta">
			<span><c:out value="${boardContent.createUser}"/></span>
			<span><c:out value="${boardContent.createDate}"/></span>
		</div>
		<div class="board-view-body">${boardContent.content}</div>
		<c:if test="${not empty boardContent.tag}">
			<div class="board-view-tag">
				TAG <span class="tag-label"><c:out value="${boardContent.tag}"/></span>
			</div>
		</c:if>
	</div>

	<div class="board-view-actions">
		<button type="button" class="btn btn-board-primary" id="btnUpdate">수정</button>
		<button type="button" class="btn btn-board-danger" id="btnDelete">삭제</button>
		<button type="button" class="btn btn-board-outline" id="btnList">목록</button>
	</div>

	<!-- Reply Section -->
	<div class="board-reply-section">
		<div class="board-reply-form">
			<h6>댓글 작성</h6>
			<form:form name="form" id="form" role="form" modelAttribute="replyVO" method="post">
			<form:hidden path="boardId" id="boardId"/>
			<form:hidden path="id" id="replyId"/>
			<div class="row">
				<div class="col-sm-10">
					<form:textarea path="content" id="replyContentId" class="form-control" rows="3" placeholder="댓글을 입력해 주세요"></form:textarea>
				</div>
				<div class="col-sm-2 d-flex flex-column">
					<input type="text" readonly class="form-control-plaintext" style="background:#f8f9fa; padding:6px 12px; border-radius:6px; font-size:0.85rem;" name="createUserReadonly" id="replyCreateUserId" value='<sec:authentication property="principal.member.userName"/>'/>
					<button type="button" class="btn btn-board-primary mt-2" id="btnReplySave" style="width:100%">등록</button>
				</div>
			</div>
			</form:form>
		</div>

		<div class="board-reply-list">
			<h6>댓글 목록</h6>
			<div id="replyList"></div>
		</div>
	</div>

</div>

<script type="text/x-jsrender" id="tmplBoardReply">
	<div class="reply-item" id="replyId{{:id}}">
		<div class="reply-avatar">{{:createUser.substring(0,1).toUpperCase()}}</div>
		<div class="reply-body">
			<div class="reply-header">
				<span class="reply-author">{{:createUser}}</span>
				<span class="reply-actions">
					<a href="javascript:void(0)" onclick="kangong.board.editReply('{{:id}}', '{{:createUser}}', '{{:content}}')">수정</a>
					<a href="javascript:void(0)" onclick="kangong.board.deleteReply('{{:id}}')">삭제</a>
				</span>
			</div>
			<div class="reply-content">{{:content}}</div>
		</div>
	</div>
</script>

<script type="text/x-jsrender" id="tmplBoardReplyEdit">
	<div class="reply-item" id="replyId{{:id}}">
		<div class="reply-avatar">{{:createUser.substring(0,1).toUpperCase()}}</div>
		<div class="reply-body">
			<div class="reply-header">
				<span class="reply-author">{{:createUser}}</span>
				<span class="reply-actions">
					<a href="javascript:void(0)" onclick="kangong.board.updateReply('{{:id}}', '{{:createUser}}')">저장</a>
					<a href="javascript:void(0)" onclick="kangong.board.listReply()">취소</a>
				</span>
			</div>
			<textarea name="editContent" id="editContent" class="form-control mt-1" rows="3">{{:content}}</textarea>
		</div>
	</div>
</script>

<c:url var="listURL" value="/board/list"></c:url>
<c:url var="editURL" value="/board/edit"></c:url>
<c:url var="deleteURL" value="/board/delete"></c:url>
<c:url var="saveReplyURL" value="/restBoard/saveReply"></c:url>
<c:url var="deleteReplyURL" value="/restBoard/deleteReply"></c:url>
<c:url var="listReplyURL" value="/restBoard/listReply"></c:url>

<script>
$( document ).ready(function() {
	kangong.board.listReply();

	$('#btnList').on('click', function(){
		kangong.form.submitPost("${listURL}");
	});

	$('#btnUpdate').on('click', function(){
		var paramObj = {};
        paramObj.id = ${boardContent.id};
        paramObj.mode = "edit";
		kangong.form.submitPost("${editURL}",paramObj);
	});

	$('#btnDelete').on('click',function(){
		if(!confirm('정말 삭제하시겠습니까?')) return;
	    var paramObj = {};
	    paramObj.id = ${boardContent.id};
	    kangong.form.submitPost("${deleteURL}",paramObj);
	});

	$("#btnReplySave").on('click', function(){
		kangong.board.saveReply();
	});
});


kangong.board = {
	editReply : function(id, createUser, content){
			var result = {};
			result.id = id;
			result.createUser = createUser;
			result.content = content;

			var tmplBoardReplyEdit = $.templates("#tmplBoardReplyEdit");
			var htmlBoardReplyEdit = tmplBoardReplyEdit.render(result);

			$('#replyId' + id).replaceWith(htmlBoardReplyEdit);
			$('#replyId' + id + ' #editContent').focus();
		},

	saveReply : function (){
			var replyContent = $('#replyContentId').val();
			var replyUser = $('#replyCreateUserId').val();

			var paramData = JSON.stringify({
					"content": replyContent
					, "createUser": replyUser
					, "boardId":'${boardContent.id}'
			});

			var headers = {"Content-Type" : "application/json", "X-HTTP-Method-Override" : "POST"};

			$.ajax({
				url: "${saveReplyURL}"
				, headers : headers
				, data : paramData
				, type : 'POST'
				, dataType : 'text'
				, success: function(result){
					kangong.board.listReply();
					$('#replyContentId').val('');
				}
				, error: function(error){
					console.log("에러 : " + error);
				}
			});
	},

	updateReply : function (id, createUser){
			var replyEditContent = $('#editContent').val();
			var paramData = JSON.stringify({
					"content": replyEditContent
					, "id": id
					, "cerateUser": createUser
			});

			var headers = {"Content-Type" : "application/json", "X-HTTP-Method-Override" : "POST"};
			$.ajax({
				url: "${saveReplyURL}"
				, headers : headers
				, data : paramData
				, type : 'POST'
				, dataType : 'text'
				, success: function(result){
		            kangong.board.listReply();
				}
				, error: function(error){
					console.log("에러 : " + error);
				}
			});
		},

	deleteReply : function (id){
		if(!confirm('댓글을 삭제하시겠습니까?')) return;
		var paramData = {"id": id};

		$.ajax({
			url: "${deleteReplyURL}"
			, data : paramData
			, type : 'POST'
			, dataType : 'text'
			, success: function(result){
				kangong.board.listReply();
			}
			, error: function(error){
				console.log("에러 : " + error);
			}
		});
	},

	listReply : function(){
		var url = "${listReplyURL}";
		var paramData = {"boardId" : "${boardContent.id}"};
		$.ajax({
	       type: 'POST',
	       url: url,
	       data: paramData,
	       dataType: 'json',
	       success: function(result) {
	          	var htmlBoardReply = "";
			if(result.length < 1){
				htmlBoardReply = '<div class="reply-empty">등록된 댓글이 없습니다.</div>';
			} else {
				var tmplBoardReply = $.templates("#tmplBoardReply");
				htmlBoardReply = tmplBoardReply.render(result);
			}
			$("#replyList").html(htmlBoardReply);
	       }
		});
	}
}
</script>
