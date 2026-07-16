package com.kangong.advstock.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kangong.stock.model.StockVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class AdvStockParser {

    private static final String NAVER_API_URL = "https://m.stock.naver.com/api/index/KOSPI/enrollStocks";
    private static final String NAVER_ETF_API_URL = "https://m.stock.naver.com/api/etf/%s/basic";
    private static final String NAVER_STOCK_API_URL = "https://m.stock.naver.com/api/stock/%s/basic";
    private static final String NAVER_SEARCH_URL = "https://ac.stock.naver.com/ac?q=%s&target=stock,etf";
    private static final int PAGE_SIZE = 50;
    private static final int MAX_PAGE = 30;
    private static final Pattern JO_PATTERN = Pattern.compile("(\\d+)조");
    private static final Pattern EOK_PATTERN = Pattern.compile("(\\d+)억");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<StockVO> fetchKospiStockList() throws Exception {
        List<StockVO> allStocks = new ArrayList<>();

        for (int page = 1; page <= MAX_PAGE; page++) {
            String url = NAVER_API_URL + "?pageSize=" + PAGE_SIZE + "&type=object&page=" + page;
            String json = getRequest(url);
            List<StockVO> pageStocks = parsePage(json);

            if (pageStocks.isEmpty()) {
                break;
            }
            allStocks.addAll(pageStocks);
            log.info("KOSPI page {}: {}건 (누적: {}건)", page, pageStocks.size(), allStocks.size());
        }

        log.info("KOSPI 전체 종목 수: {}", allStocks.size());
        return allStocks;
    }

    private List<StockVO> parsePage(String jsonStr) throws Exception {
        List<StockVO> stockList = new ArrayList<>();
        JsonNode root = objectMapper.readTree(jsonStr);
        JsonNode stocks = root.get("stocks");

        if (stocks == null || !stocks.isArray()) {
            return stockList;
        }

        for (JsonNode node : stocks) {
            String stockEndType = getText(node, "stockEndType");
            if (!"stock".equals(stockEndType) && !"etf".equals(stockEndType)) {
                continue;
            }

            StockVO vo = StockVO.builder()
                    .stockId(getText(node, "itemCode"))
                    .name(getText(node, "stockName"))
                    .price(cleanNumber(getText(node, "closePrice")))
                    .priceChange(cleanNumber(getText(node, "compareToPreviousClosePrice")))
                    .fluctuationRate(cleanNumber(getText(node, "fluctuationsRatio")))
                    .volumn(cleanNumber(getText(node, "accumulatedTradingVolume")))
                    .tradingValue(cleanNumber(getText(node, "accumulatedTradingValue")))
                    .marketCapitalization(cleanNumber(getText(node, "marketValue")))
                    .build();
            stockList.add(vo);
        }

        return stockList;
    }

    private String getRequest(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Naver API 호출 실패 (HTTP " + responseCode + ", URL: " + httpUrl + ")");
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            conn.disconnect();
        }
        return sb.toString();
    }

    private String getText(JsonNode node, String field) {
        JsonNode f = node.get(field);
        return (f != null && !f.isNull()) ? f.asText("") : "";
    }

    public StockVO fetchStockByCode(String stockId) throws Exception {
        try {
            String json = getRequest(String.format(NAVER_ETF_API_URL, stockId));
            JsonNode node = objectMapper.readTree(json);
            if (node.has("itemCode")) {
                return StockVO.builder()
                        .stockId(getText(node, "itemCode"))
                        .name(getText(node, "stockName"))
                        .price(cleanNumber(getText(node, "closePrice")))
                        .volumn(cleanNumber(getText(node, "accumulatedTradingVolume")))
                        .marketCapitalization(parseKoreanMarketCap(getText(node, "marketValue")))
                        .build();
            }
        } catch (Exception e) {
            log.debug("ETF API 실패 ({}), stock API 시도", stockId);
        }

        String json = getRequest(String.format(NAVER_STOCK_API_URL, stockId));
        JsonNode node = objectMapper.readTree(json);
        return StockVO.builder()
                .stockId(getText(node, "itemCode"))
                .name(getText(node, "stockName"))
                .price(cleanNumber(getText(node, "closePrice")))
                .volumn("0")
                .marketCapitalization("0")
                .build();
    }

    String parseKoreanMarketCap(String value) {
        if (value == null || value.isEmpty()) return "0";
        String cleaned = value.replaceAll("[,\\s]", "");
        long total = 0;
        Matcher joMatcher = JO_PATTERN.matcher(cleaned);
        if (joMatcher.find()) {
            total += Long.parseLong(joMatcher.group(1)) * 10000;
        }
        Matcher eokMatcher = EOK_PATTERN.matcher(cleaned);
        if (eokMatcher.find()) {
            total += Long.parseLong(eokMatcher.group(1));
        }
        return total > 0 ? String.valueOf(total) : cleanNumber(value);
    }

    public List<StockVO> searchStock(String keyword) {
        List<StockVO> result = new ArrayList<>();
        try {
            String encoded = java.net.URLEncoder.encode(keyword, "UTF-8");
            String json = getRequest(String.format(NAVER_SEARCH_URL, encoded));
            JsonNode root = objectMapper.readTree(json);
            JsonNode items = root.path("items");
            if (items.isArray()) {
                for (JsonNode item : items) {
                    String code = getText(item, "code");
                    String name = getText(item, "name");
                    if (!code.isEmpty() && !name.isEmpty()) {
                        result.add(StockVO.builder().stockId(code).name(name).build());
                    }
                }
            }
        } catch (Exception e) {
            log.debug("네이버 종목 검색 실패: {}", e.getMessage());
        }
        return result;
    }

    private String cleanNumber(String value) {
        if (value == null || value.isEmpty() || "N/A".equals(value) || "-".equals(value.trim())) return "0";
        String cleaned = value.replaceAll("[^0-9.\\-]", "");
        return cleaned.isEmpty() || "-".equals(cleaned) ? "0" : cleaned;
    }
}
