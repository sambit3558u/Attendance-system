package com.smartattendance.backend.dto;

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
public class StudentDashboardTimetableItem {

    private Long timetableId;

    private Long sessionId;

    private String subjectName;

    private String subjectCode;

    private String teacherName;

    private String branch;

    private Integer semester;

    private LocalTime startTime;

    private LocalTime endTime;

    /*
     * UPCOMING
     * LIVE
     * COMPLETED
     */
    private String status;
}