package com.smartattendance.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.SubjectRequest;
import com.smartattendance.backend.dto.SubjectResponse;
import com.smartattendance.backend.service.SubjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Subject Controller
 * ----------------------------------------------------------
 * Subject related HTTP requests handle karta hai.
 *
 * Main APIs:
 *
 * 1. POST /api/subjects
 *    Teacher/Admin subject add karega.
 *
 * 2. GET /api/subjects
 *    Branch + Semester ke subjects return karega.
 * ==========================================================
 */

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * ======================================================
     * Add Subject
     * ------------------------------------------------------
     * Teacher/Admin ke liye.
     *
     * POST:
     * /api/subjects
     *
     * Request Body Example:
     *
     * {
     *   "subjectName": "AIML",
     *   "subjectCode": "MCA303",
     *   "branch": "Master of Computer Applications",
     *   "semester": 3
     * }
     * ======================================================
     */
    @PostMapping
    public ResponseEntity<SubjectResponse> addSubject(
            @Valid
            @RequestBody
            SubjectRequest request
    ) {

        SubjectResponse response =
                subjectService.addSubject(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * ======================================================
     * Get Subjects By Branch And Semester
     * ------------------------------------------------------
     *
     * GET:
     *
     * /api/subjects
     * ?branch=Master of Computer Applications
     * &semester=3
     *
     * Student Attendance aur Timetable me
     * ye API use ho sakti hai.
     * ======================================================
     */
    @GetMapping
    public ResponseEntity<List<SubjectResponse>>
            getSubjects(
                    @RequestParam
                    String branch,

                    @RequestParam
                    Integer semester
            ) {

        List<SubjectResponse> subjects =
                subjectService.getSubjects(
                        branch,
                        semester
                );

        return ResponseEntity.ok(
                subjects
        );
    }
}