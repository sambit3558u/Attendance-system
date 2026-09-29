package com.smartattendance.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartattendance.backend.dto.StudentDashboardResponse;
import com.smartattendance.backend.service.StudentDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardResponse> getDashboard(
            @RequestParam String email) {

        StudentDashboardResponse response = studentDashboardService
                .getDashboard(email);

        return ResponseEntity.ok(response);
    }
}