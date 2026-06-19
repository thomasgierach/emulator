package com.zos.home.service;

import com.zos.auth.proto.LoginReply;
import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthClientService {

    private final WebClient webClient;

    private static final MediaType PROTOBUF =
            MediaType.valueOf("application/x-protobuf");

    public AuthClientService(
            WebClient.Builder builder,
            @Value("${auth.service.base-url}") String authServiceBaseUrl
    ) {
        this.webClient = builder
                .baseUrl(authServiceBaseUrl)
                .build();
    }

    public LoginResponseDto login(LoginRequestDto request) {
        LoginReply reply = webClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROTOBUF)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LoginReply.class)
                .block();

        System.out.println("AUTH REPLY:");
        System.out.println(reply);

        return new LoginResponseDto(
                reply.getSuccess(),
                reply.getUsername(),
                reply.getRole(),
                reply.getToken(),
                reply.getErrorMessage()
        );
    }
}
