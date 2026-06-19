package com.zos.auth.controller;

import com.zos.auth.dto.CreateUserDto;
import com.zos.auth.dto.LoginDto;
import com.zos.auth.proto.CreateUserReply;
import com.zos.auth.proto.LoginReply;
//import com.zos.auth.proto.LoginRequest;
import com.zos.auth.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private static final MediaType PROTOBUF =
        MediaType.valueOf("application/x-protobuf");

    @PostMapping("/users")
    public ResponseEntity<CreateUserReply> createUser(@RequestBody CreateUserDto dto) {
        CreateUserReply reply = authService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    @PostMapping(
        value = "/login",
        produces = "application/x-protobuf"
    )
    public LoginReply login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }
    /* 
    @PostMapping(
        value = "/auth/login",
        produces = "application/x-protobuf",
        consumes = "application/x-protobuf"
    )
    @PostMapping("/login")
    public LoginReply login(@RequestBody LoginDto dto) {
        LoginReply reply = authService.login(dto);

        /*
        LoginResponseDto response = new LoginResponseDto(
            reply.getSuccess(),
            reply.getUsername(),
            reply.getRole(),
            reply.getToken(),
            reply.getErrorMessage()
        );

        if (!response.getSuccess()) {
            return ResponseEntity.status(401).body(response);
        }
        
        return reply;
    }
    */
    @PostMapping("/sessions/validate")
    public ResponseEntity<?> validateSession(@RequestBody String token) {
        boolean valid = authService.validateSession(token);

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired session");
        }

        return ResponseEntity.ok("Session is valid");
    }
}
