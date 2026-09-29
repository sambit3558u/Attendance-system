package com.smartattendance.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkStudentAttendanceRequest {

    // =====================================================
    // LIVE CLASS SESSION
    // =====================================================

    @NotNull(message = "Class session ID is required")
    private Long sessionId;

    // =====================================================
    // CURRENT STUDENT GPS
    // =====================================================

    @NotNull(message = "Latitude is required")
    @DecimalMin(
            value = "-90.0",
            message = "Latitude must be >= -90"
    )
    @DecimalMax(
            value = "90.0",
            message = "Latitude must be <= 90"
    )
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(
            value = "-180.0",
            message = "Longitude must be >= -180"
    )
    @DecimalMax(
            value = "180.0",
            message = "Longitude must be <= 180"
    )
    private Double longitude;

    // Browser geolocation accuracy in meters.
    // Optional rahega.
    @PositiveOrZero(
            message = "GPS accuracy cannot be negative"
    )
    private Double accuracy;

    // =====================================================
    // DEVICE VERIFICATION
    // =====================================================

    @NotBlank(message = "Device ID is required")
    private String deviceId;
}