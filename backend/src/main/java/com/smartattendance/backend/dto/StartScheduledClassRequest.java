package com.smartattendance.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartScheduledClassRequest {

    @NotNull(message = "Timetable ID is required")
    private Long timetableId;

    @NotNull(message = "Attendance location is required")
    private Long attendanceLocationId;
}