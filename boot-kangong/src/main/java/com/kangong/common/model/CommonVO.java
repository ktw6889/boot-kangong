package com.kangong.common.model;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kangong.common.security.model.CustomUser;
import com.kangong.common.util.BeanUtils;
import com.kangong.common.util.CommonUtil;

public class CommonVO {
	private String id;
	private Timestamp createDate;
	private String createUser;
	private Timestamp updateDate;
	private String updateUser;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		if(createUser == null) {
			CommonUtil commonUtil = (CommonUtil)BeanUtils.getBean("commonUtil");
			createUser = commonUtil.getLoginUserId();
		}
		return createUser;
	}
	public void setCreateUser(String createUser) {		
		
		this.createUser = createUser;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateUser() {
		if(updateUser == null) {
			CommonUtil commonUtil = (CommonUtil)BeanUtils.getBean("commonUtil");
			updateUser = commonUtil.getLoginUserId();
		}
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	
}
