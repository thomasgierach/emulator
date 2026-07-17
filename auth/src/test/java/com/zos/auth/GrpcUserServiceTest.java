package com.zos.auth;

import com.zos.auth.dto.CreateUserDto;
import com.zos.auth.dto.LoginDto;
import com.zos.auth.model.User;
import com.zos.auth.model.UserRoles;
import com.zos.auth.service.AuthService;
import com.zos.auth.proto.CreateUserRequest;
import com.zos.auth.proto.CreateUserReply;
import com.zos.auth.proto.LoginRequest;
import com.zos.auth.proto.LoginReply;
//import com.zos.auth.proto.ValidateSessionRequest;
//import com.zos.auth.proto.ValidateSessionReply;
import com.zos.auth.repository.UsersRepository;
import com.zos.auth.service.GrpcUserService;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcUserServiceTest {

    private GrpcUserService grpcUserService;
    private AuthService authService;
    private UsersRepository usersRepository;
    private final String login_successful = "Login successful";
    private final String login_failed = "Invalid username or password";

    @BeforeEach
    void setUp() {
        usersRepository = mock(UsersRepository.class);
        authService = mock(AuthService.class);
        grpcUserService = new GrpcUserService(authService);
    }

    @Test
    void testSuccessfulLogin() {
        String username = "testuser";
        String rawPassword = "password123";

        LoginRequest request = LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(rawPassword)
                .build();

        LoginReply expectedReply = LoginReply.newBuilder()
                .setUsername(username)
                .setRole(UserRoles.BASIC.name())
                .setToken("generated-token")
                .setSuccess(true)
                .setErrorMessage(login_successful)
                .build();

        doReturn(expectedReply)
                .when(authService)
                .login(any(LoginDto.class));

        @SuppressWarnings("unchecked")
        StreamObserver<LoginReply> observer = mock(StreamObserver.class);

        grpcUserService.login(request, observer);

        ArgumentCaptor<LoginDto> dtoCaptor =
                ArgumentCaptor.forClass(LoginDto.class);

        verify(authService).login(dtoCaptor.capture());

        LoginDto capturedDto = dtoCaptor.getValue();

        assertAll(
                () -> assertEquals(username, capturedDto.getUsername()),
                () -> assertEquals(rawPassword, capturedDto.getPassword())
        );

        verify(observer).onNext(expectedReply);
        verify(observer).onCompleted();
        verify(observer, never()).onError(any());
    }

    @Test
    void testFailedLogin() {
        String username = "testuser";
        String rawPassword = "password123";

        LoginRequest request = LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(rawPassword)
                .build();

        LoginReply expectedReply = LoginReply.newBuilder()
                .setUsername(username)
                .setRole("")
                .setToken("")
                .setSuccess(false)
                .setErrorMessage(login_failed)
                .build();

        doReturn(expectedReply)
                .when(authService)
                .login(any(LoginDto.class));

        @SuppressWarnings("unchecked")
        StreamObserver<LoginReply> observer = mock(StreamObserver.class);

        grpcUserService.login(request, observer);

        ArgumentCaptor<LoginDto> dtoCaptor =
                ArgumentCaptor.forClass(LoginDto.class);

        verify(authService).login(dtoCaptor.capture());

        LoginDto capturedDto = dtoCaptor.getValue();

        assertAll(
                () -> assertEquals(username, capturedDto.getUsername()),
                () -> assertEquals(rawPassword, capturedDto.getPassword())
        );

        verify(observer).onNext(expectedReply);
        verify(observer).onCompleted();
        verify(observer, never()).onError(any());
    }

    @Test
    void createUser_whenServiceSucceeds_returnsSuccessfulReply() {
        String username = "testuser";
        String password = "password123";
        String email = "test@gmail.com";
    
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setEmail(email)
                .build();
    
        CreateUserReply expectedReply = CreateUserReply.newBuilder()
                .setSuccess(true)
                .setUsername(username)
                .setErrorMessage("User created successfully")
                .build();
    
        doReturn(expectedReply)
                .when(authService)
                .createUser(any(CreateUserDto.class));
    
        @SuppressWarnings("unchecked")
        StreamObserver<CreateUserReply> observer =
                mock(StreamObserver.class);
    
        grpcUserService.createUser(request, observer);
    
        ArgumentCaptor<CreateUserDto> captor =
                ArgumentCaptor.forClass(CreateUserDto.class);
    
        verify(authService).createUser(captor.capture());
    
        CreateUserDto actualDto = captor.getValue();
    
        assertAll(
                () -> assertEquals(username, actualDto.getUsername()),
                () -> assertEquals(email, actualDto.getEmail()),
                () -> assertEquals(password, actualDto.getPassword())
        );
    
        verify(observer).onNext(expectedReply);
        verify(observer).onCompleted();
        verify(observer, never()).onError(any());
    }
}