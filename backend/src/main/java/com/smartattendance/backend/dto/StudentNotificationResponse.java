package com.smartattendance.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Student Notification Response DTO
 * ----------------------------------------------------------
 * StudentNotifications.jsx ko notification data return
 * karne ke liye use hoga.
 *
 * Example:
 *
 * {
 *   "id": 1,
 *   "type": "ATTENDANCE",
 *   "title": "Attendance Marked",
 *   "message": "Your attendance has been marked PRESENT.",
 *   "read": false,
 *   "createdAt": "2026-08-12T10:30:00"
 * }
 * ==========================================================
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentNotificationResponse {

    // Notification ID
    private Long id;

    // LIVE_CLASS / ATTENDANCE / LOW_ATTENDANCE / TIMETABLE
    private String type;

    // Notification title
    private String title;

    // Notification message
    private String message;

    // Read / Unread
    private Boolean read;

    // Notification created time
    private LocalDateTime createdAt;
}