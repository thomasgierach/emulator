package com.zos.home;

import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;
import com.zos.home.service.AuthClientService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.abort;

@SpringBootTest
class AuthClientServiceIntegrationTest {

    private static final MockWebServer AUTH_SERVER = new MockWebServer();

    static {
        try {
            AUTH_SERVER.start();
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @DynamicPropertySource
    static void configureAuthService(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "auth.service.base-url",
                () -> AUTH_SERVER.url("/").toString()
        );
    }

    @Autowired
    private AuthClientService authClientService;

    @AfterAll
    static void shutDownServer() throws IOException {
        AUTH_SERVER.shutdown();
    }

    @Test
    void loginSendsRequestAndReturnsSuccessfulResponse()
            throws InterruptedException {

        // Given
        AUTH_SERVER.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader(
                                "Content-Type",
                                "application/json"
                        )
                        .setBody("""
                                {
                                  "success": true,
                                  "username": "testuser",
                                  "role": "BASIC",
                                  "token": "mocked-token",
                                  "errorMessage": "Login successful"
                                }
                                """)
        );

        LoginRequestDto request =
                new LoginRequestDto(
                        "testuser",
                        "password123456789"
                );

        // When
        LoginResponseDto response =
                authClientService.login(request);

        // Then: verify the response was deserialized
        assertNotNull(response);
        System.out.println("Response: " + response.toString());
        assertAll(
                () -> assertTrue(response.getSuccess()),
                () -> assertEquals(
                        "testuser",
                        response.getUsername()
                ),
                () -> assertEquals(
                        "BASIC",
                        response.getRole()
                ),
                () -> assertEquals(
                        "mocked-token",
                        response.getToken()
                )
        );

        // Then: verify that AuthClientService made an HTTP request
        RecordedRequest recordedRequest =
                AUTH_SERVER.takeRequest(
                        2,
                        TimeUnit.SECONDS
                );

        assertNotNull(
                recordedRequest,
                "AuthClientService did not send an HTTP request"
        );

        assertEquals("POST", recordedRequest.getMethod());

        String requestBody =
                recordedRequest.getBody().readUtf8();

        assertAll(
                () -> assertTrue(
                        requestBody.contains("\"username\":\"testuser\"")
                ),
                () -> assertTrue(
                        requestBody.contains(
                                "\"password\":\"password123456789\""
                        )
                )
        );
    }
}