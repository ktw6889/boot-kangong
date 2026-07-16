package com.kangong.common.commontable.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kangong.common.model.Pagination;

public class CommonTableVO extends Pagination {

	private String deleteYn;
	private String tableCode;
	private String tableName;
	private String tableDesc;

	private List<CommonTableColumnVO> commonTableColumnList = new ArrayList<>();

	private String paramType;

	private List<CommonTableColumnVO> columnList = new ArrayList<>();
	private List<Map<String, Object>> dataList = new ArrayList<>();
	private String jsonColumnList;
	private String jsonDataList;
	private String jsonData;

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

	public List<CommonTableColumnVO> getCommonTableColumnList() {
		return commonTableColumnList;
	}

	public void setCommonTableColumnList(List<CommonTableColumnVO> commonTableColumnList) {
		this.commonTableColumnList = commonTableColumnList;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public List<CommonTableColumnVO> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<CommonTableColumnVO> columnList) {
		this.columnList = columnList;
	}

	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<String, Object>> dataList) {
		this.dataList = dataList;
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

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
}
