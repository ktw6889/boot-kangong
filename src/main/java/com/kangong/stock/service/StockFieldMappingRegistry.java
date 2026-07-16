package com.kangong.stock.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.kangong.stock.model.StockDailyPriceVO;
import com.kangong.stock.model.StockEsgVO;
import com.kangong.stock.model.StockFinancialVO;
import com.kangong.stock.model.StockMarketIndexVO;
import com.kangong.stock.model.StockVO;

public final class StockFieldMappingRegistry {

	private StockFieldMappingRegistry() {}

	private static Map<String, String> buildMapping(Class<?> voClass, String... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new IllegalArgumentException("키-값 쌍이 맞지 않습니다");
		}
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < keysAndValues.length; i += 2) {
			String key = keysAndValues[i];
			String fieldName = keysAndValues[i + 1];
			if (map.containsKey(key)) {
				throw new IllegalStateException("중복 매핑 키: " + key);
			}
			try {
				voClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				throw new IllegalStateException(
						voClass.getSimpleName() + "에 필드가 없습니다: " + fieldName);
			}
			map.put(key, fieldName);
		}
		return Collections.unmodifiableMap(map);
	}

	// ==================== StockVO ====================

	private static final Map<String, String> STOCK_MOBILE_API = buildMapping(StockVO.class,
			"openPrice", "price",
			"lastClosePrice", "priceBeforeday",
			"accumulatedTradingVolume", "volumn",
			"marketValue", "marketCapitalization",
			"foreignRate", "foreignerRatio",
			"highPriceOf52Weeks", "max52",
			"lowPriceOf52Weeks", "min52",
			"per", "per",
			"eps", "eps",
			"cnsPer", "estimationPer",
			"cnsEps", "estimationEps",
			"pbr", "pbr",
			"bps", "bps",
			"dividendYieldRatio", "dividendRate",
			"recommMean", "investmentOpinion",
			"priceTargetMean", "targetPrice",
			"fundPay", "fundPay"
	);

	private static final Map<String, String> STOCK_HTML_TABLE = buildMapping(StockVO.class,
			"종목명", "name",
			"현재가", "price",
			"전일가", "priceBeforeday",
			"액면가", "faceValue",
			"시가총액", "marketCapitalization",
			"상장주식수", "stockQty",
			"외국인비율", "foreignerRatio",
			"거래량", "volumn",
			"PER", "per",
			"ROE", "roe"
	);

	// ==================== StockDailyPriceVO ====================

	private static final Map<String, String> DAILY_PRICE_MOBILE_API = buildMapping(StockDailyPriceVO.class,
			"localTradedAt", "tradingDate",
			"closePrice", "closingPrice",
			"compareToPreviousClosePrice", "previousDayRate",
			"fluctuationsRatio", "fluctuationRate",
			"accumulatedTradingVolume", "volumn"
	);

	private static final Map<String, String> DAILY_PRICE_HTML_TABLE = buildMapping(StockDailyPriceVO.class,
			"날짜", "tradingDate",
			"종가", "closingPrice",
			"전일비", "previousDayRate",
			"등락률", "fluctuationRate",
			"거래량", "volumn",
			"기관_순매매량", "organTradingVolumn",
			"외국인_순매매량", "foreignTradingVolumn",
			"외국인_보유주수", "foreignHoldingVolumn",
			"외국인_보유율", "foreignHoldingRate"
	);

	// ==================== StockFinancialVO ====================

	private static final Map<String, String> FINANCIAL_MOBILE_API = buildMapping(StockFinancialVO.class,
			"매출액", "totalSales",
			"영업이익", "profits",
			"당기순이익", "earnings",
			"영업이익률", "profitsRatio",
			"순이익률", "netProfitRatio",
			"ROE", "roe",
			"부채비율", "deptRatio",
			"유보율", "reserveRatio",
			"EPS", "eps",
			"PER", "per",
			"BPS", "bps",
			"PBR", "pbr",
			"주당배당금", "dividendsPerShare"
	);

	private static final Map<String, String> FINANCIAL_HTML_TABLE = buildMapping(StockFinancialVO.class,
			"매출액", "totalSales",
			"영업이익", "profits",
			"당기순이익", "earnings",
			"영업이익률", "profitsRatio",
			"순이익률", "netProfitRatio",
			"ROE(%)", "roe",
			"부채비율", "deptRatio",
			"자본유보율", "reserveRatio",
			"EPS(원)", "eps",
			"PER(배)", "per",
			"BPS(원)", "bps",
			"PBR(배)", "pbr",
			"현금DPS(원)", "dividendsPerShare",
			"현금배당수익률", "dividendsRate",
			"현금배당성향(%)", "dividendsTendency",
			"자산총계", "totalAssets",
			"발행주식수(보통주)", "sharesOutstanding",
			"부채총계", "totalDept",
			"자본총계", "totalCapital",
			"자본금", "capital"
	);

	private static final Map<String, String> BALANCE_SHEET_HTML_TABLE = buildMapping(StockFinancialVO.class,
			"유동자산", "liquidAsset",
			"유동부채", "liquidDept",
			"발행주식수", "totalStockQty",
			"보통주", "commonStockQty",
			"우선주", "preferredStockQty"
	);

	private static final Map<String, String> CASH_FLOW_API = buildMapping(StockFinancialVO.class,
			"영업활동현금흐름", "operatingCashFlow",
			"I. 영업활동현금흐름", "operatingCashFlow",
			"영업활동으로인한현금흐름", "operatingCashFlow",
			"영업활동", "operatingCashFlow"
	);

	// ==================== StockEsgVO ====================

	private static final Map<String, String> ESG_MOBILE_API = buildMapping(StockEsgVO.class,
			"E01", "waterRecyclingRate",
			"E02", "greenHouseEmission",
			"E03", "energyUsage",
			"E04", "wasteRecyclingRate",
			"E05", "fineDustUsage",
			"S01", "donation",
			"S02", "continuousServiceYear",
			"S04", "nonRegularEmplymentRate",
			"S05", "averageAnnualSalary",
			"G01", "executiveAverageAnnualSalary",
			"G02", "salaryRatio",
			"G03", "outsideDirectorRate",
			"G04", "directorateIndependence",
			"G05", "largestShareHolderRatio"
	);

	// ==================== StockMarketIndexVO ====================

	private static final Map<String, String> MARKET_INDEX_MOBILE_API = buildMapping(StockMarketIndexVO.class,
			"KRCALLBOKK", "domesticInterestCall",
			"KFIA114000", "domesticInterestCd",
			"KRCOFIXMANF", "domesticInterestCofixManf",
			"KRCOFIXOUTB", "domesticInterestCofixOutb",
			"KRNCOFIXOUTB", "domesticInterestNcofixOutb",
			"USFOMC=ECIX", "standardInterestUs",
			"KROCRT=ECIX", "standardInterestKr",
			"EUECBR=ECIX", "standardInterestEu",
			"GBBOEI=ECIX", "standardInterestGb",
			"JPINTN=ECIX", "standardInterestJp",
			"GCcv1", "metalGc",
			"CMDT_GD", "metalCmdt",
			"SIcv1", "metalSi",
			"HGcv1", "metalHg",
			"PLcv1", "metalPl",
			".CCFIDXSSE", "transportCcf",
			".SCFIDXSSE", "transportScf",
			".BADI", "transportBadi",
			".BACI", "transportBack",
			".BPNI", "transportBpni",
			".BSIS", "transportBsis",
			".BHSI", "transportBhsi",
			".BAID", "transportBaid",
			".BAIT", "transportBait",
			"US10YT=RR", "bondUs10yt",
			"KR10YT=RR", "bondKr10yt",
			"JP10YT=RR", "bondJp10yt",
			"DE10YT=RR", "bondDe10yt",
			"CN10YT=RR", "bondCn10yt",
			"CLcv1", "energyCl",
			"LCOcv1", "energyLco",
			"RBcv1", "energyRb",
			"HOcv1", "energyHo",
			"DCBc1", "energyDcb"
	);

	// ==================== Public 접근 메서드 ====================

	public static Map<String, String> stockMobileApi()          { return STOCK_MOBILE_API; }
	public static Map<String, String> stockHtmlTable()          { return STOCK_HTML_TABLE; }
	public static Map<String, String> dailyPriceMobileApi()     { return DAILY_PRICE_MOBILE_API; }
	public static Map<String, String> dailyPriceHtmlTable()     { return DAILY_PRICE_HTML_TABLE; }
	public static Map<String, String> financialMobileApi()      { return FINANCIAL_MOBILE_API; }
	public static Map<String, String> financialHtmlTable()      { return FINANCIAL_HTML_TABLE; }
	public static Map<String, String> balanceSheetHtmlTable()   { return BALANCE_SHEET_HTML_TABLE; }
	public static Map<String, String> cashFlowApi()             { return CASH_FLOW_API; }
	public static Map<String, String> esgMobileApi()            { return ESG_MOBILE_API; }
	public static Map<String, String> marketIndexMobileApi()    { return MARKET_INDEX_MOBILE_API; }
}
