package com.smartattendance.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.StudentNotificationResponse;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.StudentNotification;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.StudentNotificationRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Student Notification Service
 * ----------------------------------------------------------
 * Student notification related business logic handle karta hai.
 *
 * Main Responsibilities:
 *
 * 1. Student ki notifications load karna.
 * 2. Unread notification count karna.
 * 3. Single notification read mark karna.
 * 4. All notifications read mark karna.
 * 5. Future modules ke liye notification create karna.
 * ==========================================================
 */

@Service
@RequiredArgsConstructor
public class StudentNotificationService {

    private final UserRepository userRepository;

    private final StudentNotificationRepository
            studentNotificationRepository;

    /**
     * ======================================================
     * Get All Student Notifications
     * ------------------------------------------------------
     * Latest notification first return hogi.
     * ======================================================
     */
    @Transactional(readOnly = true)
    public List<StudentNotificationResponse>
            getNotifications(
                    String email
            ) {

        User student =
                findStudentByEmail(email);

        List<StudentNotification> notifications =
                studentNotificationRepository
                        .findByStudentIdOrderByCreatedAtDesc(
                                student.getId()
                        );

        return notifications
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * ======================================================
     * Get Unread Notification Count
     * ======================================================
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(
            String email
    ) {

        User student =
                findStudentByEmail(email);

        return studentNotificationRepository
                .countByStudentIdAndReadFalse(
                        student.getId()
                );
    }

    /**
     * ======================================================
     * Mark Single Notification As Read
     * ======================================================
     */
    @Transactional
    public StudentNotificationResponse markAsRead(
            String email,
            Long notificationId
    ) {

        User student =
                findStudentByEmail(email);

        StudentNotification notification =
                studentNotificationRepository
                        .findById(notificationId)
                        .orElseThrow(
                                () -> new ApiException(
                                        "Notification not found"
                                )
                        );

        /*
         * Security check:
         * Student sirf apni notification read mark kar sakta hai.
         */
        if (!notification
                .getStudent()
                .getId()
                .equals(student.getId())) {

            throw new ApiException(
                    "You cannot access this notification"
            );
        }

        if (!Boolean.TRUE.equals(
                notification.getRead()
        )) {

            notification.setRead(true);

            notification =
                    studentNotificationRepository
                            .save(notification);
        }

        return convertToResponse(
                notification
        );
    }

    /**
     * ======================================================
     * Mark All Notifications As Read
     * ======================================================
     */
    @Transactional
    public void markAllAsRead(
            String email
    ) {

        User student =
                findStudentByEmail(email);

        List<StudentNotification> unreadNotifications =
                studentNotificationRepository
                        .findByStudentIdAndReadFalseOrderByCreatedAtDesc(
                                student.getId()
                        );

        for (StudentNotification notification
                : unreadNotifications) {

            notification.setRead(true);
        }

        if (!unreadNotifications.isEmpty()) {

            studentNotificationRepository
                    .saveAll(
                            unreadNotifications
                    );
        }
    }

    /**
     * ======================================================
     * Create Notification
     * ------------------------------------------------------
     * Ye helper future modules use karenge.
     *
     * Examples:
     *
     * Teacher class start:
     * LIVE_CLASS
     *
     * Attendance marked:
     * ATTENDANCE
     *
     * Attendance low:
     * LOW_ATTENDANCE
     *
     * Timetable changed:
     * TIMETABLE
     * ======================================================
     */
    @Transactional
    public StudentNotificationResponse createNotification(
            User student,
            String type,
            String title,
            String message
    ) {

        if (student == null) {
            throw new ApiException(
                    "Student is required"
            );
        }

        if (student.getRole()
                != Role.STUDENT) {

            throw new ApiException(
                    "Notification can only be created for students"
            );
        }

        String normalizedType =
                normalizeRequiredText(
                        type,
                        "Notification type is required"
                ).toUpperCase();

        String normalizedTitle =
                normalizeRequiredText(
                        title,
                        "Notification title is required"
                );

        String normalizedMessage =
                normalizeRequiredText(
                        message,
                        "Notification message is required"
                );

        StudentNotification notification =
                StudentNotification
                        .builder()
                        .student(student)
                        .type(normalizedType)
                        .title(normalizedTitle)
                        .message(normalizedMessage)
                        .read(false)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        StudentNotification savedNotification =
                studentNotificationRepository
                        .save(notification);

        return convertToResponse(
                savedNotification
        );
    }

    /**
     * ======================================================
     * Find Student By Email
     * ======================================================
     */
    private User findStudentByEmail(
            String email
    ) {

        if (email == null
                || email.isBlank()) {

            throw new ApiException(
                    "Email is required"
            );
        }

        String normalizedEmail =
                email
                        .trim()
                        .toLowerCase();

        User student =
                userRepository
                        .findByEmail(
                                normalizedEmail
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Student not found"
                                )
                        );

        if (student.getRole()
                != Role.STUDENT) {

            throw new ApiException(
                    "Only students can access student notifications"
            );
        }

        return student;
    }

    /**
     * ======================================================
     * Convert Entity To Response DTO
     * ======================================================
     */
    private StudentNotificationResponse convertToResponse(
            StudentNotification notification
    ) {

        return StudentNotificationResponse
                .builder()
                .id(
                        notification.getId()
                )
                .type(
                        notification.getType()
                )
                .title(
                        notification.getTitle()
                )
                .message(
                        notification.getMessage()
                )
                .read(
                        notification.getRead()
                )
                .createdAt(
                        notification.getCreatedAt()
                )
                .build();
    }

    /**
     * ======================================================
     * Normalize Required Text
     * ======================================================
     */
    private String normalizeRequiredText(
            String value,
            String errorMessage
    ) {

        if (value == null
                || value.isBlank()) {

            throw new ApiException(
                    errorMessage
            );
        }

        return value.trim();
    }
}