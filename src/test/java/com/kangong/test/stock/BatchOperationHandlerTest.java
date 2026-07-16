package com.kangong.test.stock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.common.util.BatchOperationHandler;

import static org.junit.jupiter.api.Assertions.*;

class BatchOperationHandlerTest {

	private static final Logger log = LogManager.getLogger(BatchOperationHandlerTest.class);

	@Test
	@DisplayName("executeBatch - 전체 성공 시 모든 항목 처리")
	void testExecuteBatch_allSuccess() {
		List<String> items = Arrays.asList("A", "B", "C");
		List<String> processed = new ArrayList<>();

		BatchOperationHandler.executeBatch(
			items,
			processed::add,
			item -> "url/" + item,
			"testOp", log);

		assertEquals(3, processed.size());
		assertEquals(Arrays.asList("A", "B", "C"), processed);
	}

	@Test
	@DisplayName("executeBatch - 일부 실패해도 나머지 항목 처리 계속")
	void testExecuteBatch_partialFailure() {
		List<String> items = Arrays.asList("A", "B", "C");
		List<String> processed = new ArrayList<>();

		BatchOperationHandler.executeBatch(
			items,
			item -> {
				if ("B".equals(item)) throw new RuntimeException("B 처리 실패");
				processed.add(item);
			},
			item -> "url/" + item,
			"testOp", log);

		assertEquals(2, processed.size());
		assertTrue(processed.contains("A"));
		assertFalse(processed.contains("B"));
		assertTrue(processed.contains("C"));
	}

	@Test
	@DisplayName("executeBatch - 전체 실패해도 예외 전파 없음")
	void testExecuteBatch_allFailure() {
		List<String> items = Arrays.asList("A", "B", "C");

		assertDoesNotThrow(() -> BatchOperationHandler.executeBatch(
			items,
			item -> { throw new RuntimeException("fail"); },
			item -> "url/" + item,
			"testOp", log));
	}

	@Test
	@DisplayName("executeBatch - 빈 리스트 처리")
	void testExecuteBatch_emptyList() {
		List<String> processed = new ArrayList<>();

		List<String> emptyItems = Collections.emptyList();
		assertDoesNotThrow(() -> BatchOperationHandler.executeBatch(
			emptyItems,
			processed::add,
			item -> "url/" + item,
			"testOp", log));

		assertTrue(processed.isEmpty());
	}

	@Test
	@DisplayName("executeBatch - Integer 타입으로 페이지 번호 처리")
	void testExecuteBatch_integerPageNumbers() {
		List<Integer> pages = Arrays.asList(1, 2, 3, 4, 5);
		List<Integer> processed = new ArrayList<>();

		BatchOperationHandler.executeBatch(
			pages,
			processed::add,
			pageNum -> "https://example.com/api?page=" + pageNum,
			"pageProcess", log);

		assertEquals(5, processed.size());
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), processed);
	}

	@Test
	@DisplayName("executeBatch - 처리 순서가 보장되는지 확인")
	void testExecuteBatch_orderPreserved() {
		List<String> items = Arrays.asList("first", "second", "third");
		List<String> order = new ArrayList<>();

		BatchOperationHandler.executeBatch(
			items,
			order::add,
			item -> item,
			"orderTest", log);

		assertEquals("first", order.get(0));
		assertEquals("second", order.get(1));
		assertEquals("third", order.get(2));
	}

	@Test
	@DisplayName("executeBatch - 실패 항목 사이에 있는 성공 항목도 처리")
	void testExecuteBatch_failBetweenSuccess() {
		List<Integer> items = Arrays.asList(1, 2, 3, 4, 5);
		List<Integer> processed = new ArrayList<>();

		BatchOperationHandler.executeBatch(
			items,
			item -> {
				if (item % 2 == 0) throw new RuntimeException("짝수 실패");
				processed.add(item);
			},
			item -> "url/" + item,
			"oddEvenTest", log);

		assertEquals(3, processed.size());
		assertEquals(Arrays.asList(1, 3, 5), processed);
	}
}
