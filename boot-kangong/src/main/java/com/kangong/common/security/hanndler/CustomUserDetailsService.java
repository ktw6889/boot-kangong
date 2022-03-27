package com.kangong.common.security.hanndler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kangong.common.security.model.CustomUser;
import com.kangong.common.security.model.MemberVO;
import com.kangong.common.security.service.MemberService;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

	@Setter(onMethod_ = { @Autowired })
	private MemberService memberService;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

		log.info("Load User By UserName : " + userName);

		// userName means userid
		MemberVO vo = memberService.read(userName);

		log.info("queried by member mapper: " + vo);

		return vo == null ? null : new CustomUser(vo);
	} 

}
