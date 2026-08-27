package com.zos.home.dto;

public class CreateUserRequestDto {
    private String username;
    private String password;
    private String email;
    
    public CreateUserRequestDto() {
    }
    public CreateUserRequestDto(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
