package com.kangong.common.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomLogoutHandler implements LogoutHandler {

    public CustomLogoutHandler() {
    	log.info("new CustomLogoutHandler");
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    	log.info("logout Handler");
    }
}