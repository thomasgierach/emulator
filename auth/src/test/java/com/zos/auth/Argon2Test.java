package com.zos.auth;

//import com.zos.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class Argon2Test {
    @Test
    public void givenRawPassword_whenEncodedWithArgon2_thenMatchesEncodedPassword() {
    String rawPassword = "mySecurePassword";
    Argon2PasswordEncoder arg2SpringSecurity = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);
    String springBouncyHash = arg2SpringSecurity.encode(rawPassword);
    System.out.println("Raw Password: " + rawPassword);
    System.out.println("Spring Security Argon2 Hash: " + springBouncyHash);
    assertTrue(arg2SpringSecurity.matches(rawPassword, springBouncyHash));
}
}
