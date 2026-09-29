package com.smartattendance.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.StudentNotification;

/**
 * ==========================================================
 * Student Notification Repository
 * ----------------------------------------------------------
 * student_notifications table ke database operations
 * handle karta hai.
 *
 * Main Responsibilities:
 *
 * 1. Student ki saari notifications load karna.
 * 2. Latest notification first return karna.
 * 3. Unread notifications count karna.
 * 4. Student notification records manage karna.
 * ==========================================================
 */

@Repository
public interface StudentNotificationRepository
        extends JpaRepository<StudentNotification, Long> {

    /**
     * Student ki saari notifications
     * latest first return karega.
     */
    List<StudentNotification>
            findByStudentIdOrderByCreatedAtDesc(
                    Long studentId
            );

    /**
     * Student ki unread notifications count karega.
     */
    long countByStudentIdAndReadFalse(
            Long studentId
    );

    /**
     * Student ki sirf unread notifications
     * latest first return karega.
     */
    List<StudentNotification>
            findByStudentIdAndReadFalseOrderByCreatedAtDesc(
                    Long studentId
            );

    /**
     * Particular student ki particular notification
     * find/check karne ke liye useful.
     */
    boolean existsByIdAndStudentId(
            Long id,
            Long studentId
    );
}