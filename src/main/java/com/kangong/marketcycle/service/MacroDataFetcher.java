package com.kangong.marketcycle.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kangong.marketcycle.dto.MacroIndicatorData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

@Log4j2
@Service
public class MacroDataFetcher {

    private static final String ECOS_BASE = "https://ecos.bok.or.kr/api/StatisticSearch";
    private static final String FRED_BASE = "https://api.stlouisfed.org/fred/series/observations";
    private static final String TE_BASE = "https://tradingeconomics.com/";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    @Value("${macro.ecos.api-key:}")
    private String ecosApiKey;

    @Value("${macro.fred.api-key:}")
    private String fredApiKey;

    @Value("${macro.cache.ttl-ms:21600000}")
    private long cacheTtl;

    private final RestTemplate rest;
    private final ObjectMapper mapper;

    private volatile MacroIndicatorData cached;
    private volatile long cachedAt;

    public MacroDataFetcher(ObjectMapper mapper) {
        this.mapper = mapper;
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(Duration.ofSeconds(5));
        f.setReadTimeout(Duration.ofSeconds(10));
        this.rest = new RestTemplate(f);
    }

    public MacroIndicatorData fetch() {
        if (cached != null && System.currentTimeMillis() - cachedAt < cacheTtl) {
            return cached;
        }

        MacroIndicatorData defaults = MacroIndicatorData.current();
        MacroIndicatorData.MacroIndicatorDataBuilder b = defaults.toBuilder();

        if (StringUtils.hasText(ecosApiKey)) {
            fetchBokBaseRate(b);
            fetchM2Growth(b);
            fetchExchangeRate(b);
        } else {
            log.info("ECOS API 키 미설정(macro.ecos.api-key) — 기본값 사용");
        }

        if (StringUtils.hasText(fredApiKey)) {
            fetchFedRate(b);
        } else {
            log.info("FRED API 키 미설정(macro.fred.api-key) — 기본값 사용");
        }

        fetchPmi(b);
        fetchKospiPer(b);
        fetchEarningsGrowth(b);

        cached = b.build();
        cachedAt = System.currentTimeMillis();
        log.info("매크로 지표 갱신: 기준금리={}%, M2={}%, Fed={}%, 환율={}원, PMI={}, PER={}, 기업이익={}%",
                cached.getBaseRate(), cached.getM2GrowthRate(),
                cached.getFedRate(), cached.getExchangeRate(),
                cached.getPmi(), cached.getKospiPer(), cached.getEarningsGrowthRate());
        return cached;
    }

    public void clearCache() {
        cached = null;
        cachedAt = 0;
    }

    // =====================================================================
    // ECOS (한국은행 경제통계)
    // =====================================================================

    private void fetchBokBaseRate(MacroIndicatorData.MacroIndicatorDataBuilder b) {
        try {
            LocalDate now = LocalDate.now();
            String start = now.minusMonths(7).format(DateTimeFormatter.ofPattern("yyyyMM"));
            String end = now.format(DateTimeFormatter.ofPattern("yyyyMM"));

            String url = ecosUrl("722Y001", "M", start, end, "0101000");
            List<double[]> rows = parseEcosTimeSeries(url);
            if (rows.isEmpty()) return;

            double current = rows.get(rows.size() - 1)[1];
            double past = rows.get(0)[1];
            b.baseRate(current);
            b.baseRateChange6m(round2(current - past));
            log.info("기준금리 조회 성공: {}% (6개월 변동: {}%p)", current, round2(current - past));
        } catch (Exception e) {
            log.warn("기준금리 조회 실패 — 기본값 사용: {}", e.getMessage());
        }
    }

