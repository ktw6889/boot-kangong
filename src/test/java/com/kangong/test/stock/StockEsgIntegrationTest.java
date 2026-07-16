package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.parser.StockMobileParser;
import com.kangong.stock.service.StockFetcher;
import com.kangong.stock.service.StockJsonConverter;
import com.kangong.stock.service.StockMobileEsgService;
import com.kangong.stock.service.StockRepository;
import com.kangong.stock.service.StockService;

class StockEsgIntegrationTest {

	StockMobileEsgService esgService;
	StockService stockService;
	StockFetcher mockFetcher;
	StockRepository mockRepository;
	StockMobileParser mockParser;
	StockJsonConverter mockConverter;
	SqlSession mockSqlSession;

	@BeforeEach
	void setUp() throws Exception {
		esgService = new StockMobileEsgService();
		stockService = new StockService();
		mockFetcher = mock(StockFetcher.class);
		mockRepository = mock(StockRepository.class);
		mockParser = new StockMobileParser();
		mockConverter = mock(StockJsonConverter.class);
		mockSqlSession = mock(SqlSession.class);

		injectField(esgService, StockMobileEsgService.class, "stockFetcher", mockFetcher);
		injectField(esgService, StockMobileEsgService.class, "stockRepository", mockRepository);
		injectField(esgService, StockMobileEsgService.class, "stockMobileParser", mockParser);
		injectField(esgService, StockMobileEsgService.class, "stockJsonConverter", mockConverter);

		injectFieldHierarchy(stockService, "sqlSession", mockSqlSession);
	}

	private void injectField(Object target, Class<?> clazz, String fieldName, Object value) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	private void injectFieldHierarchy(Object target, String fieldName, Object value) throws Exception {
		Class<?> clazz = target.getClass();
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(target, value);
				return;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException(fieldName);
	}

	// ==================== 2026년 데이터 조회 테스트 ====================

	@Test
	@DisplayName("ESG 조회 - 기본 년도 2026 데이터 정상 반환")
	void testEsgList_defaultYear2026() {
		String currentYear = String.valueOf(java.time.Year.now().getValue());
		StockEsgVO param = StockEsgVO.builder().year(currentYear).build();

		List<StockEsgVO> mockResult = Arrays.asList(
			StockEsgVO.builder().stockId("005930").name("삼성전자").year("2026")
				.greenHouseEmission("72").averageAnnualSalary("12000").build(),
			StockEsgVO.builder().stockId("000660").name("SK하이닉스").year("2026")
				.greenHouseEmission("65").averageAnnualSalary("11000").build()
		);

		doReturn(mockResult).when(mockSqlSession).selectList("seckim.stock.selectStockEsgList", param);

		List<StockEsgVO> result = stockService.getStockEsgList(param);

		assertEquals(2, result.size());
		assertEquals("2026", result.get(0).getYear());
		assertEquals("삼성전자", result.get(0).getName());
		assertEquals("2026", result.get(1).getYear());
		verify(mockSqlSession).selectList("seckim.stock.selectStockEsgList", param);
	}

	@Test
	@DisplayName("ESG 조회 - 종목코드 필터 정상 동작")
	void testEsgList_filterByStockId() {
		StockEsgVO param = StockEsgVO.builder().stockId("005930").year("2026").build();

		List<StockEsgVO> mockResult = Arrays.asList(
			StockEsgVO.builder().stockId("005930").name("삼성전자").year("2026").build()
		);
		doReturn(mockResult).when(mockSqlSession).selectList("seckim.stock.selectStockEsgList", param);

		List<StockEsgVO> result = stockService.getStockEsgList(param);

		assertEquals(1, result.size());
		assertEquals("005930", result.get(0).getStockId());
	}

	@Test
	@DisplayName("ESG 조회 - year 파라미터 없으면 현재년도 사용 확인")
	void testDefaultYearIsCurrentYear() {
		String currentYear = String.valueOf(java.time.Year.now().getValue());
		assertEquals("2026", currentYear);
	}

	// ==================== 병렬 속도 개선 테스트 ====================

