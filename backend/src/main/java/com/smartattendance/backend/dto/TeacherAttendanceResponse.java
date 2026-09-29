package com.smartattendance.backend.dto;

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
public class TeacherAttendanceResponse {

    private String status;

    private String message;

    private Double distanceMeters;

    private Double allowedRadiusMeters;

    private String placeName;

    private String department;
}