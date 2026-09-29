package com.smartattendance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Subject Response DTO
 * ----------------------------------------------------------
 * Subject data frontend ko return karne ke liye use hoga.
 *
 * Example:
 *
 * {
 *   "id": 1,
 *   "subjectName": "Data Structures & Algorithms",
 *   "subjectCode": "MCA301",
 *   "branch": "Master of Computer Applications",
 *   "semester": 3
 * }
 * ==========================================================
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResponse {

    // Subject database ID
    private Long id;

    // Subject Name
    private String subjectName;

    // Subject Code
    private String subjectCode;

    // Student Branch
    private String branch;

    // Student Semester
    private Integer semester;
}