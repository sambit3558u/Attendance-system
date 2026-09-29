package com.smartattendance.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.StudentSettingsResponse;
import com.smartattendance.backend.dto.StudentSettingsUpdateRequest;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.StudentSettings;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.StudentSettingsRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Student Settings Service
 * ----------------------------------------------------------
 * Student application settings related business logic
 * handle karta hai.
 *
 * Main Responsibilities:
 *
 * 1. Student ko email se find karna.
 * 2. Student role validate karna.
 * 3. Default settings automatically create karna.
 * 4. Current settings frontend ko return karna.
 * 5. Settings update karke database me save karna.
 * ==========================================================
 */

@Service
@RequiredArgsConstructor
public class StudentSettingsService {

    private final UserRepository userRepository;

    private final StudentSettingsRepository
            studentSettingsRepository;

    /**
     * ======================================================
     * Get Student Settings
     * ------------------------------------------------------
     * Agar student ka settings record exist nahi karta,
     * default settings automatically create hongi.
     * ======================================================
     */
    @Transactional
    public StudentSettingsResponse getSettings(
            String email
    ) {

        User student =
                findStudentByEmail(email);

        StudentSettings settings =
                getOrCreateSettings(student);

        return convertToResponse(
                settings
        );
    }

    /**
     * ======================================================
     * Update Student Settings
     * ======================================================
     */
    @Transactional
    public StudentSettingsResponse updateSettings(
            String email,
            StudentSettingsUpdateRequest request
    ) {

        if (request == null) {
            throw new ApiException(
                    "Settings data is required"
            );
        }

        User student =
                findStudentByEmail(email);

        StudentSettings settings =
                getOrCreateSettings(student);

        // =================================================
        // Validate Theme
        // =================================================

        String theme =
                request.getTheme();

        if (theme == null
                || theme.isBlank()) {

            throw new ApiException(
                    "Theme is required"
            );
        }

        theme =
                theme
                        .trim()
                        .toUpperCase();

        if (!theme.equals("DARK")
                && !theme.equals("LIGHT")) {

            throw new ApiException(
                    "Theme must be DARK or LIGHT"
            );
        }

        // =================================================
        // Update Settings
        // =================================================

        settings.setNotificationsEnabled(
                request.getNotificationsEnabled()
        );

        settings.setClassReminderEnabled(
                request.getClassReminderEnabled()
        );

        settings.setAttendanceAlertEnabled(
                request.getAttendanceAlertEnabled()
        );

        settings.setTimetableAlertEnabled(
                request.getTimetableAlertEnabled()
        );

        settings.setTheme(
                theme
        );

        StudentSettings updatedSettings =
                studentSettingsRepository.save(
                        settings
                );

        return convertToResponse(
                updatedSettings
        );
    }

    /**
     * ======================================================
     * Find Student By Email
     * ======================================================
     */
    private User findStudentByEmail(
            String email
    ) {

        if (email == null
                || email.isBlank()) {

            throw new ApiException(
                    "Email is required"
            );
        }

        String normalizedEmail =
                email
                        .trim()
                        .toLowerCase();

        User student =
                userRepository
                        .findByEmail(
                                normalizedEmail
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Student not found"
                                )
                        );

        if (student.getRole()
                != Role.STUDENT) {

            throw new ApiException(
                    "Only students can access student settings"
            );
        }

        return student;
    }

    /**
     * ======================================================
     * Get Or Create Settings
     * ------------------------------------------------------
     * Student ka settings record agar nahi mila,
     * default settings automatically create karega.
     * ======================================================
     */
    private StudentSettings getOrCreateSettings(
            User student
    ) {

        return studentSettingsRepository
                .findByStudentId(
                        student.getId()
                )
                .orElseGet(
                        () -> createDefaultSettings(
                                student
                        )
                );
    }

    /**
     * ======================================================
     * Create Default Settings
     * ======================================================
     */
    private StudentSettings createDefaultSettings(
            User student
    ) {

        StudentSettings settings =
                StudentSettings
                        .builder()
                        .student(
                                student
                        )
                        .notificationsEnabled(
                                true
                        )
                        .classReminderEnabled(
                                true
                        )
                        .attendanceAlertEnabled(
                                true
                        )
                        .timetableAlertEnabled(
                                true
                        )
                        .theme(
                                "DARK"
                        )
                        .build();

        return studentSettingsRepository.save(
                settings
        );
    }

    /**
     * ======================================================
     * Convert Entity To Response DTO
     * ======================================================
     */
    private StudentSettingsResponse convertToResponse(
            StudentSettings settings
    ) {

        User student =
                settings.getStudent();

        return StudentSettingsResponse
                .builder()
                .studentId(
                        student.getId()
                )
                .studentName(
                        student.getName()
                )
                .email(
                        student.getEmail()
                )
                .notificationsEnabled(
                        settings.getNotificationsEnabled()
                )
                .classReminderEnabled(
                        settings.getClassReminderEnabled()
                )
                .attendanceAlertEnabled(
                        settings.getAttendanceAlertEnabled()
                )
                .timetableAlertEnabled(
                        settings.getTimetableAlertEnabled()
                )
                .theme(
                        settings.getTheme()
                )
                .build();
    }
}