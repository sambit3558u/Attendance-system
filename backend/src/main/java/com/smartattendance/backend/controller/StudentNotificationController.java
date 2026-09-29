package com.smartattendance.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.StudentNotificationResponse;
import com.smartattendance.backend.service.StudentNotificationService;

import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Student Notification Controller
 * ----------------------------------------------------------
 * Student notification related HTTP requests handle karta hai.
 *
 * APIs:
 *
 * 1. GET
 *    /api/student/notifications?email=...
 *
 * 2. GET
 *    /api/student/notifications/unread-count?email=...
 *
 * 3. PATCH
 *    /api/student/notifications/{id}/read?email=...
 *
 * 4. PATCH
 *    /api/student/notifications/read-all?email=...
 * ==========================================================
 */

@RestController
@RequestMapping("/api/student/notifications")
@RequiredArgsConstructor
public class StudentNotificationController {

    private final StudentNotificationService
            studentNotificationService;

    /**
     * ======================================================
     * Get All Notifications
     * ======================================================
     */
    @GetMapping
    public ResponseEntity<List<StudentNotificationResponse>>
            getNotifications(
                    @RequestParam
                    String email
            ) {

        List<StudentNotificationResponse> response =
                studentNotificationService
                        .getNotifications(
                                email
                        );

        return ResponseEntity.ok(
                response
        );
    }

    /**
     * ======================================================
     * Get Unread Count
     * ======================================================
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long>
            getUnreadCount(
                    @RequestParam
                    String email
            ) {

        long count =
                studentNotificationService
                        .getUnreadCount(
                                email
                        );

        return ResponseEntity.ok(
                count
        );
    }

    /**
     * ======================================================
     * Mark Single Notification As Read
     * ======================================================
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<StudentNotificationResponse>
            markAsRead(
                    @PathVariable
                    Long id,

                    @RequestParam
                    String email
            ) {

        StudentNotificationResponse response =
                studentNotificationService
                        .markAsRead(
                                email,
                                id
                        );

        return ResponseEntity.ok(
                response
        );
    }

    /**
     * ======================================================
     * Mark All Notifications As Read
     * ======================================================
     */
    @PatchMapping("/read-all")
    public ResponseEntity<String>
            markAllAsRead(
                    @RequestParam
                    String email
            ) {

        studentNotificationService
                .markAllAsRead(
                        email
                );

        return ResponseEntity.ok(
                "All notifications marked as read"
        );
    }
}