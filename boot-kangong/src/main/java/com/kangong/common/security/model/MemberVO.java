package com.kangong.common.security.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class MemberVO {
    
	private String id;
	private String userId;
	private String password;
	private String userName;
	private boolean enabled;

	private Date createDate;
	private Date updateDate;
	private List<AuthVO> authList;

}