    private void fetchM2Growth(MacroIndicatorData.MacroIndicatorDataBuilder b) {
        try {
            LocalDate now = LocalDate.now();
            DateTimeFormatter mf = DateTimeFormatter.ofPattern("yyyyMM");
            String start = now.minusMonths(15).format(mf);
            String end = now.format(mf);

            String url = ecosUrl("161Y006", "M", start, end, "BBHA00");
            List<double[]> rows = parseEcosTimeSeries(url);
            if (rows.size() < 2) return;

            double latest = rows.get(rows.size() - 1)[1];
            double latestTime = rows.get(rows.size() - 1)[0];
            double targetTime = latestTime - 100; // 전년 동월 (YYYYMM - 100)
            double yearAgo = latest;
            for (double[] row : rows) {
                if (Math.abs(row[0] - targetTime) < 1) {
                    yearAgo = row[1];
                    break;
                }
            }
            if (yearAgo > 0 && yearAgo != latest) {
                double growth = round2((latest - yearAgo) / yearAgo * 100);
                b.m2GrowthRate(growth);
                log.info("M2 증가율 조회 성공: {}% (최신: {}, 전년동월: {})", growth, latest, yearAgo);
            }
        } catch (Exception e) {
            log.warn("M2 증가율 조회 실패 — 기본값 사용: {}", e.getMessage());
        }
    }

    private void fetchExchangeRate(MacroIndicatorData.MacroIndicatorDataBuilder b) {
        try {
            LocalDate now = LocalDate.now();
            String start = now.minusDays(14).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String end = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String url = ecosUrl("731Y003", "D", start, end, "0000002");
            List<double[]> rows = parseEcosTimeSeries(url);
            if (rows.isEmpty()) return;

            double latest = rows.get(rows.size() - 1)[1];
            b.exchangeRate(latest);
            log.info("환율 조회 성공: {}원", latest);
        } catch (Exception e) {
            log.warn("환율 조회 실패 — 기본값 사용: {}", e.getMessage());
        }
    }

    private String ecosUrl(String statCode, String period, String start, String end, String... items) {
        StringBuilder sb = new StringBuilder(ECOS_BASE);
        sb.append("/").append(ecosApiKey);
        sb.append("/json/kr/1/100/");
        sb.append(statCode).append("/").append(period);
        sb.append("/").append(start).append("/").append(end);
        for (String item : items) {
            sb.append("/").append(item);
        }
        return sb.toString();
    }

