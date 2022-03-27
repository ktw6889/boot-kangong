package com.kangong.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kangong.user.model.UserVO;
import com.kangong.user.service.UserService;

import lombok.extern.log4j.Log4j2;

@RequestMapping("/user")
@Log4j2
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/test")
	public String test() throws Exception {
		log.info("id: " + userService.getNewId("ST_USER_INFO_ITEM"));
		return "/admin/admin";
	}

	@RequestMapping(value = "/view")
	public String userView(Model model, UserVO userVo) throws Exception {
		log.info("userEdit");
		UserVO resultVo = userService.getUserInfo(userVo);

		model.addAttribute("userVo", resultVo);
		return "kims:/user/userView";
	}

	@RequestMapping(value = "/edit")
	public String userEdit(Model model, UserVO userVo) throws Exception {
		log.info("userEdit");
		UserVO resultVo = userService.getUserInfo(userVo);

		model.addAttribute("userVo", resultVo);
		return "kims:/user/userEdit";
	}

	@RequestMapping(value = "/list")
	public String userList(Model model, UserVO userVo) throws Exception {
		log.info("userList");
		List<UserVO> resultList = userService.getUserList(userVo);

		model.addAttribute("userList", resultList);
		return "kims:/user/userList";
	}

	@RequestMapping(value = "/save")
	public String userSave(UserVO userVo) throws Exception {
		log.info("userSave");
		userService.saveUser(userVo);
		return "forward:/user/edit";
	}

	@ResponseBody
	@RequestMapping(value = "/userInfoJson")
	public UserVO userInfoJson(Model model, UserVO userVo) throws Exception {
		log.info("userInfoJson");
		UserVO resultVo = userService.getUserInfo(userVo);
		model.addAttribute("userVo", resultVo);
		return resultVo;
	}

	@ResponseBody
	@RequestMapping(value = "/checkUserId")
	public UserVO checkUserId(Model model, UserVO userVo) throws Exception {
		log.info("checkUserId");
		String resultMsg = "";
		List<UserVO> resultList = userService.getUserList(userVo);
		if (resultList.size() == 1)
			resultMsg = "success";
		userVo.setUserId(resultMsg);
		return userVo;
	}

	@RequestMapping(value = "/editJson")
	public String userEditJson(Model model, UserVO userVo) throws Exception {
		log.info("userEditJson");
		return "kims:/user/userEditJson";
	}

	@RequestMapping(value = "/saveJson")
	public String userSaveJson(@RequestBody UserVO userVo) throws Exception {
		log.info("userEditJson:" + userVo.getUserName() + ":" + userVo.getUserItemList().size());
		userService.saveUser(userVo);
		return "forward:/user/edit";
	}

}
