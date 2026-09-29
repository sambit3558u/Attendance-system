package com.smartattendance.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * ==========================================================
 * Student Settings Update Request DTO
 * ----------------------------------------------------------
 * Student Settings page se editable settings backend ko
 * receive karne ke liye use hoga.
 *
 * Editable:
 * - Notifications Enabled
 * - Class Reminder Enabled
 * - Attendance Alert Enabled
 * - Timetable Alert Enabled
 * - Theme
 *
 * NOT included:
 * - Student ID
 * - Name
 * - Email
 * - Password
 * ==========================================================
 */

@Getter
@Setter
public class StudentSettingsUpdateRequest {

    // =====================================================
    // Main Notifications
    // =====================================================

    @NotNull(message = "Notification setting is required")
    private Boolean notificationsEnabled;

    // =====================================================
    // Class Reminder
    // =====================================================

    @NotNull(message = "Class reminder setting is required")
    private Boolean classReminderEnabled;

    // =====================================================
    // Attendance Alert
    // =====================================================

    @NotNull(message = "Attendance alert setting is required")
    private Boolean attendanceAlertEnabled;

    // =====================================================
    // Timetable Update Alert
    // =====================================================

    @NotNull(message = "Timetable alert setting is required")
    private Boolean timetableAlertEnabled;

    // =====================================================
    // Theme
    // -----------------------------------------------------
    // Allowed:
    // DARK
    // LIGHT
    // =====================================================

    @NotBlank(message = "Theme is required")
    @Pattern(
            regexp = "^(DARK|LIGHT)$",
            message = "Theme must be DARK or LIGHT"
    )
    private String theme;
}