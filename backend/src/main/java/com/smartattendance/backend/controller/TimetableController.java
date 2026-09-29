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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.StudentTimetableResponse;
import com.smartattendance.backend.dto.TimetableRequest;
import com.smartattendance.backend.dto.TimetableResponse;
import com.smartattendance.backend.service.TimetableService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Timetable Controller
 * ----------------------------------------------------------
 * Timetable related HTTP requests handle karta hai.
 *
 * Main APIs:
 *
 * 1. POST   /api/timetables
 *    Teacher/Admin timetable create karega.
 *
 * 2. PUT    /api/timetables/{id}
 *    Existing timetable update karega.
 *
 * 3. DELETE /api/timetables/{id}
 *    Existing timetable delete karega.
 *
 * 4. GET    /api/student/timetable
 *    Student ka current Branch + Semester timetable.
 *
 * 5. GET    /api/teacher/timetable
 *    Teacher ka assigned timetable.
 * ==========================================================
 */

@RestController
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    /**
     * ======================================================
     * Create Timetable
     * ------------------------------------------------------
     * Teacher/Admin timetable create karega.
     *
     * POST:
     * /api/timetables
     * ======================================================
     */
    @PostMapping("/api/timetables")
    public ResponseEntity<TimetableResponse>
            createTimetable(
                    @Valid
                    @RequestBody
                    TimetableRequest request
            ) {

        TimetableResponse response =
                timetableService.createTimetable(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * ======================================================
     * Update Timetable
     * ------------------------------------------------------
     *
     * PUT:
     * /api/timetables/{id}
     * ======================================================
     */
    @PutMapping("/api/timetables/{id}")
    public ResponseEntity<TimetableResponse>
            updateTimetable(
                    @PathVariable
                    Long id,

                    @Valid
                    @RequestBody
                    TimetableRequest request
            ) {

        TimetableResponse response =
                timetableService.updateTimetable(
                        id,
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }

    /**
     * ======================================================
     * Delete Timetable
     * ------------------------------------------------------
     *
     * DELETE:
     * /api/timetables/{id}
     * ======================================================
     */
    @DeleteMapping("/api/timetables/{id}")
    public ResponseEntity<String>
            deleteTimetable(
                    @PathVariable
                    Long id
            ) {

        timetableService.deleteTimetable(
                id
        );

        return ResponseEntity.ok(
                "Timetable deleted successfully"
        );
    }

    /**
     * ======================================================
     * Student Timetable
     * ------------------------------------------------------
     *
     * GET:
     * /api/student/timetable?email=student@gmail.com
     *
     * Backend users table se Student ka latest:
     *
     * Branch
     * Semester
     *
     * read karega.
     *
     * Agar Semester change hota hai:
     *
     * 3 -> 4
     *
     * next API request par Semester 4 timetable
     * automatically return hoga.
     * ======================================================
     */
    @GetMapping("/api/student/timetable")
    public ResponseEntity<StudentTimetableResponse>
            getStudentTimetable(
                    @RequestParam
                    String email
            ) {

        StudentTimetableResponse response =
                timetableService
                        .getStudentTimetable(
                                email
                        );

        return ResponseEntity.ok(
                response
        );
    }

    /**
     * ======================================================
     * Teacher Timetable
     * ------------------------------------------------------
     *
     * GET:
     * /api/teacher/timetable
     * ?teacherEmail=teacher@gmail.com
     * ======================================================
     */
    @GetMapping("/api/teacher/timetable")
    public ResponseEntity<List<TimetableResponse>>
            getTeacherTimetable(
                    @RequestParam
                    String teacherEmail
            ) {

        List<TimetableResponse> response =
                timetableService
                        .getTeacherTimetable(
                                teacherEmail
                        );

        return ResponseEntity.ok(
                response
        );
    }
}