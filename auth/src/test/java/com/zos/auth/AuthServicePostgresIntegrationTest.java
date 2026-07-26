package com.zos.auth;

import com.zos.auth.dto.CreateUserRequestDto;
import com.zos.auth.dto.CreateUserResponseDto;
import com.zos.auth.dto.LoginRequestDto;
import com.zos.auth.dto.LoginResponseDto;
import com.zos.auth.repository.UsersRepository;
import com.zos.auth.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class AuthServicePostgresIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:15-alpine")
            )
                    .withDatabaseName("auth_test")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private AuthService authService;

    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
    }

    @Test
    void loginFailsWhenUserDoesNotExist() {
        LoginRequestDto request =
                new LoginRequestDto(
                        "nonexistentuser",
                        "wrongpassword"
                );

        LoginResponseDto response = authService.login(request);

        assertNotNull(response);

        assertAll(
                () -> assertFalse(response.getSuccess()),
                () -> assertNull(response.getToken())
        );
    }

    @Test
    void createdUserCanLogin() {
        String username = "testuser";
        String password = "password123456789";
        String email = "test@gmail.com";

        CreateUserResponseDto createResponse =
                authService.createUser(
                        new CreateUserRequestDto(
                                username,
                                password,
                                email
                        )
                );

        assertNotNull(createResponse);
        assertTrue(
                createResponse.getSuccess(),
                createResponse.getErrorMessage()
        );

        LoginResponseDto loginResponse =
                authService.login(
                        new LoginRequestDto(username, password)
                );

        assertNotNull(loginResponse);

        assertAll(
                () -> assertTrue(loginResponse.getSuccess()),
                () -> assertNotNull(loginResponse.getToken())
        );
    }

    @Test
    void loginFailsWithIncorrectPassword() {
        String username = "testuser";
        String password = "password123456789";

        CreateUserResponseDto createResponse =
                authService.createUser(
                        new CreateUserRequestDto(
                                username,
                                password,
                                "test@gmail.com"
                        )
                );

        assertTrue(createResponse.getSuccess());

        LoginResponseDto loginResponse =
                authService.login(
                        new LoginRequestDto(
                                username,
                                "incorrect-password"
                        )
                );

        assertNotNull(loginResponse);

        assertAll(
                () -> assertFalse(loginResponse.getSuccess()),
                () -> assertNull(loginResponse.getToken())
        );
    }

    @Test
    void duplicateUsernameIsRejected() {
        CreateUserRequestDto request =
                new CreateUserRequestDto(
                        "testuser",
                        "password123456789",
                        "test@gmail.com"
                );

        CreateUserResponseDto firstResponse =
                authService.createUser(request);

        CreateUserResponseDto duplicateResponse =
                authService.createUser(request);

        assertTrue(firstResponse.getSuccess());
        assertNotNull(duplicateResponse);

        assertAll(
                () -> assertFalse(duplicateResponse.getSuccess()),
                () -> assertEquals(
                        "testuser",
                        duplicateResponse.getUsername()
                ),
                () -> assertNotNull(
                        duplicateResponse.getErrorMessage()
                )
        );
    }
}