    private List<double[]> parseEcosTimeSeries(String url) throws Exception {
        String json = rest.getForObject(url, String.class);
        JsonNode root = mapper.readTree(json);

        JsonNode error = root.path("RESULT");
        if (!error.isMissingNode()) {
            log.debug("ECOS 응답: {} — {}", error.path("CODE").asText(), error.path("MESSAGE").asText());
            return Collections.emptyList();
        }

        JsonNode rows = root.path("StatisticSearch").path("row");
        if (rows.isMissingNode() || !rows.isArray()) return Collections.emptyList();

        List<double[]> result = new ArrayList<>();
        for (JsonNode row : rows) {
            String timeStr = row.path("TIME").asText();
            String valueStr = row.path("DATA_VALUE").asText("").replace(",", "").trim();
            if (valueStr.isEmpty() || valueStr.equals("-") || valueStr.equals("*")) continue;
            try {
                double time = Double.parseDouble(timeStr);
                double value = Double.parseDouble(valueStr);
                result.add(new double[]{time, value});
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    // =====================================================================
    // FRED (미국 연방준비은행)
    // =====================================================================

    private void fetchFedRate(MacroIndicatorData.MacroIndicatorDataBuilder b) {
        try {
            LocalDate now = LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

            Double current = fetchFredValue("FEDFUNDS",
                    now.minusMonths(2).format(fmt), now.format(fmt));
            Double past = fetchFredValue("FEDFUNDS",
                    now.minusMonths(8).format(fmt), now.minusMonths(5).format(fmt));

            if (current != null) {
                b.fedRate(current);
                if (past != null) {
                    b.fedRateChange6m(round2(current - past));
                }
                log.info("Fed 금리 조회 성공: {}% (6개월 변동: {}%p)",
                        current, past != null ? round2(current - past) : "N/A");
            }
        } catch (Exception e) {
            log.warn("Fed 금리 조회 실패 — 기본값 사용: {}", e.getMessage());
        }
    }

    private Double fetchFredValue(String seriesId, String start, String end) throws Exception {
        String url = String.format(
                "%s?series_id=%s&api_key=%s&file_type=json&sort_order=desc&limit=1&observation_start=%s&observation_end=%s",
                FRED_BASE, seriesId, fredApiKey, start, end);

        String json = rest.getForObject(url, String.class);
        JsonNode root = mapper.readTree(json);
        JsonNode obs = root.path("observations");
        if (obs.isMissingNode() || !obs.isArray() || obs.isEmpty()) return null;

        String val = obs.get(0).path("value").asText("").trim();
        if (val.isEmpty() || val.equals(".")) return null;
        return Double.parseDouble(val);
    }

    // =====================================================================
    // TradingEconomics 크롤링 (PMI, 코스피PER, 기업이익)
    // =====================================================================

    private void fetchPmi(MacroIndicatorData.MacroIndicatorDataBuilder b) {
        try {
            String html = fetchTradingEconomicsPage("south-korea/manufacturing-pmi");
            if (html == null) return;

            Matcher m = Pattern.compile(
                    "Manufacturing PMI in South Korea[^.]*?(?:to|at)\\s+([\\d.]+)")
                    .matcher(html);
            if (m.find()) {
                double pmi = Double.parseDouble(m.group(1));
                if (pmi > 0 && pmi < 100) {
                    b.pmi(pmi);
                    log.info("PMI 크롤링 성공: {}", pmi);
                }
            }
        } catch (Exception e) {
            log.warn("PMI 크롤링 실패 — 기본값 사용: {}", e.getMessage());
        }
    }

    private void fetchKospiPer(MacroIndicatorData.MacroIndicatorDataBuilder b) {
        try {
            String html = Jsoup.connect("https://www.multpl.com/s-p-500-pe-ratio")
                    .userAgent(USER_AGENT)
                    .timeout(15000)
                    .execute()
                    .body();
            if (html == null) return;

            Matcher m = Pattern.compile(
                    "Current S&P 500 PE Ratio is ([\\d.]+)")
                    .matcher(html);
            if (m.find()) {
                double per = Double.parseDouble(m.group(1));
                if (per > 1 && per < 100) {
                    b.kospiPer(per);
                    b.historicalPerAvg(19.6);
                    log.info("시장 PER 크롤링 성공(S&P500): {}", per);
                }
            }
        } catch (Exception e) {
            log.warn("시장 PER 크롤링 실패 — 기본값 사용: {}", e.getMessage());
        }
    }

    private void fetchEarningsGrowth(MacroIndicatorData.MacroIndicatorDataBuilder b) {
        if (!StringUtils.hasText(fredApiKey)) return;
        try {
            LocalDate now = LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

            Double current = fetchFredValue("CP",
                    now.minusMonths(6).format(fmt), now.format(fmt));
            Double yearAgo = fetchFredValue("CP",
                    now.minusMonths(18).format(fmt), now.minusMonths(12).format(fmt));

            if (current != null && yearAgo != null && yearAgo > 0) {
                double growth = round2((current - yearAgo) / yearAgo * 100);
                b.earningsGrowthRate(growth);
                log.info("기업이익 증가율 조회 성공(FRED CP): {}% (현재={}, 전년={})",
                        growth, current, yearAgo);
            }
        } catch (Exception e) {
            log.warn("기업이익 조회 실패 — 기본값 사용: {}", e.getMessage());
        }
    }

    private String fetchTradingEconomicsPage(String path) {
        try {
            return Jsoup.connect(TE_BASE + path)
                    .userAgent(USER_AGENT)
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .ignoreContentType(true)
                    .timeout(15000)
                    .execute()
                    .body();
        } catch (Exception e) {
            log.debug("TradingEconomics 페이지 로드 실패 ({}): {}", path, e.getMessage());
            return null;
        }
    }

    // =====================================================================
    // 유틸
    // =====================================================================

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
