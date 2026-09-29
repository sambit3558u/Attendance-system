package com.smartattendance.backend.dto;

import java.time.LocalDateTime;

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
public class MarkStudentAttendanceResponse {

    private Long attendanceId;

    private Long sessionId;

    private String subjectName;

    private String status;

    private String message;

    private Boolean geoVerified;

    private Double distanceMeters;

    private Double allowedRadiusMeters;

    private String placeName;

    private LocalDateTime attendanceTime;
}