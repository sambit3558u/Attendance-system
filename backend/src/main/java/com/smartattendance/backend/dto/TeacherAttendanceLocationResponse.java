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
public class TeacherAttendanceLocationResponse {

    private Long id;

    private String placeName;

    private String department;

    private Double latitude;

    private Double longitude;

    private Double radiusMeters;
}