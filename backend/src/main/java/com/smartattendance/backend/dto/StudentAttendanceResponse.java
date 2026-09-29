package com.smartattendance.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAttendanceResponse {

    private Long userId;

    private String name;

    private String rollNumber;

    private String branch;

    private Integer semester;

    private long totalClasses;

    private long presentClasses;

    private long absentClasses;

    private double attendancePercentage;

    private List<SubjectAttendanceResponse> subjectAttendance;

    private List<AttendanceHistoryResponse> attendanceHistory;
}