package com.kangong.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import com.kangong.common.service.CommonService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class SqlBuilder {

	private static final Set<String> SKIP_DELETE = Set.of("DELETE_YN");
	private static final Set<String> SKIP_UPDATE = Set.of("ID", "DELETE_YN", "CREATE_USER", "CREATE_DATE");

	@Autowired
	private CommonService commonService;

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
			log.error("컬럼 정보 출력 실패", e);
		}
	}

	// ==================== SQL 생성 ====================

	public void generateVO(List<Map<String, String>> list) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");
			String dataType = map.get("DATA_TYPE");
			String columnComment = map.get("COLUMN_COMMENT");
			String resultColumnType = "timestamp".equals(dataType) ? "Timestamp" : "String";
			sb.append("private ").append(resultColumnType).append(" ")
					.append(toProperty(columnName)).append("; //").append(columnComment).append("\n");
		}
		log.info("strVO: \n{}", sb);
	}

	public void generateInsertSql(String tableName, List<Map<String, String>> list) throws Exception {
		String columns = joinColumns(list, SKIP_DELETE, col -> col);
		String values = joinColumns(list, SKIP_DELETE, this::toBindVar);
		log.info("strInsertSql: \n INSERT INTO {} \n   ({}) \n VALUES \n   ( {})", tableName, columns, values);
	}

	public void generateUpdateSql(String tableName, List<Map<String, String>> list) throws Exception {
		String setClauses = joinColumns(list, SKIP_UPDATE, col -> col + " = " + toBindVar(col));
		log.info("strUpdateSql: \n UPDATE {} \n SET {} \n WHERE ID =#{{id}}", tableName, setClauses);
	}

	public void generateMergeSql(String tableName, List<Map<String, String>> list) throws Exception {
		String updateClauses = joinColumns(list, SKIP_UPDATE, col -> col + " = " + toBindVar(col));
		String insertColumns = joinColumns(list, SKIP_DELETE, col -> col);
		String insertValues = joinColumns(list, SKIP_DELETE, this::toBindVar);

		StringBuilder sb = new StringBuilder();
		sb.append(" MERGE INTO ").append(tableName).append(" A \n");
		sb.append(" USING  DUAL \n");
		sb.append(" ON  A.ID = {#id} \n");
		sb.append(" WHEN MATCHED THEN \n");
		sb.append("  UPDATE SET ").append(updateClauses);
		sb.append(" \n NOT WHEN MATCHED THEN \n ");
		sb.append(" INSERT (").append(insertColumns).append(") ");
		sb.append(" \n  VALUES ( ").append(insertValues).append(") ");
		log.info("strMergeSql: \n{}", sb);
	}

	public void generateSelectSql(String tableName, List<Map<String, String>> list) throws Exception {
		String columns = joinColumns(list, Collections.emptySet(), col -> col + " " + toProperty(col));
		log.info("strSelectSql: \n SELECT  \n{}\n FROM {} ", columns, tableName);
	}

	// ==================== 공통 유틸리티 ====================

	private String joinColumns(List<Map<String, String>> list, Set<String> skipColumns,
							   Function<String, String> formatter) {
		StringJoiner joiner = new StringJoiner(", ");
		for (Map<String, String> map : list) {
			String columnName = map.get("COLUMN_NAME");
			if (skipColumns.contains(columnName)) continue;
			joiner.add(formatter.apply(columnName));
		}
		return joiner.toString();
	}

	private String toProperty(String columnName) {
		return JdbcUtils.convertUnderscoreNameToPropertyName(columnName);
	}

	private String toBindVar(String columnName) {
		return "#{" + toProperty(columnName) + "}";
	}
}
