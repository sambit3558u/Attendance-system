package com.smartattendance.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.StudentSettingsResponse;
import com.smartattendance.backend.dto.StudentSettingsUpdateRequest;
import com.smartattendance.backend.service.StudentSettingsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Student Settings Controller
 * ----------------------------------------------------------
 * Student settings related HTTP requests handle karta hai.
 *
 * APIs:
 *
 * 1. GET
 *    /api/student/settings?email=student@gmail.com
 *
 *    Current saved settings return karega.
 *
 * 2. PUT
 *    /api/student/settings?email=student@gmail.com
 *
 *    Student settings update karega.
 * ==========================================================
 */

@RestController
@RequestMapping("/api/student/settings")
@RequiredArgsConstructor
public class StudentSettingsController {

    private final StudentSettingsService
            studentSettingsService;

    /**
     * ======================================================
     * Get Student Settings
     * ------------------------------------------------------
     *
     * GET:
     *
     * /api/student/settings
     * ?email=student@gmail.com
     *
     * Agar settings record exist nahi karta,
     * backend default settings automatically create karega.
     * ======================================================
     */
    @GetMapping
    public ResponseEntity<StudentSettingsResponse>
            getSettings(
                    @RequestParam
                    String email
            ) {

        StudentSettingsResponse response =
                studentSettingsService
                        .getSettings(
                                email
                        );

        return ResponseEntity.ok(
                response
        );
    }

    /**
     * ======================================================
     * Update Student Settings
     * ------------------------------------------------------
     *
     * PUT:
     *
     * /api/student/settings
     * ?email=student@gmail.com
     *
     * Request Example:
     *
     * {
     *   "notificationsEnabled": true,
     *   "classReminderEnabled": true,
     *   "attendanceAlertEnabled": true,
     *   "timetableAlertEnabled": false,
     *   "theme": "DARK"
     * }
     * ======================================================
     */
    @PutMapping
    public ResponseEntity<StudentSettingsResponse>
            updateSettings(
                    @RequestParam
                    String email,

                    @Valid
                    @RequestBody
                    StudentSettingsUpdateRequest request
            ) {

        StudentSettingsResponse response =
                studentSettingsService
                        .updateSettings(
                                email,
                                request
                        );

        return ResponseEntity.ok(
                response
        );
    }
}