package com.smartattendance.backend.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.ClassSession;
import com.smartattendance.backend.entity.ClassSessionStatus;

@Repository
public interface ClassSessionRepository
        extends JpaRepository<ClassSession, Long> {

    // =====================================================
    // TEACHER LIVE CLASSES
    // =====================================================

    List<ClassSession>
    findByTeacherIdAndStatusOrderByActualStartedAtDesc(
            Long teacherId,
            ClassSessionStatus status
    );


    // =====================================================
    // STUDENT LIVE CLASS
    // =====================================================

    Optional<ClassSession>
    findFirstByBranchAndSemesterAndStatusOrderByActualStartedAtDesc(
            String branch,
            Integer semester,
            ClassSessionStatus status
    );


    // =====================================================
    // TEACHER MONTHLY / DATE RANGE SESSIONS
    // =====================================================

    List<ClassSession>
    findByTeacherIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long teacherId,
            LocalDateTime start,
            LocalDateTime end
    );


    // =====================================================
    // FIND TODAY'S SESSION FOR A TIMETABLE CLASS
    // =====================================================

    Optional<ClassSession>
    findFirstByTeacherIdAndSubjectNameIgnoreCaseAndBranchIgnoreCaseAndSemesterAndStartTimeAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long teacherId,
            String subjectName,
            String branch,
            Integer semester,
            LocalTime startTime,
            LocalDateTime dayStart,
            LocalDateTime dayEnd
    );


    // =====================================================
    // OPTIONAL OLD METHOD
    //
    // Keeping this prevents older code from breaking.
    // =====================================================

    boolean existsByTeacherIdAndStatus(
            Long teacherId,
            ClassSessionStatus status
    );
}