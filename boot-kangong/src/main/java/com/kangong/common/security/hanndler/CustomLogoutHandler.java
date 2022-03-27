package com.kangong.common.security.hanndler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class CustomLogoutHandler implements LogoutHandler {

    public CustomLogoutHandler() {
    	System.out.println("new CustomLogoutHandler");
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, 
      Authentication authentication) {
    	System.out.println("logout Handler");
    }
}