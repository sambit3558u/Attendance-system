package com.smartattendance.backend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.MarkStudentAttendanceRequest;
import com.smartattendance.backend.dto.MarkStudentAttendanceResponse;
import com.smartattendance.backend.entity.Attendance;
import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.ClassSession;
import com.smartattendance.backend.entity.ClassSessionStatus;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceRepository;
import com.smartattendance.backend.repository.ClassSessionRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentAttendanceMarkService {

    private final UserRepository userRepository;

    private final AttendanceRepository attendanceRepository;

    private final ClassSessionRepository classSessionRepository;


    // =====================================================
    // MARK STUDENT ATTENDANCE
    // =====================================================

    @Transactional
    public MarkStudentAttendanceResponse markAttendance(
            String studentEmail,
            MarkStudentAttendanceRequest request
    ) {

        // =================================================
        // REQUEST VALIDATION
        // =================================================

        if (request == null) {

            throw new ApiException(
                    "Attendance request is required"
            );
        }

        if (
                studentEmail == null ||
                studentEmail.isBlank()
        ) {

            throw new ApiException(
                    "Authenticated student is required"
            );
        }

        if (
                request.getSessionId() == null
        ) {

            throw new ApiException(
                    "Class session ID is required"
            );
        }

        if (
                request.getLatitude() == null ||
                request.getLongitude() == null
        ) {

            throw new ApiException(
                    "Current GPS location is required"
            );
        }

        if (
                request.getDeviceId() == null ||
                request.getDeviceId().isBlank()
        ) {

            throw new ApiException(
                    "Device ID is required"
            );
        }


        // =================================================
        // LOAD STUDENT
        // =================================================

        User student =
                userRepository
                        .findByEmail(
                                studentEmail
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Student not found"
                                )
                        );


        // =================================================
        // ROLE CHECK
        // =================================================

        if (
                student.getRole() !=
                Role.STUDENT
        ) {

            throw new ApiException(
                    "Only students can mark attendance"
            );
        }


        // =================================================
        // STUDENT PROFILE
        // =================================================

        if (
                student.getBranch() == null ||
                student.getBranch().isBlank()
        ) {

            throw new ApiException(
                    "Student branch is not configured"
            );
        }

        if (
                student.getSemester() == null
        ) {

            throw new ApiException(
                    "Student semester is not configured"
            );
        }


        // =================================================
        // DEVICE VERIFICATION
        // =================================================

        if (
                student.getDeviceId() == null ||
                student.getDeviceId().isBlank()
        ) {

            throw new ApiException(
                    "Student device is not registered. Please login again."
            );
        }

        String savedDeviceId =
                student
                        .getDeviceId()
                        .trim();

        String requestDeviceId =
                request
                        .getDeviceId()
                        .trim();

        if (
                !savedDeviceId.equals(
                        requestDeviceId
                )
        ) {

            throw new ApiException(
                    "Attendance can only be marked from your registered device"
            );
        }


        // =================================================
        // LOAD CLASS SESSION
        // =================================================

        ClassSession session =
                classSessionRepository
                        .findById(
                                request.getSessionId()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Class session not found"
                                )
                        );


        // =================================================
        // SESSION MUST BE LIVE
        // =================================================

        if (
                session.getStatus() !=
                ClassSessionStatus.LIVE
        ) {

            throw new ApiException(
                    "Attendance session is not live"
            );
        }


        // =================================================
        // BRANCH CHECK
        // =================================================

        if (
                session.getBranch() == null ||
                !session
                        .getBranch()
                        .trim()
                        .equalsIgnoreCase(
                                student
                                        .getBranch()
                                        .trim()
                        )
        ) {

            throw new ApiException(
                    "This attendance session does not belong to your branch"
            );
        }


        // =================================================
        // SEMESTER CHECK
        // =================================================

        if (
                session.getSemester() == null ||
                !session
                        .getSemester()
                        .equals(
                                student.getSemester()
                        )
        ) {

            throw new ApiException(
                    "This attendance session does not belong to your semester"
            );
        }


        // =================================================
        // DUPLICATE ATTENDANCE CHECK
        // =================================================

        boolean alreadyMarked =
                attendanceRepository
                        .existsByStudentIdAndClassSessionId(
                                student.getId(),
                                session.getId()
                        );

        if (
                alreadyMarked
        ) {

            throw new ApiException(
                    "Attendance already marked for this class"
            );
        }


        // =================================================
        // VALIDATE STUDENT GPS
        // =================================================

        double studentLatitude =
                request.getLatitude();

        double studentLongitude =
                request.getLongitude();

        if (
                studentLatitude < -90 ||
                studentLatitude > 90
        ) {

            throw new ApiException(
                    "Invalid latitude"
            );
        }

        if (
                studentLongitude < -180 ||
                studentLongitude > 180
        ) {

            throw new ApiException(
                    "Invalid longitude"
            );
        }


        // =================================================
        // CLASS SESSION ATTENDANCE LOCATION
        //
        // IMPORTANT:
        //
        // We DO NOT search AttendanceLocation by branch.
        //
        // Teacher already selected the attendance location
        // while starting this class.
        //
        // The selected location was copied into ClassSession.
        // These snapshot values are authoritative for this
        // specific attendance session.
        // =================================================

        if (
                session.getAttendanceLocationId() == null
        ) {

            throw new ApiException(
                    "Attendance location was not selected for this class"
            );
        }

        if (
                session.getAttendancePlaceName() == null ||
                session.getAttendancePlaceName().isBlank()
        ) {

            throw new ApiException(
                    "Attendance place is not configured for this class"
            );
        }

        if (
                session.getAttendanceLatitude() == null ||
                session.getAttendanceLongitude() == null
        ) {

            throw new ApiException(
                    "Attendance location coordinates are not configured for this class"
            );
        }

        if (
                session.getAttendanceRadiusMeters() == null ||
                session.getAttendanceRadiusMeters() <= 0
        ) {

            throw new ApiException(
                    "Attendance radius is not configured for this class"
            );
        }


        // =================================================
        // VALIDATE SESSION LOCATION COORDINATES
        // =================================================

        double attendanceLatitude =
                session.getAttendanceLatitude();

        double attendanceLongitude =
                session.getAttendanceLongitude();

        double allowedRadiusMeters =
                session.getAttendanceRadiusMeters();

        if (
                attendanceLatitude < -90 ||
                attendanceLatitude > 90
        ) {

            throw new ApiException(
                    "Attendance location latitude is invalid"
            );
        }

        if (
                attendanceLongitude < -180 ||
                attendanceLongitude > 180
        ) {

            throw new ApiException(
                    "Attendance location longitude is invalid"
            );
        }


        // =================================================
        // CALCULATE DISTANCE
        //
        // STUDENT GPS
        //      ↓
        // SELECTED CLASS SESSION LOCATION
        // =================================================

        double distanceMeters =
                calculateDistanceMeters(
                        studentLatitude,
                        studentLongitude,
                        attendanceLatitude,
                        attendanceLongitude
                );


        // =================================================
        // GEOFENCE CHECK
        //
        // OUTSIDE:
        // Do NOT save ABSENT here.
        //
        // Student simply cannot mark PRESENT.
        //
        // When teacher ends attendance, missing eligible
        // students will automatically become ABSENT.
        // =================================================

        if (
                distanceMeters >
                allowedRadiusMeters
        ) {

            throw new ApiException(
                    "You are outside the attendance area. "
                            + "Place: "
                            + session.getAttendancePlaceName()
                            + ". Distance: "
                            + Math.round(distanceMeters)
                            + " meters. Allowed radius: "
                            + Math.round(allowedRadiusMeters)
                            + " meters."
            );
        }


        // =================================================
        // SAVE PRESENT
        // =================================================

        LocalDateTime now =
                LocalDateTime.now();

        Attendance attendance =
                Attendance
                        .builder()

                        .student(
                                student
                        )

                        .classSession(
                                session
                        )

                        .subject(
                                session.getSubjectName()
                        )

                        .branch(
                                session.getBranch()
                        )

                        .semester(
                                session.getSemester()
                        )

                        .status(
                                AttendanceStatus.PRESENT
                        )

                        .geoVerified(
                                true
                        )

                        .studentLatitude(
                                studentLatitude
                        )

                        .studentLongitude(
                                studentLongitude
                        )

                        .gpsAccuracy(
                                request.getAccuracy()
                        )

                        .distanceMeters(
                                distanceMeters
                        )

                        .allowedRadiusMeters(
                                allowedRadiusMeters
                        )

                        .attendanceTime(
                                now
                        )

                        .build();


        Attendance savedAttendance =
                attendanceRepository
                        .save(
                                attendance
                        );


        // =================================================
        // RESPONSE
        // =================================================

        return MarkStudentAttendanceResponse
                .builder()

                .attendanceId(
                        savedAttendance.getId()
                )

                .sessionId(
                        session.getId()
                )

                .subjectName(
                        session.getSubjectName()
                )

                .status(
                        AttendanceStatus
                                .PRESENT
                                .name()
                )

                .message(
                        "Attendance marked successfully at "
                                + session
                                        .getAttendancePlaceName()
                )

                .geoVerified(
                        true
                )

                .distanceMeters(
                        roundTwoDecimals(
                                distanceMeters
                        )
                )

                .allowedRadiusMeters(
                        allowedRadiusMeters
                )

                .placeName(
                        session
                                .getAttendancePlaceName()
                )

                .attendanceTime(
                        savedAttendance
                                .getAttendanceTime()
                )

                .build();
    }


    // =====================================================
    // HAVERSINE DISTANCE
    //
    // Returns distance between two GPS coordinates
    // in meters.
    // =====================================================

    private double calculateDistanceMeters(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2
    ) {

        final double earthRadiusMeters =
                6371000.0;

        double lat1 =
                Math.toRadians(
                        latitude1
                );

        double lat2 =
                Math.toRadians(
                        latitude2
                );

        double deltaLat =
                Math.toRadians(
                        latitude2 -
                                latitude1
                );

        double deltaLon =
                Math.toRadians(
                        longitude2 -
                                longitude1
                );

        double a =
                Math.sin(
                        deltaLat / 2
                )
                        *
                        Math.sin(
                                deltaLat / 2
                        )
                        +
                        Math.cos(
                                lat1
                        )
                                *
                                Math.cos(
                                        lat2
                                )
                                *
                                Math.sin(
                                        deltaLon / 2
                                )
                                *
                                Math.sin(
                                        deltaLon / 2
                                );

        double c =
                2
                        *
                        Math.atan2(
                                Math.sqrt(
                                        a
                                ),
                                Math.sqrt(
                                        1 - a
                                )
                        );

        return earthRadiusMeters * c;
    }


    // =====================================================
    // ROUND TO TWO DECIMAL PLACES
    // =====================================================

    private double roundTwoDecimals(
            double value
    ) {

        return Math.round(
                value * 100.0
        ) / 100.0;
    }
}