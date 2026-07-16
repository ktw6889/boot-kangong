package com.kangong.common.security.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.kangong.common.security.SecurityConstants.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
			throws IOException, ServletException {
		log.warn("Login Success");
		List<String> roleNames = new ArrayList<>();
		auth.getAuthorities().forEach(authority -> {
			roleNames.add(authority.getAuthority());
		});

		log.warn("ROLE NAMES: " + roleNames);
		if (roleNames.contains(ROLE_ADMIN)) {
			//response.sendRedirect("/security/admin");
			response.sendRedirect(request.getContextPath()+"/stock");
			return;
		}

		if (roleNames.contains(ROLE_MEMBER)) {
			//response.sendRedirect("/security/member");
			response.sendRedirect(request.getContextPath()+"/stock");
			return;
		}

		response.sendRedirect("/");
	}
}
