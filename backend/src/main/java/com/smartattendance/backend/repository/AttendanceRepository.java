package com.smartattendance.backend.repository;

import com.smartattendance.backend.entity.Attendance;
import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.ClassSessionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    // =====================================================
    // STUDENT ATTENDANCE COUNTS
    // =====================================================

    long countByStudentId(
            Long studentId
    );

    long countByStudentIdAndStatus(
            Long studentId,
            AttendanceStatus status
    );

    long countByStudentIdAndSemester(Long studentId, Integer semester);

    long countByStudentIdAndSemesterAndStatus(
            Long studentId,
            Integer semester,
            AttendanceStatus status
    );

    // =====================================================
    // STUDENT ATTENDANCE LIST
    // =====================================================

    List<Attendance> findByStudentId(
            Long studentId
    );

    List<Attendance> findByStudentIdOrderByAttendanceTimeDesc(
            Long studentId
    );

    List<Attendance> findTop10ByStudentIdOrderByAttendanceTimeDesc(
            Long studentId
    );

    // =====================================================
    // CLASS SESSION ATTENDANCE
    // =====================================================

    boolean existsByStudentIdAndClassSessionId(
            Long studentId,
            Long classSessionId
    );

    Optional<Attendance> findByStudentIdAndClassSessionId(
            Long studentId,
            Long classSessionId
    );

    List<Attendance> findByClassSessionId(
            Long classSessionId
    );

    // =====================================================
    // PRESENT STUDENTS IN A CLASS SESSION
    // =====================================================

    long countByClassSession_IdAndStatus(
            Long classSessionId,
            AttendanceStatus status
    );

    // =====================================================
    // CURRENT SEMESTER ATTENDANCE
    // =====================================================

    List<Attendance> findByStudentIdAndSemesterOrderByAttendanceTimeDesc(
            Long studentId,
            Integer semester
    );

    List<Attendance> findTop10ByStudentIdAndSemesterOrderByAttendanceTimeDesc(
            Long studentId,
            Integer semester
    );

    // =====================================================
    // COMPLETED CLASS COUNTS
    // classSession.status is a nested property
    // =====================================================

    long countByStudentIdAndSemesterAndClassSession_Status(
            Long studentId,
            Integer semester,
            ClassSessionStatus classSessionStatus
    );

    long countByStudentIdAndSemesterAndStatusAndClassSession_Status(
            Long studentId,
            Integer semester,
            AttendanceStatus status,
            ClassSessionStatus classSessionStatus
    );
}
