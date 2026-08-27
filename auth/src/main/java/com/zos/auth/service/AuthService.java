package com.zos.auth.service;

import com.zos.auth.dto.CreateUserDto;
import com.zos.auth.dto.LoginDto;
import com.zos.auth.model.User;
import com.zos.auth.model.UserRoles;
import com.zos.auth.proto.CreateUserReply;
import com.zos.auth.proto.LoginReply;
import com.zos.auth.repository.UsersRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final UsersRepository usersRepository;
    private final Argon2PasswordEncoder passwordEncoder =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public AuthService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public LoginReply login(LoginDto dto) {
        if (dto.getUsername() == null || dto.getUsername().isEmpty() ||
            dto.getPassword() == null || dto.getPassword().isEmpty()) {
            logger.warn("Login attempt with empty username or password.");
            return failedLogin(dto.getUsername());
        }
        Optional<User> userOptional = usersRepository.findByUsername(dto.getUsername());

        if (userOptional.isEmpty()) {
            logger.warn("Login attempt failed for non-existent user: {}", dto.getUsername());
            return failedLogin(dto.getUsername());
        }

        User user = userOptional.get();

        String rawPassword = dto.getPassword();
        String storedHash = user.getPasswordHash();

        /* 
         * Log the raw password and stored hash for debugging purposes.
         * Note: Logging raw passwords is a security risk and should be avoided in production.
         */
        //logger.info("login(): Raw password: " + rawPassword);
        logger.info("login(): Raw password length: " + rawPassword.length());
        logger.info("login(): Stored hash: " + storedHash);
        logger.info("login(): Stored hash length: " + storedHash.length());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            logger.warn("Login attempt failed for user: {}. Incorrect password.", dto.getUsername());
            return failedLogin(dto.getUsername());
        }
        logger.info("User {} logged in successfully.", dto.getUsername());
        return LoginReply.newBuilder()
                .setSuccess(true)
                .setUsername(user.getUsername())
                .setRole(user.getUserRole().name())
                .setToken("generated-token")
                .setErrorMessage("Login successful")
                .build();
    }

    public CreateUserReply createUser(CreateUserDto dto) {
        if (dto.getUsername() == null || dto.getUsername().isEmpty() ||
            dto.getPassword() == null || dto.getPassword().isEmpty() ||
            dto.getEmail() == null || dto.getEmail().isEmpty()) {
            logger.warn("Attempt to create user with empty username, password, or email.");
            return CreateUserReply.newBuilder()
                    .setSuccess(false)
                    .setUsername(dto.getUsername())
                    .setErrorMessage("Username, password, and email must not be empty")
                    .build();
        }
        if (dto.getEmail().contains("@") == false) {
            logger.warn("Attempt to create user with invalid email: {}", dto.getEmail());
            return CreateUserReply.newBuilder()
                    .setSuccess(false)
                    .setUsername(dto.getUsername())
                    .setErrorMessage("Invalid email address")
                    .build();
        }
        Optional<User> existingUser = usersRepository.findByUsername(dto.getUsername());

        if (existingUser.isPresent()) {
            logger.warn("Attempt to create user with existing username: {}", dto.getUsername());
            return CreateUserReply.newBuilder()
                    .setSuccess(false)
                    .setUsername(dto.getUsername())
                    .setErrorMessage("Username already exists")
                    .build();
        }

        String storedHash = passwordEncoder.encode(dto.getPassword());
        
        //logger.info("createUser(): Raw password: " + rawPassword);
        logger.info("createUser(): Raw password length: " + dto.getPassword().length());
        logger.info("createUser(): Stored hash: " + storedHash);
        logger.info("createUser(): Stored hash length: " + storedHash.length());

        logger.info(
            "Hash matches immediately after encoding: {}",
            passwordEncoder.matches(dto.getPassword(), storedHash)
        );
        User newUser = new User(
                dto.getUsername(),
                dto.getEmail(),
                UserRoles.BASIC,
                storedHash
        );
        try {
            usersRepository.save(newUser);
            logger.info("User {} created successfully.", dto.getUsername());
            return CreateUserReply.newBuilder()
                .setSuccess(true)
                .setUsername(newUser.getUsername())
                .setErrorMessage("User created successfully")
                .build();
        } catch (Exception e) {
            logger.error("Error creating user {}: {}", dto.getUsername(), e.getMessage());
            return CreateUserReply.newBuilder()
                    .setSuccess(false)
                    .setUsername(dto.getUsername())
                    .setErrorMessage("Error creating user.")
                    .build();
        }

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