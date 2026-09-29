package com.smartattendance.backend.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * ==========================================================
 * START CLASS REQUEST
 * ----------------------------------------------------------
 * Data required when Teacher starts attendance for a class.
 *
 * Teacher selects an attendance location before starting.
 * Only the location ID comes from frontend.
 *
 * Latitude / longitude / radius will NEVER be trusted from
 * frontend. Backend will load them from AttendanceLocation.
 * ==========================================================
 */

@Getter
@Setter
public class StartClassRequest {

    // =====================================================
    // SUBJECT
    // =====================================================

    @NotBlank(
            message = "Subject Name is required"
    )
    private String subjectName;

    // =====================================================
    // CLASS TIME
    // =====================================================

    @NotNull(
            message = "Start Time is required"
    )
    private LocalTime startTime;

    @NotNull(
            message = "End Time is required"
    )
    private LocalTime endTime;

    // =====================================================
    // CLASS BRANCH
    // =====================================================

    @NotBlank(
            message = "Branch is required"
    )
    private String branch;

    // =====================================================
    // CLASS SEMESTER
    // =====================================================

    @NotNull(
            message = "Semester is required"
    )
    private Integer semester;

    // =====================================================
    // SELECTED ATTENDANCE LOCATION
    //
    // Teacher chooses this from the popup.
    //
    // Example:
    // 1 = MCA Computer Lab
    // 2 = MCA Block
    // 3 = Seminar Hall
    // =====================================================

    @NotNull(
            message = "Attendance location is required"
    )
    private Long attendanceLocationId;
}