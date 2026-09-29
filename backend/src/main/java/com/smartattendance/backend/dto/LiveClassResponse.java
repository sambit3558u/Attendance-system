package com.smartattendance.backend.dto;

import java.time.LocalTime;

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
public class LiveClassResponse {

    private Long sessionId;

    private String subjectName;

    private String teacherName;

    private LocalTime startTime;

    private LocalTime endTime;

    private String attendanceStatus;
}
