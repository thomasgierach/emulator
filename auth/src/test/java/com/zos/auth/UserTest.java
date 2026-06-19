package com.zos.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import com.zos.auth.model.User;
import com.zos.auth.model.UserRoles;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
/* 
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
*/


public class UserTest {
    private User user;
    private final Argon2PasswordEncoder passwordEncoder =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    @Test
    public void testUser() {
        //Long userPk = 1L;
        String username = "testuser";
        String email = "test@gmail.com";
        UserRoles role = UserRoles.BASIC;
        String password = "password123456789";
       
        String springBouncyHash = passwordEncoder.encode(password);
        System.out.println("Spring Security Argon2 Hash: " + springBouncyHash);
        assertTrue(passwordEncoder.matches(password, springBouncyHash));
        
        user = new User(username, email, role, springBouncyHash);
        
        //assertEquals(userPk, user.getUserPk());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(role, user.getUserRole());
        assertEquals(springBouncyHash, user.getPasswordHash());

    }
}
