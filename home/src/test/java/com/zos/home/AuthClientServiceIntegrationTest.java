package com.zos.home;

import com.zos.home.service.AuthClientService;
import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import org.springframework.web.reactive.function.client.WebClient;


public class AuthClientServiceIntegrationTest {
    private AuthClientService authClientService;
    private WebClient.Builder builder;

    @BeforeEach
    public void setUp() {
        builder = WebClient.builder();
        authClientService = new AuthClientService(builder, "http://localhost:8081");
    }
    @Disabled("Requires actual auth service running at http://localhost:8081")
    @Test
    public void testLogin() {
        // Given
        String username = "testuser";
        String password = "password123456789";
        LoginRequestDto request = new LoginRequestDto(username, password);

        // When
        LoginResponseDto response = authClientService.login(request);

        // Then
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getToken());
    }
}