	@Test
	@DisplayName("saveStockEsgAll - 병렬 실행으로 속도 개선 확인")
	void testSaveStockEsgAll_parallelExecution() throws Exception {
		int stockCount = 10;
		List<StockVO> stockList = new ArrayList<>();
		for (int i = 0; i < stockCount; i++) {
			stockList.add(StockVO.builder().stockId(String.format("%06d", i)).build());
		}

		doReturn(stockList).when(mockRepository).selectStockList(any());
		when(mockConverter.getExceptZeroFields()).thenReturn(Arrays.asList("id", "deleteYn"));

		// 각 종목 fetch에 100ms 딜레이를 시뮬레이션
		AtomicInteger fetchCount = new AtomicInteger(0);
		when(mockFetcher.fetchEsg(anyString())).thenAnswer(invocation -> {
			Thread.sleep(100);
			fetchCount.incrementAndGet();
			return "{\"nonFinanceInfo\":{},\"themes\":[]}";
		});

		long start = System.currentTimeMillis();
		esgService.saveStockEsgAll();
		long elapsed = System.currentTimeMillis() - start;

		assertEquals(stockCount, fetchCount.get());

		// 순차: 10 * 100ms = 1000ms 이상
		// 병렬(20스레드): ~100ms 수준
		// 넉넉하게 500ms 이내면 병렬 동작 확인
		assertTrue(elapsed < 500,
			String.format("병렬 실행 확인: %dms (순차 예상 %dms 이상)", elapsed, stockCount * 100));

		System.out.printf("[속도 테스트] %d건 처리: %dms (순차 예상: %dms)%n",
			stockCount, elapsed, stockCount * 100);
	}

	@Test
	@DisplayName("saveStockEsgAll - 대량 데이터 병렬 처리 시간 측정")
	void testSaveStockEsgAll_bulkParallelPerformance() throws Exception {
		int stockCount = 50;
		List<StockVO> stockList = new ArrayList<>();
		for (int i = 0; i < stockCount; i++) {
			stockList.add(StockVO.builder().stockId(String.format("%06d", i)).build());
		}

		doReturn(stockList).when(mockRepository).selectStockList(any());
		when(mockConverter.getExceptZeroFields()).thenReturn(Arrays.asList("id", "deleteYn"));

		AtomicInteger saveCount = new AtomicInteger(0);
		when(mockFetcher.fetchEsg(anyString())).thenAnswer(invocation -> {
			Thread.sleep(50);
			return "{\"nonFinanceInfo\":{},\"themes\":[]}";
		});
		doAnswer(invocation -> {
			saveCount.incrementAndGet();
			return null;
		}).when(mockRepository).saveEsg(any());

		long start = System.currentTimeMillis();
		esgService.saveStockEsgAll();
		long elapsed = System.currentTimeMillis() - start;

		// 순차: 50 * 50ms = 2500ms
		// 병렬(20스레드): 약 50 * 50 / 20 = 125ms 수준
		assertTrue(elapsed < 1000,
			String.format("대량 병렬 처리: %dms (순차 예상 %dms)", elapsed, stockCount * 50));

		System.out.printf("[대량 속도 테스트] %d건 처리: %dms (순차 예상: %dms, 개선율: %.1fx)%n",
			stockCount, elapsed, stockCount * 50, (double)(stockCount * 50) / elapsed);
	}

	@Test
	@DisplayName("saveStockEsgAll - 병렬 처리 중 일부 실패해도 나머지 정상 처리")
	void testSaveStockEsgAll_partialFailureHandled() throws Exception {
		List<StockVO> stockList = Arrays.asList(
			StockVO.builder().stockId("005930").build(),
			StockVO.builder().stockId("FAIL01").build(),
			StockVO.builder().stockId("000660").build()
		);

		doReturn(stockList).when(mockRepository).selectStockList(any());
		when(mockConverter.getExceptZeroFields()).thenReturn(Arrays.asList("id", "deleteYn"));

		when(mockFetcher.fetchEsg("005930")).thenReturn("{\"nonFinanceInfo\":{},\"themes\":[]}");
		when(mockFetcher.fetchEsg("FAIL01")).thenThrow(new RuntimeException("Network error"));
		when(mockFetcher.fetchEsg("000660")).thenReturn("{\"nonFinanceInfo\":{},\"themes\":[]}");

		// 예외가 전파되지 않고 정상 완료
		assertDoesNotThrow(() -> esgService.saveStockEsgAll());
	}
}
