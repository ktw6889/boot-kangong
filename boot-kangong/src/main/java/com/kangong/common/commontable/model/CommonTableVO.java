package com.kangong.common.commontable.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.kangong.common.model.Pagination;

public class CommonTableVO extends Pagination {

	private String id; // ID
	private Timestamp createDate; // 생성일
	private String createUser; // 생성자
	private Timestamp updateDate; // 수정일
	private String updateUser; // 수정자
	private String deleteYn; // 삭제여부
	private String tableCode; // TABLE CODE
	private String tableName; // TABLE명
	private String tableDesc; // TABLE설명

	private List<CommonTableColumnVO> commonTalbeColumnList = new ArrayList<CommonTableColumnVO>();

	private int startList;
	private int listSize = 10;

	private String paramType;

	// json
	private List columnList = new ArrayList();
	private List dataList = new ArrayList();
	private String jsonColumnList;
	private String jsonDataList;
	private String jsonData;

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public List getColumnList() {
		return columnList;
	}

	public void setColumnList(List columnList) {
		this.columnList = columnList;
	}

	public List getDataList() {
		return dataList;
	}

	public void setDataList(List dataList) {
		this.dataList = dataList;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public String getJsonColumnList() {
		return jsonColumnList;
	}

	public void setJsonColumnList(String jsonColumnList) {
		this.jsonColumnList = jsonColumnList;
	}

	public String getJsonDataList() {
		return jsonDataList;
	}

	public void setJsonDataList(String jsonDataList) {
		this.jsonDataList = jsonDataList;
	}

	public List<CommonTableColumnVO> getCommonTalbeColumnList() {
		return commonTalbeColumnList;
	}

	public void setCommonTalbeColumnList(List<CommonTableColumnVO> commonTalbeColumnList) {
		this.commonTalbeColumnList = commonTalbeColumnList;
	}

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

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}

}
