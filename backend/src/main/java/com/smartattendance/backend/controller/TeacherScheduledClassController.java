package com.smartattendance.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.AttendanceLocationResponse;
import com.smartattendance.backend.dto.StartScheduledClassRequest;
import com.smartattendance.backend.dto.StartScheduledClassResponse;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.service.TeacherScheduledClassService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(
        "/api/teacher/scheduled-classes"
)
@RequiredArgsConstructor
public class TeacherScheduledClassController {

    private final TeacherScheduledClassService
            teacherScheduledClassService;


    // =====================================================
    // GET AVAILABLE ATTENDANCE LOCATIONS
    //
    // Teacher clicks Start Attendance.
    // Frontend calls this endpoint first.
    // =====================================================

    @GetMapping(
            "/{timetableId}/locations"
    )
    public ResponseEntity<List<AttendanceLocationResponse>>
    getAvailableLocations(
            Authentication authentication,

            @PathVariable
            Long timetableId
    ) {

        User teacher =
                getAuthenticatedTeacher(
                        authentication
                );

        List<AttendanceLocationResponse> locations =
                teacherScheduledClassService
                        .getAvailableLocations(
                                teacher,
                                timetableId
                        );

        return ResponseEntity.ok(
                locations
        );
    }


    // =====================================================
    // START ATTENDANCE
    // =====================================================

    @PostMapping(
            "/start"
    )
    public ResponseEntity<StartScheduledClassResponse>
    startAttendance(
            Authentication authentication,

            @Valid
            @RequestBody
            StartScheduledClassRequest request
    ) {

        User teacher =
                getAuthenticatedTeacher(
                        authentication
                );

        StartScheduledClassResponse response =
                teacherScheduledClassService
                        .startClass(
                                teacher,
                                request.getTimetableId(),
                                request.getAttendanceLocationId()
                        );

        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // AUTHENTICATED TEACHER
    // =====================================================

    private User getAuthenticatedTeacher(
            Authentication authentication
    ) {

        if (
                authentication == null ||
                authentication.getPrincipal() == null
        ) {

            throw new IllegalStateException(
                    "Teacher authentication required"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (
                !(principal instanceof User)
        ) {

            throw new IllegalStateException(
                    "Invalid teacher authentication"
            );
        }

        return (User) principal;
    }
}