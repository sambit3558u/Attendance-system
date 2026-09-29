package com.smartattendance.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.LiveClassResponse;
import com.smartattendance.backend.dto.StudentDashboardHistoryItem;
import com.smartattendance.backend.dto.StudentDashboardResponse;
import com.smartattendance.backend.dto.StudentDashboardTimetableItem;
import com.smartattendance.backend.entity.Attendance;
import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.ClassSession;
import com.smartattendance.backend.entity.ClassSessionStatus;
import com.smartattendance.backend.entity.DayOfWeek;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.Timetable;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceRepository;
import com.smartattendance.backend.repository.ClassSessionRepository;
import com.smartattendance.backend.repository.StudentNotificationRepository;
import com.smartattendance.backend.repository.TimetableRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDashboardService {

    private final UserRepository userRepository;

    private final AttendanceRepository attendanceRepository;

    private final TimetableRepository timetableRepository;

    private final ClassSessionRepository classSessionRepository;

    private final StudentNotificationRepository
            studentNotificationRepository;

    private final ClassSessionService classSessionService;

    // =====================================================
    // STUDENT DASHBOARD
    // =====================================================

    @Transactional(readOnly = true)
    public StudentDashboardResponse getDashboard(
            String email
    ) {

        // =================================================
        // VALIDATE EMAIL
        // =================================================

        if (
                email == null ||
                email.isBlank()
        ) {

            throw new ApiException(
                    "Student email is required"
            );
        }

        // =================================================
        // LOAD STUDENT
        // =================================================

        User student =
                userRepository
                        .findByEmail(
                                email
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
                    "Only students can access student dashboard"
            );
        }

        // =================================================
        // BRANCH CHECK
        // =================================================

        if (
                student.getBranch() == null ||
                student.getBranch().isBlank()
        ) {

            throw new ApiException(
                    "Student branch is not configured"
            );
        }

        // =================================================
        // SEMESTER CHECK
        // =================================================

        if (
                student.getSemester() == null
        ) {

            throw new ApiException(
                    "Student semester is not configured"
            );
        }

        // =================================================
        // CURRENT SEMESTER ATTENDANCE
        //
        // Count every attendance record for the current semester, including
        // the currently LIVE session. This keeps the summary in sync with
        // the date-wise attendance history immediately after marking.
        // =================================================

        long totalClasses = attendanceRepository.countByStudentIdAndSemester(
                student.getId(), student.getSemester());

        long presentClasses = attendanceRepository.countByStudentIdAndSemesterAndStatus(
                student.getId(), student.getSemester(), AttendanceStatus.PRESENT);

        long absentClasses = attendanceRepository.countByStudentIdAndSemesterAndStatus(
                student.getId(), student.getSemester(), AttendanceStatus.ABSENT);

        // =================================================
        // ATTENDANCE PERCENTAGE
        // =================================================

        double attendancePercentage =
                calculatePercentage(
                        presentClasses,
                        totalClasses
                );

        // =================================================
        // LIVE CLASS
        // =================================================

        LiveClassResponse liveClass =
                classSessionService
                        .getLiveClass(
                                student.getBranch(),
                                student.getSemester()
                        );

        if (liveClass != null && liveClass.getSessionId() != null) {
            attendanceRepository
                    .findByStudentIdAndClassSessionId(student.getId(), liveClass.getSessionId())
                    .ifPresent(row -> liveClass.setAttendanceStatus(
                            row.getStatus() == null ? null : row.getStatus().name()
                    ));
        }

        // =================================================
        // TODAY'S TIMETABLE
        // =================================================

        List<StudentDashboardTimetableItem>
                todayTimetable =
                buildTodayTimetable(
                        student
                );

        // =================================================
        // RECENT ATTENDANCE
        // =================================================

        List<StudentDashboardHistoryItem>
                recentAttendance =
                attendanceRepository
                        .findTop10ByStudentIdAndSemesterOrderByAttendanceTimeDesc(
                                student.getId(),
                                student.getSemester()
                        )
                        .stream()
                        .map(
                                this::toHistoryItem
                        )
                        .toList();

        // =================================================
        // UNREAD NOTIFICATIONS
        // =================================================

        long unreadNotificationCount =
                studentNotificationRepository
                        .countByStudentIdAndReadFalse(
                                student.getId()
                        );

        // =================================================
        // RESPONSE
        // =================================================

        return StudentDashboardResponse
                .builder()

                .userId(
                        student.getId()
                )

                .name(
                        student.getName()
                )

                .email(
                        student.getEmail()
                )

                .rollNumber(
                        student.getRollNumber()
                )

                .branch(
                        student.getBranch()
                )

                .semester(
                        student.getSemester()
                )

                .totalClasses(
                        totalClasses
                )

                .presentClasses(
                        presentClasses
                )

                .absentClasses(
                        absentClasses
                )

                .attendancePercentage(
                        attendancePercentage
                )

                .liveClass(
                        liveClass
                )

                .todayTimetable(
                        todayTimetable
                )

                .recentAttendance(
                        recentAttendance
                )

                .unreadNotificationCount(
                        unreadNotificationCount
                )

                .build();
    }

    // =====================================================
    // TODAY'S TIMETABLE
    // =====================================================

    private List<StudentDashboardTimetableItem>
    buildTodayTimetable(
            User student
    ) {

        LocalDate today =
                LocalDate.now();

        // =================================================
        // JAVA DAY -> PROJECT DAY ENUM
        // =================================================

        DayOfWeek currentDay =
                DayOfWeek.valueOf(
                        today
                                .getDayOfWeek()
                                .name()
                );

        // =================================================
        // STUDENT TODAY TIMETABLE
        // =================================================

        List<Timetable> timetableRows =
                timetableRepository
                        .findByBranchAndSemesterAndDayOrderByStartTimeAsc(
                                student.getBranch(),
                                student.getSemester(),
                                currentDay
                        );

        // =================================================
        // TODAY RANGE
        // =================================================

        LocalDateTime dayStart =
                today.atStartOfDay();

        LocalDateTime dayEnd =
                today
                        .plusDays(1)
                        .atStartOfDay()
                        .minusNanos(1);

        // =================================================
        // MAP RESPONSE
        // =================================================

        return timetableRows
                .stream()
                .map(
                        timetable ->
                                toTimetableItem(
                                        timetable,
                                        dayStart,
                                        dayEnd
                                )
                )
                .toList();
    }

    // =====================================================
    // TIMETABLE -> DASHBOARD ITEM
    // =====================================================

    private StudentDashboardTimetableItem
    toTimetableItem(
            Timetable timetable,
            LocalDateTime dayStart,
            LocalDateTime dayEnd
    ) {

        ClassSession session =
                classSessionRepository
                        .findFirstByTeacherIdAndSubjectNameIgnoreCaseAndBranchIgnoreCaseAndSemesterAndStartTimeAndCreatedAtBetweenOrderByCreatedAtDesc(
                                timetable
                                        .getTeacher()
                                        .getId(),

                                timetable
                                        .getSubject()
                                        .getSubjectName(),

                                timetable
                                        .getBranch(),

                                timetable
                                        .getSemester(),

                                timetable
                                        .getStartTime(),

                                dayStart,

                                dayEnd
                        )
                        .orElse(
                                null
                        );

        String status =
                "UPCOMING";

        Long sessionId =
                null;

        // =================================================
        // SESSION STATUS
        // =================================================

        if (
                session != null
        ) {

            sessionId =
                    session.getId();

            if (
                    session.getStatus() ==
                    ClassSessionStatus.LIVE
            ) {

                status =
                        "LIVE";

            } else if (
                    session.getStatus() ==
                    ClassSessionStatus.COMPLETED
            ) {

                status =
                        "COMPLETED";
            }
        }

        // =================================================
        // TEACHER NAME
        // =================================================

        String teacherName =
                "Teacher";

        if (
                timetable.getTeacher() != null &&
                timetable
                        .getTeacher()
                        .getName() != null
        ) {

            teacherName =
                    timetable
                            .getTeacher()
                            .getName();
        }

        // =================================================
        // SUBJECT INFO
        // =================================================

        String subjectName =
                "";

        String subjectCode =
                "";

        if (
                timetable.getSubject() != null
        ) {

            if (
                    timetable
                            .getSubject()
                            .getSubjectName() != null
            ) {

                subjectName =
                        timetable
                                .getSubject()
                                .getSubjectName();
            }

            if (
                    timetable
                            .getSubject()
                            .getSubjectCode() != null
            ) {

                subjectCode =
                        timetable
                                .getSubject()
                                .getSubjectCode();
            }
        }

        // =================================================
        // RESPONSE
        // =================================================

        return StudentDashboardTimetableItem
                .builder()

                .timetableId(
                        timetable.getId()
                )

                .sessionId(
                        sessionId
                )

                .subjectName(
                        subjectName
                )

                .subjectCode(
                        subjectCode
                )

                .teacherName(
                        teacherName
                )

                .branch(
                        timetable.getBranch()
                )

                .semester(
                        timetable.getSemester()
                )

                .startTime(
                        timetable.getStartTime()
                )

                .endTime(
                        timetable.getEndTime()
                )

                .status(
                        status
                )

                .build();
    }

    // =====================================================
    // ATTENDANCE -> HISTORY ITEM
    // =====================================================

    private StudentDashboardHistoryItem
    toHistoryItem(
            Attendance attendance
    ) {

        String teacherName =
                "Teacher";

        // =================================================
        // TEACHER NAME
        // =================================================

        if (
                attendance.getClassSession() != null &&
                attendance
                        .getClassSession()
                        .getTeacher() != null &&
                attendance
                        .getClassSession()
                        .getTeacher()
                        .getName() != null
        ) {

            teacherName =
                    attendance
                            .getClassSession()
                            .getTeacher()
                            .getName();
        }

        // =================================================
        // DATE + TIME
        // =================================================

        LocalDate date =
                null;

        java.time.LocalTime time =
                null;

        if (
                attendance.getAttendanceTime() != null
        ) {

            date =
                    attendance
                            .getAttendanceTime()
                            .toLocalDate();

            time =
                    attendance
                            .getAttendanceTime()
                            .toLocalTime();
        }

        // =================================================
        // SAFE STATUS
        // =================================================

        String attendanceStatus =
                attendance.getStatus() != null
                        ? attendance
                                .getStatus()
                                .name()
                        : "UNKNOWN";

        // =================================================
        // RESPONSE
        // =================================================

        return StudentDashboardHistoryItem
                .builder()

                .attendanceId(
                        attendance.getId()
                )

                .date(
                        date
                )

                .subjectName(
                        attendance.getSubject()
                )

                .teacherName(
                        teacherName
                )

                .status(
                        attendanceStatus
                )

                .time(
                        time
                )

                .build();
    }

    // =====================================================
    // ATTENDANCE %
    // =====================================================

    private double calculatePercentage(
            long present,
            long total
    ) {

        if (
                total <= 0
        ) {
            return 0.0;
        }

        double percentage =
                (
                        (double) present /
                        (double) total
                ) * 100.0;

        return Math.round(
                percentage * 100.0
        ) / 100.0;
    }
}
