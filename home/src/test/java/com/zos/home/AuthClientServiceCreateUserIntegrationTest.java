package com.zos.home;

import com.zos.auth.proto.CreateUserReply;
import com.zos.home.dto.CreateUserRequestDto;
import com.zos.home.dto.CreateUserResponseDto;
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
class AuthClientServiceCreateUserIntegrationTest {

    private static final MockWebServer AUTH_SERVER =
            new MockWebServer();

    private static final String CREATE_USER_PATH =
            "/auth/users";

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

    private void assertCreateUserResponse(
            CreateUserReply protobufResponse,
            CreateUserResponseDto response
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
                "AuthClientService did not send a create-user request"
        );

        return recordedRequest;
    }

    private void assertCreateUserRequest(
            RecordedRequest recordedRequest
    ) {
        assertAll(
                () -> assertEquals(
                        "POST",
                        recordedRequest.getMethod()
                ),
                () -> assertEquals(
                        CREATE_USER_PATH,
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
                ),
                () -> assertTrue(
                        requestJson.contains(
                                "\"email\":\"test@email.com\""
                        )
                )
        );
    }

    @Test
    void createUserSendsRequestAndReturnsApplicationSuccessfulResponse()
            throws InterruptedException {

        CreateUserReply protobufResponse =
                CreateUserReply.newBuilder()
                        .setSuccess(true)
                        .setUsername("testuser")
                        .setErrorMessage(
                                "User created successfully"
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

        CreateUserRequestDto request =
                new CreateUserRequestDto(
                        "testuser",
                        "password123456789",
                        "test@email.com"
                );

        CreateUserResponseDto response =
                authClientService.createUser(request);

        assertCreateUserResponse(
                protobufResponse,
                response
        );

        assertCreateUserRequest(
                takeRecordedRequest()
        );
    }

    @Test
    void createUserSendsRequestAndReturnsApplicationFailedResponse()
            throws InterruptedException {

        CreateUserReply protobufResponse =
                CreateUserReply.newBuilder()
                        .setSuccess(false)
                        .setUsername("testuser")
                        .setErrorMessage("Error creating user.")
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

        CreateUserRequestDto request =
                new CreateUserRequestDto(
                        "testuser",
                        "password123456789",
                        "test@email.com"
                );

        CreateUserResponseDto response =
                authClientService.createUser(request);

        assertCreateUserResponse(
                protobufResponse,
                response
        );

        assertCreateUserRequest(
                takeRecordedRequest()
        );
    }

    @Test
    void createUserThrowsExceptionWhenAuthServerReturns500()
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

        CreateUserRequestDto request =
                new CreateUserRequestDto(
                        "testuser",
                        "password123456789",
                        "test@email.com"
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authClientService.createUser(request)
                );

        assertTrue(
                exception.getMessage().contains("500")
        );

        assertCreateUserRequest(
                takeRecordedRequest()
        );
    }
}
