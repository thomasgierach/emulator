package com.zos.home;

import com.zos.home.controller.CreateUserController;
import com.zos.home.service.AuthClientService;
import com.zos.home.dto.CreateUserRequestDto;
import com.zos.home.dto.CreateUserResponseDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CreateUserControllerTest {
    private CreateUserController createUserController;
    private AuthClientService authClientService;

    @BeforeEach 
    public void setup() {
        authClientService = Mockito.mock(AuthClientService.class);
        createUserController = new CreateUserController(authClientService);
    }

    @Test 
    public void testSuccessfulLogin() {
        String username = "testuser";
        String password = "password123456789";
        String email = "test@gmail.com";

        CreateUserRequestDto request = new CreateUserRequestDto(username, password, email);
        CreateUserResponseDto response = new CreateUserResponseDto(true, username, "Create User successful");

        when(authClientService.createUser(request))
            .thenReturn(response);

        ResponseEntity<?> result = createUserController.createUser(request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
/* 
        System.out.println("status = " + result.getStatusCode());
        System.out.println("success = " + body.getSuccess());
        System.out.println("token = " + body.getToken());
        System.out.println("error = " + body.getErrorMessage());
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertNotNull(result.getBody().getToken());
        */
    }
}
