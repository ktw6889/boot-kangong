package com.kangong.common.util;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class CommonExceptionAdvice {


	@ExceptionHandler(Exception.class)
	public String errorException(Model model, Exception e) {
		log.info("@ControllerAdvice 방식 \n###exeption : " + e);
		try {
			throw new Exception(e);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		model.addAttribute("exception", e);

		return "kims:/error/exception";
	}

}
