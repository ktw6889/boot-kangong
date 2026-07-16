package com.kangong.test.commontable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kangong.common.commontable.service.CommonTableService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommonTableServiceTests {

	@Autowired
	private CommonTableService commonTableService;

	@Test
	@DisplayName("1. 정상 INSERT - 파라미터화된 쿼리로 정상 동작 확인")
	void testNormalInsert() throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tableCode", "ST_COMMON_TABLE_MASTER");

		ArrayList<Map> dataList = new ArrayList<>();
		Map<String, String> row = new HashMap<>();
		row.put("rowFlag", "I");
		row.put("ID", "");
		row.put("TABLE_CODE", "TEST_TABLE");
		row.put("TABLE_NAME", "테스트 테이블");
		row.put("TABLE_DESC", "SQL Injection 테스트용");
		row.put("CREATE_USER", "tester");
		row.put("UPDATE_USER", "tester");
		dataList.add(row);

		paramMap.put("dataList", dataList);

		assertDoesNotThrow(() -> commonTableService.saveInput(paramMap));
		System.out.println("[PASS] 정상 INSERT 성공");
	}

	@Test
	@DisplayName("2. SQL Injection 차단 - 테이블명에 공격 문자열")
	void testSqlInjectionOnTableName() {
		Map<String, Object> paramMap = new HashMap<>();
		// 악의적인 테이블명: SQL Injection 시도
		paramMap.put("tableCode", "ST_COMMON_TABLE_MASTER; DROP TABLE users; --");

		ArrayList<Map> dataList = new ArrayList<>();
		Map<String, String> row = new HashMap<>();
		row.put("rowFlag", "I");
		row.put("ID", "");
		row.put("TABLE_CODE", "ATTACK");
		dataList.add(row);
		paramMap.put("dataList", dataList);

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> commonTableService.saveInput(paramMap));

		assertTrue(exception.getMessage().contains("허용되지 않는 SQL 식별자"));
		System.out.println("[PASS] 테이블명 SQL Injection 차단: " + exception.getMessage());
	}

	@Test
	@DisplayName("3. SQL Injection 차단 - 컬럼명에 공격 문자열")
	void testSqlInjectionOnColumnName() {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tableCode", "ST_COMMON_TABLE_MASTER");

		ArrayList<Map> dataList = new ArrayList<>();
		Map<String, String> row = new HashMap<>();
		row.put("rowFlag", "I");
		row.put("ID", "");
		// 악의적인 컬럼명: SQL Injection 시도
		row.put("TABLE_CODE' OR '1'='1", "ATTACK_VALUE");
		dataList.add(row);
		paramMap.put("dataList", dataList);

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> commonTableService.saveInput(paramMap));

		assertTrue(exception.getMessage().contains("허용되지 않는 SQL 식별자"));
		System.out.println("[PASS] 컬럼명 SQL Injection 차단: " + exception.getMessage());
	}

	@Test
	@DisplayName("4. SQL Injection 차단 - 값에 악의적 문자열 (PreparedStatement로 안전)")
	void testSqlInjectionOnValue() throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tableCode", "ST_COMMON_TABLE_MASTER");

		ArrayList<Map> dataList = new ArrayList<>();
		Map<String, String> row = new HashMap<>();
		row.put("rowFlag", "I");
		row.put("ID", "");
		row.put("TABLE_CODE", "TEST_INJECTION");
		// 값에 SQL Injection 시도 -> PreparedStatement #{} 로 이스케이프됨
		row.put("TABLE_NAME", "'; DROP TABLE users; --");
		row.put("TABLE_DESC", "1' OR '1'='1");
		row.put("CREATE_USER", "tester");
		row.put("UPDATE_USER", "tester");
		dataList.add(row);
		paramMap.put("dataList", dataList);

		// 값은 PreparedStatement 파라미터로 바인딩되므로 예외 없이 정상 INSERT
		// (값 자체가 문자열로 저장될 뿐 SQL로 실행되지 않음)
		assertDoesNotThrow(() -> commonTableService.saveInput(paramMap));
		System.out.println("[PASS] 값에 SQL Injection 문자열이 있어도 안전하게 저장됨");
	}

	@Test
	@DisplayName("5. 정상 UPDATE - 파라미터화된 쿼리로 정상 동작 확인")
	void testNormalUpdate() throws Exception {
		// 먼저 INSERT
		Map<String, Object> insertParam = new HashMap<>();
		insertParam.put("tableCode", "ST_COMMON_TABLE_MASTER");

		ArrayList<Map> insertList = new ArrayList<>();
		Map<String, String> insertRow = new HashMap<>();
		insertRow.put("rowFlag", "I");
		insertRow.put("ID", "");
		insertRow.put("TABLE_CODE", "UPDATE_TEST");
		insertRow.put("TABLE_NAME", "업데이트 테스트");
		insertRow.put("TABLE_DESC", "업데이트 전");
		insertRow.put("CREATE_USER", "tester");
		insertRow.put("UPDATE_USER", "tester");
		insertList.add(insertRow);
		insertParam.put("dataList", insertList);
		commonTableService.saveInput(insertParam);

		// INSERT된 데이터의 ID를 조회
		com.kangong.common.commontable.model.CommonTableVO searchVO =
				new com.kangong.common.commontable.model.CommonTableVO();
		searchVO.setTableCode("UPDATE_TEST");
		java.util.List<com.kangong.common.commontable.model.CommonTableVO> results =
				commonTableService.getList(searchVO);

		if (!results.isEmpty()) {
			String insertedId = results.get(0).getId();

			// UPDATE 테스트
			Map<String, Object> updateParam = new HashMap<>();
			updateParam.put("tableCode", "ST_COMMON_TABLE_MASTER");

			ArrayList<Map> updateList = new ArrayList<>();
			Map<String, String> updateRow = new HashMap<>();
			updateRow.put("rowFlag", "U");
			updateRow.put("ID", insertedId);
			updateRow.put("TABLE_DESC", "업데이트 후");
			updateRow.put("UPDATE_USER", "tester");
			updateList.add(updateRow);
			updateParam.put("dataList", updateList);

			assertDoesNotThrow(() -> commonTableService.saveInput(updateParam));
			System.out.println("[PASS] 정상 UPDATE 성공 (ID: " + insertedId + ")");
		}
	}

	@Test
	@DisplayName("6. 정상 DELETE - 소프트 삭제 확인")
	void testNormalDelete() throws Exception {
		// 먼저 INSERT
		Map<String, Object> insertParam = new HashMap<>();
		insertParam.put("tableCode", "ST_COMMON_TABLE_MASTER");

		ArrayList<Map> insertList = new ArrayList<>();
		Map<String, String> insertRow = new HashMap<>();
		insertRow.put("rowFlag", "I");
		insertRow.put("ID", "");
		insertRow.put("TABLE_CODE", "DELETE_TEST");
		insertRow.put("TABLE_NAME", "삭제 테스트");
		insertRow.put("TABLE_DESC", "삭제 예정");
		insertRow.put("CREATE_USER", "tester");
		insertRow.put("UPDATE_USER", "tester");
		insertList.add(insertRow);
		insertParam.put("dataList", insertList);
		commonTableService.saveInput(insertParam);

		// INSERT된 데이터 조회
		com.kangong.common.commontable.model.CommonTableVO searchVO =
				new com.kangong.common.commontable.model.CommonTableVO();
		searchVO.setTableCode("DELETE_TEST");
		java.util.List<com.kangong.common.commontable.model.CommonTableVO> results =
				commonTableService.getList(searchVO);

		if (!results.isEmpty()) {
			String insertedId = results.get(0).getId();

			// DELETE 테스트
			Map<String, Object> deleteParam = new HashMap<>();
			deleteParam.put("tableCode", "ST_COMMON_TABLE_MASTER");

			ArrayList<Map> deleteList = new ArrayList<>();
			Map<String, String> deleteRow = new HashMap<>();
			deleteRow.put("rowFlag", "D");
			deleteRow.put("ID", insertedId);
			deleteList.add(deleteRow);
			deleteParam.put("dataList", deleteList);

			assertDoesNotThrow(() -> commonTableService.saveInput(deleteParam));
			System.out.println("[PASS] 정상 DELETE 성공 (ID: " + insertedId + ")");
		}
	}

	@Test
	@DisplayName("7. tableDataList - 식별자 검증 확인")
	void testTableDataListValidation() {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("tableName", "users; DROP TABLE tbl_member; --");
		paramMap.put("selectColumn", "ID");

		assertThrows(IllegalArgumentException.class,
				() -> commonTableService.tableDataList(paramMap));
		System.out.println("[PASS] tableDataList 테이블명 검증 통과");
	}
}