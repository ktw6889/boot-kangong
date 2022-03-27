package com.kangong.user.model;

import org.springframework.util.StringUtils;

public class UserItemVO {

	private String id;
	private String userInfoId;

	private String displayOrder; // 순서
	private String projectCompany; // 회사
	private String projectName; // 프로젝트명
	private String projectYear; // 년도
	private String applyModule; // 적용모듈
	private String description; // 설명

	private String[] strsApplyModule;
	private String rowFlag;

	public String convertStrsApplyModule() {
		return StringUtils.arrayToCommaDelimitedString(strsApplyModule);
	}

	public String[] getStrsApplyModule() {
		return strsApplyModule;
	}

	public void setStrsApplyModule(String[] strsApplyModule) {
		this.strsApplyModule = strsApplyModule;
	}

	public String getUserInfoId() {
		return userInfoId;
	}

	public void setUserInfoId(String userInfoId) {
		this.userInfoId = userInfoId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(String displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getProjectCompany() {
		return projectCompany;
	}

	public void setProjectCompany(String projectCompany) {
		this.projectCompany = projectCompany;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectYear() {
		return projectYear;
	}

	public void setProjectYear(String projectYear) {
		this.projectYear = projectYear;
	}

	public String getApplyModule() {
		return applyModule;
	}

	public void setApplyModule(String applyModule) {
		this.applyModule = applyModule;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRowFlag() {
		return rowFlag;
	}

	public void setRowFlag(String rowFlag) {
		this.rowFlag = rowFlag;
	}

}
