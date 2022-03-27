package com.kangong.common.dd.model;

import java.sql.Timestamp;

public class ComDdValueVO {

	private String id; // ID
	private Timestamp createDate; // 생성일
	private String createUser; // 생성자
	private Timestamp updateDate; // 수정일
	private String updateUser; // 수정자
	private String deleteYn; // 삭제여부
	private String ddId; // DD ID
	private String ddVal; // DD VALUE
	private String ddKo; // DD LABEL KO
	private String ddEn; // DD LABEL EN
	private String ddDefault; // Default 여부
	private String useYn; // 사용여부
	private String ddOrder; // DD 순서
	private String ddFilter; // DD FILTER

	private String rowFlag;

	public String getRowFlag() {
		return rowFlag;
	}

	public void setRowFlag(String rowFlag) {
		this.rowFlag = rowFlag;
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

	public String getDdId() {
		return ddId;
	}

	public void setDdId(String ddId) {
		this.ddId = ddId;
	}

	public String getDdVal() {
		return ddVal;
	}

	public void setDdVal(String ddVal) {
		this.ddVal = ddVal;
	}

	public String getDdKo() {
		return ddKo;
	}

	public void setDdKo(String ddKo) {
		this.ddKo = ddKo;
	}

	public String getDdEn() {
		return ddEn;
	}

	public void setDdEn(String ddEn) {
		this.ddEn = ddEn;
	}

	public String getDdDefault() {
		return ddDefault;
	}

	public void setDdDefault(String ddDefault) {
		this.ddDefault = ddDefault;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public String getDdOrder() {
		return ddOrder;
	}

	public void setDdOrder(String ddOrder) {
		this.ddOrder = ddOrder;
	}

	public String getDdFilter() {
		return ddFilter;
	}

	public void setDdFilter(String ddFilter) {
		this.ddFilter = ddFilter;
	}

}
