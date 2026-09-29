package com.smartattendance.backend.dto;

import com.smartattendance.backend.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Student Profile Response DTO
 * ----------------------------------------------------------
 * Student Profile page ko student ki complete information
 * return karne ke liye use hoga.
 *
 * Editable fields later:
 * - Name
 * - Phone
 * - Roll Number
 * - Semester
 *
 * Read-only fields:
 * - Email
 * - Branch
 * - Role
 * ==========================================================
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileResponse {

    // Student database ID
    private Long userId;

    // Student Full Name
    private String name;

    // Email read-only rahega
    private String email;

    // Phone Number
    private String phone;

    // Student Roll Number
    private String rollNumber;

    // Branch visible but editable nahi hoga
    private String branch;

    // Student current semester
    private Integer semester;

    // STUDENT
    private Role role;
}