package com.smartattendance.backend.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.TeacherAttendanceRequest;
import com.smartattendance.backend.dto.TeacherAttendanceResponse;
import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.TeacherAttendance;
import com.smartattendance.backend.entity.TeacherAttendanceLocation;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.TeacherAttendanceLocationRepository;
import com.smartattendance.backend.repository.TeacherAttendanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherAttendanceService {

    private final TeacherAttendanceRepository teacherAttendanceRepository;

    private final TeacherAttendanceLocationRepository
            teacherAttendanceLocationRepository;

    // =====================================================
    // MARK TEACHER ATTENDANCE
    // =====================================================

    @Transactional
    public TeacherAttendanceResponse markAttendance(
            User teacher,
            TeacherAttendanceRequest request
    ) {

        // =================================================
        // 1. AUTHENTICATION CHECK
        // =================================================

        if (teacher == null) {
            throw new ApiException(
                    "Teacher authentication required"
            );
        }

        // =================================================
        // 2. ROLE CHECK
        // =================================================

        if (teacher.getRole() != Role.TEACHER) {
            throw new ApiException(
                    "Only teachers can mark teacher attendance"
            );
        }

        // =================================================
        // 3. REQUEST CHECK
        // =================================================

        if (request == null) {
            throw new ApiException(
                    "Attendance request is required"
            );
        }

        // =================================================
        // 4. CURRENT GPS CHECK
        // =================================================

        if (
                request.getLatitude() == null ||
                request.getLongitude() == null
        ) {
            throw new ApiException(
                    "Current latitude and longitude are required"
            );
        }

        final double teacherLatitude =
                request.getLatitude();

        final double teacherLongitude =
                request.getLongitude();

        // =================================================
        // 5. VALIDATE LATITUDE
        // =================================================

        if (
                teacherLatitude < -90.0 ||
                teacherLatitude > 90.0
        ) {
            throw new ApiException(
                    "Invalid teacher latitude"
            );
        }

        // =================================================
        // 6. VALIDATE LONGITUDE
        // =================================================

        if (
                teacherLongitude < -180.0 ||
                teacherLongitude > 180.0
        ) {
            throw new ApiException(
                    "Invalid teacher longitude"
            );
        }

        // =================================================
        // 7. GET TEACHER DEPARTMENT
        //
        // IMPORTANT:
        // Keep department final because it is used inside
        // the orElseThrow lambda below.
        // =================================================

        String teacherDepartment =
                teacher.getDepartment();

        if (
                teacherDepartment == null ||
                teacherDepartment.isBlank()
        ) {
            throw new ApiException(
                    "Department is not assigned to this teacher"
            );
        }

        final String department =
                teacherDepartment.trim();

        // =================================================
        // 8. CURRENT DATE
        // =================================================

        final LocalDate today =
                LocalDate.now();

        // =================================================
        // 9. CHECK IF ALREADY MARKED TODAY
        //
        // Only one teacher attendance record per day.
        // =================================================

        TeacherAttendance existingAttendance =
                teacherAttendanceRepository
                        .findByTeacherIdAndAttendanceDate(
                                teacher.getId(),
                                today
                        )
                        .orElse(null);

        // =================================================
        // 10. RETURN EXISTING ATTENDANCE
        // =================================================

        if (existingAttendance != null) {

            Double existingDistance =
                    existingAttendance
                            .getDistanceMeters();

            double responseDistance =
                    existingDistance != null
                            ? round(existingDistance)
                            : 0.0;

            return TeacherAttendanceResponse
                    .builder()
                    .status(
                            existingAttendance
                                    .getStatus()
                                    .name()
                    )
                    .message(
                            "Attendance already recorded for today"
                    )
                    .distanceMeters(
                            responseDistance
                    )
                    .allowedRadiusMeters(
                            existingAttendance
                                    .getAllowedRadiusMeters()
                    )
                    .placeName(
                            existingAttendance
                                    .getPlaceName()
                    )
                    .department(
                            existingAttendance
                                    .getDepartment()
                    )
                    .build();
        }

        // =================================================
        // 11. FIND TEACHER ATTENDANCE LOCATION
        //
        // Teacher attendance location is based ONLY on:
        //
        // Department
        //
        // Branch is NOT used.
        // =================================================

        TeacherAttendanceLocation attendanceLocation =
                teacherAttendanceLocationRepository
                        .findByDepartmentIgnoreCase(
                                department
                        )
                        .orElseThrow(
                                () ->
                                        new ApiException(
                                                "Admin has not configured teacher attendance location for department: "
                                                        + department
                                        )
                        );

        // =================================================
        // 12. VALIDATE REGISTERED LOCATION
        // =================================================

        if (
                attendanceLocation.getLatitude() == null ||
                attendanceLocation.getLongitude() == null
        ) {
            throw new ApiException(
                    "Teacher attendance location coordinates are not configured correctly"
            );
        }

        if (
                attendanceLocation.getRadiusMeters() == null ||
                attendanceLocation.getRadiusMeters() <= 0
        ) {
            throw new ApiException(
                    "Teacher attendance radius is not configured correctly"
            );
        }

        // =================================================
        // 13. ADMIN REGISTERED LOCATION
        // =================================================

        final double registeredLatitude =
                attendanceLocation
                        .getLatitude();

        final double registeredLongitude =
                attendanceLocation
                        .getLongitude();

        final double allowedRadiusMeters =
                attendanceLocation
                        .getRadiusMeters();

        // =================================================
        // 14. CALCULATE GPS DISTANCE
        // =================================================

        final double distanceMeters =
                calculateDistanceMeters(
                        teacherLatitude,
                        teacherLongitude,
                        registeredLatitude,
                        registeredLongitude
                );

        // =================================================
        // 15. DETERMINE ATTENDANCE STATUS
        //
        // IMPORTANT:
        //
        // Frontend does NOT decide status.
        //
        // Current GPS inside radius:
        // PRESENT
        //
        // Current GPS outside radius:
        // ABSENT
        // =================================================

        final AttendanceStatus attendanceStatus;

        if (distanceMeters <= allowedRadiusMeters) {

            attendanceStatus =
                    AttendanceStatus.PRESENT;

        } else {

            attendanceStatus =
                    AttendanceStatus.ABSENT;
        }

        // =================================================
        // 16. GPS ACCURACY
        // =================================================

        Double gpsAccuracy =
                request.getAccuracy();

        // =================================================
        // 17. CREATE ATTENDANCE RECORD
        // =================================================

        TeacherAttendance teacherAttendance =
                TeacherAttendance
                        .builder()

                        // Logged-in teacher
                        .teacher(
                                teacher
                        )

                        // Today's date
                        .attendanceDate(
                                today
                        )

                        // PRESENT / ABSENT
                        .status(
                                attendanceStatus
                        )

                        // Teacher current GPS
                        .teacherLatitude(
                                teacherLatitude
                        )

                        .teacherLongitude(
                                teacherLongitude
                        )

                        // Browser GPS accuracy
                        .gpsAccuracy(
                                gpsAccuracy
                        )

                        // Admin registered GPS
                        .registeredLatitude(
                                registeredLatitude
                        )

                        .registeredLongitude(
                                registeredLongitude
                        )

                        // Calculated distance
                        .distanceMeters(
                                distanceMeters
                        )

                        // Admin configured radius
                        .allowedRadiusMeters(
                                allowedRadiusMeters
                        )

                        // Registered place
                        .placeName(
                                attendanceLocation
                                        .getPlaceName()
                        )

                        // Teacher department
                        .department(
                                department
                        )

                        .build();

        // =================================================
        // 18. SAVE ATTENDANCE
        // =================================================

        TeacherAttendance savedAttendance =
                teacherAttendanceRepository
                        .save(
                                teacherAttendance
                        );

        // =================================================
        // 19. RESPONSE MESSAGE
        // =================================================

        final String message;

        if (
                savedAttendance.getStatus() ==
                        AttendanceStatus.PRESENT
        ) {

            message =
                    "Location verified. Teacher attendance marked Present.";

        } else {

            message =
                    "You are outside the allowed department location. Teacher attendance marked Absent.";
        }

        // =================================================
        // 20. RETURN RESPONSE
        // =================================================

        return TeacherAttendanceResponse
                .builder()
                .status(
                        savedAttendance
                                .getStatus()
                                .name()
                )
                .message(
                        message
                )
                .distanceMeters(
                        round(
                                savedAttendance
                                        .getDistanceMeters()
                        )
                )
                .allowedRadiusMeters(
                        savedAttendance
                                .getAllowedRadiusMeters()
                )
                .placeName(
                        savedAttendance
                                .getPlaceName()
                )
                .department(
                        savedAttendance
                                .getDepartment()
                )
                .build();
    }

    // =====================================================
    // HAVERSINE DISTANCE CALCULATION
    //
    // Calculates distance between:
    //
    // Teacher current GPS
    //        and
    // Admin configured department GPS
    //
    // Result is returned in meters.
    // =====================================================

    private double calculateDistanceMeters(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2
    ) {

        final double EARTH_RADIUS_METERS =
                6_371_000.0;

        // -------------------------------------------------
        // Latitude difference
        // -------------------------------------------------

        double latitudeDistance =
                Math.toRadians(
                        latitude2 -
                                latitude1
                );

        // -------------------------------------------------
        // Longitude difference
        // -------------------------------------------------

        double longitudeDistance =
                Math.toRadians(
                        longitude2 -
                                longitude1
                );

        // -------------------------------------------------
        // Convert latitude to radians
        // -------------------------------------------------

        double latitude1Radians =
                Math.toRadians(
                        latitude1
                );

        double latitude2Radians =
                Math.toRadians(
                        latitude2
                );

        // -------------------------------------------------
        // Haversine
        // -------------------------------------------------

        double sinLatitude =
                Math.sin(
                        latitudeDistance /
                                2.0
                );

        double sinLongitude =
                Math.sin(
                        longitudeDistance /
                                2.0
                );

        double a =
                sinLatitude *
                        sinLatitude
                        +
                        Math.cos(
                                latitude1Radians
                        )
                                *
                                Math.cos(
                                        latitude2Radians
                                )
                                *
                                sinLongitude
                                *
                                sinLongitude;

        // Protect against floating point issues
        a =
                Math.min(
                        1.0,
                        Math.max(
                                0.0,
                                a
                        )
                );

        double c =
                2.0 *
                        Math.atan2(
                                Math.sqrt(a),
                                Math.sqrt(
                                        1.0 - a
                                )
                        );

        return EARTH_RADIUS_METERS *
                c;
    }

    // =====================================================
    // ROUND DISTANCE TO ONE DECIMAL
    //
    // Example:
    // 53.786 -> 53.8
    // =====================================================

    private double round(
            double value
    ) {

        return Math.round(
                value * 10.0
        ) / 10.0;
    }
}