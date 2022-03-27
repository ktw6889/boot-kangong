package com.kangong.common.commontable.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kangong.common.commontable.model.CommonTableColumnVO;
import com.kangong.common.commontable.model.CommonTableVO;
import com.kangong.common.service.CommonService;

@Service
public class CommonTableService extends CommonService {

	public List<CommonTableColumnVO> importData(CommonTableVO paramVO) throws Exception {
		return sqlSession.selectList("seckim.commontable.importData", paramVO);
	}

	public List<CommonTableVO> getList(CommonTableVO masterVO) throws Exception {
		return sqlSession.selectList("seckim.commontable.list", masterVO);
	}

	// 총 게시글 개수 확인
	public int getListCnt(CommonTableVO masterVO) throws Exception {
		return sqlSession.selectOne("seckim.commontable.listCnt", masterVO);
	}

	public CommonTableVO getSelect(String id) throws Exception {
		CommonTableVO masterVO = sqlSession.selectOne("seckim.commontable.select", id);
		if (masterVO != null && !StringUtils.isEmpty(masterVO.getId()))
			masterVO.setCommonTalbeColumnList(getListItem(id));
		return masterVO;
	}

	public CommonTableVO save(CommonTableVO masterVO) throws Exception {
		if (StringUtils.isEmpty(masterVO.getId())) {
			String id = getNewId("ST_COMMON_TABLE_MASTER");
			masterVO.setId(id);
			insert(masterVO);
		} else {
			update(masterVO);
		}

		saveItem(masterVO);
		return masterVO;
	}

	public void insert(CommonTableVO masterVO) throws Exception {
		sqlSession.insert("seckim.commontable.insert", masterVO);
	}

	public void update(CommonTableVO masterVO) throws Exception {
		sqlSession.update("seckim.commontable.update", masterVO);
	}

	public void delete(String id) throws Exception {
		sqlSession.update("seckim.commontable.delete", id);
	}

	public List<CommonTableColumnVO> getListItem(String masterId) throws Exception {
		return sqlSession.selectList("seckim.commontable.listItem", masterId);
	}

	public void saveItem(CommonTableVO masterVO) throws Exception {
		for (CommonTableColumnVO itemVO : masterVO.getCommonTalbeColumnList()) {
			if ("I".equals(itemVO.getRowFlag())) {
				String id = getNewId("ST_COMMON_TABLE_COLUMN");
				itemVO.setId(id);
				itemVO.setMasterId(masterVO.getId());
				insertItem(itemVO);
			} else if ("U".equals(itemVO.getRowFlag())) {
				updateItem(itemVO);
			} else if ("D".equals(itemVO.getRowFlag())) {
				deleteItem(itemVO.getId());
			}
		}
	}

	public void insertItem(CommonTableColumnVO itemVO) throws Exception {
		sqlSession.insert("seckim.commontable.insertItem", itemVO);
	}

	public void updateItem(CommonTableColumnVO itemVO) throws Exception {
		sqlSession.update("seckim.commontable.updateItem", itemVO);
	}

	public void deleteItem(String id) throws Exception {
		sqlSession.update("seckim.commontable.deleteItem", id);
	}

	public List<Map> tableDataList(Map map) throws Exception {
		return sqlSession.selectList("seckim.commontable.tableDataList", map);
	}

	public void saveInput(Map<String, Object> paramMap) throws Exception {
		String tableName = (String) paramMap.get("tableCode");
		ArrayList<Map> dataList = (ArrayList) paramMap.get("dataList");
		for (Map<String, String> rowMap : dataList) {
			String rowFlag = rowMap.get("rowFlag");
			String strSql = "";
			if ("I".equals(rowFlag)) {
				strSql = generateInsertSql(tableName, rowMap);
			} else if ("U".equals(rowFlag)) {
				strSql = generateUpdateSql(tableName, rowMap);
			} else if ("D".equals(rowFlag)) {
				strSql = generateDeleteSql(tableName, rowMap);
			}
			updateTableData(strSql);
		}

	}

	public void updateTableData(String strSql) throws Exception {
		Map paramMap = new HashMap();
		paramMap.put("updateStatement", strSql);
		sqlSession.update("seckim.commontable.updateTableData", paramMap);
	}

	public String generateInsertSql(String tableName, Map<String, String> rowMap) throws Exception {
		StringBuffer strInsertSql = new StringBuffer();
		StringBuffer strValueSql = new StringBuffer();
		strInsertSql.append(" INSERT INTO " + tableName + " \n   (");
		strValueSql.append(" \n VALUES \n   ( ");

		Iterator<String> iterator = rowMap.keySet().iterator();
		while (iterator.hasNext()) {
			String columnName = iterator.next();

			if (("DELETE_YN").equals(columnName))
				continue;
			if (("rowFlag").equals(columnName))
				continue;

			strInsertSql.append(columnName);
			if (iterator.hasNext()) {
				strInsertSql.append(", ");
			} else {
				strInsertSql.append(") ");
			}

			if ("ID".equals(columnName)) {
				strValueSql.append("'" + getNewId(tableName) + "'");
			} else {
				strValueSql.append("'" + rowMap.get(columnName) + "'");
			}

			if (iterator.hasNext()) {
				strValueSql.append(", ");
			} else {
				strValueSql.append(") ");
			}
		}

		strInsertSql.append(strValueSql);

		System.out.println("strInsertSql: \n" + strInsertSql.toString());
		return strInsertSql.toString();
	}

	public String generateUpdateSql(String tableName, Map<String, String> rowMap) throws Exception {
		StringBuffer strUpdateSql = new StringBuffer();
		strUpdateSql.append(" UPDATE " + tableName + " \n");
		strUpdateSql.append(" SET ");

		Iterator<String> iterator = rowMap.keySet().iterator();
		while (iterator.hasNext()) {
			String columnName = iterator.next();

			if ("ID".equals(columnName))
				continue;
			if (("DELETE_YN").equals(columnName))
				continue;
			if (("CREATE_USER").equals(columnName))
				continue;
			if (("CREATE_DATE").equals(columnName))
				continue;

			strUpdateSql.append(columnName + " = " + "'" + rowMap.get(columnName) + "'");
			if (iterator.hasNext()) {
				strUpdateSql.append(", ");
			} else {
				strUpdateSql.append(" \n WHERE ID = " + "'" + rowMap.get("ID") + "'");
			}
		}

		System.out.println("strUpdateSql: \n" + strUpdateSql.toString());
		return strUpdateSql.toString();
	}

	public String generateDeleteSql(String tableName, Map<String, String> rowMap) throws Exception {
		StringBuffer strUpdateSql = new StringBuffer();
		strUpdateSql.append(" UPDATE " + tableName + " \n");
		strUpdateSql.append(" SET ");
		strUpdateSql.append(" DELETE_YN = 'Y' ");
		strUpdateSql.append(" , UPDATE_DATE = NOW() ");
		strUpdateSql.append("  WHERE ID = " + "'" + rowMap.get("ID") + "'");

		System.out.println("strUpdateSql: \n" + strUpdateSql.toString());
		return strUpdateSql.toString();
	}

}
