package com.zos.home.dto;

public class LoginResponseDto {
    private Boolean success;
    private String username;
    private String role;
    private String token;
    private String errorMessage;

    public LoginResponseDto() {
    }
    public LoginResponseDto(Boolean success, String username, String role, String token, String errorMessage) {
        this.success = success;
        this.username = username;
        this.role = role;
        this.token = token;
        this.errorMessage = errorMessage;
    }

    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    @Override
    public String toString() {
        return "LoginResponseDto{" +
                "success=" + success +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", token='" + token + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
