package com.smartattendance.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;

import com.smartattendance.backend.dto.StudentProfileResponse;
import com.smartattendance.backend.dto.StudentProfileUpdateRequest;
import com.smartattendance.backend.service.StudentProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Student Profile Controller
 * ----------------------------------------------------------
 * Student profile related HTTP requests handle karta hai.
 *
 * APIs:
 *
 * 1. GET
 *    /api/student/profile?email=student@gmail.com
 *
 *    Student ki current profile return karega.
 *
 * 2. PUT
 *    /api/student/profile?email=student@gmail.com
 *
 *    Student ke editable fields update karega.
 *
 * Editable:
 * - Name
 * - Phone
 * - Roll Number
 * - Semester
 *
 * Read-only:
 * - Email
 * - Branch
 * - Role
 * ==========================================================
 */

@RestController
@RequestMapping("/api/student/profile")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    /**
     * ======================================================
     * Get Student Profile
     * ------------------------------------------------------
     *
     * GET:
     *
     * /api/student/profile
     * ?email=student@gmail.com
     * ======================================================
     */
    @GetMapping
    public ResponseEntity<StudentProfileResponse>
            getProfile(
                    Authentication authentication,
                    @RequestParam(required = false)
                    String email
            ) {

        String accountEmail = authenticatedEmail(authentication, email);

        StudentProfileResponse response =
                studentProfileService
                        .getProfile(
                                accountEmail
                        );

        return ResponseEntity.ok(
                response
        );
    }

    /**
     * ======================================================
     * Update Student Profile
     * ------------------------------------------------------
     *
     * PUT:
     *
     * /api/student/profile
     * ?email=student@gmail.com
     *
     * Request Body Example:
     *
     * {
     *   "name": "Sambit Kumar Patra",
     *   "phone": "8658623356",
     *   "rollNumber": "2512050040",
     *   "semester": 4
     * }
     *
     * Branch / Email / Role request body me nahi aayenge.
     * ======================================================
     */
    @PutMapping
    public ResponseEntity<StudentProfileResponse>
            updateProfile(
                    Authentication authentication,
                    @RequestParam
                    String email,

                    @Valid
                    @RequestBody
                    StudentProfileUpdateRequest request
            ) {

        String accountEmail = authenticatedEmail(authentication, email);

        StudentProfileResponse response =
                studentProfileService
                        .updateProfile(
                                accountEmail,
                                request
                        );

        return ResponseEntity.ok(
                response
        );
    }

    private String authenticatedEmail(Authentication authentication, String requestedEmail) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.getEmail();
        }
        if (requestedEmail != null && !requestedEmail.isBlank()) {
            return requestedEmail;
        }
        throw new ApiException("Authentication required");
    }
}
