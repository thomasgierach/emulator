package com.zos.auth;

import com.zos.auth.dto.CreateUserDto;
import com.zos.auth.dto.CreateUserResponseDto;
import com.zos.auth.dto.LoginDto;
import com.zos.auth.dto.LoginResponseDto;
import com.zos.auth.proto.CreateUserReply;
import com.zos.auth.proto.LoginReply;
import com.zos.auth.repository.UsersRepository;
import com.zos.auth.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;

import org.testcontainers.postgresql.PostgreSQLContainer;
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
    static final PostgreSQLContainer POSTGRES =
            (new PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
                    .withDatabaseName("auth_test")
                    .withUsername("test")
                    .withPassword("test"));

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
        LoginDto request =
                new LoginDto(
                        "nonexistentuser",
                        "wrongpassword"
                );

        LoginReply response = authService.login(request);

        assertNotNull(response);

        System.out.println("get success: " + response.getSuccess());
        System.out.println("get token: " + response.getToken());

        String expectedToken = "";

        assertAll(
                () -> assertFalse(response.getSuccess()),
                () -> assertEquals(response.getToken(), expectedToken)
        );
    }

    @Test
    void createdUserCanLogin() {
        String username = "testuser_createdUserCanLogin";
        String password = "password123456789";
        String email = "test_createdUserCanLogin@gmail.com";

        CreateUserReply createResponse =
                authService.createUser(
                        new CreateUserDto(
                                username,
                                email,
                                password
                        )
                );

        assertNotNull(createResponse);
        assertTrue(
                createResponse.getSuccess(),
                createResponse.getErrorMessage()
        );

        LoginReply loginResponse =
                authService.login(
                        new LoginDto(username, password)
                );

        assertNotNull(loginResponse);

        assertAll(
                () -> assertTrue(loginResponse.getSuccess()),
                () -> assertNotNull(loginResponse.getToken())
        );
    }
    @Test
    void loginFailsWithEmptyUsername() {
        LoginDto request =
                new LoginDto(
                        "",
                        "password123456789"
                );

        LoginReply response = authService.login(request);

        assertNotNull(response);

        String expectedToken = "";

        assertAll(
                () -> assertFalse(response.getSuccess()),
                () -> assertEquals(response.getToken(), expectedToken)
        );
    }
    @Test
    void loginFailsWithEmptyPassword() {
        LoginDto request =
                new LoginDto(
                        "testuser_loginFailsWithEmptyPassword",
                        ""
                );

        LoginReply response = authService.login(request);

        assertNotNull(response);

        String expectedToken = "";

        assertAll(
                () -> assertFalse(response.getSuccess()),
                () -> assertEquals(response.getToken(), expectedToken)
        );
    }
    @Test
    void loginFailsWithIncorrectPassword() {
        String username = "testuser_loginFailsWithIncorrectPassword";
        String password = "password123456789";
        String email = "test@gmail.com";

        CreateUserReply createResponse =
                authService.createUser(
                        new CreateUserDto(
                                username,
                                email,
                                password
                        )
                );

        assertTrue(createResponse.getSuccess());

        LoginReply loginResponse =
                authService.login(
                        new LoginDto(
                                username,
                                "incorrect-password"
                        )
                );
        final String expectedToken = "";
        assertNotNull(loginResponse);

        assertAll(
                () -> assertFalse(loginResponse.getSuccess()),
                () -> assertEquals(loginResponse.getToken(),expectedToken)
        );
    }

    @Test
    void createUserFailsWithEmptyUsername() {
        CreateUserDto request =
                new CreateUserDto(
                        "",
                        "password123456789",
                        "test@gmail.com"
                );
        CreateUserReply response =
                authService.createUser(request);
        assertNotNull(response);
        assertFalse(response.getSuccess());
    }
    @Test
    void createUserFailsWithEmptyPassword() {
        CreateUserDto request =
                new CreateUserDto(
                        "testuser",
                        "",
                        "test@gmail.com"
                );
        CreateUserReply response =
                authService.createUser(request);
        assertNotNull(response);
        assertFalse(response.getSuccess());
    }
    @Test
    void createUserFailsWithEmptyEmail() {
        CreateUserDto request =
                new CreateUserDto(
                        "testuser",
                        "password123456789",
                        ""
                );
        CreateUserReply response =
                authService.createUser(request);
        assertNotNull(response);
        assertFalse(response.getSuccess());
    }
    @Test
    void duplicateUsernameIsRejected() {
        CreateUserDto request =
                new CreateUserDto(
                        "testuser",
                        "password123456789",
                        "test@gmail.com"
                );

        CreateUserReply firstResponse =
                authService.createUser(request);

        CreateUserReply duplicateResponse =
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