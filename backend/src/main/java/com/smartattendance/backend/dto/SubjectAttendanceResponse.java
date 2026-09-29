package com.smartattendance.backend.dto;

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
public class SubjectAttendanceResponse {

    private String subjectName;

    private long totalClasses;

    private long presentClasses;

    private long absentClasses;

    private double percentage;
}