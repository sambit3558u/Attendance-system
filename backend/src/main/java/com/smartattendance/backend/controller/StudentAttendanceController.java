package com.smartattendance.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.MarkStudentAttendanceRequest;
import com.smartattendance.backend.dto.MarkStudentAttendanceResponse;
import com.smartattendance.backend.dto.StudentAttendanceResponse;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.service.StudentAttendanceMarkService;
import com.smartattendance.backend.service.StudentAttendanceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentAttendanceController {

    private final StudentAttendanceService
            studentAttendanceService;

    private final StudentAttendanceMarkService
            studentAttendanceMarkService;

    // =====================================================
    // GET ATTENDANCE STATISTICS / HISTORY
    // -----------------------------------------------------
    // Existing API preserve ki gayi hai.
    // =====================================================

    @GetMapping("/attendance")
    public ResponseEntity<StudentAttendanceResponse>
    getAttendance(
            @RequestParam
            String email
    ) {

        return ResponseEntity.ok(
                studentAttendanceService
                        .getAttendance(
                                email
                        )
        );
    }

    // =====================================================
    // MARK ATTENDANCE
    //
    // POST /api/student/attendance/mark
    //
    // IMPORTANT:
    // Student email frontend se trust nahi kiya jayega.
    // JWT Authentication se logged-in user identify hoga.
    // =====================================================

    @PostMapping("/attendance/mark")
    public ResponseEntity<MarkStudentAttendanceResponse>
    markAttendance(
            Authentication authentication,

            @Valid
            @RequestBody
            MarkStudentAttendanceRequest request
    ) {

        String studentEmail =
                resolveAuthenticatedEmail(
                        authentication
                );

        MarkStudentAttendanceResponse response =
                studentAttendanceMarkService
                        .markAttendance(
                                studentEmail,
                                request
                        );

        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
    // AUTHENTICATED EMAIL
    // =====================================================

    private String resolveAuthenticatedEmail(
            Authentication authentication
    ) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Student is not authenticated"
            );
        }

        Object principal =
                authentication.getPrincipal();

        /*
         * Current JWT implementation principal ko User
         * object ke form me set karta hai.
         */
        if (principal instanceof User user) {

            return user.getEmail();
        }

        /*
         * Fallback:
         * Agar future me principal username/email string
         * ho jaye to Authentication.getName() work karega.
         */
        return authentication.getName();
    }
}