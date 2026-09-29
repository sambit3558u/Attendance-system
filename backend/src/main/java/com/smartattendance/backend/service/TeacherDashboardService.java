package com.smartattendance.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.TeacherDashboardResponse;
import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.ClassSession;
import com.smartattendance.backend.entity.ClassSessionStatus;
import com.smartattendance.backend.entity.DayOfWeek;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.TeacherAttendance;
import com.smartattendance.backend.entity.Timetable;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceRepository;
import com.smartattendance.backend.repository.ClassSessionRepository;
import com.smartattendance.backend.repository.TeacherAttendanceRepository;
import com.smartattendance.backend.repository.TimetableRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private final UserRepository userRepository;

    private final TimetableRepository timetableRepository;

    private final ClassSessionRepository classSessionRepository;

    private final TeacherAttendanceRepository teacherAttendanceRepository;

    // =====================================================
    // NEW:
    // STUDENT ATTENDANCE REPOSITORY
    // =====================================================

    private final AttendanceRepository attendanceRepository;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a");


    // =====================================================
    // GET TEACHER DASHBOARD
    // =====================================================

    @Transactional(readOnly = true)
    public TeacherDashboardResponse getDashboard(
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
                    "Teacher email is required"
            );
        }


        // =================================================
        // FIND TEACHER
        // =================================================

        User teacher =
                userRepository
                        .findByEmail(
                                email
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
                    "Only teachers can access this dashboard"
            );
        }


        // =================================================
        // TEACHER INFO
        // =================================================

        TeacherDashboardResponse.TeacherInfo teacherInfo =
                TeacherDashboardResponse
                        .TeacherInfo
                        .builder()

                        .name(
                                teacher.getName()
                        )

                        .department(
                                teacher.getDepartment()
                        )

                        .build();


        // =================================================
        // CURRENT DATE
        // =================================================

        LocalDate today =
                LocalDate.now();

        LocalDateTime todayStart =
                today.atStartOfDay();

        LocalDateTime todayEnd =
                today.atTime(
                        LocalTime.MAX
                );


        // =================================================
        // CURRENT DAY
        // =================================================

        DayOfWeek projectDay;

        try {

            projectDay =
                    DayOfWeek.valueOf(
                            today
                                    .getDayOfWeek()
                                    .name()
                    );

        } catch (
                IllegalArgumentException exception
        ) {

            throw new ApiException(
                    "Unable to determine today's timetable day"
            );
        }


        // =================================================
        // TEACHER'S COMPLETE WEEKLY TIMETABLE
        // =================================================

        List<Timetable> teacherTimetable =
                timetableRepository
                        .findByTeacherIdOrderByDayAscStartTimeAsc(
                                teacher.getId()
                        );


        if (teacherTimetable == null) {
            teacherTimetable =
                    new ArrayList<>();
        }


        // =================================================
        // TODAY'S TIMETABLE
        // =================================================

        List<Timetable> todayTimetable =
                timetableRepository
                        .findByTeacherIdAndDayOrderByStartTimeAsc(
                                teacher.getId(),
                                projectDay
                        );


        if (todayTimetable == null) {
            todayTimetable =
                    new ArrayList<>();
        }


        // =================================================
        // CLASSES TODAY
        // =================================================

        long classesToday =
                todayTimetable.size();


        // =================================================
        // ACADEMIC YEAR
        // JULY -> JUNE
        // =================================================

        LocalDate academicYearStart;

        LocalDate academicYearEnd;


        if (
                today.getMonthValue() >= 7
        ) {

            academicYearStart =
                    LocalDate.of(
                            today.getYear(),
                            7,
                            1
                    );


            academicYearEnd =
                    LocalDate.of(
                            today.getYear() + 1,
                            6,
                            30
                    );

        } else {

            academicYearStart =
                    LocalDate.of(
                            today.getYear() - 1,
                            7,
                            1
                    );


            academicYearEnd =
                    LocalDate.of(
                            today.getYear(),
                            6,
                            30
                    );
        }


        // =================================================
        // TOTAL CLASSES
        // =================================================

        long totalClasses =
                calculateAcademicYearClasses(
                        teacherTimetable,
                        academicYearStart,
                        academicYearEnd
                );


        // =================================================
        // TODAY'S SCHEDULE
        // =================================================

        List<TeacherDashboardResponse.ScheduleItem>
                todaySchedule =
                new ArrayList<>();


        for (
                Timetable timetable :
                todayTimetable
        ) {

            if (timetable == null) {
                continue;
            }


            if (
                    timetable.getSubject() ==
                    null
            ) {
                continue;
            }


            String subjectName =
                    timetable
                            .getSubject()
                            .getSubjectName();


            if (
                    subjectName == null ||
                    subjectName.isBlank()
            ) {

                subjectName =
                        "Unknown Subject";

            } else {

                subjectName =
                        subjectName.trim();
            }


            // =============================================
            // FIND TODAY'S CLASS SESSION
            // =============================================

            Optional<ClassSession>
                    existingSession =
                    classSessionRepository
                            .findFirstByTeacherIdAndSubjectNameIgnoreCaseAndBranchIgnoreCaseAndSemesterAndStartTimeAndCreatedAtBetweenOrderByCreatedAtDesc(
                                    teacher.getId(),
                                    subjectName,
                                    timetable.getBranch(),
                                    timetable.getSemester(),
                                    timetable.getStartTime(),
                                    todayStart,
                                    todayEnd
                            );


            String status =
                    "SCHEDULED";

            Long sessionId =
                    null;


            if (
                    existingSession != null &&
                    existingSession.isPresent()
            ) {

                ClassSession session =
                        existingSession.get();


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

                } else {

                    status =
                            session.getStatus() != null
                                    ? session
                                            .getStatus()
                                            .name()
                                    : "SCHEDULED";
                }
            }


            String formattedTime =
                    formatTimeRange(
                            timetable.getStartTime(),
                            timetable.getEndTime()
                    );


            todaySchedule.add(
                    TeacherDashboardResponse
                            .ScheduleItem
                            .builder()

                            .id(
                                    timetable.getId()
                            )

                            .sessionId(
                                    sessionId
                            )

                            .time(
                                    formattedTime
                            )

                            .subject(
                                    subjectName
                            )

                            .branch(
                                    timetable.getBranch()
                            )

                            .semester(
                                    timetable.getSemester()
                            )

                            .status(
                                    status
                            )

                            .build()
            );
        }


        // =================================================
        // TEACHER OWN ATTENDANCE
        // CURRENT MONTH
        // =================================================

        YearMonth currentMonth =
                YearMonth.from(
                        today
                );


        LocalDate monthStart =
                currentMonth
                        .atDay(1);


        LocalDate monthEnd =
                currentMonth
                        .atEndOfMonth();


        // =================================================
        // PRESENT COUNT
        // =================================================

        long presentCount =
                teacherAttendanceRepository
                        .countByTeacherIdAndAttendanceDateBetweenAndStatus(
                                teacher.getId(),
                                monthStart,
                                monthEnd,
                                AttendanceStatus.PRESENT
                        );


        // =================================================
        // ABSENT COUNT
        // =================================================

        long absentCount =
                teacherAttendanceRepository
                        .countByTeacherIdAndAttendanceDateBetweenAndStatus(
                                teacher.getId(),
                                monthStart,
                                monthEnd,
                                AttendanceStatus.ABSENT
                        );


        long totalMarkedDays =
                presentCount +
                absentCount;


        double presentPercentage =
                0.0;

        double absentPercentage =
                0.0;


        if (
                totalMarkedDays > 0
        ) {

            presentPercentage =
                    (
                            (double) presentCount /
                            totalMarkedDays
                    ) * 100.0;


            absentPercentage =
                    (
                            (double) absentCount /
                            totalMarkedDays
                    ) * 100.0;
        }


        presentPercentage =
                round(
                        presentPercentage
                );


        absentPercentage =
                round(
                        absentPercentage
                );


        // =================================================
        // ATTENDANCE OVERVIEW
        // =================================================

        TeacherDashboardResponse.AttendanceOverview
                attendanceOverview =
                TeacherDashboardResponse
                        .AttendanceOverview
                        .builder()

                        .presentPercentage(
                                presentPercentage
                        )

                        .absentPercentage(
                                absentPercentage
                        )

                        .presentCount(
                                presentCount
                        )

                        .absentCount(
                                absentCount
                        )

                        .totalMarkedDays(
                                totalMarkedDays
                        )

                        .build();


        // =================================================
        // TODAY'S TEACHER ATTENDANCE
        // =================================================

        TeacherAttendance todayAttendance =
                teacherAttendanceRepository
                        .findByTeacherIdAndAttendanceDate(
                                teacher.getId(),
                                today
                        )
                        .orElse(null);


        String teacherAttendanceStatus =
                "NOT_MARKED";


        Double teacherAttendanceDistance =
                null;


        if (
                todayAttendance != null
        ) {

            if (
                    todayAttendance.getStatus() !=
                    null
            ) {

                teacherAttendanceStatus =
                        todayAttendance
                                .getStatus()
                                .name();
            }


            teacherAttendanceDistance =
                    todayAttendance
                            .getDistanceMeters();


            if (
                    teacherAttendanceDistance !=
                    null
            ) {

                teacherAttendanceDistance =
                        round(
                                teacherAttendanceDistance
                        );
            }
        }


        // =================================================
        // CURRENT MONTH CLASS SESSION RANGE
        // =================================================

        LocalDateTime monthDateTimeStart =
                monthStart
                        .atStartOfDay();


        LocalDateTime monthDateTimeEnd =
                monthEnd
                        .atTime(
                                LocalTime.MAX
                        );


        // =================================================
        // TEACHER MONTHLY CLASS SESSIONS
        // =================================================

        List<ClassSession> monthSessions =
                classSessionRepository
                        .findByTeacherIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                                teacher.getId(),
                                monthDateTimeStart,
                                monthDateTimeEnd
                        );


        if (monthSessions == null) {
            monthSessions =
                    new ArrayList<>();
        }


        // =================================================
        // MY CLASSES
        // =================================================

        List<TeacherDashboardResponse.MyClassItem>
                myClasses =
                new ArrayList<>();


        // =================================================
        // RECENT CLASSES
        // =================================================

        List<TeacherDashboardResponse.RecentClassItem>
                recentClasses =
                new ArrayList<>();


        // =================================================
        // BUILD COMPLETED CLASSES
        // =================================================

        for (
                ClassSession session :
                monthSessions
        ) {

            if (session == null) {
                continue;
            }


            if (
                    session.getStatus() !=
                    ClassSessionStatus.COMPLETED
            ) {
                continue;
            }


            // =============================================
            // BRANCH + SEMESTER
            // =============================================

            String branch =
                    session.getBranch() != null
                            ? session.getBranch()
                            : "-";


            String semester =
                    session.getSemester() != null
                            ? String.valueOf(
                                    session.getSemester()
                            )
                            : "-";


            String course =
                    branch +
                    " • Semester " +
                    semester;


            // =============================================
            // CLASS TIME
            // =============================================

            String schedule =
                    formatTimeRange(
                            session.getStartTime(),
                            session.getEndTime()
                    );


            // =============================================
            // SUBJECT
            // =============================================

            String sessionSubject =
                    session.getSubjectName();


            if (
                    sessionSubject == null ||
                    sessionSubject.isBlank()
            ) {

                sessionSubject =
                        "Unknown Subject";
            }


            // =============================================
            // REAL PRESENT STUDENT COUNT
            //
            // IMPORTANT:
            // Only PRESENT students are counted.
            // ABSENT students are NOT included.
            // =============================================

            long presentStudents =
                    attendanceRepository
                            .countByClassSession_IdAndStatus(
                                    session.getId(),
                                    AttendanceStatus.PRESENT
                            );


            // =============================================
            // MY CLASSES
            // =============================================

            myClasses.add(
                    TeacherDashboardResponse
                            .MyClassItem
                            .builder()

                            .id(
                                    session.getId()
                            )

                            .className(
                                    sessionSubject
                            )

                            .course(
                                    course
                            )

                            // =================================
                            // PRESENT STUDENTS ONLY
                            // =================================

                            .studentCount(
                                    presentStudents
                            )

                            .schedule(
                                    schedule
                            )

                            .build()
            );


            // =============================================
            // RECENT CLASSES
            // =============================================

            recentClasses.add(
                    TeacherDashboardResponse
                            .RecentClassItem
                            .builder()

                            .id(
                                    session.getId()
                            )

                            .subject(
                                    sessionSubject
                            )

                            .course(
                                    course
                            )

                            .time(
                                    schedule
                            )

                            .status(
                                    "COMPLETED"
                            )

                            .build()
            );
        }


        // =================================================
        // RETURN DASHBOARD
        // =================================================

        return TeacherDashboardResponse
                .builder()

                .teacher(
                        teacherInfo
                )

                .totalStudents(
                        0
                )

                .totalClasses(
                        totalClasses
                )

                .classesToday(
                        classesToday
                )

                .averageAttendance(
                        presentPercentage
                )

                .teacherAttendanceStatus(
                        teacherAttendanceStatus
                )

                .teacherAttendanceDistance(
                        teacherAttendanceDistance
                )

                .todaySchedule(
                        todaySchedule
                )

                .attendanceOverview(
                        attendanceOverview
                )

                .recentClasses(
                        recentClasses
                )

                .myClasses(
                        myClasses
                )

                .topAttendance(
                        new ArrayList<>()
                )

                .build();
    }


    // =====================================================
    // CALCULATE FULL ACADEMIC YEAR CLASSES
    // =====================================================

    private long calculateAcademicYearClasses(
            List<Timetable> timetableList,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (
                timetableList == null ||
                timetableList.isEmpty() ||
                startDate == null ||
                endDate == null
        ) {
            return 0;
        }


        long count =
                0;


        for (
                Timetable timetable :
                timetableList
        ) {

            if (
                    timetable == null ||
                    timetable.getDay() == null
            ) {
                continue;
            }


            java.time.DayOfWeek javaDay;


            try {

                javaDay =
                        java.time.DayOfWeek
                                .valueOf(
                                        timetable
                                                .getDay()
                                                .name()
                                );

            } catch (
                    IllegalArgumentException exception
            ) {

                continue;
            }


            LocalDate firstMatchingDate =
                    startDate.with(
                            TemporalAdjusters
                                    .nextOrSame(
                                            javaDay
                                    )
                    );


            if (
                    firstMatchingDate.isAfter(
                            endDate
                    )
            ) {
                continue;
            }


            long days =
                    ChronoUnit.DAYS
                            .between(
                                    firstMatchingDate,
                                    endDate
                            );


            long occurrences =
                    (days / 7) + 1;


            count +=
                    occurrences;
        }


        return count;
    }


    // =====================================================
    // FORMAT CLASS TIME
    // =====================================================

    private String formatTimeRange(
            LocalTime startTime,
            LocalTime endTime
    ) {

        if (
                startTime == null ||
                endTime == null
        ) {
            return "-";
        }


        return startTime
                .format(
                        TIME_FORMATTER
                )
                +
                " - "
                +
                endTime
                        .format(
                                TIME_FORMATTER
                        );
    }


    // =====================================================
    // ROUND TO 1 DECIMAL
    // =====================================================

    private double round(
            double value
    ) {

        return Math.round(
                value * 10.0
        ) / 10.0;
    }
}