package com.kangong.test.stock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kangong.stock.model.StockCategoryLinkVO;
import com.kangong.stock.model.StockCategoryVO;
import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockMarketIndexVO;
import com.kangong.stock.model.StockVO;
import com.kangong.stock.service.StockRepository;

class StockRepositoryTest {

	StockRepository stockRepository;
	SqlSession mockSqlSession;

	@BeforeEach
	void setUp() throws Exception {
		stockRepository = new StockRepository();
		mockSqlSession = mock(SqlSession.class);
		Field field = StockRepository.class.getDeclaredField("sqlSession");
		field.setAccessible(true);
		field.set(stockRepository, mockSqlSession);
	}

	// ==================== Stock ====================

	@Test
	@DisplayName("selectStockList - 올바른 쿼리 ID로 호출")
	void testSelectStockList() {
		StockVO param = StockVO.builder().build();
		List<StockVO> expected = Arrays.asList(
			StockVO.builder().stockId("005930").build(),
			StockVO.builder().stockId("000660").build()
		);
		doReturn(expected).when(mockSqlSession).selectList("seckim.stock.select", param);

		List<StockVO> result = stockRepository.selectStockList(param);

		assertEquals(2, result.size());
		assertEquals("005930", result.get(0).getStockId());
		verify(mockSqlSession).selectList("seckim.stock.select", param);
	}

	@Test
	@DisplayName("selectStock - 올바른 쿼리 ID로 단건 조회")
	void testSelectStock() {
		StockVO param = StockVO.builder().stockId("005930").build();
		StockVO expected = StockVO.builder().stockId("005930").name("삼성전자").build();
		doReturn(expected).when(mockSqlSession).selectOne("seckim.stock.select", param);

		StockVO result = stockRepository.selectStock(param);

		assertEquals("삼성전자", result.getName());
		verify(mockSqlSession).selectOne("seckim.stock.select", param);
	}

	@Test
	@DisplayName("saveSimple - seckim.stock.saveSimple 호출")
	void testSaveSimple() {
		StockVO stockVO = StockVO.builder().stockId("005930").build();

		stockRepository.saveSimple(stockVO);

		verify(mockSqlSession).update("seckim.stock.saveSimple", stockVO);
	}

	@Test
	@DisplayName("saveStock - seckim.stock.save 호출")
	void testSaveStock() {
		StockVO stockVO = StockVO.builder().stockId("005930").name("삼성전자").build();

		stockRepository.saveStock(stockVO);

		verify(mockSqlSession).update("seckim.stock.save", stockVO);
	}

	@Test
	@DisplayName("saveMasterForStockId - seckim.mobilestock.saveMasterForStockId 호출")
	void testSaveMasterForStockId() {
		StockVO stockVO = StockVO.builder().stockId("005930").build();

		stockRepository.saveMasterForStockId(stockVO);

		verify(mockSqlSession).update("seckim.mobilestock.saveMasterForStockId", stockVO);
	}

	// ==================== DailyPrice ====================

	@Test
	@DisplayName("saveDailyPrice - seckim.stock.saveStockDailyPrice 호출")
	void testSaveDailyPrice() {
		StockDailyPriceVO vo = StockDailyPriceVO.builder()
			.stockId("005930").tradingDate("2024-01-15").build();

		stockRepository.saveDailyPrice(vo);

		verify(mockSqlSession).update("seckim.stock.saveStockDailyPrice", vo);
	}

	// ==================== Financial ====================

	@Test
	@DisplayName("saveFinancial - seckim.stock.saveFinancail 호출")
	void testSaveFinancial() {
		StockFinancialVO vo = StockFinancialVO.builder()
			.stockId("005930").year("2023").build();

		stockRepository.saveFinancial(vo);

		verify(mockSqlSession).update("seckim.stock.saveFinancail", vo);
	}

	// ==================== ESG ====================

	@Test
	@DisplayName("saveEsg - seckim.stock.saveStockEsg 호출")
	void testSaveEsg() {
		StockEsgVO vo = StockEsgVO.builder().stockId("005930").year("2023").build();

		stockRepository.saveEsg(vo);

		verify(mockSqlSession).update("seckim.stock.saveStockEsg", vo);
	}

	// ==================== MarketIndex ====================

	@Test
	@DisplayName("saveMarketIndex - seckim.stock.saveStockMarketIndex 호출")
	void testSaveMarketIndex() {
		StockMarketIndexVO vo = StockMarketIndexVO.builder().yyyymmdd("20240115").build();

		stockRepository.saveMarketIndex(vo);

		verify(mockSqlSession).update("seckim.stock.saveStockMarketIndex", vo);
	}

	@Test
	@DisplayName("selectIndexList - seckim.mobilestock.selectIndexList 호출")
	void testSelectIndexList() {
		StockMarketIndexVO param = StockMarketIndexVO.builder().build();
		List<StockMarketIndexVO> expected = Arrays.asList(
			StockMarketIndexVO.builder().yyyymmdd("20240115").build()
		);
		doReturn(expected).when(mockSqlSession).selectList("seckim.mobilestock.selectIndexList", param);

		List<StockMarketIndexVO> result = stockRepository.selectIndexList(param);

		assertEquals(1, result.size());
		verify(mockSqlSession).selectList("seckim.mobilestock.selectIndexList", param);
	}

	// ==================== Category ====================

	@Test
	@DisplayName("saveCategory - seckim.stock.saveStockCategory 호출")
	void testSaveCategory() {
		StockCategoryVO vo = StockCategoryVO.builder()
			.categoryNo("100").categoryName("반도체").build();

		stockRepository.saveCategory(vo);

		verify(mockSqlSession).update("seckim.stock.saveStockCategory", vo);
	}

	@Test
	@DisplayName("saveCategoryLink - seckim.stock.saveStockCategoryLink 호출")
	void testSaveCategoryLink() {
		StockCategoryLinkVO vo = StockCategoryLinkVO.builder()
			.categoryNo("100").stockId("005930").build();

		stockRepository.saveCategoryLink(vo);

		verify(mockSqlSession).update("seckim.stock.saveStockCategoryLink", vo);
	}

	// ==================== 전체 검증 ====================

	@Test
	@DisplayName("모든 저장 메서드가 정확히 1회씩 sqlSession 호출")
	void testAllSaveMethodsCallSqlSessionOnce() {
		stockRepository.saveSimple(StockVO.builder().build());
		stockRepository.saveStock(StockVO.builder().build());
		stockRepository.saveDailyPrice(StockDailyPriceVO.builder().build());
		stockRepository.saveFinancial(StockFinancialVO.builder().build());
		stockRepository.saveEsg(StockEsgVO.builder().build());
		stockRepository.saveMarketIndex(StockMarketIndexVO.builder().build());
		stockRepository.saveCategory(StockCategoryVO.builder().build());
		stockRepository.saveCategoryLink(StockCategoryLinkVO.builder().build());
		stockRepository.saveMasterForStockId(StockVO.builder().build());

		verify(mockSqlSession, times(9)).update(anyString(), any());
	}
}