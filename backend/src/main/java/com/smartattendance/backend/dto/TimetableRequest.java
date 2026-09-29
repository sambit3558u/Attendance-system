package com.smartattendance.backend.dto;

import java.time.LocalTime;

import com.smartattendance.backend.entity.DayOfWeek;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * ==========================================================
 * Timetable Request DTO
 * ----------------------------------------------------------
 * Teacher/Admin timetable create ya update karte waqt
 * frontend se ye data backend ko bhejega.
 *
 * Example:
 *
 * {
 *   "subjectId": 1,
 *   "teacherEmail": "teacher@gmail.com",
 *   "branch": "Master of Computer Applications",
 *   "semester": 3,
 *   "day": "MONDAY",
 *   "startTime": "09:45",
 *   "endTime": "10:45"
 * }
 * ==========================================================
 */

@Getter
@Setter
public class TimetableRequest {

    // =====================================================
    // Subject ID
    // -----------------------------------------------------
    // subjects table ka ID.
    // =====================================================

    @NotNull(message = "Subject is required")
    private Long subjectId;

    // =====================================================
    // Teacher Email
    // -----------------------------------------------------
    // Assigned teacher identify karne ke liye.
    // =====================================================

    @NotBlank(message = "Teacher Email is required")
    private String teacherEmail;

    // =====================================================
    // Branch
    // =====================================================

    @NotBlank(message = "Branch is required")
    private String branch;

    // =====================================================
    // Semester
    // =====================================================

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 8, message = "Semester must not be greater than 8")
    private Integer semester;

    // =====================================================
    // Day
    // -----------------------------------------------------
    // MONDAY, TUESDAY, WEDNESDAY, etc.
    // =====================================================

    @NotNull(message = "Day is required")
    private DayOfWeek day;

    // =====================================================
    // Start Time
    // Example:
    // 09:45
    // =====================================================

    @NotNull(message = "Start Time is required")
    private LocalTime startTime;

    // =====================================================
    // End Time
    // Example:
    // 10:45
    // =====================================================

    @NotNull(message = "End Time is required")
    private LocalTime endTime;
}