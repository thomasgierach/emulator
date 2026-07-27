package com.zos.home;

import com.zos.home.dto.CreateUserRequestDto;
import com.zos.home.dto.CreateUserResponseDto;
import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;
import com.zos.home.service.AuthClientService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import com.zos.auth.proto.CreateUserReply;
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

        void loginAssertions(LoginReply protobufResponse, LoginResponseDto response) {
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
        void createUserAssertions(CreateUserReply protobufResponse, CreateUserResponseDto response) {
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

                loginAssertions(protobufResponse, response);

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
        @Test
        void loginSendsRequestAndReturnsFailedResponse()
                throws InterruptedException {

                // Given
                LoginReply protobufResponse =
                        LoginReply.newBuilder()
                                .setSuccess(false)
                                .setUsername("testuser")
                                .setRole("")
                                .setToken("")
                                .setErrorMessage("Invalid username or password")
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

                loginAssertions(protobufResponse, response);

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
        @Test
        void createUserSendsRequestAndReturnsSuccessfulResponse()
                throws InterruptedException {

                // Given
                CreateUserReply protobufResponse =
                        CreateUserReply.newBuilder()
                                .setSuccess(true)
                                .setUsername("testuser")
                                .setErrorMessage("User created successfully")
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

                CreateUserRequestDto request =
                        new CreateUserRequestDto(
                                "testuser",
                                "password123456789",
                                "test@email.com"
                        );

                // When
                CreateUserResponseDto response =
                        authClientService.createUser(request);

                createUserAssertions(protobufResponse, response);

                RecordedRequest recordedRequest =
                        AUTH_SERVER.takeRequest();

                assertEquals("POST", recordedRequest.getMethod());
                assertEquals(
                        "/auth/users",
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