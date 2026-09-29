package com.smartattendance.backend.service;

import java.util.List;

import com.smartattendance.backend.dto.AttendanceLocationResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.StartScheduledClassResponse;
import com.smartattendance.backend.entity.AttendanceLocation;
import com.smartattendance.backend.entity.ClassSession;
import com.smartattendance.backend.entity.ClassSessionStatus;
import com.smartattendance.backend.entity.DayOfWeek;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.Timetable;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceLocationRepository;
import com.smartattendance.backend.repository.ClassSessionRepository;
import com.smartattendance.backend.repository.TimetableRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherScheduledClassService {

    private final TimetableRepository timetableRepository;

    private final ClassSessionRepository classSessionRepository;

    private final AttendanceLocationRepository
            attendanceLocationRepository;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a");

// =====================================================
// GET AVAILABLE ATTENDANCE LOCATIONS
//
// Used when teacher clicks Start Attendance.
//
// Only locations matching:
// 1. scheduled class branch
// 2. teacher department
//
// are returned.
// =====================================================

@Transactional(readOnly = true)
public List<AttendanceLocationResponse>
getAvailableLocations(
        User teacher,
        Long timetableId
) {

    // =================================================
    // TEACHER CHECK
    // =================================================

    if (
            teacher == null ||
            teacher.getId() == null
    ) {

        throw new ApiException(
                "Teacher authentication required"
        );
    }

    if (
            teacher.getRole() != Role.TEACHER
    ) {

        throw new ApiException(
                "Only teachers can view attendance locations"
        );
    }

    // =================================================
    // TIMETABLE ID
    // =================================================

    if (
            timetableId == null
    ) {

        throw new ApiException(
                "Timetable ID is required"
        );
    }

    // =================================================
    // FIND TIMETABLE
    // =================================================

    Timetable timetable =
            timetableRepository
                    .findById(
                            timetableId
                    )
                    .orElseThrow(
                            () -> new ApiException(
                                    "Scheduled class not found"
                            )
                    );

    // =================================================
    // VERIFY TIMETABLE BELONGS TO TEACHER
    // =================================================

    if (
            timetable.getTeacher() == null ||
            timetable.getTeacher().getId() == null ||
            !timetable
                    .getTeacher()
                    .getId()
                    .equals(
                            teacher.getId()
                    )
    ) {

        throw new ApiException(
                "This class is not assigned to you"
        );
    }

    // =================================================
    // BRANCH
    // =================================================

    if (
            timetable.getBranch() == null ||
            timetable.getBranch().isBlank()
    ) {

        throw new ApiException(
                "Branch is missing from timetable"
        );
    }

    String branch =
            timetable
                    .getBranch()
                    .trim();

    // =================================================
    // DEPARTMENT
    // =================================================

    if (
            teacher.getDepartment() == null ||
            teacher.getDepartment().isBlank()
    ) {

        throw new ApiException(
                "Teacher department is not configured"
        );
    }

    String department =
            teacher
                    .getDepartment()
                    .trim();

    // =================================================
    // FIND LOCATIONS
    //
    // Same teacher department + timetable branch.
    // =================================================

    List<AttendanceLocation> locations =
            attendanceLocationRepository
                    .findByDepartmentIgnoreCaseAndBranchIgnoreCaseOrderByPlaceNameAsc(
                            department,
                            branch
                    );

    // =================================================
    // CONVERT TO RESPONSE
    // =================================================

    return locations
            .stream()
            .map(
                    location ->
                            AttendanceLocationResponse
                                    .builder()

                                    .id(
                                            location.getId()
                                    )

                                    .placeName(
                                            location.getPlaceName()
                                    )

                                    .department(
                                            location.getDepartment()
                                    )

                                    .branch(
                                            location.getBranch()
                                    )

                                    .latitude(
                                            location.getLatitude()
                                    )

                                    .longitude(
                                            location.getLongitude()
                                    )

                                    .radiusMeters(
                                            location.getRadiusMeters()
                                    )

                                    .build()
            )
            .toList();
}
    // =====================================================
    // START SCHEDULED CLASS
    // =====================================================

    @Transactional

    public StartScheduledClassResponse startClass(
            User teacher,
            Long timetableId,
            Long attendanceLocationId
    ) {

        // =================================================
        // AUTH CHECK
        // =================================================

        if (
                teacher == null ||
                teacher.getId() == null
        ) {

            throw new ApiException(
                    "Teacher authentication required"
            );
        }

        if (
                teacher.getRole() != Role.TEACHER
        ) {

            throw new ApiException(
                    "Only teachers can start attendance"
            );
        }


        // =================================================
        // TIMETABLE ID
        // =================================================

        if (
                timetableId == null
        ) {

            throw new ApiException(
                    "Timetable ID is required"
            );
        }


        // =================================================
        // LOCATION ID
        // =================================================

        if (
                attendanceLocationId == null
        ) {

            throw new ApiException(
                    "Please select an attendance location"
            );
        }


        // =================================================
        // FIND TIMETABLE
        // =================================================

        Timetable timetable =
                timetableRepository
                        .findById(
                                timetableId
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Scheduled class not found"
                                )
                        );


        // =================================================
        // VERIFY ASSIGNED TEACHER
        // =================================================

        if (
                timetable.getTeacher() == null ||
                timetable.getTeacher().getId() == null ||
                !timetable
                        .getTeacher()
                        .getId()
                        .equals(
                                teacher.getId()
                        )
        ) {

            throw new ApiException(
                    "This class is not assigned to you"
            );
        }


        // =================================================
        // VERIFY CLASS IS TODAY
        // =================================================

        DayOfWeek today =
                DayOfWeek.valueOf(
                        LocalDate
                                .now()
                                .getDayOfWeek()
                                .name()
                );

        if (
                timetable.getDay() != today
        ) {

            throw new ApiException(
                    "This class is not scheduled for today"
            );
        }


        // =================================================
        // SUBJECT
        // =================================================

        if (
                timetable.getSubject() == null
        ) {

            throw new ApiException(
                    "Subject is not assigned to this timetable"
            );
        }

        String subjectName =
                timetable
                        .getSubject()
                        .getSubjectName();

        if (
                subjectName == null ||
                subjectName.isBlank()
        ) {

            throw new ApiException(
                    "Subject name is missing"
            );
        }

        subjectName =
                subjectName.trim();


        // =================================================
        // CLASS DETAILS
        // =================================================

        if (
                timetable.getBranch() == null ||
                timetable.getBranch().isBlank()
        ) {

            throw new ApiException(
                    "Branch is missing from timetable"
            );
        }

        String classBranch =
                timetable
                        .getBranch()
                        .trim();

        if (
                timetable.getSemester() == null
        ) {

            throw new ApiException(
                    "Semester is missing from timetable"
            );
        }

        if (
                timetable.getStartTime() == null
        ) {

            throw new ApiException(
                    "Class start time is missing"
            );
        }

        if (
                timetable.getEndTime() == null
        ) {

            throw new ApiException(
                    "Class end time is missing"
            );
        }


        // =================================================
        // FIND SELECTED ATTENDANCE LOCATION
        // =================================================

        AttendanceLocation selectedLocation =
                attendanceLocationRepository
                        .findById(
                                attendanceLocationId
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Selected attendance location not found"
                                )
                        );


        // =================================================
        // LOCATION BRANCH MUST MATCH CLASS BRANCH
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
        // OPTIONAL BUT IMPORTANT:
        // DEPARTMENT CHECK
        //
        // If teacher has department configured, selected
        // student attendance location must belong to the
        // same department.
        // =================================================

        if (
                teacher.getDepartment() != null &&
                !teacher.getDepartment().isBlank()
        ) {

            if (
                    selectedLocation.getDepartment() == null ||
                    !selectedLocation
                            .getDepartment()
                            .trim()
                            .equalsIgnoreCase(
                                    teacher
                                            .getDepartment()
                                            .trim()
                            )
            ) {

                throw new ApiException(
                        "Selected attendance location does not belong to your department"
                );
            }
        }


        // =================================================
        // LOCATION DATA VALIDATION
        // =================================================

        if (
                selectedLocation.getPlaceName() == null ||
                selectedLocation.getPlaceName().isBlank()
        ) {

            throw new ApiException(
                    "Selected attendance place name is missing"
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
        // TODAY DATE RANGE
        // =================================================

        LocalDate currentDate =
                LocalDate.now();

        LocalDateTime dayStart =
                currentDate.atStartOfDay();

        LocalDateTime dayEnd =
                currentDate.atTime(
                        LocalTime.MAX
                );


        // =================================================
        // SESSION ALREADY EXISTS TODAY?
        // =================================================

        Optional<ClassSession> existingSession =
                classSessionRepository
                        .findFirstByTeacherIdAndSubjectNameIgnoreCaseAndBranchIgnoreCaseAndSemesterAndStartTimeAndCreatedAtBetweenOrderByCreatedAtDesc(
                                teacher.getId(),
                                subjectName,
                                classBranch,
                                timetable.getSemester(),
                                timetable.getStartTime(),
                                dayStart,
                                dayEnd
                        );


        // =================================================
        // EXISTING SESSION
        // =================================================

        if (
                existingSession.isPresent()
        ) {

            ClassSession session =
                    existingSession.get();

            String message;

            if (
                    session.getStatus() ==
                    ClassSessionStatus.LIVE
            ) {

                message =
                        "Attendance is already running for this class.";

            } else {

                message =
                        "Attendance for this class has already been completed.";
            }

            return buildResponse(
                    timetable,
                    session,
                    message
            );
        }


        // =================================================
        // DON'T ALLOW MULTIPLE LIVE CLASSES
        // =================================================

        boolean anotherLiveSession =
                !classSessionRepository
                        .findByTeacherIdAndStatusOrderByActualStartedAtDesc(
                                teacher.getId(),
                                ClassSessionStatus.LIVE
                        )
                        .isEmpty();

        if (
                anotherLiveSession
        ) {

            throw new ApiException(
                    "You already have another live attendance session. Complete it before starting a new class."
            );
        }


        // =================================================
        // CREATE LIVE SESSION
        //
        // IMPORTANT:
        // Selected location is SNAPSHOTTED here.
        // =================================================

        ClassSession session =
                ClassSession
                        .builder()

                        .teacher(
                                teacher
                        )

                        .subjectName(
                                subjectName
                        )

                        .startTime(
                                timetable.getStartTime()
                        )

                        .endTime(
                                timetable.getEndTime()
                        )

                        .branch(
                                classBranch
                        )

                        .semester(
                                timetable.getSemester()
                        )

                        .status(
                                ClassSessionStatus.LIVE
                        )

                        // =================================
                        // LOCATION ID
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

                        .actualStartedAt(
                                LocalDateTime.now()
                        )

                        .actualEndedAt(
                                null
                        )

                        .build();


        ClassSession saved =
                classSessionRepository
                        .save(
                                session
                        );


        // =================================================
        // RESPONSE
        // =================================================

        return buildResponse(
                timetable,
                saved,
                "Attendance started successfully at "
                        + selectedLocation
                                .getPlaceName()
                                .trim()
                        + "."
        );
    }


    // =====================================================
    // BUILD RESPONSE
    // =====================================================

    private StartScheduledClassResponse buildResponse(
            Timetable timetable,
            ClassSession session,
            String message
    ) {

        String formattedTime =
                "-";

        if (
                session.getStartTime() != null &&
                session.getEndTime() != null
        ) {

            formattedTime =
                    session
                            .getStartTime()
                            .format(
                                    TIME_FORMATTER
                            )
                            +
                            " - "
                            +
                            session
                                    .getEndTime()
                                    .format(
                                            TIME_FORMATTER
                                    );
        }


        return StartScheduledClassResponse
                .builder()

                .sessionId(
                        session.getId()
                )

                .timetableId(
                        timetable.getId()
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

                .time(
                        formattedTime
                )

                .status(
                        session.getStatus() != null
                                ? session
                                        .getStatus()
                                        .name()
                                : "UNKNOWN"
                )

                .message(
                        message
                )

                .build();
    }
}