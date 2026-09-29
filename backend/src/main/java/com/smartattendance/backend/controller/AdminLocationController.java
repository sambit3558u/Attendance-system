package com.smartattendance.backend.controller;

import com.smartattendance.backend.dto.AttendanceLocationRequest;
import com.smartattendance.backend.dto.AttendanceLocationResponse;
import com.smartattendance.backend.service.AttendanceLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/locations")
@RequiredArgsConstructor
public class AdminLocationController {

    private final AttendanceLocationService
            attendanceLocationService;

    // =====================================================
    // GET ALL
    // =====================================================

    @GetMapping
    public ResponseEntity<List<AttendanceLocationResponse>>
    getAll() {

        return ResponseEntity.ok(
                attendanceLocationService
                        .getAllLocations()
        );
    }

    // =====================================================
    // CREATE
    // =====================================================

    @PostMapping
    public ResponseEntity<AttendanceLocationResponse>
    create(
            @Valid
            @RequestBody
            AttendanceLocationRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        attendanceLocationService
                                .create(request)
                );
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceLocationResponse>
    update(
            @PathVariable Long id,

            @Valid
            @RequestBody
            AttendanceLocationRequest request
    ) {

        return ResponseEntity.ok(
                attendanceLocationService
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
    public ResponseEntity<Void>
    delete(
            @PathVariable Long id
    ) {

        attendanceLocationService
                .delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}