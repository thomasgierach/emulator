package com.zos.auth.dto;

public class CreateUserResponseDto {
    private Boolean success;
    private String username;
    private String errorMessage;

    public CreateUserResponseDto() {
    }

    public CreateUserResponseDto(Boolean success, String username, String errorMessage) {
        this.success = success;
        this.username = username;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
