package com.kangong.test.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MenuUrlTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private String sessionCookie;

    @BeforeEach
    public void login() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", "admin@kangong.local");
        form.add("password", "1234");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<String> loginResponse = restTemplate.exchange(
                "/login", HttpMethod.POST, request, String.class);

        List<String> cookies = loginResponse.getHeaders().get("Set-Cookie");
        if (cookies != null) {
            sessionCookie = cookies.stream()
                    .filter(c -> c.startsWith("JSESSIONID"))
                    .findFirst()
                    .orElse(null);
        }
        System.out.println("Login status: " + loginResponse.getStatusCode());
        System.out.println("Session cookie: " + (sessionCookie != null ? "획득" : "없음"));
    }

    private void testMenuPost(String url, String menuName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            if (sessionCookie != null) {
                headers.set("Cookie", sessionCookie);
            }

            HttpEntity<String> request = new HttpEntity<>("", headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class);

            int status = response.getStatusCodeValue();
            String body = response.getBody();
            int bodyLen = body != null ? body.length() : 0;
            boolean hasHtml = body != null && body.contains("<!DOCTYPE html>");
            boolean has404 = body != null && body.contains("404");
            boolean hasError = body != null && (body.contains("Whitelabel Error") || body.contains("Error Page"));

            String result;
            if (status == 200 && hasHtml && !hasError) {
                result = "OK (HTML " + bodyLen + "자)";
            } else if (status == 200 && hasError) {
                result = "ERROR PAGE (body " + bodyLen + "자)";
            } else {
                result = "HTTP " + status + " (body " + bodyLen + "자)";
            }

            System.out.printf("[%s] %-15s POST %-25s => %s%n",
                    (status == 200 && hasHtml && !hasError) ? " OK " : "FAIL",
                    menuName, url, result);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-15s POST %-25s => EXCEPTION: %s%n",
                    menuName, url, e.getMessage());
        }
    }

    @Test
    @DisplayName("모든 메뉴 URL 실제 HTTP POST 테스트")
    public void testAllMenuUrls() {
        System.out.println("============ 메뉴 URL 실제 렌더링 테스트 ============");

        testMenuPost("/board/list", "게시판");
        testMenuPost("/stock2", "증권");
        testMenuPost("/calendar/list", "일정");
        testMenuPost("/calendar/test", "일정테스트");
        testMenuPost("/user/list", "사용자등록");
        testMenuPost("/commontable/list", "공용테이블");
        testMenuPost("/common/dd/list", "DD");

        System.out.println("\n============ 비인증 URL 테스트 ============");
        testMenuPost("/security/customLogin", "로그인");

        System.out.println("============ 완료 ============");
    }

    @Test
    @DisplayName("로그인 페이지 JSP 렌더링 확인")
    public void testLoginPageRendering() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/security/customLogin", String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains("로그인");
        System.out.println("[OK] 로그인 페이지 정상 렌더링 (HTML " + response.getBody().length() + "자)");
    }
}
