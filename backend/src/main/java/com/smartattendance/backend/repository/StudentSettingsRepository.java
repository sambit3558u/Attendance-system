package com.smartattendance.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.StudentSettings;

/**
 * ==========================================================
 * Student Settings Repository
 * ----------------------------------------------------------
 * student_settings table ke database operations handle
 * karta hai.
 *
 * Main operations:
 *
 * - Student ki settings find karna
 * - Settings create/save karna
 * - Settings update karna
 * - Student ke settings record ka existence check karna
 * ==========================================================
 */

@Repository
public interface StudentSettingsRepository
        extends JpaRepository<StudentSettings, Long> {

    /**
     * Student ID ke according settings find karega.
     *
     * Example:
     * studentId = 5
     */
    Optional<StudentSettings> findByStudentId(
            Long studentId
    );

    /**
     * Check karega ki student ka settings record
     * already exist karta hai ya nahi.
     */
    boolean existsByStudentId(
            Long studentId
    );
}