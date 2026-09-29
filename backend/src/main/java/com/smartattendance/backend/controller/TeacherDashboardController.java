package com.smartattendance.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.TeacherDashboardResponse;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.service.TeacherDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final TeacherDashboardService teacherDashboardService;


    @GetMapping("/dashboard")
    public ResponseEntity<TeacherDashboardResponse>
    getDashboard(
            Authentication authentication
    ) {

        /*
         * JwtAuthenticationFilter me principal
         * User object set kiya gaya hai.
         */
        User teacher =
                (User) authentication.getPrincipal();


        TeacherDashboardResponse response =
                teacherDashboardService
                        .getDashboard(
                                teacher.getEmail()
                        );


        return ResponseEntity.ok(response);
    }
}   