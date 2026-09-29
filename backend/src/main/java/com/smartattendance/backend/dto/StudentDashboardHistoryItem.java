package com.smartattendance.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardHistoryItem {

    private Long attendanceId;

    private LocalDate date;

    private String subjectName;

    private String teacherName;

    private String status;

    private LocalTime time;
}