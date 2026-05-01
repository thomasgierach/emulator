package com.zos.auth.controller;

import com.zos.auth.dto.CreateUserDto;
import com.zos.auth.dto.LoginDto;
import com.zos.auth.proto.CreateUserReply;
import com.zos.auth.proto.LoginReply;
import com.zos.auth.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/users")
    public ResponseEntity<CreateUserReply> createUser(@RequestBody CreateUserDto dto) {
        CreateUserReply reply = authService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginReply> login(@RequestBody LoginDto dto) {
        LoginReply reply = authService.login(dto);
        return ResponseEntity.ok(reply);
    }

    @PostMapping("/sessions/validate")
    public ResponseEntity<?> validateSession(@RequestBody String token) {
        boolean valid = authService.validateSession(token);

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired session");
        }

        return ResponseEntity.ok("Session is valid");
    }
}
