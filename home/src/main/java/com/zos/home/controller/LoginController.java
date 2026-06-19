package com.zos.home.controller;

import com.zos.home.dto.LoginRequestDto;
import com.zos.home.dto.LoginResponseDto;
import com.zos.auth.proto.LoginReply;

import com.zos.home.service.AuthClientService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final AuthClientService authClientService;

    public LoginController(AuthClientService authClientService) {
        this.authClientService = authClientService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto response = authClientService.login(request);

        if (!response.getSuccess()) {
            return ResponseEntity.status(401).body(response);
        }

        return ResponseEntity.ok(response);
    }
}