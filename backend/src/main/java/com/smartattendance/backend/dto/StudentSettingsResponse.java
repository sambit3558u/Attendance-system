package com.smartattendance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Student Settings Response DTO
 * ----------------------------------------------------------
 * Student Settings page ko current saved settings return
 * karne ke liye use hoga.
 *
 * Response includes:
 *
 * - Student ID
 * - Student Name
 * - Email
 * - Notifications Enabled
 * - Class Reminder Enabled
 * - Attendance Alert Enabled
 * - Timetable Alert Enabled
 * - Theme
 *
 * Password information kabhi response me nahi bhejna hai.
 * ==========================================================
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSettingsResponse {

    // =====================================================
    // Student Information
    // =====================================================

    private Long studentId;

    private String studentName;

    private String email;

    // =====================================================
    // Main Notifications
    // =====================================================

    private Boolean notificationsEnabled;

    // =====================================================
    // Class Reminder
    // =====================================================

    private Boolean classReminderEnabled;

    // =====================================================
    // Attendance Alert
    // =====================================================

    private Boolean attendanceAlertEnabled;

    // =====================================================
    // Timetable Update Alert
    // =====================================================

    private Boolean timetableAlertEnabled;

    // =====================================================
    // Theme
    // -----------------------------------------------------
    // Expected values:
    // DARK
    // LIGHT
    // =====================================================

    private String theme;
}