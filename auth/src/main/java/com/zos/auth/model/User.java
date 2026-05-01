package com.zos.auth.model;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.Size;
import com.zos.auth.model.UserRoles;
import java.util.HashMap;
import jakarta.persistence.GenerationType;
//import org.hibernate.annotations.TypeDef;
//import org.hibernate.annotations.Type;
//import com.zos.auth.util.PostgreSQLEnumType;



@Entity
@Table(name = "users")
/*
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType.class
)
*/
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_pk")
    private Long userPk;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRoles userRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(min = 15)
    private String passwordHash;

    public User() {
    }

    public User(String username, String email, UserRoles userRole, String passwordHash) {
        this.username = username;
        this.email = email;
        this.userRole = userRole;
        this.passwordHash = passwordHash;
    }

    public Long getUserPk() {
        return userPk;
    }

    public void setUserPk(Long userPk) {
        this.userPk = userPk;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public UserRoles getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRoles userRole) {
        this.userRole = userRole;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public HashMap toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userPk", userPk);
        map.put( "email", email);
        map.put("username", username);
        map.put("role", userRole);
        return map;
    }
}
