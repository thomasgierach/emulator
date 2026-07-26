package com.zos.home.service;

import com.zos.auth.proto.LoginReply;
import com.zos.auth.proto.CreateUserReply;
import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;
import com.zos.home.dto.CreateUserRequestDto;
import com.zos.home.dto.CreateUserResponseDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthClientService {

    private final WebClient webClient;

    private static final MediaType PROTOBUF =
            MediaType.valueOf("application/x-protobuf");

    private static final Logger logger = LoggerFactory.getLogger(AuthClientService.class);

    public AuthClientService(
            WebClient.Builder builder,
            @Value("${auth.service.base-url}") String authServiceBaseUrl
    ) {
        this.webClient = builder
                .baseUrl(authServiceBaseUrl)
                .build();
    }

    public LoginResponseDto login(LoginRequestDto request) {
        logger.info(webClient.get().uri("/auth/login").toString());
        LoginReply reply = webClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROTOBUF)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LoginReply.class)
                .block();

        logger.info("AUTH REPLY: success={}, username={}, role={}, token={}, errorMessage={}",
                reply.getSuccess(),
                reply.getUsername(),
                reply.getRole(),
                reply.getToken(),
                reply.getErrorMessage()
        );

        return new LoginResponseDto(
                reply.getSuccess(),
                reply.getUsername(),
                reply.getRole(),
                reply.getToken(),
                reply.getErrorMessage()
        );
    }
    public CreateUserResponseDto createUser(CreateUserRequestDto request) {
        CreateUserReply reply = webClient.post()
                .uri("/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROTOBUF)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CreateUserReply.class)
                .block();

        logger.info("AUTH REPLY: {}", reply.getSuccess());


        return new CreateUserResponseDto(
                reply.getSuccess(),
                reply.getUsername(),
                reply.getErrorMessage()
        );
    }
}
