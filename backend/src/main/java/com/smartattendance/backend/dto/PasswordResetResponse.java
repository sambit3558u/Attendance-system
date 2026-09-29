package com.smartattendance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PasswordResetResponse {
    private boolean success;
    private String message;
    private String resetToken;
}
