package com.kangong.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kangong.common.security.model.CustomUser;
import com.kangong.common.security.model.MemberVO;

public class SecurityContextUtil {

	private SecurityContextUtil() {
	}

	public static String getLoginUserId() {
		return getLoginUser().getId();
	}

	public static MemberVO getLoginUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUser userVO = (CustomUser) authentication.getPrincipal();
		return userVO.getMember();
	}
}
