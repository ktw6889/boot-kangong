package com.kangong.stock.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import jakarta.annotation.PreDestroy;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kangong.marketcycle.dto.MarketCycleDashboard;
import com.kangong.marketcycle.dto.SectorOverview;
import com.kangong.stock.model.MacroAdjCoeff;
import com.kangong.stock.model.MacroIndicatorVO;
import com.kangong.stock.model.StockInterestParamVO;
import com.kangong.stock.model.StockInterestVO;
import com.kangong.stock.model.TrendSignalVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MacroIndicatorService {

	private static final String YAHOO_CHART_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";
	private static final String FRED_CSV_URL = "https://fred.stlouisfed.org/graph/fredgraph.csv";
	private static final String VKOSPI_URL = "https://kr.investing.com/indices/kospi-volatility";

	private static final String USER_AGENT =
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

	private final ObjectMapper objectMapper = new ObjectMapper();
	private volatile double cachedUsdKrw = 0;
	private long cachedUsdKrwTime = 0;
	private volatile MacroAdjCoeff cachedAdjCoeff = null;
	private volatile long cachedAdjCoeffTime = 0;
	private static final long ADJ_COEFF_TTL = 24 * 60 * 60 * 1000L;
	private volatile TrendSignalVO cachedTrendSignal = null;
	private volatile long cachedTrendSignalTime = 0;
	private static final long TREND_TTL = 24 * 60 * 60 * 1000L;
	private final ExecutorService fetchExecutor = Executors.newFixedThreadPool(12);

	@PreDestroy
	public void shutdown() {
		fetchExecutor.shutdown();
	}

	private static final LinkedHashMap<String, String[]> INDICATORS = new LinkedHashMap<>();
	static {
		INDICATORS.put("^VIX", new String[]{"미국 공포지수 (VIX)", "fearIndex"});
		INDICATORS.put("BTC-USD", new String[]{"비트코인 (BTC)", "commodity"});
		INDICATORS.put("GC=F", new String[]{"금 (1돈)", "commodity"});
		INDICATORS.put("HG=F", new String[]{"구리 (1kg)", "commodity"});
		INDICATORS.put("KRW=X", new String[]{"달러/원 (USD/KRW)", "exchange"});
		INDICATORS.put("EURKRW=X", new String[]{"유로/원 (EUR/KRW)", "exchange"});
		INDICATORS.put("JPY=X", new String[]{"달러/엔 (USD/JPY)", "exchange"});
		INDICATORS.put("CNY=X", new String[]{"달러/위안 (USD/CNY)", "exchange"});
		INDICATORS.put("DX-Y.NYB", new String[]{"달러 인덱스 (DXY)", "exchange"});

		INDICATORS.put("^TNX", new String[]{"미국 10년물 금리 (%)", "bond"});
		INDICATORS.put("^FVX", new String[]{"미국 5년물 금리 (%)", "bond"});
		INDICATORS.put("^IRX", new String[]{"미국 13주 단기금리 (%)", "bond"});
		INDICATORS.put("^TYX", new String[]{"미국 30년물 금리 (%)", "bond"});

		INDICATORS.put("^GSPC", new String[]{"S&P 500", "index"});
		INDICATORS.put("^KS11", new String[]{"코스피 (KOSPI)", "index"});
		INDICATORS.put("^IXIC", new String[]{"나스닥 (NASDAQ)", "index"});
		INDICATORS.put("^KS200", new String[]{"코스피 200", "index"});
	}

	// ==================== Category-based AJAX ====================

	public List<MacroIndicatorVO> fetchByCategory(String category) {
		switch (category) {
			case "fearIndex": return fetchFearIndex();
			case "m2": return fetchM2Data();
			case "commodity": return fetchYahooByCategory("commodity");
			case "exchange": return fetchYahooByCategory("exchange");
			case "bond": return fetchBondData();
			case "index": return fetchYahooByCategory("index");
			default: return new ArrayList<>();
		}
	}

	private List<MacroIndicatorVO> fetchFearIndex() {
		CompletableFuture<MacroIndicatorVO> vixFuture = CompletableFuture.supplyAsync(() -> {
			try { return fetchYahooQuote("^VIX"); }
			catch (Exception e) { log.error("VIX 조회 오류", e); return null; }
		}, fetchExecutor);
		CompletableFuture<MacroIndicatorVO> vkospiFuture = CompletableFuture.supplyAsync(() -> {
			try { return fetchVkospi(); }
			catch (Exception e) { log.warn("VKOSPI 조회 실패: {}", e.getMessage()); return null; }
		}, fetchExecutor);

		List<MacroIndicatorVO> result = new ArrayList<>();
		try { MacroIndicatorVO v = vixFuture.get(20, TimeUnit.SECONDS); if (v != null) result.add(v); }
		catch (Exception e) { log.warn("VIX 대기 실패", e); }
		try { MacroIndicatorVO v = vkospiFuture.get(20, TimeUnit.SECONDS); if (v != null) result.add(v); }
		catch (Exception e) { log.warn("VKOSPI 대기 실패", e); }
		return result;
	}

	private List<MacroIndicatorVO> fetchYahooByCategory(String category) {
		List<CompletableFuture<MacroIndicatorVO>> futures = new ArrayList<>();
		for (Map.Entry<String, String[]> entry : INDICATORS.entrySet()) {
			if (category.equals(entry.getValue()[1])) {
				String symbol = entry.getKey();
				futures.add(CompletableFuture.supplyAsync(() -> {
					try { return fetchYahooQuote(symbol); }
					catch (Exception e) { log.warn("{} 조회 실패: {}", symbol, e.getMessage()); return null; }
				}, fetchExecutor));
			}
		}
		return futures.stream()
				.map(f -> { try { return f.get(20, TimeUnit.SECONDS); } catch (Exception e) { return null; } })
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	// ==================== USD/KRW Rate ====================

	private double getUsdKrwRate() {
		if (cachedUsdKrw > 0 && System.currentTimeMillis() - cachedUsdKrwTime < 300_000) {
			return cachedUsdKrw;
		}
		try {
			String url = YAHOO_CHART_URL + "KRW%3DX?range=1d&interval=1d";
			HttpURLConnection conn = openConnection(url);
			if (conn.getResponseCode() == 200) {
				String json = readResponse(conn);
				conn.disconnect();
				JsonNode meta = objectMapper.readTree(json).path("chart").path("result").get(0).get("meta");
				cachedUsdKrw = meta.get("regularMarketPrice").asDouble();
				cachedUsdKrwTime = System.currentTimeMillis();
				return cachedUsdKrw;
			}
			conn.disconnect();
		} catch (Exception e) {
			log.warn("USD/KRW 환율 조회 실패: {}", e.getMessage());
		}
		return cachedUsdKrw > 0 ? cachedUsdKrw : 1400;
	}

	// ==================== Yahoo v8 Chart API (no crumb needed) ====================

	private MacroIndicatorVO fetchYahooQuote(String symbol) throws Exception {
		String url = YAHOO_CHART_URL
				+ URLEncoder.encode(symbol, StandardCharsets.UTF_8.name())
				+ "?range=1d&interval=1d";

		HttpURLConnection conn = openConnection(url);
		if (conn.getResponseCode() != 200) {
			conn.disconnect();
			return null;
		}

		String json = readResponse(conn);
		conn.disconnect();

		JsonNode results = objectMapper.readTree(json).path("chart").path("result");
		if (!results.isArray() || results.isEmpty()) return null;

		JsonNode meta = results.get(0).get("meta");
		if (meta == null) return null;

		String[] info = INDICATORS.get(symbol);
		if (info == null) return null;

		double price = getDouble(meta, "regularMarketPrice");
		double prevClose = getDouble(meta, "chartPreviousClose");
		double high52 = getDouble(meta, "fiftyTwoWeekHigh");
		double low52 = getDouble(meta, "fiftyTwoWeekLow");
		String currency = getText(meta, "currency");

		if ("BTC-USD".equals(symbol)) {
			double krw = getUsdKrwRate();
			price = Math.round(price * krw);
			prevClose = Math.round(prevClose * krw);
			high52 = Math.round(high52 * krw);
			low52 = Math.round(low52 * krw);
			currency = "KRW";
		} else if ("GC=F".equals(symbol)) {
			double krw = getUsdKrwRate();
			double donFactor = 3.75 / 31.1035;
			price = Math.round(price * donFactor * krw);
			prevClose = Math.round(prevClose * donFactor * krw);
			high52 = Math.round(high52 * donFactor * krw);
			low52 = Math.round(low52 * donFactor * krw);
			currency = "KRW";
		} else if ("HG=F".equals(symbol)) {
			double krw = getUsdKrwRate();
			double kgFactor = krw / 0.453592;
			price = Math.round(price * kgFactor);
			prevClose = Math.round(prevClose * kgFactor);
			high52 = Math.round(high52 * kgFactor);
			low52 = Math.round(low52 * kgFactor);
			currency = "KRW";
		}

		double change = price - prevClose;
		double changePct = prevClose != 0 ? (change / prevClose) * 100 : 0;

		MacroIndicatorVO vo = MacroIndicatorVO.builder()
				.symbol(symbol)
				.name(getText(meta, "shortName"))
				.nameKr(info[0])
				.category(info[1])
				.price(formatNumber(price))
				.change(formatSignedNumber(change))
				.changePercent(formatSignedDecimal(changePct))
				.fiftyTwoWeekHigh(formatNumber(high52))
				.fiftyTwoWeekLow(formatNumber(low52))
				.previousClose(formatNumber(prevClose))
				.currency(currency)
				.priceRaw(price)
				.build();
		evaluateSignal(vo, high52, low52);
		return vo;
	}

	// ==================== VKOSPI (Investing.com) ====================

	private MacroIndicatorVO fetchVkospi() {
		try {
			Document doc = Jsoup.connect(VKOSPI_URL)
					.userAgent(USER_AGENT)
					.header("Accept-Language", "ko-KR,ko;q=0.9")
					.timeout(15000)
					.get();

			Element priceEl = doc.selectFirst("[data-test=instrument-price-last]");
			Element changeEl = doc.selectFirst("[data-test=instrument-price-change]");
			Element pctEl = doc.selectFirst("[data-test=instrument-price-change-percent]");

			if (priceEl == null) return null;

			String price = priceEl.text().trim();
			String change = changeEl != null ? changeEl.text().trim() : "0";
			String pctRaw = pctEl != null ? pctEl.text().trim() : "0";
			String pct = pctRaw.replaceAll("[()%]", "");

			if (!change.startsWith("+") && !change.startsWith("-")) {
				try {
					if (Double.parseDouble(change) > 0) change = "+" + change;
				} catch (NumberFormatException ignored) {}
			}

			return MacroIndicatorVO.builder()
					.symbol("VKOSPI")
					.name("V-KOSPI200")
					.nameKr("한국 공포지수 (VKOSPI)")
					.category("fearIndex")
					.price(price)
					.change(change)
					.changePercent(pct)
					.fiftyTwoWeekHigh("").fiftyTwoWeekLow("")
					.previousClose("")
					.currency("KRW")
					.build();
		} catch (Exception e) {
			log.warn("VKOSPI 크롤링 실패: {}", e.getMessage());
			return null;
		}
	}

	// ==================== M2 (FRED CSV) ====================

	private List<MacroIndicatorVO> fetchM2Data() {
		List<MacroIndicatorVO> result = new ArrayList<>();
		try {
			String csvData = fetchFredCsv("M2SL", "2024-01-01");
			if (csvData != null) {
				result.addAll(parseM2Csv(csvData, "미국 M2 통화량 (십억달러)", "M2SL", "USD"));
			}
		} catch (Exception e) {
			log.warn("미국 M2 조회 실패: {}", e.getMessage());
		}

		try {
			MacroIndicatorVO krM2 = fetchKrM2Data();
			if (krM2 != null) {
				result.add(krM2);
			}
		} catch (Exception e) {
			log.warn("한국 M2 조회 실패: {}", e.getMessage());
		}

		return result;
	}

	private MacroIndicatorVO fetchKrM2Data() {
		try {
			String html = Jsoup.connect("https://tradingeconomics.com/south-korea/money-supply-m2")
					.userAgent(USER_AGENT)
					.header("Accept-Language", "en-US,en;q=0.9")
					.ignoreContentType(true)
					.timeout(15000)
					.execute()
					.body();

			if (html == null) return null;

			java.util.regex.Pattern p = java.util.regex.Pattern.compile(
					"Money Supply M2 in South Korea (?:increased|decreased) to ([\\d,.]+)\\s+KRW Billion in (\\w+) from ([\\d,.]+)\\s+KRW Billion in (\\w+) of (\\d+)");
			java.util.regex.Matcher m = p.matcher(html);
			if (!m.find()) return null;

			double current = Double.parseDouble(m.group(1).replace(",", ""));
			String currentMonth = m.group(2);
			double previous = Double.parseDouble(m.group(3).replace(",", ""));
			String prevMonth = m.group(4);
			String year = m.group(5);

			double currentJo = Math.round(current / 10.0) / 100.0;
			double previousJo = Math.round(previous / 10.0) / 100.0;
			double changeJo = Math.round((currentJo - previousJo) * 100.0) / 100.0;
			double changePct = previousJo != 0 ? (changeJo / previousJo) * 100 : 0;

			return MacroIndicatorVO.builder()
					.symbol("M2_KR")
					.name("MoM: " + year + " " + prevMonth + " → " + currentMonth)
					.nameKr("한국 M2 통화량 (조원)")
					.category("m2")
					.price(formatNumber(currentJo))
					.change(formatSignedNumber(changeJo))
					.changePercent(formatSignedDecimal(changePct))
					.fiftyTwoWeekHigh("").fiftyTwoWeekLow("")
					.previousClose(formatNumber(previousJo))
					.currency("KRW")
					.build();
		} catch (Exception e) {
			log.warn("한국 M2 크롤링 실패: {}", e.getMessage());
			return null;
		}
	}

	private List<MacroIndicatorVO> parseM2Csv(String csvData, String nameKr, String symbol, String currency) {
		List<MacroIndicatorVO> result = new ArrayList<>();
		String[] lines = csvData.split("\n");

		int lastIdx = lines.length - 1;
		while (lastIdx > 0) {
			String line = lines[lastIdx].trim();
			if (line.isEmpty()) { lastIdx--; continue; }
			String[] parts = line.split(",");
			if (parts.length >= 2 && !".".equals(parts[1].trim())) break;
			lastIdx--;
		}
		if (lastIdx < 2) return result;

		try {
			String[] latest = lines[lastIdx].split(",");
			String[] prev = lines[lastIdx - 1].split(",");
			if (latest.length < 2 || prev.length < 2) return result;

			String latestVal = latest[1].trim();
			String prevVal = prev[1].trim();
			if (".".equals(latestVal) || ".".equals(prevVal)) return result;

			double latestValue = Double.parseDouble(latestVal);
			double prevValue = Double.parseDouble(prevVal);
			double change = latestValue - prevValue;
			double changePct = (change / prevValue) * 100;

			result.add(MacroIndicatorVO.builder()
					.symbol(symbol)
					.name("MoM: " + prev[0].trim() + " → " + latest[0].trim())
					.nameKr(nameKr + " (전월비)")
					.category("m2")
					.price(formatNumber(latestValue))
					.change(formatSignedNumber(change))
					.changePercent(formatSignedDecimal(changePct))
					.fiftyTwoWeekHigh("").fiftyTwoWeekLow("")
					.previousClose(formatNumber(prevValue))
					.currency(currency)
					.build());

			if (lastIdx >= 13) {
				String[] yearAgo = lines[lastIdx - 12].split(",");
				if (yearAgo.length >= 2 && !".".equals(yearAgo[1].trim())) {
					double yearAgoValue = Double.parseDouble(yearAgo[1].trim());
					double yoyChange = latestValue - yearAgoValue;
					double yoyPct = (yoyChange / yearAgoValue) * 100;

					result.add(MacroIndicatorVO.builder()
							.symbol(symbol + "_YOY")
							.name("YoY: " + yearAgo[0].trim() + " → " + latest[0].trim())
							.nameKr(nameKr + " (전년동월비)")
							.category("m2")
							.price(formatNumber(latestValue))
							.change(formatSignedNumber(yoyChange))
							.changePercent(formatSignedDecimal(yoyPct))
							.fiftyTwoWeekHigh("").fiftyTwoWeekLow("")
							.previousClose(formatNumber(yearAgoValue))
							.currency(currency)
							.build());
				}
			}
		} catch (NumberFormatException e) {
			log.warn("M2 파싱 실패: {}", e.getMessage());
		}
		return result;
	}

	private String fetchFredCsv(String seriesId, String startDate) throws Exception {
		String url = FRED_CSV_URL + "?id=" + seriesId + "&cosd=" + startDate;
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/csv, */*");
		conn.setInstanceFollowRedirects(true);
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(30000);

		int responseCode = conn.getResponseCode();
		if (responseCode != 200) {
			conn.disconnect();
			return null;
		}

		String csv = readResponse(conn);
		conn.disconnect();

		if (csv == null || !csv.contains(",") || csv.contains("<html")) return null;
		return csv;
	}

	// ==================== Chart Data ====================

	public Map<String, Object> fetchChartData(String category) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("labels", new ArrayList<>());
		result.put("datasets", new ArrayList<>());

		List<String[]> symbols = getSymbolsForCategory(category);
		TreeSet<String> allLabels = new TreeSet<>();
		List<Map<String, List<?>>> rawResults = new ArrayList<>();
		List<String[]> validSymbols = new ArrayList<>();

		for (String[] sym : symbols) {
			try {
				List<String> labels;
				List<Double> data;

				if ("M2SL".equals(sym[0])) {
					Map<String, Object> fredData = parseFredCsvForChart("M2SL");
					if (fredData == null) continue;
					labels = castList(fredData.get("labels"));
					data = castList(fredData.get("data"));
				} else if ("M2_KR".equals(sym[0])) {
					Map<String, Object> krData = fetchKrM2ChartData();
					if (krData == null) continue;
					labels = castList(krData.get("labels"));
					data = castList(krData.get("data"));
				} else {
					Map<String, Object> yahooData = fetchYahooChartHistory(sym[0]);
					if (yahooData == null) continue;
					labels = castList(yahooData.get("labels"));
					data = castList(yahooData.get("data"));
					if ("BTC-USD".equals(sym[0])) {
						double krw = getUsdKrwRate();
						data.replaceAll(v -> v != null ? (double) Math.round(v * krw) : null);
					} else if ("GC=F".equals(sym[0])) {
						double krw = getUsdKrwRate();
						double donFactor = 3.75 / 31.1035;
						data.replaceAll(v -> v != null ? (double) Math.round(v * donFactor * krw) : null);
					} else if ("HG=F".equals(sym[0])) {
						double krw = getUsdKrwRate();
						double kgFactor = krw / 0.453592;
						data.replaceAll(v -> v != null ? (double) Math.round(v * kgFactor) : null);
					}
				}

				if (labels.isEmpty()) continue;
				allLabels.addAll(labels);
				Map<String, List<?>> raw = new LinkedHashMap<>();
				raw.put("labels", labels);
				raw.put("data", data);
				rawResults.add(raw);
				validSymbols.add(sym);
			} catch (Exception e) {
				log.warn("차트 데이터 오류 ({}): {}", sym[0], e.getMessage());
			}
		}

		List<String> masterLabels = new ArrayList<>(allLabels);
		List<Map<String, Object>> datasets = new ArrayList<>();

		for (int i = 0; i < rawResults.size(); i++) {
			List<String> labels = castList(rawResults.get(i).get("labels"));
			List<Double> data = castList(rawResults.get(i).get("data"));
			Map<String, Double> dateMap = new LinkedHashMap<>();
			for (int j = 0; j < labels.size() && j < data.size(); j++) {
				dateMap.put(labels.get(j), data.get(j));
			}

			List<Double> aligned = new ArrayList<>();
			for (String lbl : masterLabels) {
				aligned.add(dateMap.getOrDefault(lbl, null));
			}

			for (int j = 0; j < aligned.size(); j++) {
				if (aligned.get(j) == null) {
					Double prev = null, next = null;
					for (int k = j - 1; k >= 0; k--) { if (aligned.get(k) != null) { prev = aligned.get(k); break; } }
					for (int k = j + 1; k < aligned.size(); k++) { if (aligned.get(k) != null) { next = aligned.get(k); break; } }
					if (prev != null && next != null) aligned.set(j, Math.round((prev + next) / 2.0 * 100.0) / 100.0);
					else if (prev != null) aligned.set(j, prev);
					else if (next != null) aligned.set(j, next);
				}
			}

			String[] sym = validSymbols.get(i);
			Map<String, Object> ds = new LinkedHashMap<>();
			ds.put("label", sym[1]);
			ds.put("data", aligned);
			ds.put("borderColor", sym[2]);
			ds.put("fill", false);
			datasets.add(ds);
		}

		result.put("labels", masterLabels);
		result.put("datasets", datasets);
		return result;
	}

	private Map<String, Object> fetchYahooChartHistory(String symbol) throws Exception {
		String url = YAHOO_CHART_URL
				+ URLEncoder.encode(symbol, StandardCharsets.UTF_8.name())
				+ "?range=1y&interval=1mo";

		HttpURLConnection conn = openConnection(url);
		if (conn.getResponseCode() != 200) {
			conn.disconnect();
			return null;
		}

		String json = readResponse(conn);
		conn.disconnect();

		JsonNode results = objectMapper.readTree(json).path("chart").path("result");
		if (!results.isArray() || results.isEmpty()) return null;

		JsonNode first = results.get(0);
		JsonNode timestamps = first.get("timestamp");
		JsonNode quote = first.path("indicators").path("quote");
		if (timestamps == null || !quote.isArray() || quote.isEmpty()) return null;

		JsonNode closes = quote.get(0).get("close");
		if (closes == null) return null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		List<String> labels = new ArrayList<>();
		List<Double> data = new ArrayList<>();

		for (int i = 0; i < timestamps.size() && i < closes.size(); i++) {
			long ts = timestamps.get(i).asLong();
			labels.add(sdf.format(new Date(ts * 1000)));
			JsonNode closeNode = closes.get(i);
			data.add(closeNode != null && !closeNode.isNull()
					? Math.round(closeNode.asDouble() * 100.0) / 100.0 : null);
		}

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) == null) {
				Double prev = null, next = null;
				for (int j = i - 1; j >= 0; j--) { if (data.get(j) != null) { prev = data.get(j); break; } }
				for (int j = i + 1; j < data.size(); j++) { if (data.get(j) != null) { next = data.get(j); break; } }
				if (prev != null && next != null) data.set(i, Math.round((prev + next) / 2.0 * 100.0) / 100.0);
				else if (prev != null) data.set(i, prev);
				else if (next != null) data.set(i, next);
			}
		}

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("labels", labels);
		result.put("data", data);
		return result;
	}

	private Map<String, Object> parseFredCsvForChart(String seriesId) throws Exception {
		String csv = fetchFredCsv(seriesId, "2024-01-01");
		if (csv == null) return null;

		String[] lines = csv.split("\n");
		List<String> labels = new ArrayList<>();
		List<Double> data = new ArrayList<>();

		int startLine = Math.max(1, lines.length - 13);
		for (int i = startLine; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.isEmpty()) continue;
			String[] parts = line.split(",");
			if (parts.length < 2) continue;
			String val = parts[1].trim();
			if (".".equals(val)) continue;
			try {
				labels.add(parts[0].trim().substring(0, 7));
				data.add(Double.parseDouble(val));
			} catch (Exception e) { /* skip */ }
		}

		if (labels.isEmpty()) return null;

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("labels", labels);
		result.put("data", data);
		return result;
	}

	private Map<String, Object> fetchKrM2ChartData() {
		try {
			String url = "https://snapshot.bok.or.kr/api/chart/exportChart?chart_id=527&lang=ko";
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Referer", "https://snapshot.bok.or.kr/");
			conn.setInstanceFollowRedirects(true);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(30000);
			if (conn.getResponseCode() != 200) { conn.disconnect(); return null; }

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (InputStream is = conn.getInputStream()) {
				byte[] buf = new byte[8192];
				int n;
				while ((n = is.read(buf)) != -1) baos.write(buf, 0, n);
			}
			conn.disconnect();

			List<String> sharedStrings = new ArrayList<>();
			byte[] sheetBytes = null;

			try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					ByteArrayOutputStream entryBaos = new ByteArrayOutputStream();
					byte[] buf = new byte[4096];
					int n;
					while ((n = zis.read(buf)) != -1) entryBaos.write(buf, 0, n);

					if (entry.getName().contains("sharedStrings")) {
						org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance()
								.newDocumentBuilder().parse(new ByteArrayInputStream(entryBaos.toByteArray()));
						org.w3c.dom.NodeList tNodes = doc.getElementsByTagName("t");
						for (int i = 0; i < tNodes.getLength(); i++) {
							sharedStrings.add(tNodes.item(i).getTextContent());
						}
					} else if (entry.getName().contains("sheet1")) {
						sheetBytes = entryBaos.toByteArray();
					}
				}
			}

			if (sheetBytes == null) return null;

			org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(new ByteArrayInputStream(sheetBytes));
			org.w3c.dom.NodeList rows = doc.getElementsByTagName("row");

			List<String> labels = new ArrayList<>();
			List<Double> data = new ArrayList<>();

			for (int i = 0; i < rows.getLength(); i++) {
				org.w3c.dom.Element row = (org.w3c.dom.Element) rows.item(i);
				org.w3c.dom.NodeList cells = row.getElementsByTagName("c");
				if (cells.getLength() < 3) continue;

				String dateVal = getCellValue(cells.item(0), sharedStrings);
				String m2Val = getCellValue(cells.item(2), sharedStrings);

				if (dateVal == null || !dateVal.matches("\\d{4}-\\d{2}")) continue;
				try {
					labels.add(dateVal);
					data.add(Math.round(Double.parseDouble(m2Val) * 10.0) / 10.0);
				} catch (Exception e) { /* skip */ }
			}

			int startIdx = Math.max(0, labels.size() - 13);
			labels = labels.subList(startIdx, labels.size());
			data = data.subList(startIdx, data.size());

			if (labels.isEmpty()) return null;
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("labels", labels);
			result.put("data", data);
			return result;
		} catch (Exception e) {
			log.warn("한국 M2 차트 데이터 조회 실패: {}", e.getMessage());
			return null;
		}
	}

	private String getCellValue(org.w3c.dom.Node cell, List<String> sharedStrings) {
		if (cell == null) return null;
		org.w3c.dom.Element el = (org.w3c.dom.Element) cell;
		org.w3c.dom.NodeList vList = el.getElementsByTagName("v");
		if (vList.getLength() == 0) return null;
		String val = vList.item(0).getTextContent();
		if ("s".equals(el.getAttribute("t")) && val.matches("\\d+")) {
			int idx = Integer.parseInt(val);
			return idx < sharedStrings.size() ? sharedStrings.get(idx) : null;
		}
		return val;
	}

	private List<String[]> getSymbolsForCategory(String category) {
		List<String[]> symbols = new ArrayList<>();
		switch (category) {
			case "fearIndex":
				symbols.add(new String[]{"^VIX", "VIX (미국)", "#d63031"});
				break;
			case "m2":
				symbols.add(new String[]{"M2SL", "미국 M2 (십억달러)", "#6c5ce7"});
				symbols.add(new String[]{"M2_KR", "한국 M2 (조원)", "#e17055"});
				break;
			case "commodity":
				symbols.add(new String[]{"GC=F", "금 1돈 (KRW)", "#f9ca24"});
				symbols.add(new String[]{"HG=F", "구리 1kg (KRW)", "#e17055"});
				symbols.add(new String[]{"BTC-USD", "비트코인 (KRW)", "#0984e3"});
				break;
			case "exchange":
				symbols.add(new String[]{"KRW=X", "USD/KRW", "#d63031"});
				symbols.add(new String[]{"EURKRW=X", "EUR/KRW", "#0984e3"});
				symbols.add(new String[]{"JPY=X", "USD/JPY", "#00b894"});
				symbols.add(new String[]{"CNY=X", "USD/CNY", "#e17055"});
				symbols.add(new String[]{"DX-Y.NYB", "DXY", "#636e72"});
				break;
			case "bond":
				symbols.add(new String[]{"^TNX", "10Y 금리", "#d63031"});
				symbols.add(new String[]{"^FVX", "5Y 금리", "#e17055"});
				symbols.add(new String[]{"^IRX", "3M 금리", "#0984e3"});
				symbols.add(new String[]{"^TYX", "30Y 금리", "#6c5ce7"});
				break;
			case "index":
				symbols.add(new String[]{"^GSPC", "S&P 500", "#d63031"});
				symbols.add(new String[]{"^IXIC", "NASDAQ", "#0984e3"});
				symbols.add(new String[]{"^KS11", "KOSPI", "#00b894"});
				symbols.add(new String[]{"^KS200", "KOSPI 200", "#e17055"});
				break;
		}
		return symbols;
	}

	// ==================== Bond Data ====================

	private List<MacroIndicatorVO> fetchBondData() {
		List<MacroIndicatorVO> result = fetchYahooByCategory("bond");

		// 장단기 금리차 계산 (10Y - 13주)
		double yield10y = 0, yield3m = 0;
		for (MacroIndicatorVO vo : result) {
			if ("^TNX".equals(vo.getSymbol())) yield10y = vo.getPriceRaw();
			if ("^IRX".equals(vo.getSymbol())) yield3m = vo.getPriceRaw();
		}
		if (yield10y > 0 && yield3m > 0) {
			double spread = yield10y - yield3m;
			MacroIndicatorVO spreadVo = MacroIndicatorVO.builder()
					.symbol("SPREAD_10Y3M")
					.name("10Y - 3M")
					.nameKr("장단기 금리차 (10Y-3M)")
					.category("bond")
					.price(String.format("%.2f", spread))
					.change(spread >= 0 ? "정상" : "역전")
					.changePercent("-")
					.fiftyTwoWeekHigh("").fiftyTwoWeekLow("")
					.previousClose("")
					.currency("%p")
					.priceRaw(spread)
					.build();
			evaluateSignal(spreadVo, 0, 0);
			result.add(0, spreadVo);
		}
		return result;
	}

	// ==================== Signal Evaluation ====================

	private void evaluateSignal(MacroIndicatorVO vo, double high52, double low52) {
		double p = vo.getPriceRaw();
		String symbol = vo.getSymbol();

		switch (symbol) {
			case "^VIX":
				if (p >= 35) setSignal(vo, "BUY", "극단적 공포 — 역발상 매수 구간");
				else if (p >= 30) setSignal(vo, "BUY", "공포 확대 — 분할 매수 고려");
				else if (p >= 20) setSignal(vo, "CAUTION", "변동성 확대 — 신규 매수 주의");
				else if (p <= 12) setSignal(vo, "SELL", "극단적 안일 — 과열 경고");
				else setSignal(vo, "NEUTRAL", "안정 구간 (12~20)");
				break;
			case "VKOSPI":
				if (p >= 30) setSignal(vo, "BUY", "극단적 공포 — 한국시장 역발상 매수");
				else if (p >= 20) setSignal(vo, "CAUTION", "변동성 확대");
				else if (p <= 10) setSignal(vo, "SELL", "과도한 안일 — 과열 주의");
				else setSignal(vo, "NEUTRAL", "안정 구간");
				break;
			case "DX-Y.NYB":
				if (p > 108) setSignal(vo, "CAUTION", "달러 강세 — 신흥국/원자재 압박");
				else if (p > 105) setSignal(vo, "CAUTION", "달러 다소 강세");
				else if (p < 95) setSignal(vo, "BUY", "달러 약세 — 신흥국/금 매수 유리");
				else setSignal(vo, "NEUTRAL", "보통 수준 (95~105)");
				break;
			case "KRW=X":
				if (p > 1450) setSignal(vo, "SELL", "원화 급락 — 외국인 이탈 위험");
				else if (p > 1350) setSignal(vo, "CAUTION", "원화 약세 — 외국인 매도 주의");
				else if (p < 1200) setSignal(vo, "BUY", "원화 강세 — 외국인 유입 기대");
				else setSignal(vo, "NEUTRAL", "보통 환율 수준");
				break;
			case "^TNX":
				if (p > 5.0) setSignal(vo, "SELL", "금리 과도 상승 — 주식 매도 고려");
				else if (p > 4.5) setSignal(vo, "CAUTION", "금리 부담 — 성장주 주의");
				else if (p < 3.0) setSignal(vo, "BUY", "저금리 — 주식/채권 매수 유리");
				else if (p < 3.5) setSignal(vo, "BUY", "금리 하락 구간 — 채권 매수 고려");
				else setSignal(vo, "NEUTRAL", "보통 금리 수준 (3.5~4.5%)");
				break;
			case "^TYX":
				if (p > 5.5) setSignal(vo, "SELL", "장기금리 과도 — 경기 과열/인플레 우려");
				else if (p > 5.0) setSignal(vo, "CAUTION", "장기금리 상승 압박");
				else if (p < 3.5) setSignal(vo, "BUY", "장기금리 하락 — 채권 매수 유리");
				else setSignal(vo, "NEUTRAL", "보통 수준");
				break;
			case "^FVX":
				if (p > 5.0) setSignal(vo, "CAUTION", "중기금리 상승 압박");
				else if (p < 3.0) setSignal(vo, "BUY", "중기금리 하락 — 매수 유리");
				else setSignal(vo, "NEUTRAL", "보통 수준");
				break;
			case "^IRX":
				if (p > 5.0) setSignal(vo, "CAUTION", "단기금리 고수준 — 긴축 지속");
				else if (p < 2.0) setSignal(vo, "BUY", "단기금리 하락 — 완화 신호");
				else setSignal(vo, "NEUTRAL", "보통 수준");
				break;
			case "SPREAD_10Y3M":
				if (p < -0.5) setSignal(vo, "SELL", "금리 역전 심화 — 경기침체 경고");
				else if (p < 0) setSignal(vo, "CAUTION", "금리 역전 — 침체 신호");
				else if (p >= 0 && p < 0.5) setSignal(vo, "CAUTION", "금리차 축소 — 주의 필요");
				else if (p > 1.5) setSignal(vo, "BUY", "금리차 정상 — 경기 확장 신호");
				else setSignal(vo, "NEUTRAL", "보통 수준");
				break;
			case "GC=F":
				evaluateByPosition(vo, high52, low52, "금");
				break;
			case "BTC-USD":
				evaluateByPosition(vo, high52, low52, "비트코인");
				break;
			case "^GSPC":
				evaluateByPosition(vo, high52, low52, "S&P 500");
				break;
			case "^IXIC":
				evaluateByPosition(vo, high52, low52, "나스닥");
				break;
			case "^KS11":
			case "^KS200":
				evaluateByPosition(vo, high52, low52, "코스피");
				break;
			default:
				setSignal(vo, "NEUTRAL", "-");
				break;
		}
	}

	private void evaluateByPosition(MacroIndicatorVO vo, double high52, double low52, String name) {
		if (high52 <= 0 || low52 <= 0) {
			setSignal(vo, "NEUTRAL", "52주 데이터 부족");
			return;
		}
		double range = high52 - low52;
		if (range <= 0) { setSignal(vo, "NEUTRAL", "-"); return; }
		double position = (vo.getPriceRaw() - low52) / range;

		if (position >= 0.95) setSignal(vo, "CAUTION", name + " 52주 최고 근접 — 과열 주의");
		else if (position >= 0.85) setSignal(vo, "CAUTION", name + " 고점 부근 — 신규 매수 신중");
		else if (position <= 0.1) setSignal(vo, "BUY", name + " 52주 최저 근접 — 매수 기회");
		else if (position <= 0.25) setSignal(vo, "BUY", name + " 저점 부근 — 분할 매수 고려");
		else setSignal(vo, "NEUTRAL", name + " 중간 구간");
	}

	private void setSignal(MacroIndicatorVO vo, String signal, String text) {
		vo.setSignal(signal);
		vo.setSignalText(text);
	}

	// ==================== All Signals (종합 시그널) ====================

	public Map<String, Object> fetchAllSignals() {
		Map<String, Object> result = new LinkedHashMap<>();

		String[][] categories = {
				{"fearIndex", "공포지수"},
				{"bond", "금리/채권"},
				{"exchange", "환율"},
				{"commodity", "원자재"},
				{"index", "주요지수"}
		};

		// 5개 카테고리를 병렬로 조회
		List<CompletableFuture<List<MacroIndicatorVO>>> catFutures = Arrays.stream(categories)
				.map(cat -> CompletableFuture.supplyAsync(() -> {
					try { return fetchByCategory(cat[0]); }
					catch (Exception e) { log.warn("{} 시그널 조회 실패: {}", cat[0], e.getMessage()); return new ArrayList<MacroIndicatorVO>(); }
				}, fetchExecutor))
				.collect(Collectors.toList());

		List<MacroIndicatorVO> all = new ArrayList<>();
		for (CompletableFuture<List<MacroIndicatorVO>> f : catFutures) {
			try { all.addAll(f.get(30, TimeUnit.SECONDS)); }
			catch (Exception e) { log.warn("카테고리 병렬 조회 실패", e); }
		}

		List<Map<String, String>> assetSignals = computeAssetSignals(all);
		result.put("indicators", all);
		result.put("assetSignals", assetSignals);
		result.put("allocation", computeAllocation(all, assetSignals));
		return result;
	}

	private List<Map<String, String>> computeAssetSignals(List<MacroIndicatorVO> indicators) {
		List<Map<String, String>> assets = new ArrayList<>();

		Map<String, MacroIndicatorVO> bySymbol = new LinkedHashMap<>();
		for (MacroIndicatorVO vo : indicators) {
			bySymbol.put(vo.getSymbol(), vo);
		}

		// 미국 주식 시그널
		assets.add(computeOneAsset("미국 주식", bySymbol,
				new String[]{"^VIX", "^TNX", "SPREAD_10Y3M", "DX-Y.NYB", "^GSPC"}));

		// 한국 주식 시그널
		assets.add(computeOneAsset("한국 주식", bySymbol,
				new String[]{"^VIX", "KRW=X", "SPREAD_10Y3M", "^KS11"}));

		// 채권 시그널 (금리 하락 = 채권 가격 상승이므로 반전)
		Map<String, String> bondSignal = new LinkedHashMap<>();
		bondSignal.put("asset", "채권 (미국)");
		double bondScore = 0; int bondCount = 0;
		MacroIndicatorVO tnx = bySymbol.get("^TNX");
		if (tnx != null && tnx.getSignal() != null) {
			String s = tnx.getSignal();
			if ("BUY".equals(s)) { bondScore += 1; }
			else if ("SELL".equals(s)) { bondScore -= 1; }
			else if ("CAUTION".equals(s)) { bondScore -= 0.5; }
			bondCount++;
		}
		MacroIndicatorVO spread = bySymbol.get("SPREAD_10Y3M");
		if (spread != null && spread.getSignal() != null) {
			if (spread.getPriceRaw() < 0) bondScore += 0.5; // 역전 시 단기채 유리
			bondCount++;
		}
		double bondAvg = bondCount > 0 ? bondScore / bondCount : 0;
		setAssetSignal(bondSignal, bondAvg);
		assets.add(bondSignal);

		// 금/원자재 시그널
		assets.add(computeOneAsset("금/원자재", bySymbol,
				new String[]{"GC=F", "DX-Y.NYB", "^TNX"}));

		return assets;
	}

	private Map<String, String> computeOneAsset(String name, Map<String, MacroIndicatorVO> bySymbol, String[] symbols) {
		Map<String, String> asset = new LinkedHashMap<>();
		asset.put("asset", name);
		double score = 0; int count = 0;
		StringBuilder reasons = new StringBuilder();

		for (String sym : symbols) {
			MacroIndicatorVO vo = bySymbol.get(sym);
			if (vo == null || vo.getSignal() == null) continue;
			double s = signalToScore(vo.getSignal());
			score += s;
			count++;
			if (!"NEUTRAL".equals(vo.getSignal())) {
				if (reasons.length() > 0) reasons.append(", ");
				reasons.append(vo.getNameKr()).append("(").append(vo.getSignal()).append(")");
			}
		}

		double avg = count > 0 ? score / count : 0;
		setAssetSignal(asset, avg);
		asset.put("reasons", reasons.toString());
		return asset;
	}

	private double signalToScore(String signal) {
		switch (signal) {
			case "BUY": return 1.0;
			case "SELL": return -1.5;
			case "CAUTION": return -0.5;
			default: return 0;
		}
	}

	private void setAssetSignal(Map<String, String> asset, double avg) {
		if (avg > 0.3) { asset.put("signal", "BUY"); asset.put("text", "매수 유리"); }
		else if (avg > -0.2) { asset.put("signal", "NEUTRAL"); asset.put("text", "중립 — 관망"); }
		else if (avg > -0.7) { asset.put("signal", "CAUTION"); asset.put("text", "주의 — 신규 매수 신중"); }
		else { asset.put("signal", "SELL"); asset.put("text", "위험 — 매도/비중 축소 고려"); }
	}

	// ==================== Asset Allocation ====================

	private Map<String, Object> computeAllocation(List<MacroIndicatorVO> indicators,
												   List<Map<String, String>> assetSignals) {
		Map<String, Object> result = new LinkedHashMap<>();

		// 자산별 시그널 → 점수 매핑
		Map<String, String> signalMap = new LinkedHashMap<>();
		for (Map<String, String> a : assetSignals) {
			signalMap.put(a.get("asset"), a.get("signal"));
		}

		// 기준 배분 (중립 시)
		int baseUsStock = 25, baseKrStock = 15, baseBond = 20, baseGold = 15, baseCash = 20, baseCrypto = 5;

		// 시그널 기반 가중치 조정
		int usStock = adjustWeight(baseUsStock, signalMap.getOrDefault("미국 주식", "NEUTRAL"));
		int krStock = adjustWeight(baseKrStock, signalMap.getOrDefault("한국 주식", "NEUTRAL"));
		int gold = adjustWeightInverse(baseGold, signalMap.getOrDefault("금/원자재", "NEUTRAL"));
		int bond = adjustBondWeight(baseBond, signalMap.getOrDefault("채권 (미국)", "NEUTRAL"), indicators);

		// 금리 높으면 현금(MMF) 매력 상승 — 별도 가산
		double rate10y = getIndicatorPrice(indicators, "^TNX");
		int cashBonus = 0;
		if (rate10y > 4.5) cashBonus = 8;
		else if (rate10y > 4.0) cashBonus = 5;
		else if (rate10y > 3.5) cashBonus = 3;

		int crypto = adjustWeight(baseCrypto, signalMap.getOrDefault("미국 주식", "NEUTRAL"));

		// 잔여분을 현금에 할당 (합계 100%)
		int used = usStock + krStock + bond + gold + crypto;
		int cash = 100 - used;
		if (cash < 5) { cash = 5; usStock -= 3; krStock -= 2; }

		// 전체 시장 상태 판단
		double totalScore = 0; int cnt = 0;
		for (Map<String, String> a : assetSignals) {
			totalScore += signalToScore(a.getOrDefault("signal", "NEUTRAL"));
			cnt++;
		}
		double avg = cnt > 0 ? totalScore / cnt : 0;

		String phase, phaseText, phaseColor;
		if (avg > 0.3) { phase = "RISK_ON"; phaseText = "적극 매수 — 위험자산 비중 확대"; phaseColor = "#27ae60"; }
		else if (avg > -0.2) { phase = "NEUTRAL"; phaseText = "중립 — 균형 포트폴리오 유지"; phaseColor = "#7f8c8d"; }
		else if (avg > -0.7) { phase = "CAUTIOUS"; phaseText = "방어적 — 현금/금 비중 확대, 주식 축소"; phaseColor = "#e67e22"; }
		else { phase = "RISK_OFF"; phaseText = "위험 회피 — 주식 최소화, 안전자산 집중"; phaseColor = "#c0392b"; }

		result.put("phase", phase);
		result.put("phaseText", phaseText);
		result.put("phaseColor", phaseColor);

		// 배분 상세
		List<Map<String, Object>> allocs = new ArrayList<>();
		allocs.add(buildAlloc("현금/MMF", baseCash, cash, "SHV, BIL, TIGER 단기통안채, CMA/MMF",
				cashDetail(cash, baseCash, rate10y)));
		allocs.add(buildAlloc("미국 주식", baseUsStock, usStock, "SPY, VOO, QQQ, SCHD",
				stockDetail("미국 주식", usStock, baseUsStock, signalMap)));
		allocs.add(buildAlloc("한국 주식", baseKrStock, krStock, "KODEX 200, TIGER 코스피, 개별종목",
				stockDetail("한국 주식", krStock, baseKrStock, signalMap)));
		allocs.add(buildAlloc("미국 채권", baseBond, bond, bondInstruments(indicators),
				bondDetail(bond, baseBond, indicators)));
		allocs.add(buildAlloc("금", baseGold, gold, "GLD, IAU, KODEX 골드선물, KRX 금현물",
				goldDetail(gold, baseGold, indicators)));
		allocs.add(buildAlloc("암호화폐", baseCrypto, crypto, "BTC, ETH (비중 소량 유지)",
				cryptoDetail(crypto, baseCrypto)));
		result.put("allocations", allocs);

		// 구체적 액션 리스트
		result.put("actions", buildActions(allocs, indicators, signalMap));

		// 경고/참고 사항
		result.put("warnings", buildWarnings(indicators));

		return result;
	}

	private int adjustWeight(int base, String signal) {
		switch (signal) {
			case "BUY": return (int) Math.round(base * 1.4);
			case "SELL": return (int) Math.round(base * 0.3);
			case "CAUTION": return (int) Math.round(base * 0.6);
			default: return base;
		}
	}

	private int adjustWeightInverse(int base, String signal) {
		// 금/원자재: 다른 자산이 위험할수록 비중 증가
		switch (signal) {
			case "BUY": return (int) Math.round(base * 1.5);
			case "SELL": return (int) Math.round(base * 0.7);
			case "CAUTION": return (int) Math.round(base * 1.3);
			default: return base;
		}
	}

	private int adjustBondWeight(int base, String signal, List<MacroIndicatorVO> indicators) {
		double rate10y = getIndicatorPrice(indicators, "^TNX");
		double spread = getIndicatorPrice(indicators, "SPREAD_10Y3M");

		int adjusted = base;
		// 금리 높을 때 → 채권 가격 낮음 → 매수 기회
		if (rate10y > 4.5) adjusted += 3;
		else if (rate10y < 3.0) adjusted -= 5;

		// 금리 역전 시 단기채 선호 (전체 채권 비중은 유지)
		if (spread < 0) adjusted += 2;

		return Math.max(5, Math.min(35, adjusted));
	}

	private double getIndicatorPrice(List<MacroIndicatorVO> indicators, String symbol) {
		for (MacroIndicatorVO vo : indicators) {
			if (symbol.equals(vo.getSymbol())) return vo.getPriceRaw();
		}
		return 0;
	}

	private Map<String, Object> buildAlloc(String asset, int baseWeight, int recWeight,
										   String instruments, String detail) {
		Map<String, Object> alloc = new LinkedHashMap<>();
		alloc.put("asset", asset);
		alloc.put("baseWeight", baseWeight);
		alloc.put("recommendedWeight", recWeight);
		alloc.put("change", recWeight - baseWeight);
		alloc.put("instruments", instruments);
		alloc.put("detail", detail);

		String action;
		int diff = recWeight - baseWeight;
		if (diff >= 5) action = "BUY";
		else if (diff <= -5) action = "REDUCE";
		else if (diff > 0) action = "SLIGHT_BUY";
		else if (diff < 0) action = "SLIGHT_REDUCE";
		else action = "HOLD";
		alloc.put("action", action);
		return alloc;
	}

	private String cashDetail(int rec, int base, double rate10y) {
		if (rec > base + 10) {
			return "시장 위험 구간 — 현금 비중 대폭 확대. " +
					(rate10y > 4.0 ? "고금리 환경이므로 MMF/단기국채로 연 " + String.format("%.1f", rate10y - 0.5) + "%+ 수익 확보 가능." : "안전자산으로 방어.");
		} else if (rec > base) {
			return "주식 비중 축소분을 현금으로 이동. " +
					(rate10y > 3.5 ? "MMF/CMA에서 금리 수익 확보." : "매수 기회 대기 자금.");
		}
		return "기본 비중 유지. 급락 시 매수 여력 확보용.";
	}

	private String stockDetail(String name, int rec, int base, Map<String, String> signalMap) {
		String signal = signalMap.getOrDefault(name, "NEUTRAL");
		int diff = rec - base;
		if ("BUY".equals(signal)) {
			return name + " 매수 유리 구간. 분할 매수로 비중 확대 권장. 변동성 대비 2~3회 나눠 매수.";
		} else if ("SELL".equals(signal)) {
			return name + " 고위험 구간. 기존 보유분 " + Math.abs(diff) + "%p 축소. 수익 실현 또는 손절 후 현금/금으로 이동.";
		} else if ("CAUTION".equals(signal)) {
			return name + " 주의 구간. 신규 매수 보류, 기존 보유분 일부(" + Math.abs(diff) + "%p) 축소. 핵심 우량주만 유지.";
		}
		return name + " 중립. 기존 비중 유지하며 관망.";
	}

	private String bondInstruments(List<MacroIndicatorVO> indicators) {
		double spread = getIndicatorPrice(indicators, "SPREAD_10Y3M");
		if (spread < 0) return "SHV(단기), BIL(초단기), TIGER 단기통안채 — 단기채 선호";
		return "TLT(장기), IEF(중기), AGG(종합), KODEX 미국채10년선물";
	}

	private String bondDetail(int rec, int base, List<MacroIndicatorVO> indicators) {
		double rate10y = getIndicatorPrice(indicators, "^TNX");
		double spread = getIndicatorPrice(indicators, "SPREAD_10Y3M");

		StringBuilder sb = new StringBuilder();
		if (rate10y > 4.5) {
			sb.append("금리 고점 부근 — 채권 가격 저점 매수 기회. 금리 인하 시 자본 차익 기대. ");
		} else if (rate10y > 3.5) {
			sb.append("금리 보통 수준 — 이자 수익 중심으로 보유. ");
		} else {
			sb.append("저금리 구간 — 채권 가격 이미 높음. 추가 매수 매력 낮음. ");
		}

		if (spread < -0.3) {
			sb.append("장단기 금리 역전 심화 → 단기채(SHV/BIL) 위주로 편입. 장기채(TLT)는 금리 인하 확신 후 진입.");
		} else if (spread < 0.5) {
			sb.append("금리차 축소 → 중기채(IEF) 중심. 장기채 비중은 소폭만.");
		} else {
			sb.append("금리차 정상 → 장기채(TLT/EDV) 포함 가능. 듀레이션 분산 권장.");
		}
		return sb.toString();
	}

	private String goldDetail(int rec, int base, List<MacroIndicatorVO> indicators) {
		double dxy = getIndicatorPrice(indicators, "DX-Y.NYB");
		double rate10y = getIndicatorPrice(indicators, "^TNX");

		StringBuilder sb = new StringBuilder();
		if (rec > base + 5) {
			sb.append("안전자산 수요 증가 구간 — 금 비중 확대 권장. ");
		} else if (rec > base) {
			sb.append("소폭 추가 매수 고려. ");
		} else {
			sb.append("기본 비중 유지 (인플레 헷지). ");
		}

		if (dxy < 100) sb.append("달러 약세로 금 매수 유리. ");
		else if (dxy > 105) sb.append("달러 강세가 금 가격 압박 중이나, 위기 헷지로 유지. ");

		if (rate10y > 4.5) sb.append("실질금리 상승 부담 있으나 경기침체 헷지로 보유 가치 있음.");
		else if (rate10y < 3.5) sb.append("저금리 환경 — 금 보유 매력 높음.");

		return sb.toString();
	}

	private String cryptoDetail(int rec, int base) {
		if (rec < base) return "고위험 자산 — 시장 불확실성 구간에서 비중 축소. 전체 포트폴리오의 5% 이내 유지 권장.";
		if (rec > base) return "위험선호 구간 — 소량 추가 매수 가능. 단, 변동성 극심하므로 손실 감내 가능 범위 내에서.";
		return "기본 비중 유지. 장기 보유 관점으로 소량 유지.";
	}

	private List<Map<String, String>> buildActions(List<Map<String, Object>> allocs,
												   List<MacroIndicatorVO> indicators,
												   Map<String, String> signalMap) {
		List<Map<String, String>> actions = new ArrayList<>();

		for (Map<String, Object> alloc : allocs) {
			String action = (String) alloc.get("action");
			String asset = (String) alloc.get("asset");
			int base = (int) alloc.get("baseWeight");
			int rec = (int) alloc.get("recommendedWeight");
			int diff = rec - base;
			String instruments = (String) alloc.get("instruments");

			if ("HOLD".equals(action) || "SLIGHT_BUY".equals(action) || "SLIGHT_REDUCE".equals(action)) {
				if (Math.abs(diff) >= 2) {
					Map<String, String> a = new LinkedHashMap<>();
					a.put("type", diff > 0 ? "SLIGHT_BUY" : "SLIGHT_REDUCE");
					a.put("asset", asset);
					a.put("text", asset + " 소폭 조정: " + base + "% → " + rec + "% (" + (diff > 0 ? "+" : "") + diff + "%p)");
					a.put("instruments", instruments);
					actions.add(a);
				}
				continue;
			}

			Map<String, String> a = new LinkedHashMap<>();
			a.put("type", action);
			a.put("asset", asset);

			if ("BUY".equals(action)) {
				a.put("text", asset + " 비중 확대: " + base + "% → " + rec + "% (+" + diff + "%p)");
			} else {
				a.put("text", asset + " 비중 축소: " + base + "% → " + rec + "% (" + diff + "%p)");
			}
			a.put("instruments", instruments);
			actions.add(a);
		}

		// 우선순위 정렬: REDUCE → BUY → SLIGHT
		actions.sort((a, b) -> {
			int pa = actionPriority(a.get("type"));
			int pb = actionPriority(b.get("type"));
			return Integer.compare(pa, pb);
		});

		return actions;
	}

	private int actionPriority(String type) {
		switch (type) {
			case "REDUCE": return 1;
			case "SLIGHT_REDUCE": return 2;
			case "BUY": return 3;
			case "SLIGHT_BUY": return 4;
			default: return 5;
		}
	}

	private List<String> buildWarnings(List<MacroIndicatorVO> indicators) {
		List<String> warnings = new ArrayList<>();

		double vix = getIndicatorPrice(indicators, "^VIX");
		double spread = getIndicatorPrice(indicators, "SPREAD_10Y3M");
		double dxy = getIndicatorPrice(indicators, "DX-Y.NYB");
		double rate10y = getIndicatorPrice(indicators, "^TNX");
		double usdkrw = getIndicatorPrice(indicators, "KRW=X");

		if (vix >= 30) warnings.add("VIX " + String.format("%.1f", vix) + " — 시장 극도의 공포. 단기 급락 가능하나 역사적으로 매수 기회.");
		if (vix <= 12) warnings.add("VIX " + String.format("%.1f", vix) + " — 극단적 안일. 시장 급락 전 나타나는 패턴 주의.");
		if (spread < -0.3) warnings.add("장단기 금리 역전 " + String.format("%.2f", spread) + "%p — 통상 6~18개월 내 경기침체 발생 확률 높음.");
		if (spread < 0) warnings.add("금리 역전 구간 — 장기채보다 단기채(1~3년) 또는 MMF가 유리.");
		if (rate10y > 5.0) warnings.add("10년물 금리 " + String.format("%.2f", rate10y) + "% — 역사적 고점 부근. 주식/부동산 밸류에이션 압박.");
		if (usdkrw > 1400) warnings.add("원/달러 " + String.format("%.0f", usdkrw) + "원 — 원화 약세. 한국 주식 외국인 매도 압력 주의.");
		if (dxy > 108) warnings.add("달러 인덱스 " + String.format("%.1f", dxy) + " — 강달러 구간. 신흥국 자산/원자재 압박.");

		if (warnings.isEmpty()) warnings.add("현재 극단적 경고 신호 없음. 기본 전략 유지.");
		return warnings;
	}

	// ==================== Macro Adj Coeff (일 1회 캐시) ====================

	public MacroAdjCoeff computeStockTypeAdjCoeff() {
		if (cachedAdjCoeff != null && System.currentTimeMillis() - cachedAdjCoeffTime < ADJ_COEFF_TTL) {
			return cachedAdjCoeff;
		}
		try {
			CompletableFuture<MacroIndicatorVO> vixF = CompletableFuture.supplyAsync(
					() -> { try { return fetchYahooQuote("^VIX");      } catch (Exception e) { return null; } }, fetchExecutor);
			CompletableFuture<MacroIndicatorVO> tnxF = CompletableFuture.supplyAsync(
					() -> { try { return fetchYahooQuote("^TNX");      } catch (Exception e) { return null; } }, fetchExecutor);
			CompletableFuture<MacroIndicatorVO> irxF = CompletableFuture.supplyAsync(
					() -> { try { return fetchYahooQuote("^IRX");      } catch (Exception e) { return null; } }, fetchExecutor);
			CompletableFuture<MacroIndicatorVO> dxyF = CompletableFuture.supplyAsync(
					() -> { try { return fetchYahooQuote("DX-Y.NYB"); } catch (Exception e) { return null; } }, fetchExecutor);

			double vix    = safeRaw(vixF.get(20, TimeUnit.SECONDS));
			double tnx    = safeRaw(tnxF.get(20, TimeUnit.SECONDS));
			double irx    = safeRaw(irxF.get(20, TimeUnit.SECONDS));
			double dxy    = safeRaw(dxyF.get(20, TimeUnit.SECONDS));
			double spread = (tnx > 0 && irx > 0) ? tnx - irx : 0;

			int stockAdj = calcStockAdj(vix, tnx, spread);
			int bondAdj  = calcBondAdj(tnx, spread);
			int commAdj  = calcCommAdj(dxy, vix, tnx);

			MacroAdjCoeff coeff = MacroAdjCoeff.builder()
					.stockAdj(stockAdj).bondAdj(bondAdj).commAdj(commAdj)
					.stockSig(adjToSig(stockAdj)).bondSig(adjToSig(bondAdj)).commSig(adjToSig(commAdj))
					.vix(Math.round(vix * 10) / 10.0)
					.tnx(Math.round(tnx * 100) / 100.0)
					.spread(Math.round(spread * 100) / 100.0)
					.dxy(Math.round(dxy * 10) / 10.0)
					.fetchedAt(new SimpleDateFormat("MM-dd HH:mm").format(new Date()))
					.build();

			cachedAdjCoeff = coeff;
			cachedAdjCoeffTime = System.currentTimeMillis();
			log.info("조정계수 갱신: 주식{}%, 채권{}%, 현물{}% (VIX={}, 10Y={}%, 스프레드={}%p, DXY={})",
					stockAdj, bondAdj, commAdj, vix, tnx, spread, dxy);
			return coeff;
		} catch (Exception e) {
			log.warn("조정계수 계산 실패, 캐시/기본값 반환: {}", e.getMessage());
			return cachedAdjCoeff != null ? cachedAdjCoeff :
					MacroAdjCoeff.builder()
							.stockAdj(5).bondAdj(0).commAdj(5)
							.stockSig("HOLD").bondSig("HOLD").commSig("HOLD")
							.fetchedAt("조회실패").build();
		}
	}

	private double safeRaw(MacroIndicatorVO vo) {
		return vo != null ? vo.getPriceRaw() : 0;
	}

	private String adjToSig(int adj) {
		return adj > 5 ? "BUY" : (adj < -5 ? "SELL" : "HOLD");
	}

	/** 주식 조정% (-25 ~ +25): VIX(역발상) + 10Y금리 + 스프레드 */
	private int calcStockAdj(double vix, double tnx, double spread) {
		int adj = 0;
		if      (vix > 40)  adj += 20;
		else if (vix > 30)  adj += 12;
		else if (vix > 20)  adj += 5;
		else if (vix > 15)  adj += 3;
		else if (vix > 0)   adj -= 3;   // 극도 안정 = 과열 주의

		if      (tnx > 5.0) adj -= 12;
		else if (tnx > 4.5) adj -= 5;
		else if (tnx > 3.5) adj += 0;
		else if (tnx > 3.0) adj += 5;
		else if (tnx > 0)   adj += 8;

		if      (spread > 1.0) adj += 3;
		else if (spread > 0)   adj += 1;
		else if (spread < 0)   adj -= 5;  // 역전
		return Math.max(-25, Math.min(25, adj));
	}

	/** 채권 조정% (-20 ~ +20): 금리 고점 = 채권 저점 매수 */
	private int calcBondAdj(double tnx, double spread) {
		int adj = 0;
		if      (tnx > 5.0)  adj += 15;
		else if (tnx > 4.5)  adj += 10;
		else if (tnx > 4.0)  adj += 5;
		else if (tnx > 3.5)  adj += 0;
		else if (tnx > 3.0)  adj -= 5;
		else if (tnx > 0)    adj -= 12;

		if      (spread < -0.3) adj += 5;   // 역전 심화 = 단기채 유리
		else if (spread < 0)    adj += 3;
		else if (spread > 1.5)  adj -= 3;
		return Math.max(-20, Math.min(20, adj));
	}

	/** 현물(금/원자재) 조정% (-15 ~ +15): DXY + VIX + 금리 */
	private int calcCommAdj(double dxy, double vix, double tnx) {
		int adj = 0;
		if      (dxy < 95)   adj += 10;
		else if (dxy < 100)  adj += 7;
		else if (dxy < 105)  adj += 3;
		else if (dxy > 108)  adj -= 5;

		if      (vix > 30)   adj += 5;
		else if (vix > 20)   adj += 2;
		else if (vix > 0 && vix < 15) adj -= 2;

		if      (tnx > 5.0)          adj -= 5;
		else if (tnx > 0 && tnx < 3.5) adj += 3;
		return Math.max(-15, Math.min(15, adj));
	}

	// ==================== Portfolio-Based Allocation ====================

	private static final String[][] ETF_CATEGORY_RULES = {
		// 순서 중요: 먼저 매칭되는 것이 우선 (구체적인 것 → 일반적인 것)
		{"머니마켓", "현금/MMF"},
		{"MMF", "현금/MMF"},
		{"초단기", "단기채/현금성"},
		{"단기국채", "단기채/현금성"},
		{"달러단기채", "단기채/현금성"},
		{"단기투자등급", "단기채/현금성"},
		{"KRX금현물", "금"},
		{"골드", "금"},
		{"Gold", "금"},
		{"리츠", "리츠/부동산"},
		{"부동산", "리츠/부동산"},
		{"REITs", "리츠/부동산"},
		{"인도", "신흥국 주식"},
		{"Nifty", "신흥국 주식"},
		{"니프티", "신흥국 주식"},
		{"신흥", "신흥국 주식"},
		{"30년국채", "미국 장기채"},
		{"20년국채", "미국 장기채"},
		{"10년국채", "미국 중기채"},
		{"국고채10년", "한국 채권"},
		{"국고채", "한국 채권"},
		{"종합채권", "한국 채권"},
		{"S&P", "미국 주식"},
		{"나스닥", "미국 주식"},
		{"다우존스", "미국 주식"},
		{"배당다우", "미국 주식"},
		{"반도체", "미국 주식"},
		{"미국배당", "미국 주식"},
		{"미국주식", "미국 주식"},
		{"200", "한국 주식"},
		{"코리아", "한국 주식"},
		{"고배당주", "한국 주식"},
		{"밸류업", "한국 주식"},
		{"코스피", "한국 주식"},
	};

	private static final String[][] ASSET_CLASS_ORDER = {
		{"미국 주식", "미국 주식", "#2980b9"},
		{"한국 주식", "한국 주식", "#27ae60"},
		{"신흥국 주식", "신흥국 주식", "#16a085"},
		{"미국 장기채", "채권", "#8e44ad"},
		{"미국 중기채", "채권", "#9b59b6"},
		{"한국 채권", "채권", "#a569bd"},
		{"단기채/현금성", "현금/MMF", "#7f8c8d"},
		{"현금/MMF", "현금/MMF", "#95a5a6"},
		{"금", "금", "#f39c12"},
		{"리츠/부동산", "리츠/부동산", "#e67e22"},
	};

	public Map<String, Object> computePortfolioAllocation(
			List<StockInterestParamVO> divisions,
			List<StockInterestVO> interestList,
			List<Map<String, String>> assetSignals,
			MarketCycleDashboard dashboard) {

		Map<String, Object> result = new LinkedHashMap<>();
		if (interestList == null || interestList.isEmpty()) return result;

		// 시그널 매핑
		Map<String, String> signalByAsset = new LinkedHashMap<>();
		if (assetSignals != null) {
			for (Map<String, String> a : assetSignals) {
				signalByAsset.put(a.get("asset"), a.get("signal"));
			}
		}

		// 사계론 자산별 buyProbability 맵
		Map<String, Integer> cycleBuyProb = buildCycleBuyProbMap(dashboard);

		// 1) 개별 ETF를 자산분류별로 분류
		Map<String, List<StockInterestVO>> stocksByCategory = new LinkedHashMap<>();
		Map<String, Long> amountByCategory = new LinkedHashMap<>();
		long totalActual = 0;

		for (StockInterestVO stock : interestList) {
			String cat = classifyEtf(stock.getName());
			stocksByCategory.computeIfAbsent(cat, k -> new ArrayList<>()).add(stock);
			amountByCategory.merge(cat, stock.getTotalPrice(), Long::sum);
			totalActual += stock.getTotalPrice();
		}
		if (totalActual == 0) return result;

		// 2) 자산분류별 현재 비중 계산 + 시그널 반영 추천 비중
		List<Map<String, Object>> items = new ArrayList<>();
		double totalRecWeight = 0;

		for (String[] assetDef : ASSET_CLASS_ORDER) {
			String category = assetDef[0];
			String signalCategory = assetDef[1];
			String color = assetDef[2];

			Long amount = amountByCategory.get(category);
			if (amount == null) continue;

			double currentWeight = amount * 100.0 / totalActual;
			String signal = findSignalForCategory(signalCategory, signalByAsset);
			int buyProb = getCycleBuyProb(signalCategory, cycleBuyProb);
			double adjFactor = combinedAdjustFactor(signal, signalCategory, buyProb);
			double recommendedWeight = Math.round(currentWeight * adjFactor * 10) / 10.0;
			double change = Math.round((recommendedWeight - currentWeight) * 10) / 10.0;
			long recommendedAmount = Math.round(totalActual * recommendedWeight / 100.0);
			long adjustAmount = recommendedAmount - amount;

			String action;
			if (change >= 3) action = "BUY";
			else if (change >= 1) action = "SLIGHT_BUY";
			else if (change <= -3) action = "REDUCE";
			else if (change <= -1) action = "SLIGHT_REDUCE";
			else action = "HOLD";

			Map<String, Object> item = new LinkedHashMap<>();
			item.put("category", category);
			item.put("signalCategory", signalCategory);
			item.put("signal", signal);
			item.put("color", color);
			item.put("currentWeight", Math.round(currentWeight * 10) / 10.0);
			item.put("recommendedWeight", recommendedWeight);
			item.put("change", change);
			item.put("action", action);
			item.put("actualAmount", amount);
			item.put("recommendedAmount", recommendedAmount);
			item.put("adjustAmount", adjustAmount);
			item.put("detail", buildAssetClassDetail(category, signalCategory, signal, change, currentWeight));
			item.put("cycleBuyProb", buyProb);
			item.put("cycleSignal", buyProb >= 65 ? "BUY" : buyProb <= 35 ? "SELL" : "HOLD");

			// 해당 자산분류의 종목들
			List<Map<String, Object>> stockList = new ArrayList<>();
			List<StockInterestVO> stocks = stocksByCategory.getOrDefault(category, Collections.emptyList());
			for (StockInterestVO s : stocks) {
				Map<String, Object> sm = new LinkedHashMap<>();
				sm.put("name", s.getName());
				sm.put("division", s.getStockDivision());
				sm.put("qty", s.getQty());
				sm.put("price", s.getPrice());
				sm.put("totalPrice", s.getTotalPrice());
				sm.put("weight", Math.round(s.getTotalPrice() * 1000.0 / totalActual) / 10.0);
				sm.put("month1Rate", s.getMonth1Rate());
				sm.put("month3Rate", s.getMonth3Rate());
				sm.put("pos52", s.getPos52());
				stockList.add(sm);
			}
			stockList.sort((a, b) -> Long.compare(
					(long) b.getOrDefault("totalPrice", 0L),
					(long) a.getOrDefault("totalPrice", 0L)));
			item.put("stocks", stockList);
			items.add(item);
			totalRecWeight += recommendedWeight;
		}

		// 합계 100% 조정 — 잔여분은 현금성 자산에 분배
		if (Math.abs(totalRecWeight - 100) > 0.5) {
			double diff = 100 - totalRecWeight;
			// 현금성 자산 찾아서 분배 (단기채 60%, MMF 30%, 금 10%)
			String[] cashTargets = {"단기채/현금성", "현금/MMF", "금"};
			double[] ratios = {0.6, 0.3, 0.1};
			boolean distributed = false;

			for (int t = 0; t < cashTargets.length; t++) {
				for (Map<String, Object> item : items) {
					if (cashTargets[t].equals(item.get("category"))) {
						double portion = diff * ratios[t];
						double w = (double) item.get("recommendedWeight") + portion;
						double curW = (double) item.get("currentWeight");
						double newChange = Math.round((w - curW) * 10) / 10.0;
						item.put("recommendedWeight", Math.round(w * 10) / 10.0);
						item.put("change", newChange);
						long newRecAmount = Math.round(totalActual * w / 100.0);
						item.put("recommendedAmount", newRecAmount);
						item.put("adjustAmount", newRecAmount - (long) item.get("actualAmount"));
						// 액션 재계산
						String newAction;
						if (newChange >= 3) newAction = "BUY";
						else if (newChange >= 1) newAction = "SLIGHT_BUY";
						else if (newChange <= -3) newAction = "REDUCE";
						else if (newChange <= -1) newAction = "SLIGHT_REDUCE";
						else newAction = "HOLD";
						item.put("action", newAction);
						item.put("detail", buildAssetClassDetail(
								(String) item.get("category"), (String) item.get("signalCategory"),
								(String) item.get("signal"), newChange, curW));
						distributed = true;
						break;
					}
				}
			}
			// 타겟 못 찾으면 첫 번째 현금성에 전부 할당
			if (!distributed) {
				for (Map<String, Object> item : items) {
					String cat = (String) item.get("category");
					if ("현금/MMF".equals(cat) || "단기채/현금성".equals(cat)) {
						double w = (double) item.get("recommendedWeight") + diff;
						double curW = (double) item.get("currentWeight");
						double newChange = Math.round((w - curW) * 10) / 10.0;
						item.put("recommendedWeight", Math.round(w * 10) / 10.0);
						item.put("change", newChange);
						long newRecAmount = Math.round(totalActual * w / 100.0);
						item.put("recommendedAmount", newRecAmount);
						item.put("adjustAmount", newRecAmount - (long) item.get("actualAmount"));
						String newAction = newChange >= 3 ? "BUY" : (newChange >= 1 ? "SLIGHT_BUY" : "HOLD");
						item.put("action", newAction);
						break;
					}
				}
			}
		}

		// 3) 계좌별 요약 (보조 정보)
		List<Map<String, Object>> accountSummary = new ArrayList<>();
		if (divisions != null) {
			for (StockInterestParamVO div : divisions) {
				Map<String, Object> acc = new LinkedHashMap<>();
				acc.put("account", div.getStockDivision());
				long accActual = 0;
				Map<String, Long> accBreakdown = new LinkedHashMap<>();
				for (StockInterestVO stock : interestList) {
					if (div.getStockDivision().equals(stock.getStockDivision())) {
						String cat = classifyEtf(stock.getName());
						accBreakdown.merge(cat, stock.getTotalPrice(), Long::sum);
						accActual += stock.getTotalPrice();
					}
				}
				acc.put("totalAmount", accActual);
				acc.put("weight", Math.round(accActual * 1000.0 / totalActual) / 10.0);
				acc.put("breakdown", accBreakdown);
				accountSummary.add(acc);
			}
		}

		result.put("totalActual", totalActual);
		result.put("items", items);
		result.put("accounts", accountSummary);
		return result;
	}

	private String classifyEtf(String name) {
		if (name == null) return "기타";
		for (String[] rule : ETF_CATEGORY_RULES) {
			if (name.contains(rule[0])) return rule[1];
		}
		return "기타";
	}

	private String findSignalForCategory(String category, Map<String, String> signalByAsset) {
		String signal = signalByAsset.get(category);
		if (signal != null) return signal;
		if ("한국 주식".equals(category)) return signalByAsset.getOrDefault("한국 주식", "NEUTRAL");
		if ("미국 주식".equals(category)) return signalByAsset.getOrDefault("미국 주식", "NEUTRAL");
		if ("신흥국 주식".equals(category)) return signalByAsset.getOrDefault("한국 주식", "NEUTRAL");
		if (category.contains("채권")) return signalByAsset.getOrDefault("채권 (미국)", "NEUTRAL");
		if ("금".equals(category)) return signalByAsset.getOrDefault("금/원자재", "NEUTRAL");
		if ("리츠/부동산".equals(category)) return signalByAsset.getOrDefault("미국 주식", "NEUTRAL");
		return "NEUTRAL";
	}

	private double signalAdjustFactor(String signal, String category) {
		boolean isSafeAsset = "현금/MMF".equals(category) || "금".equals(category);

		if (isSafeAsset) {
			switch (signal) {
				case "BUY": return 1.0;
				case "SELL": return 1.5;
				case "CAUTION": return 1.3;
				default: return 1.0;
			}
		} else {
			switch (signal) {
				case "BUY": return 1.3;
				case "SELL": return 0.4;
				case "CAUTION": return 0.7;
				default: return 1.0;
			}
		}
	}

	private Map<String, Integer> buildCycleBuyProbMap(MarketCycleDashboard dashboard) {
		Map<String, Integer> map = new HashMap<>();
		if (dashboard == null || dashboard.getAssetOverviews() == null) return map;
		for (SectorOverview ov : dashboard.getAssetOverviews()) {
			map.put(ov.getSector(), ov.getBuyProbability());
		}
		return map;
	}

	private int getCycleBuyProb(String signalCategory, Map<String, Integer> cycleBuyProb) {
		if (cycleBuyProb == null || cycleBuyProb.isEmpty()) return 50;
		switch (signalCategory) {
			case "한국 주식": {
				int a = cycleBuyProb.getOrDefault("KODEX200", 50);
				int b = cycleBuyProb.getOrDefault("코리아밸류업", 50);
				return (a + b) / 2;
			}
			case "미국 주식": {
				int a = cycleBuyProb.getOrDefault("S&P500", 50);
				int b = cycleBuyProb.getOrDefault("나스닥100", 50);
				return (a + b) / 2;
			}
			case "채권 (미국)":
			case "채권": return cycleBuyProb.getOrDefault("채권", 50);
			case "단기채/현금성": return cycleBuyProb.getOrDefault("CD금리", 50);
			case "현금/MMF": return cycleBuyProb.getOrDefault("CD금리", 50);
			case "금":
			case "금/원자재": return cycleBuyProb.getOrDefault("금", 50);
			default: return 50;
		}
	}

	private double combinedAdjustFactor(String signal, String signalCategory, int cycleBuyProb) {
		double macroFactor = signalAdjustFactor(signal, signalCategory);
		double cycleFactor = 0.7 + (cycleBuyProb / 100.0) * 0.6;  // range 0.7~1.3
		return macroFactor * 0.6 + cycleFactor * 0.4;
	}

	private String buildAssetClassDetail(String category, String signalCategory, String signal, double change, double currentWeight) {
		StringBuilder sb = new StringBuilder();
		double recWeight = currentWeight + change;

		switch (signal) {
			case "BUY":
				sb.append(category + " 매수 유리. ");
				sb.append(String.format("%.1f%% → %.1f%%로 비중 확대. ", currentWeight, recWeight));
				sb.append("분할 매수 권장.");
				break;
			case "SELL":
				sb.append(category + " 위험 구간. ");
				sb.append(String.format("%.1f%% → %.1f%%로 비중 축소. ", currentWeight, recWeight));
				if (category.contains("주식")) sb.append("수익 실현 후 단기채/현금으로 이동.");
				else sb.append("안전자산으로 교체.");
				break;
			case "CAUTION":
				sb.append(category + " 주의. ");
				if (Math.abs(change) >= 1) sb.append(String.format("%.1f%% → %.1f%%로 조정. ", currentWeight, recWeight));
				sb.append("신규 매수 보류, 기존 보유분 유지.");
				break;
			default:
				sb.append(category + " 중립. 사계절 기본 비중(" + String.format("%.1f%%", currentWeight) + ") 유지.");
				break;
		}
		return sb.toString();
	}

	// ==================== Chart Symbols for New Categories ====================

	private List<String[]> getChartSymbolsForBond() {
		List<String[]> symbols = new ArrayList<>();
		symbols.add(new String[]{"^TNX", "10Y 금리", "#d63031"});
		symbols.add(new String[]{"^FVX", "5Y 금리", "#e17055"});
		symbols.add(new String[]{"^IRX", "3M 금리", "#0984e3"});
		symbols.add(new String[]{"^TYX", "30Y 금리", "#6c5ce7"});
		return symbols;
	}

	// ==================== HTTP / Utility ====================

	private HttpURLConnection openConnection(String httpUrl) throws Exception {
		URL url = new URL(httpUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setRequestProperty("Accept", "application/json, text/plain, */*");
		conn.setInstanceFollowRedirects(true);
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(30000);
		return conn;
	}

	private String readResponse(HttpURLConnection conn) throws Exception {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (sb.length() > 0) sb.append("\n");
				sb.append(line);
			}
		}
		return sb.toString();
	}

	private String getText(JsonNode node, String field) {
		JsonNode f = node.get(field);
		return (f != null && !f.isNull()) ? f.asText("") : "";
	}

	private double getDouble(JsonNode node, String field) {
		JsonNode f = node.get(field);
		return (f != null && !f.isNull()) ? f.asDouble(0) : 0;
	}

	private String formatNumber(double value) {
		if (value == 0) return "0";
		if (Math.abs(value) >= 100) {
			if (Math.abs(value - Math.round(value)) < 0.01) return String.format("%,.0f", value);
			return String.format("%,.2f", value);
		}
		return String.valueOf(Math.round(value * 10000.0) / 10000.0);
	}

	private String formatSignedNumber(double value) {
		String formatted = formatNumber(Math.abs(value));
		if (value > 0.0001) return "+" + formatted;
		if (value < -0.0001) return "-" + formatted;
		return formatted;
	}

	private String formatSignedDecimal(double value) {
		String formatted = String.format("%.2f", Math.abs(value));
		if (value > 0.0001) return "+" + formatted;
		if (value < -0.0001) return "-" + formatted;
		return formatted;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> castList(Object obj) {
		return (List<T>) obj;
	}

	// ==================== 200MA 추세 신호 (일 1회 캐시) ====================

	public TrendSignalVO computeTrendSignal() {
		long now = System.currentTimeMillis();
		if (cachedTrendSignal != null && now - cachedTrendSignalTime < TREND_TTL) {
			return cachedTrendSignal;
		}
		try {
			CompletableFuture<double[]> spFuture = CompletableFuture.supplyAsync(
					() -> { try { return fetch200MAData("^GSPC"); } catch (Exception e) { return null; } }, fetchExecutor);
			CompletableFuture<double[]> ksFuture = CompletableFuture.supplyAsync(
					() -> { try { return fetch200MAData("^KS11"); } catch (Exception e) { return null; } }, fetchExecutor);
			CompletableFuture<Double> peFuture = CompletableFuture.supplyAsync(
					() -> { try { return fetchSpPE(); } catch (Exception e) { return 0.0; } }, fetchExecutor);

			double[] spData = spFuture.get(30, TimeUnit.SECONDS);
			double[] ksData = ksFuture.get(30, TimeUnit.SECONDS);
			double   spPE   = peFuture.get(15, TimeUnit.SECONDS);

			TrendSignalVO signal = TrendSignalVO.builder()
					.spPrice(spData != null ? spData[0] : 0)
					.spMa200(spData != null ? spData[1] : 0)
					.spDiffPct(spData != null ? spData[2] : 0)
					.spSignal(spData != null ? maToSig(spData[2]) : "HOLD")
					.spPE(spPE)
					.ksPrice(ksData != null ? ksData[0] : 0)
					.ksMa200(ksData != null ? ksData[1] : 0)
					.ksDiffPct(ksData != null ? ksData[2] : 0)
					.ksSignal(ksData != null ? maToSig(ksData[2]) : "HOLD")
					.fetchedAt(new SimpleDateFormat("MM-dd HH:mm").format(new Date()))
					.build();

			cachedTrendSignal = signal;
			cachedTrendSignalTime = now;
			log.info("200MA 신호 갱신: SP500={}/{} ({}%), KOSPI={}/{} ({}%), PE={}",
					(int)signal.getSpPrice(), (int)signal.getSpMa200(), signal.getSpDiffPct(),
					(int)signal.getKsPrice(), (int)signal.getKsMa200(), signal.getKsDiffPct(), spPE);
			return signal;
		} catch (Exception e) {
			log.warn("200MA 신호 계산 실패: {}", e.getMessage());
			return cachedTrendSignal != null ? cachedTrendSignal :
					TrendSignalVO.builder().spSignal("HOLD").ksSignal("HOLD").fetchedAt("조회실패").build();
		}
	}

	/** Yahoo Finance 1y 일별 데이터로 200MA 계산. returns [현재가, 200MA, 이격도%] */
	private double[] fetch200MAData(String symbol) throws Exception {
		String url = YAHOO_CHART_URL + URLEncoder.encode(symbol, StandardCharsets.UTF_8.name())
				+ "?range=1y&interval=1d";
		HttpURLConnection conn = openConnection(url);
		if (conn.getResponseCode() != 200) { conn.disconnect(); return null; }
		String json = readResponse(conn);
		conn.disconnect();

		JsonNode results = objectMapper.readTree(json).path("chart").path("result");
		if (!results.isArray() || results.isEmpty()) return null;

		JsonNode closes = results.get(0).path("indicators").path("quote").get(0).path("close");
		if (closes == null || !closes.isArray()) return null;

		List<Double> prices = new ArrayList<>();
		for (JsonNode c : closes) {
			if (c != null && !c.isNull() && c.isNumber()) prices.add(c.asDouble());
		}
		if (prices.size() < 10) return null;

		double currentPrice = prices.get(prices.size() - 1);
		int maLen = Math.min(200, prices.size());
		double ma200 = prices.subList(prices.size() - maLen, prices.size())
				.stream().mapToDouble(Double::doubleValue).average().orElse(0);
		double diffPct = ma200 > 0 ? (currentPrice - ma200) / ma200 * 100 : 0;

		return new double[]{
			Math.round(currentPrice * 100) / 100.0,
			Math.round(ma200 * 100) / 100.0,
			Math.round(diffPct * 10) / 10.0
		};
	}

	/** S&P500 Trailing PE — multpl.com meta description 파싱 */
	private double fetchSpPE() throws Exception {
		HttpURLConnection conn = openConnection("https://www.multpl.com/s-p-500-pe-ratio");
		conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*");
		int code = conn.getResponseCode();
		if (code != 200) {
			log.warn("multpl.com PE 응답 코드: {}", code);
			conn.disconnect();
			return 0;
		}
		String html = readResponse(conn);
		conn.disconnect();
		// S&P 또는 S&amp;P 양쪽 대응
		Matcher m = Pattern.compile("Current S[&](?:amp;)?P 500 PE Ratio is ([\\d.]+)").matcher(html);
		if (m.find()) {
			double pe = Math.round(Double.parseDouble(m.group(1)) * 10) / 10.0;
			log.info("multpl.com PE 조회 성공: {}", pe);
			return pe;
		}
		log.warn("multpl.com PE 파싱 실패 — HTML에서 값 미발견");
		return 0;
	}

	private String maToSig(double diffPct) {
		return diffPct > 3 ? "BUY" : (diffPct < -3 ? "SELL" : "HOLD");
	}
}
