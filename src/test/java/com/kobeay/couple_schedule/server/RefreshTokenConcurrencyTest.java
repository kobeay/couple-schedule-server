package com.kobeay.couple_schedule.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RefreshTokenConcurrencyTest {

    private static final String TEST_PASSWORD = "123456";

    @LocalServerPort
    private int port;

    @Autowired
    private TestDataCleanup testDataCleanup;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void sameRefreshTokenCannotBeUsedTwiceConcurrently() throws Exception {

        String baseUrl = "http://localhost:" + port;

        // 1. 테스트마다 새로운 이메일 생성
        String testEmail = "test-" + UUID.randomUUID() + "@test.com";

        try {
            // 2. 테스트용 회원가입
            String signUpBody = objectMapper.writeValueAsString(
                    new SignUpData(
                            testEmail,
                            TEST_PASSWORD,
                            TEST_PASSWORD,
                            "테스트유저"
                    )
            );

            HttpRequest signUpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/users/signup"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(signUpBody))
                    .build();

            HttpResponse<String> signUpResponse = client.send(
                    signUpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, signUpResponse.statusCode());

            // 3. 로그인해서 Refresh Token 발급
            String loginBody = objectMapper.writeValueAsString(
                    new LoginData(testEmail, TEST_PASSWORD)
            );

            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/users/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                    .build();

            HttpResponse<String> loginResponse = client.send(
                    loginRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, loginResponse.statusCode());

            JsonNode loginJson = objectMapper.readTree(loginResponse.body());
            String refreshToken = loginJson.get("refreshToken").asText();

            // 4. 동일한 Refresh Token으로 요청 2개 준비
            String refreshBody = objectMapper.writeValueAsString(
                    new RefreshData(refreshToken)
            );

            HttpRequest refreshRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/users/refresh"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(refreshBody))
                    .build();

            // 5. 두 요청을 동시에 시작
            CountDownLatch startSignal = new CountDownLatch(1);

            CompletableFuture<Integer> first = CompletableFuture.supplyAsync(() -> {
                try {
                    startSignal.await();

                    return client.send(
                            refreshRequest,
                            HttpResponse.BodyHandlers.ofString()
                    ).statusCode();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            CompletableFuture<Integer> second = CompletableFuture.supplyAsync(() -> {
                try {
                    startSignal.await();

                    return client.send(
                            refreshRequest,
                            HttpResponse.BodyHandlers.ofString()
                    ).statusCode();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            startSignal.countDown();

            int firstStatus = first.get(15, TimeUnit.SECONDS);
            int secondStatus = second.get(15, TimeUnit.SECONDS);

            System.out.println("첫 번째 요청: " + firstStatus);
            System.out.println("두 번째 요청: " + secondStatus);

            // 6. 하나는 200, 다른 하나는 401이어야 함
            int successCount = 0;
            int unauthorizedCount = 0;

            if (firstStatus == 200) successCount++;
            if (secondStatus == 200) successCount++;

            if (firstStatus == 401) unauthorizedCount++;
            if (secondStatus == 401) unauthorizedCount++;

            assertEquals(1, successCount);
            assertEquals(1, unauthorizedCount);

        } finally {
            // 7. 테스트 회원과 Refresh Token 자동 삭제
            testDataCleanup.cleanup(testEmail);
        }
    }

    record SignUpData(
            String email,
            String password,
            String passwordConfirm,
            String nickname
    ) {}

    record LoginData(String email, String password) {}

    record RefreshData(String refreshToken) {}
}