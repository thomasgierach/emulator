package com.zos.auth;

import com.zos.auth.model.User;
import com.zos.auth.model.UserRoles;
import com.zos.auth.repository.UsersRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
public class UsersRepositoryTest {
    //PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:9.6.12")
     //       .withDatabaseName("testdb");
            
        //jdbc:tc:postgresql:9.6.8:///databasename
    @Autowired
    private UsersRepository usersRepository;

    private final Argon2PasswordEncoder passwordEncoder =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @Test
    void saveFindAndDeleteUser() {
        String username = "testuser";
        String emailString = "test@gmail.com";
        UserRoles role = UserRoles.BASIC;
        String rawPassword = "password123456789";
        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setEmail(emailString);
        user.setUserRole(role);
        user.setPasswordHash(passwordHash);

        usersRepository.save(user);

        Optional<User> userOptional = usersRepository.findByUsername(username);

        assertTrue(userOptional.isPresent(), "User should be found in the repository");

        User retrievedUser = userOptional.get();

        assertEquals(username, retrievedUser.getUsername());
        assertEquals(role, retrievedUser.getUserRole());
        assertEquals(emailString, retrievedUser.getEmail());
        assertTrue(passwordEncoder.matches(rawPassword, retrievedUser.getPasswordHash()));
    }
}
