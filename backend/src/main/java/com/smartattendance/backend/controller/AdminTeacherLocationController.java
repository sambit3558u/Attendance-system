package com.smartattendance.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.TeacherAttendanceLocationRequest;
import com.smartattendance.backend.dto.TeacherAttendanceLocationResponse;
import com.smartattendance.backend.service.TeacherAttendanceLocationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(
        "/api/admin/teacher-locations"
)
@RequiredArgsConstructor
public class AdminTeacherLocationController {

    private final TeacherAttendanceLocationService
            teacherAttendanceLocationService;

    // =====================================================
    // GET ALL
    // =====================================================

    @GetMapping
    public ResponseEntity<
            List<TeacherAttendanceLocationResponse>
            > getAllLocations() {

        return ResponseEntity.ok(
                teacherAttendanceLocationService
                        .getAllLocations()
        );
    }

    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping
    public ResponseEntity<
            TeacherAttendanceLocationResponse
            > createLocation(

            @Valid
            @RequestBody
            TeacherAttendanceLocationRequest request

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        teacherAttendanceLocationService
                                .create(request)
                );
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<
            TeacherAttendanceLocationResponse
            > updateLocation(

            @PathVariable
            Long id,

            @Valid
            @RequestBody
            TeacherAttendanceLocationRequest request

    ) {

        return ResponseEntity.ok(
                teacherAttendanceLocationService
                        .update(
                                id,
                                request
                        )
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(

            @PathVariable
            Long id

    ) {

        teacherAttendanceLocationService
                .delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}