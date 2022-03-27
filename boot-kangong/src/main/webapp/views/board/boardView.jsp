<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />

	<article>
		<div class="container" role="main">
			<h2>게시판 조회</h2>

			<div class="bg-white rounded shadow-sm">
				<div class="board_title"><c:out value="${boardContent.title}"/></div>
				<div class="board_info_box">
					<span class="board_author"><c:out value="${boardContent.createUser}"/>,</span><span class="board_date"><c:out value="${boardContent.createDate}"/></span>
				</div>
				<div class="board_content">${boardContent.content}</div>
				<div class="board_tag">TAG : <c:out value="${boardContent.tag}"/></div>
			</div>

			<div style="margin-top : 20px">
				<button type="button" class="btn btn-sm btn-primary" id="btnUpdate">수정</button>
				<button type="button" class="btn btn-sm btn-primary" id="btnDelete">삭제</button>
				<button type="button" class="btn btn-sm btn-primary" id="btnList">목록</button>
			</div>

			<!-- Reply Form {s} -->
			<div class="my-3 p-3 bg-white rounded shadow-sm" style="padding-top: 10px">
				<form:form name="form" id="form" role="form" modelAttribute="replyVO" method="post">
				<form:hidden path="boardId" id="boardId"/>
				<form:hidden path="id" id="replyId"/>
				<div class="row">
					<div class="col-sm-10">
						<form:textarea path="content" id="replyContentId" class="form-control" rows="3" placeholder="댓글을 입력해 주세요"></form:textarea>
					</div>
					<div class="col-sm-2">
					    <input type="text" readonly class="form-control" name="createUserReadonly" id="replyCreateUserId" value='<sec:authentication property="principal.member.userName"/>'/>
						
						<button type="button" class="btn btn-sm btn-primary" id="btnReplySave" style="width: 100%; margin-top: 10px"> 저 장 </button>
					</div>
				</div>
				</form:form>
			</div>
			<!-- Reply Form {e} -->

			<!-- Reply List {s}-->
			<div class="my-3 p-3 bg-white rounded shadow-sm" style="padding-top: 10px">
				<h6 class="border-bottom pb-2 mb-0">Reply list</h6>
				<div id="replyList"></div>
			</div>
			<!-- Reply List {e}-->

		</div>
	</article>


<script type="text/x-jsrender" id="tmplBoardReply">
	<div class="media text-muted pt-3" id="replyId{{:id}}">
         <svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder:32x32">
         	<title>Placeholder</title>
            <rect width="100%" height="100%" fill="#007bff"></rect>
            <text x="50%" fill="#007bff" dy=".3em">32x32</text>
         </svg>
         <p class="media-body pb-3 mb-0 small lh-125 border-bottom horder-gray">
            <span class="d-block">
            	<strong class="text-gray-dark">{{:createUser}}</strong>
           		<span style="padding-left: 7px; font-size: 9pt">
                 	<a href="javascript:void(0)" onclick="kangong.board.editReply('{{:id}}', '{{:createUser}}', '{{:content}}' )" style="padding-right:5px">수정</a>
                 	<a href="javascript:void(0)" onclick="kangong.board.deleteReply('{{:id}}')" >삭제</a>
                </span>
            </span>
                   {{:content}}
         </p>
    </div>
</script>

<script type="text/x-jsrender" id="tmplBoardReplyEdit">
	<div class="media text-muted pt-3" id="replyId{{:id}}">
		<svg class="bd-placeholder-img mr-2 rounded" width="32" height="32" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img" aria-label="Placeholder:32x32">
			<title>Placeholder</title>
			<rect width="100%" height="100%" fill="#007bff"></rect>
			<text x="50%" fill="#007bff" dy=".3em">32x32</text>
		</svg>
		<p class="media-body pb-3 mb-0 small lh-125 border-bottom horder-gray">
		<span class="d-block">
			<strong class="text-gray-dark">{{:createUser}}</strong>
			<span style="padding-left: 7px; font-size: 9pt">
				<a href="javascript:void(0)" onclick="kangong.board.updateReply('{{:id}}', '{{:createUser}}')" style="padding-right:5px">저장</a>
				<a href="javascript:void(0)" onClick="kangong.board.listReply()">취소<a>
			</span>
		</span>
		<textarea name="editContent" id="editContent" class="form-control" rows="3">{{:content}}</textarea>
		</p>
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
		var url = "${listURL}";
		kangong.form.submitPost(url);
	});

	//수정 버튼 클릭 이벤트
	$('#btnUpdate').on('click', function(){
		var url = "${editURL}";

		var paramObj = {};
        paramObj.id = ${boardContent.id};
        paramObj.mode = "edit";
		kangong.form.submitPost(url,paramObj);
	});

	//삭제 버튼 클릭 이벤트
	$('#btnDelete').on('click',function(){
	    var url = "${deleteURL}";
	    var paramObj = {};
	    paramObj.id = ${boardContent.id};

	    kangong.form.submitPost(url,paramObj);
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

//댓글 저장
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
					//$('#replyCreateUserId').val('');
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
		            console.log(result);
		            kangong.board.listReply();
				}
				, error: function(error){
					console.log("에러 : " + error);
				}
			});
		},

	deleteReply : function (id){
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
	    	   console.log(result);
	          	var htmlBoardReply = "";
			if(result.length < 1){
				htmlBoardReply += "등록된 댓글이 없습니다.";
			} else {
				var tmplBoardReply = $.templates("#tmplBoardReply");
				var htmlBoardReply = tmplBoardReply.render(result);
			}

			$("#replyList").html(htmlBoardReply);
	       }	   // Ajax success end
		});	// Ajax end
	}
}
</script>

