package com.kangong.common.model;

import java.sql.Timestamp;

public class MessageVo {

	private String id; // ID
	private Timestamp createDate; // 생성일
	private String createUser; // 생성자
	private Timestamp updateDate; // 수정일
	private String updateUser; // 수정자
	private String deleteYn; // 삭제여부
	private String messageCode; // 메시지코드
	private String lableKo; // 메시지_한글
	private String lableEn; // 메시지_영문
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

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
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getDeleteYn() {
		return deleteYn;
	}

	public void setDeleteYn(String deleteYn) {
		this.deleteYn = deleteYn;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getLableKo() {
		return lableKo;
	}

	public void setLableKo(String lableKo) {
		this.lableKo = lableKo;
	}

	public String getLableEn() {
		return lableEn;
	}

	public void setLableEn(String lableEn) {
		this.lableEn = lableEn;
	}

}
