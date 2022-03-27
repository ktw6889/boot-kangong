package com.kangong.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kangong.user.model.UserVO;
import com.kangong.user.service.UserService;

import lombok.extern.log4j.Log4j2;

@RequestMapping("/user/simple")
@Log4j2
@Controller
public class UserSimpleController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/edit")
	public String userEdit(Model model, UserVO userVo) throws Exception {
		log.info("userEdit");
		UserVO resultVo = userService.getUserInfo(userVo);

		model.addAttribute("userVo", resultVo);
		return "kims:/user/simple/userEdit";
	}
	
	
	@RequestMapping(value = "/saveJson")
	public String userSaveJson(@RequestBody UserVO userVo) throws Exception {
		log.info("userEditJson:" + userVo.getUserName() );
		userService.saveUserSimple(userVo);
		return "forward:/user/simple/edit";
	}
	
	@RequestMapping(value = "/save")
	public String userSave(UserVO userVo) throws Exception {
		log.info("userSave");
		userService.saveUserSimple(userVo);
		return "forward:/user/simple/edit";
	}

}
