package com.kangong.advstock.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kangong.advstock.model.YahooStockVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class AdvStockYahooParser {

    private static final String YAHOO_COOKIE_URL = "https://fc.yahoo.com/";
    private static final String YAHOO_CRUMB_URL = "https://query2.finance.yahoo.com/v1/test/getcrumb";
    private static final String YAHOO_QUOTE_URL = "https://query1.finance.yahoo.com/v7/finance/quote";
    private static final String YAHOO_SEARCH_URL = "https://query1.finance.yahoo.com/v1/finance/search";

    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static final String[] KOREAN_ETF_BRANDS = {
            "KODEX", "TIGER", "KBSTAR", "ARIRANG", "HANARO",
            "SOL", "ACE", "KOSEF", "RISE", "PLUS"
    };

    private static final long UNIT_EOKWON = 100_000_000L;
    private static final int QUOTE_BATCH_SIZE = 100;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String sessionCookie;
    private String crumb;
    private long sessionInitTimeMs = 0;
    private static final long SESSION_TTL_MS = 10 * 60 * 1000L; // 10분 캐시

    public List<YahooStockVO> fetchStockList() throws Exception {
        return fetchStockList(null);
    }

    public List<YahooStockVO> fetchQuotesBySymbols(List<String> symbols) throws Exception {
        if (symbols == null || symbols.isEmpty()) return new ArrayList<>();
        initSession();
        return fetchQuotes(symbols);
    }

    public double fetchUsdKrwRate() throws Exception {
        initSession();
        List<YahooStockVO> result = fetchQuotes(List.of("USDKRW=X"));
        if (result.isEmpty()) throw new Exception("USDKRW=X 환율 조회 실패");
        double rate = Double.parseDouble(result.get(0).getPrice());
        log.info("USD/KRW 환율: {}", rate);
        return rate;
    }

    public List<YahooStockVO> fetchStockList(String keyword) throws Exception {
        initSession();

        Set<String> symbols = new LinkedHashSet<>();

        if (keyword != null && !keyword.isEmpty()) {
            symbols.addAll(searchSymbols(keyword));
        } else {
            for (String brand : KOREAN_ETF_BRANDS) {
                try {
                    symbols.addAll(searchSymbols(brand));
                } catch (Exception e) {
                    log.warn("브랜드 '{}' 검색 실패: {}", brand, e.getMessage());
                }
            }
        }

        if (symbols.isEmpty()) {
            log.warn("Yahoo Finance에서 종목을 찾을 수 없습니다.");
            return new ArrayList<>();
        }

        List<YahooStockVO> result = fetchQuotes(new ArrayList<>(symbols));

        result.sort((a, b) -> Long.compare(
                parseLongSafe(b.getMarketCapitalization()),
                parseLongSafe(a.getMarketCapitalization())));

        log.info("Yahoo Finance 조회 완료: {}건 (keyword: {})",
                result.size(), keyword != null ? keyword : "전체");
        return result;
    }

    private void initSession() throws Exception {
        long now = System.currentTimeMillis();
        if (crumb != null && !crumb.isEmpty() && (now - sessionInitTimeMs) < SESSION_TTL_MS) {
            log.debug("Yahoo 세션 재사용 (남은 유효: {}초)", (SESSION_TTL_MS - (now - sessionInitTimeMs)) / 1000);
            return;
        }
        log.info("Yahoo 세션 초기화");
        HttpURLConnection cookieConn = openConnection(YAHOO_COOKIE_URL);
        cookieConn.setInstanceFollowRedirects(true);
        cookieConn.getResponseCode();

        StringBuilder cookies = new StringBuilder();
        List<String> setCookies = cookieConn.getHeaderFields().get("Set-Cookie");
        if (setCookies != null) {
            for (String c : setCookies) {
                if (cookies.length() > 0) cookies.append("; ");
                cookies.append(c.split(";")[0]);
            }
        }
        sessionCookie = cookies.toString();
        cookieConn.disconnect();

        HttpURLConnection crumbConn = openConnection(YAHOO_CRUMB_URL);
        crumbConn.setRequestProperty("Cookie", sessionCookie);
        crumb = readResponse(crumbConn);
        crumbConn.disconnect();
        sessionInitTimeMs = System.currentTimeMillis();
        log.debug("Yahoo Finance 세션 초기화 완료 (crumb length: {})", crumb.length());
    }

    public void invalidateSession() {
        crumb = null;
        sessionInitTimeMs = 0;
        log.info("Yahoo 세션 무효화");
    }

    private List<String> searchSymbols(String query) throws Exception {
        String url = YAHOO_SEARCH_URL
                + "?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8.name())
                + "&quotesCount=200&newsCount=0";

        HttpURLConnection conn = openConnection(url);
        String json = readResponse(conn);
        conn.disconnect();

        JsonNode root = objectMapper.readTree(json);
        JsonNode quotes = root.get("quotes");

        List<String> symbols = new ArrayList<>();
        if (quotes != null && quotes.isArray()) {
            for (JsonNode node : quotes) {
                String symbol = getText(node, "symbol");
                String quoteType = getText(node, "quoteType");

                if ("ETF".equalsIgnoreCase(quoteType)
                        && (symbol.endsWith(".KS") || symbol.endsWith(".KQ"))) {
                    symbols.add(symbol);
                }
            }
        }

        log.debug("Yahoo 검색 '{}': {}건", query, symbols.size());
        return symbols;
    }

    private List<YahooStockVO> fetchQuotes(List<String> symbols) throws Exception {
        List<YahooStockVO> stockList = new ArrayList<>();

        for (int i = 0; i < symbols.size(); i += QUOTE_BATCH_SIZE) {
            List<String> batch = symbols.subList(i,
                    Math.min(i + QUOTE_BATCH_SIZE, symbols.size()));
            String symbolStr = String.join(",", batch);

            String url = YAHOO_QUOTE_URL
                    + "?symbols=" + symbolStr
                    + "&crumb=" + URLEncoder.encode(crumb, StandardCharsets.UTF_8.name());

            HttpURLConnection conn = openConnection(url);
            conn.setRequestProperty("Cookie", sessionCookie);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                log.warn("Yahoo Quote API 응답 오류 (HTTP {})", responseCode);
                conn.disconnect();
                continue;
            }

            String json = readResponse(conn);
            conn.disconnect();

            JsonNode quoteResponse = objectMapper.readTree(json).get("quoteResponse");
            if (quoteResponse == null) continue;

            JsonNode result = quoteResponse.get("result");
            if (result == null || !result.isArray()) continue;

            for (JsonNode node : result) {
                stockList.add(convertToVO(node));
            }
        }

        return stockList;
    }

    private YahooStockVO convertToVO(JsonNode node) {
        String symbol = getText(node, "symbol");
        String stockId = symbol.replaceAll("\\.(KS|KQ)$", "");

        double price = getDouble(node, "regularMarketPrice");
        double change = getDouble(node, "regularMarketChange");
        double changePercent = getDouble(node, "regularMarketChangePercent");
        double nav = getDouble(node, "navPrice");
        long volume = getLong(node, "regularMarketVolume");
        long marketCap = getLong(node, "marketCap");

        if (marketCap == 0) {
            marketCap = getLong(node, "totalAssets");
        }

        long marketCapEok = marketCap / UNIT_EOKWON;

        return YahooStockVO.builder()
                .stockId(stockId)
                .name(getText(node, "shortName"))
                .price(formatNumber(price))
                .priceChange(formatNumber(change))
                .fluctuationRate(formatDecimal(changePercent))
                .nav(nav > 0 ? formatDecimal(nav) : "")
                .volumn(String.valueOf(volume))
                .marketCapitalization(marketCapEok > 0 ? String.valueOf(marketCapEok) : "0")
                .currency(getText(node, "currency"))
                .build();
    }

    private HttpURLConnection openConnection(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept", "application/json, text/plain, */*");
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

    private long getLong(JsonNode node, String field) {
        JsonNode f = node.get(field);
        return (f != null && !f.isNull()) ? f.asLong(0) : 0;
    }

    private String formatNumber(double value) {
        if (value == 0) return "0";
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(Math.round(value * 100.0) / 100.0);
    }

    private String formatDecimal(double value) {
        if (value == 0) return "0";
        return String.valueOf(Math.round(value * 100.0) / 100.0);
    }

    private long parseLongSafe(String value) {
        if (value == null || value.isEmpty()) return 0;
        try {
            return Long.parseLong(value.replace(",", ""));
        } catch (NumberFormatException e) {
            try {
                return (long) Double.parseDouble(value.replace(",", ""));
            } catch (NumberFormatException e2) {
                return 0;
            }
        }
    }
}
