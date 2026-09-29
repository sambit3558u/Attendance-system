package com.smartattendance.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * ==========================================================
 * Student Profile Update Request DTO
 * ----------------------------------------------------------
 * Student apni profile update karte waqt frontend se
 * editable fields backend ko bhejega.
 *
 * Editable:
 * - Name
 * - Phone
 * - Roll Number
 * - Semester
 *
 * NOT Editable:
 * - Email
 * - Branch
 * - Role
 * - User ID
 *
 * Semester allowed values:
 * 1, 2, 3, 4, 5, 6, 7, 8
 *
 * Branch intentionally is DTO me nahi hai.
 * Isliye student profile update API se branch
 * change nahi kar sakta.
 * ==========================================================
 */

@Getter
@Setter
public class StudentProfileUpdateRequest {

    // =====================================================
    // Name
    // =====================================================

    @NotBlank(message = "Name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Name must be between 2 and 100 characters"
    )
    private String name;

    // =====================================================
    // Phone Number
    // =====================================================

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Enter a valid 10 digit mobile number"
    )
    private String phone;

    // =====================================================
    // Roll Number
    // =====================================================

    @NotBlank(message = "Roll number is required")
    @Size(
            max = 50,
            message = "Roll number must not exceed 50 characters"
    )
    private String rollNumber;

    // =====================================================
    // Semester
    // -----------------------------------------------------
    // Allowed:
    // 1, 2, 3, 4, 5, 6, 7, 8
    //
    // Frontend me dropdown/select use hoga.
    //
    // Semester update hone ke baad Student Timetable
    // automatically current semester ke according
    // timetable load karega.
    // =====================================================

    @NotNull(message = "Semester is required")
    @Min(
            value = 1,
            message = "Semester must be between 1 and 8"
    )
    @Max(
            value = 8,
            message = "Semester must be between 1 and 8"
    )
    private Integer semester;
}