package com.smartattendance.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.AttendanceHistoryResponse;
import com.smartattendance.backend.dto.StudentAttendanceResponse;
import com.smartattendance.backend.dto.SubjectAttendanceResponse;
import com.smartattendance.backend.entity.Attendance;
import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.Subject;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceRepository;
import com.smartattendance.backend.repository.SubjectRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Student Attendance Service
 * ----------------------------------------------------------
 * Student attendance related business logic handle karta hai.
 *
 * Main Responsibilities:
 *
 * 1. Student ko email se find karna.
 * 2. Overall attendance calculate karna.
 * 3. Branch + Semester ke saare subjects load karna.
 * 4. Subject-wise attendance calculate karna.
 * 5. Attendance history return karna.
 *
 * Important:
 *
 * Agar kisi subject ki attendance abhi 0 hai,
 * tab bhi subject Student Attendance page par show hoga.
 * ==========================================================
 */

@Service
@RequiredArgsConstructor
public class StudentAttendanceService {

    /*
     * User database operations.
     */
    private final UserRepository userRepository;

    /*
     * Attendance database operations.
     */
    private final AttendanceRepository attendanceRepository;

    /*
     * Branch + Semester ke subjects load karne ke liye.
     */
    private final SubjectRepository subjectRepository;

    /**
     * ======================================================
     * Get Student Attendance
     * ======================================================
     */
    @Transactional(readOnly = true)
    public StudentAttendanceResponse getAttendance(
            String email
    ) {

        // ==================================================
        // Find Student
        // ==================================================

        if (email == null || email.isBlank()) {
            throw new ApiException(
                    "Email is required"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        User student =
                userRepository
                        .findByEmail(normalizedEmail)
                        .orElseThrow(
                                () -> new ApiException(
                                        "Student not found"
                                )
                        );

        // ==================================================
        // Role Check
        // ==================================================

        if (student.getRole() != Role.STUDENT) {
            throw new ApiException(
                    "Only students can access attendance"
            );
        }

        // ==================================================
        // Overall Attendance
        // ==================================================

        long totalClasses =
                attendanceRepository
                        .countByStudentId(
                                student.getId()
                        );

        long presentClasses =
                attendanceRepository
                        .countByStudentIdAndStatus(
                                student.getId(),
                                AttendanceStatus.PRESENT
                        );

        long absentClasses =
                attendanceRepository
                        .countByStudentIdAndStatus(
                                student.getId(),
                                AttendanceStatus.ABSENT
                        );

        double attendancePercentage =
                calculatePercentage(
                        presentClasses,
                        totalClasses
                );

        // ==================================================
        // Get All Student Attendance Records
        // ==================================================

        List<Attendance> allAttendance =
                attendanceRepository
                        .findByStudentId(
                                student.getId()
                        );

        // ==================================================
        // Load Subjects From Subject Table
        // ==================================================
        /*
         * Example:
         *
         * Student Branch:
         * Master of Computer Applications
         *
         * Semester:
         * 3
         *
         * Database subjects:
         *
         * AIML
         * IOT
         * Python Programming
         * Data Structures & Algorithms
         *
         * Attendance 0 hone par bhi ye subjects
         * response me aayenge.
         */

        List<Subject> subjects =
                subjectRepository
                        .findByBranchAndSemesterOrderBySubjectNameAsc(
                                student.getBranch(),
                                student.getSemester()
                        );

        // ==================================================
        // Subject-wise Attendance
        // ==================================================

        List<SubjectAttendanceResponse>
                subjectAttendance =
                buildSubjectAttendance(
                        subjects,
                        allAttendance
                );

        // ==================================================
        // Attendance History
        // ==================================================

        List<Attendance> recentAttendance =
                attendanceRepository
                        .findTop10ByStudentIdOrderByAttendanceTimeDesc(
                                student.getId()
                        );

        List<AttendanceHistoryResponse>
                attendanceHistory =
                recentAttendance
                        .stream()
                        .map(
                                attendance ->
                                        AttendanceHistoryResponse
                                                .builder()
                                                .attendanceId(
                                                        attendance.getId()
                                                )
                                                .subjectName(
                                                        attendance.getSubject()
                                                )
                                                .status(
                                                        attendance.getStatus()
                                                )
                                                .attendanceTime(
                                                        attendance.getAttendanceTime()
                                                )
                                                .build()
                        )
                        .toList();

        // ==================================================
        // Return Response
        // ==================================================

        return StudentAttendanceResponse
                .builder()
                .userId(
                        student.getId()
                )
                .name(
                        student.getName()
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
                .subjectAttendance(
                        subjectAttendance
                )
                .attendanceHistory(
                        attendanceHistory
                )
                .build();
    }

    /**
     * ======================================================
     * Build Subject-wise Attendance
     * ------------------------------------------------------
     * Subjects table ko base maan kar attendance calculate.
     *
     * Isliye attendance record na hone par bhi:
     *
     * Total   = 0
     * Present = 0
     * Absent  = 0
     * %       = 0.0
     * ======================================================
     */
    private List<SubjectAttendanceResponse>
            buildSubjectAttendance(
                    List<Subject> subjects,
                    List<Attendance> attendanceList
            ) {

        List<SubjectAttendanceResponse>
                responseList =
                new ArrayList<>();

        /*
         * Subjects table ke har subject par loop.
         */
        for (Subject subject : subjects) {

            String subjectName =
                    subject.getSubjectName();

            /*
             * Current subject ke total attendance records.
             */
            long total =
                    attendanceList
                            .stream()
                            .filter(
                                    attendance ->
                                            subjectName.equalsIgnoreCase(
                                                    attendance.getSubject()
                                            )
                            )
                            .count();

            /*
             * Current subject PRESENT count.
             */
            long present =
                    attendanceList
                            .stream()
                            .filter(
                                    attendance ->
                                            subjectName.equalsIgnoreCase(
                                                    attendance.getSubject()
                                            )
                            )
                            .filter(
                                    attendance ->
                                            attendance.getStatus()
                                                    == AttendanceStatus.PRESENT
                            )
                            .count();

            /*
             * Current subject ABSENT count.
             */
            long absent =
                    attendanceList
                            .stream()
                            .filter(
                                    attendance ->
                                            subjectName.equalsIgnoreCase(
                                                    attendance.getSubject()
                                            )
                            )
                            .filter(
                                    attendance ->
                                            attendance.getStatus()
                                                    == AttendanceStatus.ABSENT
                            )
                            .count();

            /*
             * Subject Attendance Percentage.
             */
            double percentage =
                    calculatePercentage(
                            present,
                            total
                    );

            /*
             * Even if:
             *
             * total = 0
             *
             * subject response create hoga.
             */
            responseList.add(
                    SubjectAttendanceResponse
                            .builder()
                            .subjectName(
                                    subjectName
                            )
                            .totalClasses(
                                    total
                            )
                            .presentClasses(
                                    present
                            )
                            .absentClasses(
                                    absent
                            )
                            .percentage(
                                    percentage
                            )
                            .build()
            );
        }

        return responseList;
    }

    /**
     * ======================================================
     * Calculate Percentage
     * ======================================================
     */
    private double calculatePercentage(
            long present,
            long total
    ) {

        /*
         * Divide by zero avoid karo.
         */
        if (total == 0) {
            return 0.0;
        }

        double percentage =
                (present * 100.0)
                        / total;

        /*
         * 2 decimal places.
         */
        return Math.round(
                percentage * 100.0
        ) / 100.0;
    }
}