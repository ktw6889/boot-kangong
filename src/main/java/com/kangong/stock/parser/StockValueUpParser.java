package com.kangong.stock.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

/**
 * 코리아밸류업지수 구성종목 파싱
 * KRX 데이터시스템 POST API 사용
 * DB 준비: ALTER TABLE ST_STOCK_MASTER ADD COLUMN VALUE_UP_YN CHAR(1) NULL DEFAULT NULL;
 */
@Log4j2
@Component
public class StockValueUpParser extends AbstractStockDataParser {

    private static final String KRX_API_URL = "https://data.krx.co.kr/comm/bldAttendant/getJsonData.cmd";
    // 코리아밸류업지수 코드: KRX 지수체계 내 indIdx=2, indIdx2=028
    private static final String VALUE_UP_INDEX_IDX  = "2";
    private static final String VALUE_UP_INDEX_IDX2 = "028";

    /**
     * 코리아밸류업지수 구성종목 코드 목록 반환
     * @return 6자리 종목코드 Set
     */
    public Set<String> fetchValueUpStockIds() throws Exception {
        String trdDd = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String body = buildPostBody(trdDd);

        String json = postKrxApi(body);
        return parseStockIds(json);
    }

    private String buildPostBody(String trdDd) {
        return "bld=dbms%2FMDC%2FSTAT%2Fstandard%2FMDCSTAT00601"
             + "&locale=ko_KR"
             + "&tboxindIdx_finder_equidx0_0=%EC%BD%94%EB%A6%AC%EC%95%84%EB%B0%B8%EB%A5%98%EC%97%85"
             + "&indIdx=" + VALUE_UP_INDEX_IDX
             + "&indIdx2=" + VALUE_UP_INDEX_IDX2
             + "&codeNm=%EC%BD%94%EB%A6%AC%EC%95%84%EB%B0%B8%EB%A5%98%EC%97%85%EC%A7%80%EC%88%98"
             + "&param1itemIdx=0"
             + "&marketCode=STK"
             + "&trdDd=" + trdDd
             + "&share=1&money=1&csvxls_isNo=false";
    }

    private String postKrxApi(String body) throws Exception {
        URL url = new URL(KRX_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0");
        conn.setRequestProperty("Referer", "https://data.krx.co.kr/");

        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8)) {
            writer.write(body);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private Set<String> parseStockIds(String json) {
        Set<String> ids = new HashSet<>();
        try {
            JSONObject root = (JSONObject) JSONValue.parse(json);
            if (root == null) return ids;

            JSONArray block = (JSONArray) root.get("OutBlock_1");
            if (block == null) return ids;

            for (Object obj : block) {
                JSONObject row = (JSONObject) obj;
                // KRX API 응답 필드: ISU_SRT_CD = 6자리 종목코드
                String code = (String) row.get("ISU_SRT_CD");
                if (code != null && !code.isEmpty()) {
                    ids.add(code.trim());
                }
            }
            log.info("코리아밸류업지수 구성종목 {}개 파싱", ids.size());
        } catch (Exception e) {
            log.warn("밸류업지수 파싱 실패: {}", e.getMessage());
        }
        return ids;
    }
}
