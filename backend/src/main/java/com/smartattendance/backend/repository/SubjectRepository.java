package com.smartattendance.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.Subject;

/**
 * ==========================================================
 * Subject Repository
 * ----------------------------------------------------------
 * Database me Subject related operations handle karta hai.
 *
 * Used for:
 * 1. Branch + Semester ke subjects find karna
 * 2. Subject Code check karna
 * 3. Single subject find karna
 * ==========================================================
 */

@Repository
public interface SubjectRepository
        extends JpaRepository<Subject, Long> {

    /**
     * Branch aur Semester ke saare subjects return karega.
     *
     * Example:
     * Branch   = Master of Computer Applications
     * Semester = 3
     */
    List<Subject> findByBranchAndSemesterOrderBySubjectNameAsc(
            String branch,
            Integer semester
    );

    /**
     * Check karega ki same Subject Code,
     * Branch aur Semester already database me hai ya nahi.
     */
    boolean existsBySubjectCodeAndBranchAndSemester(
            String subjectCode,
            String branch,
            Integer semester
    );

    /**
     * Particular Subject Code + Branch + Semester
     * ka subject find karega.
     */
    Optional<Subject> findBySubjectCodeAndBranchAndSemester(
            String subjectCode,
            String branch,
            Integer semester
    );
}