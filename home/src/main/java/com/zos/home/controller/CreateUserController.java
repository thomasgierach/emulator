package com.zos.home.controller;

import com.zos.home.dto.CreateUserRequestDto;
import com.zos.home.dto.CreateUserResponseDto;
import com.zos.auth.proto.CreateUserReply;

import com.zos.home.service.AuthClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CreateUserController {

    private static final Logger logger =
            LoggerFactory.getLogger(CreateUserController.class);

    private final AuthClientService authClientService;

    public CreateUserController(AuthClientService authClientService) {
        this.authClientService = authClientService;
    }

    @PostMapping("/create-user")
    public ResponseEntity<CreateUserResponseDto> createUser(@RequestBody CreateUserRequestDto request) {
        logger.info("Received create user request: {}", request);
        CreateUserResponseDto response = authClientService.createUser(request);

        if (!response.getSuccess()) {
            return ResponseEntity.status(401).body(response);
        }

        return ResponseEntity.ok(response);
    }
}