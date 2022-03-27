package com.kangong.common.security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.kangong.common.security.hanndler.CustomAccessDeniedHandler;
import com.kangong.common.security.hanndler.CustomLoginSuccessHandler;
import com.kangong.common.security.hanndler.CustomLogoutHandler;
import com.kangong.common.security.hanndler.CustomNoOpPasswordEncoder;
import com.kangong.common.security.hanndler.CustomUserDetailsService;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Setter(onMethod_ = { @Autowired })
	private DataSource dataSource;

	@Bean
	public UserDetailsService customUserService() {
		return new CustomUserDetailsService();
	}

	// in custom userdetails
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(customUserService()).passwordEncoder(passwordEncoder());
	}

	// @Override
	// protected void configure(AuthenticationManagerBuilder auth) throws Exception
	// {
	// log.info("configure JDBC ............................");
	//
	// String queryUser = "select userid , userpw , enabled from tbl_member where
	// userid = ? ";
	// String queryDetails = "select userid, auth from tbl_member_auth where userid
	// = ? ";
	//
	// auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder())
	// .usersByUsernameQuery(queryUser).authoritiesByUsernameQuery(queryDetails);
	// }

	@Bean
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return new CustomLoginSuccessHandler();
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		.antMatchers("/security/all").permitAll()	
		.antMatchers("/hello").permitAll()
		.antMatchers("/jsonList").permitAll()
		.antMatchers("/util/*").permitAll()
		.antMatchers("/stock/*").permitAll()
		.antMatchers("/security/admin")
		.access("hasRole('ROLE_ADMIN')")
		.antMatchers("/commontable/*")
		.access("hasRole('ROLE_ADMIN')")	
		.antMatchers("/common/dd/*")
		.access("hasRole('ROLE_ADMIN')")
		
		.antMatchers("/*")
		.access("hasRole('ROLE_MEMBER')")	
		.antMatchers("/security/member")
		.access("hasRole('ROLE_MEMBER')")		
		.antMatchers("/user/*")
		.access("hasRole('ROLE_MEMBER')")
		.antMatchers("/board/*")
		.access("hasRole('ROLE_MEMBER')")
		.antMatchers("/calendar/*")
		.access("hasRole('ROLE_MEMBER')")	
		;		

		http.formLogin()
		.loginPage("/security/customLogin")
		.loginProcessingUrl("/login")
		.successHandler(loginSuccessHandler());

		http.logout()
		.logoutUrl("/security/customLogout")
		.addLogoutHandler(logoutHandler())
		.logoutSuccessUrl("/security/customLogin")
		.invalidateHttpSession(true)
		.deleteCookies("remember-me","JSESSION_ID");
		
		//persistent_logins 테이블 관리
		http.rememberMe()
	      .key("kangong")
	      .tokenRepository(persistentTokenRepository())
	      .tokenValiditySeconds(604800);
		
		http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());

		http.csrf().disable();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new CustomNoOpPasswordEncoder();
		//return new BCryptPasswordEncoder();
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
	
	public LogoutHandler logoutHandler() {
		return new CustomLogoutHandler();
	}

}