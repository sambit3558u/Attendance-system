package com.smartattendance.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.LiveClassResponse;
import com.smartattendance.backend.dto.StartClassRequest;
import com.smartattendance.backend.entity.Attendance;
import com.smartattendance.backend.entity.AttendanceLocation;
import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.ClassSession;
import com.smartattendance.backend.entity.ClassSessionStatus;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceLocationRepository;
import com.smartattendance.backend.repository.AttendanceRepository;
import com.smartattendance.backend.repository.ClassSessionRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassSessionService {

    private final ClassSessionRepository classSessionRepository;

    private final UserRepository userRepository;

    private final AttendanceRepository attendanceRepository;

    // =====================================================
    // NEW:
    // ADMIN-CONFIGURED STUDENT ATTENDANCE LOCATIONS
    // =====================================================

    private final AttendanceLocationRepository
            attendanceLocationRepository;


    // =====================================================
    // START CLASS
    // =====================================================

    @Transactional
    public LiveClassResponse startClass(
            String teacherEmail,
            StartClassRequest request
    ) {

        // =================================================
        // AUTH EMAIL
        // =================================================

        if (
                teacherEmail == null ||
                teacherEmail.isBlank()
        ) {

            throw new ApiException(
                    "Teacher email is required"
            );
        }

        // =================================================
        // REQUEST
        // =================================================

        if (
                request == null
        ) {

            throw new ApiException(
                    "Class details are required"
            );
        }

        // =================================================
        // FIND TEACHER
        // =================================================

        User teacher =
                userRepository
                        .findByEmail(
                                teacherEmail
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Teacher not found"
                                )
                        );

        // =================================================
        // ROLE CHECK
        // =================================================

        if (
                teacher.getRole() !=
                Role.TEACHER
        ) {

            throw new ApiException(
                    "Only Teacher can start a class"
            );
        }

        // =================================================
        // SUBJECT
        // =================================================

        if (
                request.getSubjectName() == null ||
                request.getSubjectName().isBlank()
        ) {

            throw new ApiException(
                    "Subject name is required"
            );
        }

        // =================================================
        // BRANCH
        // =================================================

        if (
                request.getBranch() == null ||
                request.getBranch().isBlank()
        ) {

            throw new ApiException(
                    "Branch is required"
            );
        }

        String classBranch =
                request
                        .getBranch()
                        .trim();

        // =================================================
        // SEMESTER
        // =================================================

        if (
                request.getSemester() == null
        ) {

            throw new ApiException(
                    "Semester is required"
            );
        }

        if (
                request.getSemester() < 1
        ) {

            throw new ApiException(
                    "Semester must be greater than 0"
            );
        }

        // =================================================
        // CLASS TIMES
        // =================================================

        if (
                request.getStartTime() == null
        ) {

            throw new ApiException(
                    "Start Time is required"
            );
        }

        if (
                request.getEndTime() == null
        ) {

            throw new ApiException(
                    "End Time is required"
            );
        }

        if (
                !request
                        .getEndTime()
                        .isAfter(
                                request.getStartTime()
                        )
        ) {

            throw new ApiException(
                    "End Time must be after Start Time"
            );
        }

        // =================================================
        // ATTENDANCE LOCATION ID
        // =================================================

        if (
                request.getAttendanceLocationId() == null
        ) {

            throw new ApiException(
                    "Please select an attendance location"
            );
        }

        // =================================================
        // LOAD SELECTED LOCATION FROM DATABASE
        //
        // IMPORTANT:
        // Frontend only sends location ID.
        //
        // Coordinates/radius always come from database.
        // =================================================

        AttendanceLocation selectedLocation =
                attendanceLocationRepository
                        .findById(
                                request.getAttendanceLocationId()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Selected attendance location not found"
                                )
                        );

        // =================================================
        // VALIDATE LOCATION BRANCH
        //
        // Teacher cannot start MCA class using BCA location.
        // =================================================

        if (
                selectedLocation.getBranch() == null ||
                !selectedLocation
                        .getBranch()
                        .trim()
                        .equalsIgnoreCase(
                                classBranch
                        )
        ) {

            throw new ApiException(
                    "Selected attendance location does not belong to this class branch"
            );
        }

        // =================================================
        // VALIDATE LOCATION DATA
        // =================================================

        if (
                selectedLocation.getPlaceName() == null ||
                selectedLocation
                        .getPlaceName()
                        .isBlank()
        ) {

            throw new ApiException(
                    "Selected attendance location has no place name"
            );
        }

        if (
                selectedLocation.getLatitude() == null ||
                selectedLocation.getLongitude() == null
        ) {

            throw new ApiException(
                    "Selected attendance location coordinates are not configured"
            );
        }

        if (
                selectedLocation.getLatitude() < -90 ||
                selectedLocation.getLatitude() > 90
        ) {

            throw new ApiException(
                    "Selected attendance location latitude is invalid"
            );
        }

        if (
                selectedLocation.getLongitude() < -180 ||
                selectedLocation.getLongitude() > 180
        ) {

            throw new ApiException(
                    "Selected attendance location longitude is invalid"
            );
        }

        if (
                selectedLocation.getRadiusMeters() == null ||
                selectedLocation.getRadiusMeters() <= 0
        ) {

            throw new ApiException(
                    "Selected attendance location radius is invalid"
            );
        }

        // =================================================
        // ONE LIVE CLASS PER TEACHER
        // =================================================

        boolean alreadyHasLiveClass =
                !classSessionRepository
                        .findByTeacherIdAndStatusOrderByActualStartedAtDesc(
                                teacher.getId(),
                                ClassSessionStatus.LIVE
                        )
                        .isEmpty();

        if (
                alreadyHasLiveClass
        ) {

            throw new ApiException(
                    "You already have a live class. Complete it before starting another class."
            );
        }

        // =================================================
        // CREATE SESSION
        //
        // Selected attendance location is copied into the
        // session as a snapshot.
        // =================================================

        ClassSession session =
                ClassSession
                        .builder()

                        // =================================
                        // TEACHER
                        // =================================

                        .teacher(
                                teacher
                        )

                        // =================================
                        // CLASS INFO
                        // =================================

                        .subjectName(
                                request
                                        .getSubjectName()
                                        .trim()
                        )

                        .startTime(
                                request.getStartTime()
                        )

                        .endTime(
                                request.getEndTime()
                        )

                        .branch(
                                classBranch
                        )

                        .semester(
                                request.getSemester()
                        )

                        // =================================
                        // STATUS
                        // =================================

                        .status(
                                ClassSessionStatus.LIVE
                        )

                        // =================================
                        // ORIGINAL LOCATION ID
                        // =================================

                        .attendanceLocationId(
                                selectedLocation.getId()
                        )

                        // =================================
                        // LOCATION SNAPSHOT
                        // =================================

                        .attendancePlaceName(
                                selectedLocation
                                        .getPlaceName()
                                        .trim()
                        )

                        .attendanceLocationDepartment(
                                selectedLocation
                                        .getDepartment() != null
                                        ? selectedLocation
                                                .getDepartment()
                                                .trim()
                                        : null
                        )

                        .attendanceLocationBranch(
                                selectedLocation
                                        .getBranch()
                                        .trim()
                        )

                        .attendanceLatitude(
                                selectedLocation
                                        .getLatitude()
                        )

                        .attendanceLongitude(
                                selectedLocation
                                        .getLongitude()
                        )

                        .attendanceRadiusMeters(
                                selectedLocation
                                        .getRadiusMeters()
                        )

                        // =================================
                        // ACTUAL TIME
                        // =================================

                        .actualStartedAt(
                                LocalDateTime.now()
                        )

                        .actualEndedAt(
                                null
                        )

                        .build();

        // =================================================
        // SAVE SESSION
        // =================================================

        ClassSession savedSession =
                classSessionRepository
                        .save(
                                session
                        );

        return createLiveClassResponse(
                savedSession
        );
    }


    // =====================================================
    // END CLASS
    //
    // Remaining eligible students become ABSENT.
    // =====================================================

    @Transactional
    public void endClass(
            Long sessionId,
            String teacherEmail
    ) {

        // =================================================
        // SESSION ID
        // =================================================

        if (
                sessionId == null
        ) {

            throw new ApiException(
                    "Class Session ID is required"
            );
        }

        // =================================================
        // TEACHER EMAIL
        // =================================================

        if (
                teacherEmail == null ||
                teacherEmail.isBlank()
        ) {

            throw new ApiException(
                    "Authenticated teacher is required"
            );
        }

        // =================================================
        // AUTHENTICATED TEACHER
        // =================================================

        User teacher =
                userRepository
                        .findByEmail(
                                teacherEmail
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Teacher not found"
                                )
                        );

        if (
                teacher.getRole() !=
                Role.TEACHER
        ) {

            throw new ApiException(
                    "Only Teacher can end a class"
            );
        }

        // =================================================
        // FIND SESSION
        // =================================================

        ClassSession session =
                classSessionRepository
                        .findById(
                                sessionId
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Class Session not found"
                                )
                        );

        // =================================================
        // SESSION STATUS
        // =================================================

        if (
                session.getStatus() ==
                ClassSessionStatus.COMPLETED
        ) {

            throw new ApiException(
                    "Class is already completed"
            );
        }

        if (
                session.getStatus() !=
                ClassSessionStatus.LIVE
        ) {

            throw new ApiException(
                    "Only a live class can be ended"
            );
        }

        // =================================================
        // SESSION OWNERSHIP
        // =================================================

        if (
                session.getTeacher() == null ||
                session.getTeacher().getId() == null ||
                !session
                        .getTeacher()
                        .getId()
                        .equals(
                                teacher.getId()
                        )
        ) {

            throw new ApiException(
                    "You cannot end another teacher's class"
            );
        }

        // =================================================
        // CLASS DATA
        // =================================================

        if (
                session.getBranch() == null ||
                session.getBranch().isBlank()
        ) {

            throw new ApiException(
                    "Class branch is not configured"
            );
        }

        if (
                session.getSemester() == null
        ) {

            throw new ApiException(
                    "Class semester is not configured"
            );
        }

        // =================================================
        // ELIGIBLE STUDENTS
        //
        // STUDENT
        // + SAME BRANCH
        // + SAME SEMESTER
        // =================================================

        List<User> eligibleStudents =
                userRepository
                        .findByRoleAndBranchIgnoreCaseAndSemester(
                                Role.STUDENT,
                                session.getBranch(),
                                session.getSemester()
                        );

        LocalDateTime endedAt =
                LocalDateTime.now();

        // =================================================
        // AUTO ABSENT
        // =================================================

        if (
                eligibleStudents != null
        ) {

            for (
                    User student :
                    eligibleStudents
            ) {

                if (
                        student == null ||
                        student.getId() == null
                ) {
                    continue;
                }

                boolean attendanceExists =
                        attendanceRepository
                                .existsByStudentIdAndClassSessionId(
                                        student.getId(),
                                        session.getId()
                                );

                // Student already has attendance row,
                // usually PRESENT.
                if (
                        attendanceExists
                ) {
                    continue;
                }

                Attendance absentAttendance =
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
                                        AttendanceStatus.ABSENT
                                )

                                .geoVerified(
                                        false
                                )

                                .studentLatitude(
                                        null
                                )

                                .studentLongitude(
                                        null
                                )

                                .gpsAccuracy(
                                        null
                                )

                                .distanceMeters(
                                        null
                                )

                                // =========================
                                // Keep session radius
                                // for audit/history.
                                // =========================

                                .allowedRadiusMeters(
                                        session
                                                .getAttendanceRadiusMeters()
                                )

                                .attendanceTime(
                                        endedAt
                                )

                                .build();

                attendanceRepository
                        .save(
                                absentAttendance
                        );
            }
        }

        // =================================================
        // COMPLETE SESSION
        // =================================================

        session.setStatus(
                ClassSessionStatus.COMPLETED
        );

        session.setActualEndedAt(
                endedAt
        );

        classSessionRepository
                .save(
                        session
                );
    }


    // =====================================================
    // GET LIVE CLASS FOR STUDENT
    // =====================================================

    @Transactional(readOnly = true)
    public LiveClassResponse getLiveClass(
            String branch,
            Integer semester
    ) {

        if (
                branch == null ||
                branch.isBlank()
        ) {

            return null;
        }

        if (
                semester == null
        ) {

            return null;
        }

        return classSessionRepository
                .findFirstByBranchAndSemesterAndStatusOrderByActualStartedAtDesc(
                        branch.trim(),
                        semester,
                        ClassSessionStatus.LIVE
                )
                .map(
                        this::createLiveClassResponse
                )
                .orElse(
                        null
                );
    }


    // =====================================================
    // ENTITY -> LIVE CLASS RESPONSE
    // =====================================================

    private LiveClassResponse createLiveClassResponse(
            ClassSession session
    ) {

        if (
                session == null
        ) {

            return null;
        }

        String teacherName =
                "Teacher";

        if (
                session.getTeacher() != null &&
                session
                        .getTeacher()
                        .getName() != null
        ) {

            teacherName =
                    session
                            .getTeacher()
                            .getName();
        }

        return LiveClassResponse
                .builder()

                .sessionId(
                        session.getId()
                )

                .subjectName(
                        session.getSubjectName()
                )

                .teacherName(
                        teacherName
                )

                .startTime(
                        session.getStartTime()
                )

                .endTime(
                        session.getEndTime()
                )

                .build();
    }
}