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
public class StartScheduledClassResponse {

    private Long sessionId;

    private Long timetableId;

    private String subject;

    private String branch;

    private Integer semester;

    private String time;

    private String status;

    private String message;
}