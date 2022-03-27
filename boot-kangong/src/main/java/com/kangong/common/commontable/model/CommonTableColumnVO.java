package com.kangong.common.commontable.model;

import java.sql.Timestamp;

public class CommonTableColumnVO {

	private String id; // ID
	private Timestamp createDate; // 생성일
	private String createUser; // 생성자
	private Timestamp updateDate; // 수정일
	private String updateUser; // 수정자
	private String deleteYn; // 삭제여부
	private String masterId; // COMMON MASTER ID
	private String columnCode; // COLUMN CODE
	private String columnComment; // COLUMN COMMENT
	private String columnType; // COLUMN TYPE
	private String inputType; // INPUT TYPE
	private String columnSize; // COLUMN SIZE
	private String columnMax; // COLUMN SIZE MAX
	private String columnPattern; // COLUMN 패턴
	private String columnOrder; // COLUMN 순서

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

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getColumnCode() {
		return columnCode;
	}

	public void setColumnCode(String columnCode) {
		this.columnCode = columnCode;
	}

	public String getColumnComment() {
		return columnComment;
	}

	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(String columnSize) {
		this.columnSize = columnSize;
	}

	public String getColumnMax() {
		return columnMax;
	}

	public void setColumnMax(String columnMax) {
		this.columnMax = columnMax;
	}

	public String getColumnPattern() {
		return columnPattern;
	}

	public void setColumnPattern(String columnPattern) {
		this.columnPattern = columnPattern;
	}

	public String getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(String columnOrder) {
		this.columnOrder = columnOrder;
	}

}
