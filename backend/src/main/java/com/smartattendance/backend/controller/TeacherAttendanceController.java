package com.smartattendance.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.TeacherAttendanceRequest;
import com.smartattendance.backend.dto.TeacherAttendanceResponse;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.service.TeacherAttendanceService;
import com.smartattendance.backend.repository.TeacherAttendanceRepository;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher/attendance")
@RequiredArgsConstructor
public class TeacherAttendanceController {

    private final TeacherAttendanceService
            teacherAttendanceService;

    private final TeacherAttendanceRepository teacherAttendanceRepository;

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> history(Authentication authentication) {
        User teacher = (User) authentication.getPrincipal();
        return ResponseEntity.ok(teacherAttendanceRepository.findByTeacherIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(teacher.getId(), java.time.LocalDate.now().minusMonths(12), java.time.LocalDate.now()).stream().map(item -> Map.<String, Object>of("date", item.getAttendanceDate(), "status", item.getStatus().name(), "placeName", item.getPlaceName() == null ? "-" : item.getPlaceName(), "distanceMeters", item.getDistanceMeters() == null ? 0 : item.getDistanceMeters())).toList());
    }

    @PostMapping("/mark")
    public ResponseEntity<TeacherAttendanceResponse>
    markAttendance(
            Authentication authentication,

            @Valid
            @RequestBody
            TeacherAttendanceRequest request
    ) {

        User teacher =
                (User)
                        authentication
                                .getPrincipal();

        return ResponseEntity.ok(
                teacherAttendanceService
                        .markAttendance(
                                teacher,
                                request
                        )
        );
    }
}
