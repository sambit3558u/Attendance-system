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

import com.smartattendance.backend.dto.LiveClassResponse;
import com.smartattendance.backend.dto.AttendanceLocationResponse;
import com.smartattendance.backend.dto.StartClassRequest;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.service.ClassSessionService;
import com.smartattendance.backend.repository.AttendanceLocationRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teacher/class")
@RequiredArgsConstructor
public class ClassSessionController {

    private final ClassSessionService
            classSessionService;

    private final AttendanceLocationRepository
            attendanceLocationRepository;

    /**
     * Returns admin-configured student attendance locations for a branch so a
     * teacher can create an ad-hoc or substitute class safely.
     */
    @GetMapping("/locations")
    public ResponseEntity<List<AttendanceLocationResponse>>
    getLocationsForBranch(
            @org.springframework.web.bind.annotation.RequestParam String branch
    ) {
        List<AttendanceLocationResponse> locations =
                attendanceLocationRepository
                        .findByBranchIgnoreCaseOrderByPlaceNameAsc(branch)
                        .stream()
                        .map(location -> AttendanceLocationResponse.builder()
                                .id(location.getId())
                                .placeName(location.getPlaceName())
                                .department(location.getDepartment())
                                .branch(location.getBranch())
                                .latitude(location.getLatitude())
                                .longitude(location.getLongitude())
                                .radiusMeters(location.getRadiusMeters())
                                .build())
                        .toList();
        return ResponseEntity.ok(locations);
    }

    // =====================================================
    // START CLASS
    //
    // POST /api/teacher/class/start
    //
    // Teacher email frontend se nahi lenge.
    // JWT authenticated teacher use hoga.
    // =====================================================

    @PostMapping("/start")
    public ResponseEntity<LiveClassResponse>
    startClass(
            Authentication authentication,

            @Valid
            @RequestBody
            StartClassRequest request
    ) {

        String teacherEmail =
                resolveAuthenticatedEmail(
                        authentication
                );

        LiveClassResponse response =
                classSessionService
                        .startClass(
                                teacherEmail,
                                request
                        );

        return ResponseEntity.ok(
                response
        );
    }

    // =====================================================
    // END CLASS
    //
    // POST /api/teacher/class/{sessionId}/end
    // =====================================================

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<String>
    endClass(
            @PathVariable
            Long sessionId,

            Authentication authentication
    ) {

        String teacherEmail =
                resolveAuthenticatedEmail(
                        authentication
                );

        classSessionService.endClass(
                sessionId,
                teacherEmail
        );

        return ResponseEntity.ok(
                "Class ended successfully. Remaining students marked ABSENT."
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

            throw new ApiException(
                    "Teacher is not authenticated"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (principal instanceof User user) {

            return user.getEmail();
        }

        String email =
                authentication.getName();

        if (email == null
                || email.isBlank()) {

            throw new ApiException(
                    "Authenticated teacher email not found"
            );
        }

        return email;
    }
}
