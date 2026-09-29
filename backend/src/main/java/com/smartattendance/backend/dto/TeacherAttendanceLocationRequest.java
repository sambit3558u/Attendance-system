package com.smartattendance.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherAttendanceLocationRequest {

    @NotBlank(
            message = "Place name is required"
    )
    private String placeName;

    @NotBlank(
            message = "Department is required"
    )
    private String department;

    @NotNull(
            message = "Latitude is required"
    )
    @DecimalMin(
            value = "-90.0",
            message = "Latitude must be at least -90"
    )
    @DecimalMax(
            value = "90.0",
            message = "Latitude must not exceed 90"
    )
    private Double latitude;

    @NotNull(
            message = "Longitude is required"
    )
    @DecimalMin(
            value = "-180.0",
            message = "Longitude must be at least -180"
    )
    @DecimalMax(
            value = "180.0",
            message = "Longitude must not exceed 180"
    )
    private Double longitude;

    @NotNull(
            message = "Radius is required"
    )
    @Positive(
            message = "Radius must be greater than 0"
    )
    private Double radiusMeters;
}