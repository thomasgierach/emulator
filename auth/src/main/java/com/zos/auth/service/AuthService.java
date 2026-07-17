package com.zos.auth.service;

import com.zos.auth.dto.CreateUserDto;
import com.zos.auth.dto.LoginDto;
import com.zos.auth.model.User;
import com.zos.auth.model.UserRoles;
import com.zos.auth.proto.CreateUserReply;
import com.zos.auth.proto.LoginReply;
import com.zos.auth.repository.UsersRepository;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final Argon2PasswordEncoder passwordEncoder =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public AuthService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public LoginReply login(LoginDto dto) {
        Optional<User> userOptional = usersRepository.findByUsername(dto.getUsername());

        if (userOptional.isEmpty()) {
            return failedLogin(dto.getUsername());
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            return failedLogin(dto.getUsername());
        }

        return LoginReply.newBuilder()
                .setSuccess(true)
                .setUsername(user.getUsername())
                .setRole(user.getUserRole().name())
                .setToken("generated-token")
                .setErrorMessage("Login successful")
                .build();
    }

    public CreateUserReply createUser(CreateUserDto dto) {
        Optional<User> existingUser = usersRepository.findByUsername(dto.getUsername());

        if (existingUser.isPresent()) {
            return CreateUserReply.newBuilder()
                    .setSuccess(false)
                    .setUsername(dto.getUsername())
                    .setErrorMessage("Username already exists")
                    .build();
        }

        User newUser = new User(
                dto.getUsername(),
                dto.getEmail(),
                UserRoles.BASIC,
                passwordEncoder.encode(dto.getPassword())
        );
        try {
            usersRepository.save(newUser);
        } catch (Exception e) {
            return CreateUserReply.newBuilder()
                    .setSuccess(false)
                    .setUsername(dto.getUsername())
                    .setErrorMessage("Error creating user: " + e.getMessage())
                    .build();
        }

        return CreateUserReply.newBuilder()
                .setSuccess(true)
                .setUsername(newUser.getUsername())
                .setErrorMessage("User created successfully")
                .build();
    }

    private LoginReply failedLogin(String username) {
        return LoginReply.newBuilder()
                .setSuccess(false)
                .setUsername(username)
                .setRole("")
                .setToken("")
                .setErrorMessage("Invalid username or password")
                .build();
    }

    public boolean validateSession(String token) {
        // In a real implementation, you would check the token against a database or cache
        // For this example, we'll just check if it's a non-empty string
        return token != null && !token.isEmpty();
    }
}