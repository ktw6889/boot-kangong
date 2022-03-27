package com.kangong.common.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kangong.common.security.model.CustomUser;
import com.kangong.common.security.model.MemberVO;
import com.kangong.common.service.CommonService;

@Component
public class CommonUtil {

	@Autowired
	private CommonService commonService;
	
	public String getNewId(String tableName) throws Exception {
		return commonService.getNewId(tableName);
	}
	
	public String getLoginUserId() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		CustomUser userVO = (CustomUser)authentication.getPrincipal();
		return userVO.getMember().getId();
	}
	
	public MemberVO getLoginUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		CustomUser userVO = (CustomUser)authentication.getPrincipal();
		return userVO.getMember();
	}
	

	public List<Map<String, String>> getColumnInfo(String tableName) throws Exception {
		return commonService.getColumnInfo(tableName);
	}
	public void printColumn(String tableName, List<Map<String, String>> list) throws Exception {
		try {

			this.generateVO(list);
			this.generateInsertSql(tableName, list);
			this.generateUpdateSql(tableName, list);
			this.generateMergeSql(tableName, list);
			this.generateSelectSql(tableName, list);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void generateVO(List<Map<String, String>> list) throws Exception {
		// VO 만들기
		StringBuffer strVO = new StringBuffer();

		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");
			String dataType = map.get("DATA_TYPE");
			String columnComment = map.get("COLUMN_COMMENT");
			String resultColumnType = "";

			strVO.append("private ");

			switch (dataType) {
			case "timestamp":
				resultColumnType = "Timestamp";
				break;
			default:
				resultColumnType = "String";
				break;
			}

			strVO.append(resultColumnType + " " + JdbcUtils.convertUnderscoreNameToPropertyName(columnName) + "; //"
					+ columnComment + "\n");
		}

		System.out.println("strVO: \n" + strVO.toString());
	}

	public void generateInsertSql(String tableName, List<Map<String, String>> list) throws Exception {
		StringBuffer strInsertSql = new StringBuffer();
		strInsertSql.append(" INSERT INTO " + tableName + " \n   (");

		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");

			if (("DELETE_YN").equals(columnName))
				continue;

			strInsertSql.append(columnName);
			if (list.lastIndexOf(map) != list.size() - 1) {
				strInsertSql.append(", ");
			} else {
				strInsertSql.append(") ");
			}
		}

		strInsertSql.append(" \n VALUES \n   ( ");
		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");

			if (("DELETE_YN").equals(columnName))
				continue;

			strInsertSql.append("#{" + JdbcUtils.convertUnderscoreNameToPropertyName(columnName) + "}");
			if (list.lastIndexOf(map) != list.size() - 1) {
				strInsertSql.append(", ");
			} else {
				strInsertSql.append(") ");
			}
		}

		System.out.println("strInsertSql: \n" + strInsertSql.toString());
	}

	public void generateUpdateSql(String tableName, List<Map<String, String>> list) throws Exception {
		StringBuffer strUpdateSql = new StringBuffer();
		strUpdateSql.append(" UPDATE " + tableName + " \n");
		strUpdateSql.append(" SET ");

		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");
			if ("ID".equals(columnName))
				continue;
			if (("DELETE_YN").equals(columnName))
				continue;
			if (("CREATE_USER").equals(columnName))
				continue;
			if (("CREATE_DATE").equals(columnName))
				continue;

			strUpdateSql.append(
					columnName + " = " + "#{" + JdbcUtils.convertUnderscoreNameToPropertyName(columnName) + "}");
			if (list.lastIndexOf(map) != list.size() - 1) {
				strUpdateSql.append(", ");
			} else {
				strUpdateSql.append(" \n WHERE ID =#{id}  ");
			}
		}

		System.out.println("strUpdateSql: \n" + strUpdateSql.toString());
	}

	public void generateMergeSql(String tableName, List<Map<String, String>> list) throws Exception {
		StringBuffer strMergeSql = new StringBuffer();
		strMergeSql.append(" MERGE INTO " + tableName + " A \n");
		strMergeSql.append(" USING  DUAL \n");
		strMergeSql.append(" ON  A.ID = {#id} \n");
		strMergeSql.append(" WHEN MATCHED THEN \n");
		strMergeSql.append("  UPDATE SET ");

		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");
			if ("ID".equals(columnName))
				continue;
			if (("DELETE_YN").equals(columnName))
				continue;
			if (("CREATE_USER").equals(columnName))
				continue;
			if (("CREATE_DATE").equals(columnName))
				continue;

			strMergeSql.append(
					columnName + " = " + "#{" + JdbcUtils.convertUnderscoreNameToPropertyName(columnName) + "}");
			if (list.lastIndexOf(map) != list.size() - 1) {
				strMergeSql.append(", ");
			}
		}

		strMergeSql.append(" \n NOT WHEN MATCHED THEN \n ");

		strMergeSql.append(" INSERT (");

		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");
			if (("DELETE_YN").equals(columnName))
				continue;

			strMergeSql.append(columnName);
			if (list.lastIndexOf(map) != list.size() - 1) {
				strMergeSql.append(", ");
			} else {
				strMergeSql.append(") ");
			}
		}

		strMergeSql.append(" \n  VALUES ( ");
		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");
			if (("DELETE_YN").equals(columnName))
				continue;

			strMergeSql.append("#{" + JdbcUtils.convertUnderscoreNameToPropertyName(columnName) + "}");
			if (list.lastIndexOf(map) != list.size() - 1) {
				strMergeSql.append(", ");
			} else {
				strMergeSql.append(") ");
			}
		}

		System.out.println("strMergeSql: \n" + strMergeSql.toString());
	}

	public void generateSelectSql(String tableName, List<Map<String, String>> list) throws Exception {
		StringBuffer strSelectSql = new StringBuffer();
		strSelectSql.append(" SELECT  \n");

		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");
			strSelectSql.append(columnName + " " + JdbcUtils.convertUnderscoreNameToPropertyName(columnName));
			if (list.lastIndexOf(map) != list.size() - 1) {
				strSelectSql.append(", ");
			}
		}
		strSelectSql.append("\n FROM " + tableName + " ");
		System.out.println("strUpdateSql: \n" + strSelectSql.toString());
	}

}
