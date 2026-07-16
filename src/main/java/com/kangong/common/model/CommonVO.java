package com.kangong.common.model;

import java.sql.Timestamp;

import com.kangong.common.util.SecurityContextUtil;

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
			createUser = SecurityContextUtil.getLoginUserId();
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
			updateUser = SecurityContextUtil.getLoginUserId();
		}
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	
}
