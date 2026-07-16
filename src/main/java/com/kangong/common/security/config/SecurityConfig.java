package com.kangong.common.security.config;

import static com.kangong.common.security.SecurityConstants.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import jakarta.servlet.DispatcherType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.kangong.common.security.handler.CustomAccessDeniedHandler;
import com.kangong.common.security.handler.CustomLoginSuccessHandler;
import com.kangong.common.security.handler.CustomLogoutHandler;
import com.kangong.common.security.handler.CustomNoOpPasswordEncoder;
import com.kangong.common.security.handler.CustomUserDetailsService;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

	@Autowired
	private DataSource dataSource;

	@Value("${security.remember-me.key:kangong}")
	private String rememberMeKey;

	@Value("${security.remember-me.token-validity-seconds:604800}")
	private int tokenValiditySeconds;

	@Bean
	public UserDetailsService customUserService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
		auth.userDetailsService(customUserService()).passwordEncoder(passwordEncoder());
		return auth.build();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
				.requestMatchers(PUBLIC_URLS).permitAll()
				.requestMatchers(ADMIN_URLS).hasRole("ADMIN")
				.requestMatchers(MEMBER_URLS).hasRole("MEMBER")
		);

		http.formLogin(form -> form
				.loginPage(LOGIN_PAGE)
				.loginProcessingUrl(LOGIN_PROCESSING_URL)
				.successHandler(loginSuccessHandler())
				.permitAll()
		);

		http.logout(logout -> logout
				.logoutUrl(LOGOUT_URL)
				.addLogoutHandler(logoutHandler())
				.logoutSuccessUrl(LOGIN_PAGE)
				.invalidateHttpSession(true)
				.deleteCookies(LOGOUT_COOKIES)
		);

		http.rememberMe(remember -> remember
				.key(rememberMeKey)
				.tokenRepository(persistentTokenRepository())
				.tokenValiditySeconds(tokenValiditySeconds)
		);

		http.exceptionHandling(ex -> ex
				.accessDeniedHandler(accessDeniedHandler())
		);

		http.csrf(csrf -> csrf.disable());

		return http.build();
	}

	@Bean
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return new CustomLoginSuccessHandler();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new CustomNoOpPasswordEncoder();
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}

	@Bean
	public LogoutHandler logoutHandler() {
		return new CustomLogoutHandler();
	}
}
