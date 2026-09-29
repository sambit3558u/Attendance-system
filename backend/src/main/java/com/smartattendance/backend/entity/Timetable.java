package com.smartattendance.backend.entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Timetable Entity
 * ----------------------------------------------------------
 * Teacher/Admin ke dwara create ki gayi class schedule
 * database me store karta hai.
 *
 * Important:
 * Timetable sirf scheduled class batata hai.
 *
 * LIVE CLASS tabhi start hogi jab Teacher manually
 * ClassSession start karega.
 * ==========================================================
 */

@Entity
@Table(name = "timetables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timetable {

    // =====================================================
    // Primary Key
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // Subject
    // -----------------------------------------------------
    // Subject table ke subject se relation.
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subject_id",
            nullable = false
    )
    private Subject subject;

    // =====================================================
    // Teacher
    // -----------------------------------------------------
    // Timetable me assigned Teacher.
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "teacher_id",
            nullable = false
    )
    private User teacher;

    // =====================================================
    // Branch
    // -----------------------------------------------------
    // Example:
    // Master of Computer Applications
    // =====================================================

    @Column(nullable = false)
    private String branch;

    // =====================================================
    // Semester
    // =====================================================

    @Column(nullable = false)
    private Integer semester;

    // =====================================================
    // Day
    // -----------------------------------------------------
    // MONDAY, TUESDAY, WEDNESDAY etc.
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek day;

    // =====================================================
    // Scheduled Start Time
    // -----------------------------------------------------
    // Example:
    // 09:45
    // =====================================================

    @Column(nullable = false)
    private LocalTime startTime;

    // =====================================================
    // Scheduled End Time
    // -----------------------------------------------------
    // Example:
    // 10:45
    // =====================================================

    @Column(nullable = false)
    private LocalTime endTime;
}