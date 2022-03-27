package com.kangong.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kangong.board.model.BoardReplyVO;
import com.kangong.board.model.BoardVO;
import com.kangong.board.service.BoardService;
import com.kangong.common.security.model.CustomUser;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class BoardController {


	@Autowired
	private BoardService boardService;

	@RequestMapping(value = {"/board/list","/"})
	public String getBoardList(Model model, BoardVO boardVO
				,@RequestParam(required = false, defaultValue = "1") int page
				,@RequestParam(required = false, defaultValue = "1") int range
			) throws Exception {
		
		// 전체 게시글 개수
		int listCnt = boardService.getBoardListCnt(boardVO);

		boardVO.pageInfo(page, range, listCnt);
		model.addAttribute("pagination", boardVO);
		model.addAttribute("boardList", boardService.selectBoardList(boardVO));
		return "kims:/board/boardList";
	}

	@RequestMapping(value = "/board/view")
	public String getBoardContent(Model model, @RequestParam("id") String id) throws Exception {
		model.addAttribute("boardContent", boardService.selectBoardInfo(id));
		model.addAttribute("replyVO", new BoardReplyVO());
		return "kims:/board/boardView";
	}

	@RequestMapping("/board/edit")
	public String editBoard(Model model, BoardVO boardVO) throws Exception {
		BoardVO resultVo = boardService.selectBoardInfo(boardVO.getId());
		if (resultVo == null || StringUtils.isEmpty(resultVo.getId())) {
			resultVo = new BoardVO();
		}
		model.addAttribute("boardVO", resultVo);
		return "kims:/board/boardEdit";
	}

	@ResponseBody
	@RequestMapping(value = "/board/save")
	public BoardVO saveBoard(BoardVO boardVO) throws Exception {
		BoardVO returnVo = boardService.saveBoard(boardVO);
		return returnVo;
	}

	@RequestMapping(value = "/board/delete")
	public String deleteBoard(@RequestParam("id") String id) throws Exception {
		boardService.deleteBoard(id);
		//return "kims:/board/boardList";
		return "redirect:/board/list";
	}

	@ExceptionHandler(RuntimeException.class)
	public String exceptionHandler(Model model, Exception e) {
		log.info("exception : " + e.getMessage());
		model.addAttribute("exception", e);
		return "kims:/error/exception";
	}

}
