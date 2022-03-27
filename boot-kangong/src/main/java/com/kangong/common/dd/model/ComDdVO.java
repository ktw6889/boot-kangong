package com.kangong.common.dd.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.kangong.common.model.Pagination;

public class ComDdVO extends Pagination {

	private String id; // ID
	private Timestamp createDate; // 생성일
	private String createUser; // 생성자
	private Timestamp updateDate; // 수정일
	private String updateUser; // 수정자
	private String deleteYn; // 삭제여부
	private String ddCode; // DD CODE
	private String ddName; // DD명
	private String ddDesc; // DD설명
	private String ddModule; // DD 모듈

	private List<ComDdValueVO> ddValueList = new ArrayList<ComDdValueVO>();

	private int startList;
	private int listSize = 10;

	@Override
	public int getStartList() {
		return startList;
	}

	@Override
	public void setStartList(int startList) {
		this.startList = startList;
	}

	@Override
	public int getListSize() {
		return listSize;
	}

	@Override
	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public List<ComDdValueVO> getDdValueList() {
		return ddValueList;
	}

	public void setDdValueList(List<ComDdValueVO> ddValueList) {
		this.ddValueList = ddValueList;
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

	public String getDdCode() {
		return ddCode;
	}

	public void setDdCode(String ddCode) {
		this.ddCode = ddCode;
	}

	public String getDdName() {
		return ddName;
	}

	public void setDdName(String ddName) {
		this.ddName = ddName;
	}

	public String getDdDesc() {
		return ddDesc;
	}

	public void setDdDesc(String ddDesc) {
		this.ddDesc = ddDesc;
	}

	public String getDdModule() {
		return ddModule;
	}

	public void setDdModule(String ddModule) {
		this.ddModule = ddModule;
	}

}
