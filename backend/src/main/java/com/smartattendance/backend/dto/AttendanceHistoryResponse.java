package com.smartattendance.backend.dto;

import java.time.LocalDateTime;

import com.smartattendance.backend.entity.AttendanceStatus;

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
public class AttendanceHistoryResponse {

    private Long attendanceId;

    private String subjectName;

    private AttendanceStatus status;

    private LocalDateTime attendanceTime;
}