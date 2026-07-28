package com.zos.home;

import com.zos.auth.proto.LoginReply;
import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;
import com.zos.home.service.AuthClientService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
class AuthClientServiceLoginIntegrationTest {

    private static final MockWebServer AUTH_SERVER =
            new MockWebServer();

    private static final String LOGIN_PATH = "/auth/login";

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

    private void assertLoginResponse(
            LoginReply protobufResponse,
            LoginResponseDto response
    ) {
        assertNotNull(response);

        assertAll(
                () -> assertEquals(
                        protobufResponse.getSuccess(),
                        response.getSuccess()
                ),
                () -> assertEquals(
                        protobufResponse.getUsername(),
                        response.getUsername()
                ),
                () -> assertEquals(
                        protobufResponse.getRole(),
                        response.getRole()
                ),
                () -> assertEquals(
                        protobufResponse.getToken(),
                        response.getToken()
                ),
                () -> assertEquals(
                        protobufResponse.getErrorMessage(),
                        response.getErrorMessage()
                )
        );
    }

    private RecordedRequest takeRecordedRequest()
            throws InterruptedException {

        RecordedRequest recordedRequest =
                AUTH_SERVER.takeRequest(
                        2,
                        TimeUnit.SECONDS
                );

        assertNotNull(
                recordedRequest,
                "AuthClientService did not send a login request"
        );

        return recordedRequest;
    }

    private void assertLoginRequest(
            RecordedRequest recordedRequest
    ) {
        assertAll(
                () -> assertEquals(
                        "POST",
                        recordedRequest.getMethod()
                ),
                () -> assertEquals(
                        LOGIN_PATH,
                        recordedRequest.getPath()
                ),
                () -> assertEquals(
                        "application/json",
                        recordedRequest.getHeader("Content-Type")
                ),
                () -> assertEquals(
                        "application/x-protobuf",
                        recordedRequest.getHeader("Accept")
                )
        );

        String requestJson =
                recordedRequest.getBody().readUtf8();

        assertAll(
                () -> assertTrue(
                        requestJson.contains(
                                "\"username\":\"testuser\""
                        )
                ),
                () -> assertTrue(
                        requestJson.contains(
                                "\"password\":\"password123456789\""
                        )
                )
        );
    }

    @Test
    void loginThrowsExceptionWhenAuthServerReturns500()
            throws InterruptedException {

        AUTH_SERVER.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .addHeader(
                                "Content-Type",
                                "text/plain"
                        )
                        .setBody("Internal server error")
        );

        LoginRequestDto request =
                new LoginRequestDto(
                        "testuser",
                        "password123456789"
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authClientService.login(request)
                );

        assertTrue(
                exception.getMessage().contains("500")
        );

        assertLoginRequest(takeRecordedRequest());
    }

    @Test
    void loginThrowsExceptionWhenResponseBodyIsEmpty()
            throws InterruptedException {

        AUTH_SERVER.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader(
                                "Content-Type",
                                "application/x-protobuf"
                        )
        );

        LoginRequestDto request =
                new LoginRequestDto(
                        "testuser",
                        "password123456789"
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authClientService.login(request)
                );

        assertTrue(
                exception.getMessage().contains(
                        "empty response body"
                )
        );

        assertLoginRequest(takeRecordedRequest());
    }

    @Test
    void loginSendsRequestAndReturnsApplicationSuccessfulResponse()
            throws InterruptedException {

        LoginReply protobufResponse =
                LoginReply.newBuilder()
                        .setSuccess(true)
                        .setUsername("testuser")
                        .setRole("BASIC")
                        .setToken("mocked-token")
                        .setErrorMessage("Login successful")
                        .build();

        Buffer responseBody = new Buffer();
        responseBody.write(
                protobufResponse.toByteArray()
        );

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

        LoginResponseDto response =
                authClientService.login(request);

        assertLoginResponse(
                protobufResponse,
                response
        );

        assertLoginRequest(takeRecordedRequest());
    }

    @Test
    void loginSendsRequestAndReturnsApplicationFailedResponse()
            throws InterruptedException {

        LoginReply protobufResponse =
                LoginReply.newBuilder()
                        .setSuccess(false)
                        .setUsername("testuser")
                        .setRole("")
                        .setToken("")
                        .setErrorMessage(
                                "Invalid username or password"
                        )
                        .build();

        Buffer responseBody = new Buffer();
        responseBody.write(
                protobufResponse.toByteArray()
        );

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

        LoginResponseDto response =
                authClientService.login(request);

        assertLoginResponse(
                protobufResponse,
                response
        );

        assertLoginRequest(takeRecordedRequest());
    }
}