package com.smartattendance.backend.dto;

import com.smartattendance.backend.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * =====================================================
 * Register Request DTO
 * -----------------------------------------------------
 * Receives registration data from frontend.
 * =====================================================
 */

@Getter
@Setter
public class RegisterRequest {

    // =====================================================
    // Common Details (Teacher & Student)
    // =====================================================

    @NotBlank(message = "Full Name is required")
    private String name;

    @Email(message = "Enter a valid Email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone Number is required")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotNull(message = "Role is required")
    private Role role;

    // =====================================================
// Teacher Details
// =====================================================

private String department;

// =====================================================
// Student Details
// =====================================================

private String rollNumber;

private String branch;

private Integer semester;

}