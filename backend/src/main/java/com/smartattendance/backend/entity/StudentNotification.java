package com.smartattendance.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Student Notification Entity
 * ----------------------------------------------------------
 * Student ko milne wali notifications database me store
 * karta hai.
 *
 * Types example:
 *
 * LIVE_CLASS
 * ATTENDANCE
 * LOW_ATTENDANCE
 * TIMETABLE
 * GENERAL
 * ==========================================================
 */

@Entity
@Table(name = "student_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentNotification {

    // =====================================================
    // Primary Key
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // Student
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private User student;

    // =====================================================
    // Notification Type
    // -----------------------------------------------------
    // LIVE_CLASS
    // ATTENDANCE
    // LOW_ATTENDANCE
    // TIMETABLE
    // GENERAL
    // =====================================================

    @Column(
            nullable = false,
            length = 30
    )
    private String type;

    // =====================================================
    // Title
    // =====================================================

    @Column(
            nullable = false,
            length = 150
    )
    private String title;

    // =====================================================
    // Message
    // =====================================================

    @Column(
            nullable = false,
            length = 1000
    )
    private String message;

    // =====================================================
    // Read Status
    // =====================================================

    @Column(name = "is_read", nullable = false)
@Builder.Default
private Boolean read = false;

    // =====================================================
    // Created Time
    // =====================================================

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt =
            LocalDateTime.now();
}