package com.smartattendance.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.AuthResponse;
import com.smartattendance.backend.dto.LoginRequest;
import com.smartattendance.backend.dto.RegisterRequest;
import com.smartattendance.backend.dto.ForgotPasswordRequest;
import com.smartattendance.backend.dto.ResetPasswordRequest;
import com.smartattendance.backend.dto.PasswordResetResponse;
import com.smartattendance.backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Authentication Controller
 * ----------------------------------------------------------
 * This controller receives authentication requests
 * from the frontend.
 *
 * Available APIs:
 *
 * POST /api/auth/register
 * POST /api/auth/login
 * POST /api/auth/logout
 * ==========================================================
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Handles registration and login business logic.
     */
    private final AuthService authService;

    /**
     * ======================================================
     * Register API
     * ------------------------------------------------------
     * URL:
     * POST /api/auth/register
     *
     * Receives Teacher or Student registration data.
     * ======================================================
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * ======================================================
     * Login API
     * ------------------------------------------------------
     * URL:
     * POST /api/auth/login
     *
     * Receives email, password and role.
     * Returns a JWT token after successful login.
     * ======================================================
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResetResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.requestPasswordReset(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    /**
     * ======================================================
     * Logout API
     * ------------------------------------------------------
     * URL:
     * POST /api/auth/logout
     *
     * Temporarily receives email as a request parameter.
     *
     * Example:
     * /api/auth/logout?email=user@gmail.com
     *
     * Later, after JWT filter is added, email will be read
     * directly from the authenticated token.
     * ======================================================
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(
            @RequestParam String email
    ) {

        AuthResponse response = authService.logout(email);

        return ResponseEntity.ok(response);
    }
}
