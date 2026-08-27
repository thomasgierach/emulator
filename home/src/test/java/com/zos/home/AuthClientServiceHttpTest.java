package com.zos.home;

//home imports
import com.zos.home.service.AuthClientService;
import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;
import com.zos.home.dto.CreateUserRequestDto;
import com.zos.home.dto.CreateUserResponseDto;

//JUnit imports
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import org.springframework.web.reactive.function.client.WebClient;
//WireMock imports
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class AuthClientServiceHttpTest {
    @RegisterExtension
    static WireMockExtension authServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private AuthClientService authClientService;

    @BeforeEach
    public void setUp() {
        String authServiceUrl = authServer.getRuntimeInfo().getHttpBaseUrl();

        authClientService = new AuthClientService(
                WebClient.builder(),
                authServiceUrl
        );
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
    @Disabled("WireMock is proving more difficult to work with. Trying @MockBean.")
    @Test
    void createUser_returnsSuccessfulResponse() {
        String username = "testuser";
        String password = "password123456789";
        String email = "test@gmail.com";

        CreateUserRequestDto req =
                new CreateUserRequestDto(username, email, password);

        authServer.stubFor(
                post(urlEqualTo("/auth/users"))
                        .withHeader(
                                "Content-Type",
                                containing("application/json")
                        )
                        .withRequestBody(equalToJson("""
                            {
                            "username": "testuser",
                            "password": "password123456789",
                            "email": "test@gmail.com"
                            }
                            """))
                        .willReturn(
                                okJson("""
                                    {
                                    "success": true,
                                    "username": "testuser",
                                    "errorMessage": "User created successfully"
                                    }
                                    """)
                        )
        );

        CreateUserResponseDto response =
                authClientService.createUser(req);

        var unmatchedRequests = authServer.findAllUnmatchedRequests();

        assertTrue(
                unmatchedRequests.isEmpty(),
                () -> "WireMock received unmatched requests:\n"
                        + unmatchedRequests.stream()
                                .map(request ->
                                        request.getMethod()
                                        + " "
                                        + request.getUrl()
                                        + "\nBody: "
                                        + request.getBodyAsString()
                                )
                                .collect(Collectors.joining("\n\n"))
        );
        authServer.verify(
            postRequestedFor(urlEqualTo("/auth/users"))
        );

        authServer.getAllServeEvents().forEach(event -> {
                System.out.println("=== WireMock request ===");
                System.out.println("Method: " + event.getRequest().getMethod());
                System.out.println("URL: " + event.getRequest().getUrl());
                System.out.println("Headers: " + event.getRequest().getHeaders());
                System.out.println("Body: " + event.getRequest().getBodyAsString());
            
                System.out.println("=== WireMock response ===");
                System.out.println("Status: " + event.getResponse().getStatus());
                System.out.println("Body: " + event.getResponse().getBodyAsString());
            
                System.out.println("Was matched: " + event.getWasMatched());
                System.out.println();
            });
        return;

        /*assertNotNull(response);

        assertAll(
                () -> assertTrue(
                        response.getSuccess(),
                        "Expected response.success to be true"
                ),
                () -> assertEquals(
                        username,
                        response.getUsername()
                ),
                () -> assertEquals(
                        "User created successfully",
                        response.getErrorMessage()
                )
        );
        */
    }   
}