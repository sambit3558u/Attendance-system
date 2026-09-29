package com.smartattendance.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.TeacherAttendance;

public interface TeacherAttendanceRepository
        extends JpaRepository<TeacherAttendance, Long> {

    Optional<TeacherAttendance>
    findByTeacherIdAndAttendanceDate(
            Long teacherId,
            LocalDate attendanceDate
    );

    boolean existsByTeacherIdAndAttendanceDate(
            Long teacherId,
            LocalDate attendanceDate
    );

    List<TeacherAttendance>
    findByTeacherIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long teacherId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByTeacherIdAndAttendanceDateBetweenAndStatus(
            Long teacherId,
            LocalDate startDate,
            LocalDate endDate,
            AttendanceStatus status
    );
}