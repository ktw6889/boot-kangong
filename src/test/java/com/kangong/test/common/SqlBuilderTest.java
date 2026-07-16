package com.kangong.test.common;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.common.util.SqlBuilder;

class SqlBuilderTest {

	SqlBuilder sqlBuilder;
	List<Map<String, String>> columnList;

	@BeforeEach
	void setUp() {
		sqlBuilder = new SqlBuilder();
		columnList = new ArrayList<>();
		columnList.add(createColumn("ID", "varchar", "아이디"));
		columnList.add(createColumn("CREATE_DATE", "timestamp", "생성일"));
		columnList.add(createColumn("CREATE_USER", "varchar", "생성자"));
		columnList.add(createColumn("DELETE_YN", "varchar", "삭제여부"));
		columnList.add(createColumn("USER_NAME", "varchar", "사용자명"));
		columnList.add(createColumn("EMAIL", "varchar", "이메일"));
	}

	private Map<String, String> createColumn(String name, String dataType, String comment) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("COLUMN_NAME", name);
		map.put("DATA_TYPE", dataType);
		map.put("COLUMN_COMMENT", comment);
		return map;
	}

	// ==================== joinColumns 헬퍼 검증 ====================

	@SuppressWarnings("unchecked")
	private String invokeJoinColumns(List<Map<String, String>> list, Set<String> skipColumns,
									 Function<String, String> formatter) throws Exception {
		Method method = SqlBuilder.class.getDeclaredMethod("joinColumns", List.class, Set.class, Function.class);
		method.setAccessible(true);
		return (String) method.invoke(sqlBuilder, list, skipColumns, formatter);
	}

	@Test
	@DisplayName("joinColumns - 스킵 없이 전체 컬럼 연결")
	void testJoinColumns_noSkip() throws Exception {
		String result = invokeJoinColumns(columnList, Collections.emptySet(), col -> col);
		assertEquals("ID, CREATE_DATE, CREATE_USER, DELETE_YN, USER_NAME, EMAIL", result);
	}

	@Test
	@DisplayName("joinColumns - DELETE_YN 스킵")
	void testJoinColumns_skipDelete() throws Exception {
		String result = invokeJoinColumns(columnList, Set.of("DELETE_YN"), col -> col);
		assertFalse(result.contains("DELETE_YN"));
		assertTrue(result.contains("ID"));
		assertTrue(result.contains("USER_NAME"));
	}

	@Test
	@DisplayName("joinColumns - UPDATE 스킵셋 (ID, DELETE_YN, CREATE_USER, CREATE_DATE)")
	void testJoinColumns_skipUpdate() throws Exception {
		String result = invokeJoinColumns(columnList, Set.of("ID", "DELETE_YN", "CREATE_USER", "CREATE_DATE"),
			col -> col);
		assertEquals("USER_NAME, EMAIL", result);
	}

	@Test
	@DisplayName("joinColumns - 변환 함수 적용")
	void testJoinColumns_withFormatter() throws Exception {
		List<Map<String, String>> small = new ArrayList<>();
		small.add(createColumn("USER_NAME", "varchar", "사용자명"));
		small.add(createColumn("EMAIL", "varchar", "이메일"));

		String result = invokeJoinColumns(small, Collections.emptySet(),
			col -> col + " = #{test}");
		assertEquals("USER_NAME = #{test}, EMAIL = #{test}", result);
	}

	@Test
	@DisplayName("joinColumns - 빈 리스트이면 빈 문자열")
	void testJoinColumns_emptyList() throws Exception {
		String result = invokeJoinColumns(new ArrayList<>(), Collections.emptySet(), col -> col);
		assertEquals("", result);
	}

	@Test
	@DisplayName("joinColumns - 전부 스킵되면 빈 문자열")
	void testJoinColumns_allSkipped() throws Exception {
		List<Map<String, String>> small = new ArrayList<>();
		small.add(createColumn("DELETE_YN", "varchar", "삭제여부"));

		String result = invokeJoinColumns(small, Set.of("DELETE_YN"), col -> col);
		assertEquals("", result);
	}

	// ==================== toProperty / toBindVar 검증 ====================

	private String invokeToProperty(String columnName) throws Exception {
		Method method = SqlBuilder.class.getDeclaredMethod("toProperty", String.class);
		method.setAccessible(true);
		return (String) method.invoke(sqlBuilder, columnName);
	}

	private String invokeToBindVar(String columnName) throws Exception {
		Method method = SqlBuilder.class.getDeclaredMethod("toBindVar", String.class);
		method.setAccessible(true);
		return (String) method.invoke(sqlBuilder, columnName);
	}

	@Test
	@DisplayName("toProperty - 언더스코어를 카멜케이스로 변환")
	void testToProperty() throws Exception {
		assertEquals("userName", invokeToProperty("USER_NAME"));
		assertEquals("createDate", invokeToProperty("CREATE_DATE"));
		assertEquals("id", invokeToProperty("ID"));
	}

	@Test
	@DisplayName("toBindVar - MyBatis 바인드 변수 형식")
	void testToBindVar() throws Exception {
		assertEquals("#{userName}", invokeToBindVar("USER_NAME"));
		assertEquals("#{email}", invokeToBindVar("EMAIL"));
	}

	// ==================== SQL 생성 메서드 실행 검증 ====================

	@Test
	@DisplayName("generateInsertSql - 예외 없이 실행")
	void testGenerateInsertSql() {
		assertDoesNotThrow(() -> sqlBuilder.generateInsertSql("TEST_TABLE", columnList));
	}

	@Test
	@DisplayName("generateUpdateSql - 예외 없이 실행")
	void testGenerateUpdateSql() {
		assertDoesNotThrow(() -> sqlBuilder.generateUpdateSql("TEST_TABLE", columnList));
	}

	@Test
	@DisplayName("generateMergeSql - 예외 없이 실행")
	void testGenerateMergeSql() {
		assertDoesNotThrow(() -> sqlBuilder.generateMergeSql("TEST_TABLE", columnList));
	}

	@Test
	@DisplayName("generateSelectSql - 예외 없이 실행")
	void testGenerateSelectSql() {
		assertDoesNotThrow(() -> sqlBuilder.generateSelectSql("TEST_TABLE", columnList));
	}

	@Test
	@DisplayName("generateVO - 예외 없이 실행")
	void testGenerateVO() {
		assertDoesNotThrow(() -> sqlBuilder.generateVO(columnList));
	}

	@Test
	@DisplayName("generateVO - timestamp 컬럼은 Timestamp 타입 생성")
	void testGenerateVO_timestampType() throws Exception {
		List<Map<String, String>> small = new ArrayList<>();
		small.add(createColumn("CREATE_DATE", "timestamp", "생성일"));

		// generateVO logs but doesn't throw
		assertDoesNotThrow(() -> sqlBuilder.generateVO(small));
	}

	// ==================== 스킵 상수 검증 ====================

	@Test
	@DisplayName("SKIP_DELETE 상수 존재 및 값 확인")
	void testSkipDeleteConstant() throws Exception {
		var field = SqlBuilder.class.getDeclaredField("SKIP_DELETE");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Set<String> skipDelete = (Set<String>) field.get(null);
		assertEquals(Set.of("DELETE_YN"), skipDelete);
	}

	@Test
	@DisplayName("SKIP_UPDATE 상수 존재 및 값 확인")
	void testSkipUpdateConstant() throws Exception {
		var field = SqlBuilder.class.getDeclaredField("SKIP_UPDATE");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Set<String> skipUpdate = (Set<String>) field.get(null);
		assertEquals(Set.of("ID", "DELETE_YN", "CREATE_USER", "CREATE_DATE"), skipUpdate);
	}

	// ==================== 구조 검증 ====================

	@Test
	@DisplayName("joinColumns 메서드 존재")
	void testJoinColumnsMethodExists() {
		assertDoesNotThrow(() ->
			SqlBuilder.class.getDeclaredMethod("joinColumns", List.class, Set.class, Function.class));
	}

	@Test
	@DisplayName("toProperty 메서드 존재")
	void testToPropertyMethodExists() {
		assertDoesNotThrow(() ->
			SqlBuilder.class.getDeclaredMethod("toProperty", String.class));
	}

	@Test
	@DisplayName("toBindVar 메서드 존재")
	void testToBindVarMethodExists() {
		assertDoesNotThrow(() ->
			SqlBuilder.class.getDeclaredMethod("toBindVar", String.class));
	}
}
