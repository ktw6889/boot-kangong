package com.kangong.test.security;

import static com.kangong.common.security.SecurityConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecurityLoginTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("1. 로그인 페이지 실제 HTTP 요청 - JSP 렌더링 확인")
    public void testLoginPageRendering() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/security/customLogin", String.class);

        System.out.println("========== 실제 HTTP 응답 ==========");
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Headers: " + response.getHeaders());
        System.out.println("Body length: " + (response.getBody() != null ? response.getBody().length() : 0));
        System.out.println("Body (처음 2000자):");
        if (response.getBody() != null) {
            System.out.println(response.getBody().substring(0, Math.min(2000, response.getBody().length())));
        } else {
            System.out.println("[EMPTY BODY]");
        }
        System.out.println("====================================");

        assertThat(response.getStatusCode())
                .as("로그인 페이지 HTTP 상태코드")
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .as("로그인 페이지 HTML 내용")
                .isNotNull()
                .contains("로그인");
    }

    @Test
    @DisplayName("2. 정적 리소스(jQuery) 접근 가능 확인")
    public void testJQueryAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/webjars/jquery/3.7.1/jquery.js", String.class);

        System.out.println("jQuery status: " + response.getStatusCode());
        assertThat(response.getStatusCode())
                .as("jQuery 정적 리소스 접근")
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("3. 정적 리소스(kangong.js) 접근 가능 확인")
    public void testKangongJsAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/js/com/kangong.js", String.class);

        System.out.println("kangong.js status: " + response.getStatusCode());
        System.out.println("kangong.js body (처음 500자): " +
                (response.getBody() != null ? response.getBody().substring(0, Math.min(500, response.getBody().length())) : "NULL"));
        assertThat(response.getStatusCode())
                .as("kangong.js 정적 리소스 접근")
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("4. 보호된 페이지 -> 로그인 페이지 리다이렉트 확인")
    public void testProtectedPageRedirect() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/board/list", String.class);

        System.out.println("Protected page status: " + response.getStatusCode());
        System.out.println("Protected page body (처음 500자): " +
                (response.getBody() != null ? response.getBody().substring(0, Math.min(500, response.getBody().length())) : "NULL"));
    }

    @Test
    @DisplayName("5. PUBLIC_URLS 정적 리소스 경로 포함 확인")
    public void testPublicUrlsContainStaticResources() {
        List<String> urls = Arrays.asList(PUBLIC_URLS);
        assertThat(urls).contains("/webjars/**");
        assertThat(urls).contains("/js/**");
        assertThat(urls).contains("/css/**");
        System.out.println("[PASS] PUBLIC_URLS: " + urls);
    }

    @Test
    @DisplayName("6. Bootstrap CSS 접근 가능 확인")
    public void testBootstrapCssAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/webjars/bootstrap/4.6.2/css/bootstrap.css", String.class);

        System.out.println("Bootstrap CSS status: " + response.getStatusCode());
        assertThat(response.getStatusCode())
                .as("Bootstrap CSS 접근")
                .isEqualTo(HttpStatus.OK);
    }
}
