package com.smartattendance.backend.dto;

import java.time.LocalTime;

import com.smartattendance.backend.entity.DayOfWeek;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Timetable Response DTO
 * ----------------------------------------------------------
 * Backend se frontend ko timetable information
 * return karne ke liye use hoga.
 *
 * Student ko database Entity directly nahi bhejenge.
 *
 * Example Response:
 *
 * {
 *   "id": 1,
 *   "subjectId": 3,
 *   "subjectName": "AIML",
 *   "subjectCode": "MCA303",
 *   "teacherId": 5,
 *   "teacherName": "Teacher Name",
 *   "branch": "Master of Computer Applications",
 *   "semester": 3,
 *   "day": "MONDAY",
 *   "startTime": "09:45:00",
 *   "endTime": "10:45:00"
 * }
 * ==========================================================
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableResponse {

    // =====================================================
    // Timetable ID
    // =====================================================

    private Long id;

    // =====================================================
    // Subject Information
    // =====================================================

    private Long subjectId;

    private String subjectName;

    private String subjectCode;

    // =====================================================
    // Teacher Information
    // =====================================================

    private Long teacherId;

    private String teacherName;

    // =====================================================
    // Branch
    // =====================================================

    private String branch;

    // =====================================================
    // Semester
    // =====================================================

    private Integer semester;

    // =====================================================
    // Day
    // =====================================================

    private DayOfWeek day;

    // =====================================================
    // Scheduled Start Time
    // =====================================================

    private LocalTime startTime;

    // =====================================================
    // Scheduled End Time
    // =====================================================

    private LocalTime endTime;
}