package com.smartattendance.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * ==========================================================
 * Subject Request DTO
 * ----------------------------------------------------------
 * Teacher/Admin subject add karte waqt
 * frontend se ye data backend ko bhejega.
 *
 * Example:
 *
 * {
 *   "subjectName": "AIML",
 *   "subjectCode": "MCA303",
 *   "branch": "Master of Computer Applications",
 *   "semester": 3
 * }
 * ==========================================================
 */

@Getter
@Setter
public class SubjectRequest {

    // =====================================================
    // Subject Name
    // Example:
    // Artificial Intelligence & Machine Learning
    // =====================================================

    @NotBlank(message = "Subject Name is required")
    private String subjectName;

    // =====================================================
    // Subject Code
    // Example:
    // MCA303
    // =====================================================

    @NotBlank(message = "Subject Code is required")
    private String subjectCode;

    // =====================================================
    // Branch
    // Example:
    // Master of Computer Applications
    // =====================================================

    @NotBlank(message = "Branch is required")
    private String branch;

    // =====================================================
    // Semester
    // Allowed range: 1 to 8
    // =====================================================

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 8, message = "Semester must not be greater than 8")
    private Integer semester;
}