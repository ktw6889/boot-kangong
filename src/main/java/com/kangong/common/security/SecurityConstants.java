package com.kangong.common.security;

public final class SecurityConstants {

	private SecurityConstants() {}

	// ==================== 역할 ====================

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_MEMBER = "ROLE_MEMBER";

	public static final String HAS_ROLE_ADMIN = "hasRole('" + ROLE_ADMIN + "')";
	public static final String HAS_ROLE_MEMBER = "hasRole('" + ROLE_MEMBER + "')";
	public static final String HAS_ANY_ROLE_ADMIN_MEMBER = "hasAnyRole('" + ROLE_ADMIN + "','" + ROLE_MEMBER + "')";

	// ==================== URL 경로 ====================

	public static final String LOGIN_PAGE = "/security/customLogin";
	public static final String LOGIN_PROCESSING_URL = "/login";
	public static final String LOGOUT_URL = "/security/customLogout";

	public static final String[] PUBLIC_URLS = {
		"/security/all", "/hello", "/main", "/jsonList", "/util/*",
		"/stock/**", "/stock2/**", "/advstock/**", "/stockMobile/**", "/retire/**", "/marketcycle/**",
		"/webjars/**", "/js/**", "/css/**", "/images/**", "/error"
	};

	public static final String[] ADMIN_URLS = {
		"/security/admin", "/commontable/**", "/common/dd/**"
	};

	public static final String[] MEMBER_URLS = {
		"/*", "/security/member", "/user/**", "/board/**", "/calendar/**",
		"/restBoard/**", "/guestbook/**"
	};

	// ==================== 쿠키 ====================

	public static final String[] LOGOUT_COOKIES = { "remember-me", "JSESSION_ID" };
}
