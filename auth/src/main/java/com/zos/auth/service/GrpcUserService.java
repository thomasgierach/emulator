package com.zos.auth.service;

import com.zos.auth.dto.CreateUserDto;
import com.zos.auth.dto.LoginDto;
import com.zos.auth.proto.CreateUserRequest;
import com.zos.auth.proto.CreateUserReply;
import com.zos.auth.proto.LoginRequest;
import com.zos.auth.proto.LoginReply;
import com.zos.auth.proto.UserServiceGrpc;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    private final AuthService authService;

    public GrpcUserService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginReply> responseObserver) {
        LoginDto dto = new LoginDto(request.getUsername(), request.getPassword());

        LoginReply reply = authService.login(dto);

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserReply> responseObserver) {
        CreateUserDto dto = new CreateUserDto(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        CreateUserReply reply = authService.createUser(dto);

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}