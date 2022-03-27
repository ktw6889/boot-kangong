package com.kangong.board.model;

import com.kangong.common.model.CommonVO;

public class BoardReplyVO extends CommonVO{
	private String deleteYn; // 삭제여부
	private String boardId; // 게시물 일련번호
	private String content; // 댓글내용

	public String getDeleteYn() {
		return deleteYn;
	}

	public void setDeleteYn(String deleteYn) {
		this.deleteYn = deleteYn;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
