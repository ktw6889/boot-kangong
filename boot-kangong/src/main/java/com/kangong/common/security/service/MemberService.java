package com.kangong.common.security.service;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.common.security.model.MemberVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MemberService {
	@Autowired
	private SqlSession sqlSession;
	
	public MemberVO read(String userid) {
		//return sqlSession.selectOne("kangong.member.read",userid);
		return sqlSession.selectOne("kangong.user.read",userid);
	}
}
