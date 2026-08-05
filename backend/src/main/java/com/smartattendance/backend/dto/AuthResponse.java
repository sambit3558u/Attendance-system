package com.smartattendance.backend.dto;

import com.smartattendance.backend.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Authentication Response DTO
 * ----------------------------------------------------------
 * This DTO sends authentication response back
 * to the frontend after successful Login
 * or Registration.
 *
 * It contains:
 * - Response Status
 * - Response Message
 * - JWT Access Token
 * - Token Expiry Time
 * - Logged-in User Information
 * ==========================================================
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    // ======================================================
    // Indicates whether request is successful.
    // true  = Success
    // false = Failed
    // ======================================================
    private boolean success;

    // ======================================================
    // Response Message
    // Examples:
    // Login Successful
    // Registration Successful
    // Invalid Credentials
    // ======================================================
    private String message;

    // ======================================================
    // JWT Access Token
    // Sent after successful login.
    // Used for Authentication.
    // ======================================================
    private String accessToken;

    // ======================================================
    // Token Expiry Time (Milliseconds)
    // Current Project:
    // 30 Days
    // ======================================================
    private Long expiresIn;

    // ======================================================
    // Logged-in User Information
    // ======================================================

    // User ID
    private Long userId;

    // Full Name
    private String name;

    // Email Address
    private String email;

    // User Role
    // TEACHER or STUDENT
    private Role role;

}