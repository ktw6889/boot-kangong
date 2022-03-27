package com.kangong.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kangong.board.model.BoardReplyVO;
import com.kangong.board.service.BoardService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "/restBoard")
public class RestBoardController {

	@Autowired
	private BoardService boardService;

	@RequestMapping(value = "/listReply", method = RequestMethod.POST)
	public List<BoardReplyVO> getReplyList(@RequestParam("boardId") String boardId) throws Exception {
		return boardService.getReplyList(boardId);
	}

	@RequestMapping(value = "/saveReply", method = RequestMethod.POST)
	public Map<String, Object> saveReply(@RequestBody BoardReplyVO replyVO) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			boardService.saveReply(replyVO);
			result.put("status", "OK");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "False");
		}

		return result;
	}

	@RequestMapping(value = "/deleteReply", method = RequestMethod.POST)
	public Map<String, Object> deleteReply(@RequestParam("id") String id) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			boardService.deleteReply(id);
			result.put("status", "OK");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "False");
		}

		return result;
	}
}
