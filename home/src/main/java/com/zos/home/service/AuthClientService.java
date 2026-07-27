package com.zos.home.service;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.zos.auth.proto.CreateUserReply;
import com.zos.auth.proto.LoginReply;
import com.zos.home.dto.CreateUserRequestDto;
import com.zos.home.dto.CreateUserResponseDto;
import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;

import reactor.core.publisher.Mono;

@Service
public class AuthClientService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthClientService.class);

    private static final MediaType PROTOBUF =
            MediaType.valueOf("application/x-protobuf");

    private static final Duration REQUEST_TIMEOUT =
            Duration.ofSeconds(10);

    private final WebClient webClient;
    private final String authServiceBaseUrl;

    public AuthClientService(
            WebClient.Builder builder,
            @Value("${auth.service.base-url}")
            String authServiceBaseUrl
    ) {
        this.authServiceBaseUrl = authServiceBaseUrl;

        logger.info(
                "Configuring AuthClientService with base URL: {}",
                authServiceBaseUrl
        );

        this.webClient = builder
                .baseUrl(authServiceBaseUrl)
                .build();
    }

    public LoginResponseDto login(LoginRequestDto request) {
        String path = "/auth/login";

        logger.info(
                "Beginning login request: method=POST, url={}{}," +
                " username={}",
                authServiceBaseUrl,
                path,
                request.getUsername()
        );

        /*
         * Never log the password.
         */
        LoginReply reply = webClient
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROTOBUF)
                .bodyValue(request)
                .exchangeToMono(response -> {
                    logger.info(
                            "Login HTTP response received: status={}," +
                            " contentType={}, contentLength={}",
                            response.statusCode(),
                            response.headers()
                                    .contentType()
                                    .map(MediaType::toString)
                                    .orElse("<missing>"),
                            response.headers()
                                    .contentLength()
                                    .orElse(-1L)
                    );

                    logger.debug(
                            "Login response headers: {}",
                            response.headers().asHttpHeaders()
                    );

                    if (response.statusCode().isError()) {
                        return response
                                .bodyToMono(String.class)
                                .defaultIfEmpty("<empty response body>")
                                .flatMap(errorBody -> {
                                    logger.error(
                                            "Auth login failed: status={}," +
                                            " responseBody={}",
                                            response.statusCode(),
                                            errorBody
                                    );

                                    return Mono.error(
                                            new IllegalStateException(
                                                    "Auth login returned " +
                                                    response.statusCode()
                                            )
                                    );
                                });
                    }

                    return response
                            .bodyToMono(LoginReply.class)
                            .switchIfEmpty(
                                    Mono.error(
                                            new IllegalStateException(
                                                    "Auth login returned an " +
                                                    "empty response body"
                                            )
                                    )
                            );
                })
                .doOnSubscribe(subscription ->
                        logger.info(
                                "Login request subscribed; HTTP exchange is starting"
                        )
                )
                .doOnNext(response ->
                        logger.info(
                                "LoginReply decoded successfully:" +
                                " success={}, username={}, role={}," +
                                " tokenPresent={}, errorMessage={}",
                                response.getSuccess(),
                                response.getUsername(),
                                response.getRole(),
                                !response.getToken().isBlank(),
                                response.getErrorMessage()
                        )
                )
                .doOnError(error ->
                        logger.error(
                                "Login request failed: errorType={}," +
                                " message={}",
                                error.getClass().getName(),
                                error.getMessage(),
                                error
                        )
                )
                .timeout(REQUEST_TIMEOUT)
                .block();

        if (reply == null) {
            throw new IllegalStateException(
                    "Login request completed without a LoginReply"
            );
        }

        logger.info(
                "Login request completed for username={}",
                request.getUsername()
        );

        return new LoginResponseDto(
                reply.getSuccess(),
                reply.getUsername(),
                reply.getRole(),
                reply.getToken(),
                reply.getErrorMessage()
        );
    }

    public CreateUserResponseDto createUser(
            CreateUserRequestDto request
    ) {
        String path = "/auth/users";

        logger.info(
                "Beginning create-user request: method=POST," +
                " url={}{} username={}, email={}",
                authServiceBaseUrl,
                path,
                request.getUsername(),
                request.getEmail()
        );

        /*
         * Never log the password.
         */
        CreateUserReply reply = webClient
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROTOBUF)
                .bodyValue(request)
                .exchangeToMono(response -> {
                    logger.info(
                            "Create-user HTTP response received:" +
                            " status={}, contentType={}, contentLength={}",
                            response.statusCode(),
                            response.headers()
                                    .contentType()
                                    .map(MediaType::toString)
                                    .orElse("<missing>"),
                            response.headers()
                                    .contentLength()
                                    .orElse(-1L)
                    );

                    logger.debug(
                            "Create-user response headers: {}",
                            response.headers().asHttpHeaders()
                    );

                    if (response.statusCode().isError()) {
                        return response
                                .bodyToMono(String.class)
                                .defaultIfEmpty("<empty response body>")
                                .flatMap(errorBody -> {
                                    logger.error(
                                            "Create-user request failed:" +
                                            " status={}, responseBody={}",
                                            response.statusCode(),
                                            errorBody
                                    );

                                    return Mono.error(
                                            new IllegalStateException(
                                                    "Create-user request returned " +
                                                    response.statusCode()
                                            )
                                    );
                                });
                    }

                    return response
                            .bodyToMono(CreateUserReply.class)
                            .switchIfEmpty(
                                    Mono.error(
                                            new IllegalStateException(
                                                    "Create-user request returned " +
                                                    "an empty response body"
                                            )
                                    )
                            );
                })
                .doOnSubscribe(subscription ->
                        logger.info(
                                "Create-user request subscribed;" +
                                " HTTP exchange is starting"
                        )
                )
                .doOnNext(response ->
                        logger.info(
                                "CreateUserReply decoded successfully:" +
                                " success={}, username={}," +
                                " errorMessage={}",
                                response.getSuccess(),
                                response.getUsername(),
                                response.getErrorMessage()
                        )
                )
                .doOnError(error ->
                        logger.error(
                                "Create-user request failed:" +
                                " errorType={}, message={}",
                                error.getClass().getName(),
                                error.getMessage(),
                                error
                        )
                )
                .timeout(REQUEST_TIMEOUT)
                .block();

        if (reply == null) {
            throw new IllegalStateException(
                    "Create-user request completed without a CreateUserReply"
            );
        }

        logger.info(
                "Create-user request completed for username={}",
                request.getUsername()
        );

        return new CreateUserResponseDto(
                reply.getSuccess(),
                reply.getUsername(),
                reply.getErrorMessage()
        );
    }
}
