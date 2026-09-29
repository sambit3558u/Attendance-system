package com.smartattendance.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.SubjectRequest;
import com.smartattendance.backend.dto.SubjectResponse;
import com.smartattendance.backend.entity.Subject;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.SubjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Subject Service
 * ----------------------------------------------------------
 * Subject related business logic handle karta hai.
 *
 * Main Responsibilities:
 *
 * 1. Teacher/Admin ke through Subject add karna.
 * 2. Duplicate Subject check karna.
 * 3. Branch + Semester ke subjects database se load karna.
 * 4. Subject Entity ko SubjectResponse DTO me convert karna.
 * ==========================================================
 */

@Service
@RequiredArgsConstructor
public class SubjectService {

    /*
     * Subject table ke saath database operations
     * perform karne ke liye repository.
     */
    private final SubjectRepository subjectRepository;

    /**
     * ======================================================
     * Add Subject
     * ------------------------------------------------------
     * Teacher/Admin subject add karega.
     *
     * Example:
     *
     * Subject Name : AIML
     * Subject Code : MCA303
     * Branch       : Master of Computer Applications
     * Semester     : 3
     * ======================================================
     */
    @Transactional
    public SubjectResponse addSubject(
            SubjectRequest request
    ) {

        /*
         * Required text normalize karo.
         */
        String subjectName = normalizeRequiredText(
                request.getSubjectName(),
                "Subject Name is required"
        );

        String subjectCode = normalizeRequiredText(
                request.getSubjectCode(),
                "Subject Code is required"
        ).toUpperCase();

        String branch = normalizeRequiredText(
                request.getBranch(),
                "Branch is required"
        );

        Integer semester = request.getSemester();

        /*
         * Semester validation.
         */
        if (semester == null
                || semester < 1
                || semester > 8) {

            throw new ApiException(
                    "Semester must be between 1 and 8"
            );
        }

        /*
         * Same Subject Code + Branch + Semester
         * duplicate nahi hona chahiye.
         */
        if (subjectRepository
                .existsBySubjectCodeAndBranchAndSemester(
                        subjectCode,
                        branch,
                        semester
                )) {

            throw new ApiException(
                    "Subject already exists for this branch and semester"
            );
        }

        /*
         * Subject Entity create karo.
         */
        Subject subject = Subject.builder()
                .subjectName(subjectName)
                .subjectCode(subjectCode)
                .branch(branch)
                .semester(semester)
                .build();

        /*
         * MySQL me save karo.
         */
        Subject savedSubject =
                subjectRepository.save(
                        subject
                );

        /*
         * Frontend ko clean DTO response bhejo.
         */
        return convertToResponse(
                savedSubject
        );
    }

    /**
     * ======================================================
     * Get Subjects By Branch And Semester
     * ------------------------------------------------------
     * Student/Teacher/Admin ke liye subject list return karega.
     * ======================================================
     */
    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjects(
            String branch,
            Integer semester
    ) {

        String normalizedBranch =
                normalizeRequiredText(
                        branch,
                        "Branch is required"
                );

        if (semester == null
                || semester < 1
                || semester > 8) {

            throw new ApiException(
                    "Semester must be between 1 and 8"
            );
        }

        List<Subject> subjects =
                subjectRepository
                        .findByBranchAndSemesterOrderBySubjectNameAsc(
                                normalizedBranch,
                                semester
                        );

        return subjects
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * ======================================================
     * Convert Subject Entity To SubjectResponse
     * ======================================================
     */
    private SubjectResponse convertToResponse(
            Subject subject
    ) {

        return SubjectResponse.builder()
                .id(subject.getId())
                .subjectName(
                        subject.getSubjectName()
                )
                .subjectCode(
                        subject.getSubjectCode()
                )
                .branch(
                        subject.getBranch()
                )
                .semester(
                        subject.getSemester()
                )
                .build();
    }

    /**
     * ======================================================
     * Normalize Required Text
     * ------------------------------------------------------
     * Blank/null values reject karta hai
     * aur extra spaces remove karta hai.
     * ======================================================
     */
    private String normalizeRequiredText(
            String value,
            String errorMessage
    ) {

        if (value == null || value.isBlank()) {
            throw new ApiException(
                    errorMessage
            );
        }

        return value.trim();
    }
}