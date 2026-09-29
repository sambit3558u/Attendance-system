package com.smartattendance.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Student Settings Entity
 * ----------------------------------------------------------
 * Student ke personal application settings database me
 * store karta hai.
 *
 * One Student = One Settings Record
 *
 * Settings:
 * - Notifications On/Off
 * - Class Reminder On/Off
 * - Attendance Alert On/Off
 * - Timetable Update Alert On/Off
 * - Theme
 *
 * Password is entity me store nahi hoga.
 * Password User entity me BCrypt hash ke form me rahega.
 * ==========================================================
 */

@Entity
@Table(name = "student_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSettings {

    // =====================================================
    // Primary Key
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // Student
    // -----------------------------------------------------
    // Ek student ka sirf ek settings record hoga.
    // =====================================================

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            unique = true
    )
    private User student;

    // =====================================================
    // Main Notifications
    // =====================================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean notificationsEnabled = true;

    // =====================================================
    // Class Reminder
    // =====================================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean classReminderEnabled = true;

    // =====================================================
    // Attendance Alert
    // =====================================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean attendanceAlertEnabled = true;

    // =====================================================
    // Timetable Update Alert
    // =====================================================

    @Column(nullable = false)
    @Builder.Default
    private Boolean timetableAlertEnabled = true;

    // =====================================================
    // Theme
    // -----------------------------------------------------
    // DARK
    // LIGHT
    // =====================================================

    @Column(nullable = false)
    @Builder.Default
    private String theme = "DARK";
}