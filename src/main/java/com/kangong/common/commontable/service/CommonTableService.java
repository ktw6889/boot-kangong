package com.kangong.common.commontable.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kangong.common.commontable.model.CommonTableColumnVO;
import com.kangong.common.commontable.model.CommonTableVO;
import com.kangong.common.service.CommonService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CommonTableService extends CommonService {

	/** SQL 식별자 허용 패턴: 영문, 숫자, 언더스코어만 허용 */
	private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");

	/** INSERT 시 제외할 컬럼 */
	private static final Set<String> INSERT_SKIP_COLUMNS = new HashSet<>(
			Arrays.asList("DELETE_YN", "rowFlag"));

	/** UPDATE 시 제외할 컬럼 */
	private static final Set<String> UPDATE_SKIP_COLUMNS = new HashSet<>(
			Arrays.asList("ID", "DELETE_YN", "CREATE_USER", "CREATE_DATE", "rowFlag"));

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
			masterVO.setCommonTableColumnList(getListItem(id));
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
		processRowFlagItems(masterVO.getCommonTableColumnList(), "ST_COMMON_TABLE_COLUMN",
			item -> {
				item.setMasterId(masterVO.getId());
				insertItem(item);
			},
			this::updateItem,
			item -> deleteItem(item.getId())
		);
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

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> tableDataList(Map<String, Object> map) throws Exception {
		// 식별자 검증 (SQL Injection 방지)
		validateIdentifier((String) map.get("tableName"));
		String selectColumn = (String) map.get("selectColumn");
		if (selectColumn != null) {
			for (String col : selectColumn.split(",")) {
				validateIdentifier(col.trim());
			}
		}
		return sqlSession.selectList("seckim.commontable.tableDataList", map);
	}

	public void saveInput(Map<String, Object> paramMap) throws Exception {
		String tableName = validateIdentifier((String) paramMap.get("tableCode"));
		@SuppressWarnings("unchecked")
		List<Map<String, String>> dataList = (List<Map<String, String>>) paramMap.get("dataList");

		for (Map<String, String> rowMap : dataList) {
			String rowFlag = rowMap.get("rowFlag");
			if ("I".equals(rowFlag)) {
				executeDynamicInsert(tableName, rowMap);
			} else if ("U".equals(rowFlag)) {
				executeDynamicUpdate(tableName, rowMap);
			} else if ("D".equals(rowFlag)) {
				executeDynamicDelete(tableName, rowMap);
			}
		}
	}

	// ==================== 파라미터화된 동적 SQL (SQL Injection 방지) ====================

	private String validateIdentifier(String identifier) {
		if (identifier == null || !SAFE_IDENTIFIER.matcher(identifier).matches()) {
			throw new IllegalArgumentException("허용되지 않는 SQL 식별자: " + identifier);
		}
		return identifier;
	}

	private List<Map.Entry<String, String>> getValidColumns(Map<String, String> rowMap, Set<String> skipColumns) {
		List<Map.Entry<String, String>> result = new ArrayList<>();
		for (Map.Entry<String, String> entry : rowMap.entrySet()) {
			if (!skipColumns.contains(entry.getKey())) {
				validateIdentifier(entry.getKey());
				result.add(entry);
			}
		}
		return result;
	}

	private Map<String, Object> createDynamicParam(String tableName, String id) {
		Map<String, Object> param = new HashMap<>();
		param.put("tableName", tableName);
		param.put("id", id);
		return param;
	}

	private void executeDynamicInsert(String tableName, Map<String, String> rowMap) throws Exception {
		List<String> columns = new ArrayList<>();
		List<String> values = new ArrayList<>();
		for (Map.Entry<String, String> entry : getValidColumns(rowMap, INSERT_SKIP_COLUMNS)) {
			columns.add(entry.getKey());
			values.add("ID".equals(entry.getKey()) ? getNewId(tableName) : entry.getValue());
		}

		Map<String, Object> param = createDynamicParam(tableName, null);
		param.put("columnString", String.join(", ", columns));
		param.put("values", values);

		log.info("dynamicInsert: table={}, columns={}", tableName, columns);
		sqlSession.insert("seckim.commontable.dynamicInsert", param);
	}

	private void executeDynamicUpdate(String tableName, Map<String, String> rowMap) throws Exception {
		List<Map<String, String>> setClauses = new ArrayList<>();
		for (Map.Entry<String, String> entry : getValidColumns(rowMap, UPDATE_SKIP_COLUMNS)) {
			Map<String, String> clause = new HashMap<>();
			clause.put("column", entry.getKey());
			clause.put("value", entry.getValue());
			setClauses.add(clause);
		}

		Map<String, Object> param = createDynamicParam(tableName, rowMap.get("ID"));
		param.put("setClauses", setClauses);

		log.info("dynamicUpdate: table={}, id={}", tableName, rowMap.get("ID"));
		sqlSession.update("seckim.commontable.dynamicUpdate", param);
	}

	private void executeDynamicDelete(String tableName, Map<String, String> rowMap) throws Exception {
		Map<String, Object> param = createDynamicParam(tableName, rowMap.get("ID"));
		log.info("dynamicDelete: table={}, id={}", tableName, rowMap.get("ID"));
		sqlSession.update("seckim.commontable.dynamicDelete", param);
	}

}
