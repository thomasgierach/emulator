package com.zos.home;

import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;
import com.zos.home.service.AuthClientService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import com.zos.auth.proto.LoginReply;

import okio.Buffer;

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
        LoginReply protobufResponse =
                LoginReply.newBuilder()
                        .setSuccess(true)
                        .setUsername("testuser")
                        .setRole("BASIC")
                        .setToken("mocked-token")
                        .setErrorMessage("Login successful")
                        .build();

        Buffer responseBody = new Buffer();
        responseBody.write(protobufResponse.toByteArray());

        AUTH_SERVER.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader(
                                "Content-Type",
                                "application/x-protobuf"
                        )
                        .setBody(responseBody)
        );

        LoginRequestDto request =
                new LoginRequestDto(
                        "testuser",
                        "password123456789"
                );

        // When
        LoginResponseDto response =
                authClientService.login(request);

        // Then
        assertNotNull(response);

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
                ),
                () -> assertEquals(
                        "Login successful",
                        response.getErrorMessage()
                )
        );

        RecordedRequest recordedRequest =
                AUTH_SERVER.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(
                "/auth/login",
                recordedRequest.getPath()
        );

        assertEquals(
                "application/json",
                recordedRequest.getHeader("Content-Type")
        );

        assertEquals(
                "application/x-protobuf",
                recordedRequest.getHeader("Accept")
        );

        String requestJson =
                recordedRequest.getBody().readUtf8();

        assertTrue(
                requestJson.contains(
                        "\"username\":\"testuser\""
                )
        );

        assertTrue(
                requestJson.contains(
                        "\"password\":\"password123456789\""
                )
        );
        }
}