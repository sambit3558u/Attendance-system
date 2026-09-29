package com.smartattendance.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Subject Entity
 * ----------------------------------------------------------
 * Stores subjects for a particular Branch and Semester.
 *
 * Example:
 *
 * Subject Name : Data Structures & Algorithms
 * Subject Code : MCA301
 * Branch       : Master of Computer Applications
 * Semester     : 3
 *
 * This table will be used by:
 *
 * 1. Student Attendance
 * 2. Student Timetable
 * 3. Teacher Class Session
 * 4. Subject-wise Attendance
 * ==========================================================
 */

@Entity
@Table(
        name = "subjects",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "subject_code",
                                "branch",
                                "semester"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    // =====================================================
    // Primary Key
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // Subject Name
    // Example:
    // Data Structures & Algorithms
    // =====================================================

    @Column(
            name = "subject_name",
            nullable = false
    )
    private String subjectName;

    // =====================================================
    // Subject Code
    // Example:
    // MCA301
    // =====================================================

    @Column(
            name = "subject_code",
            nullable = false
    )
    private String subjectCode;

    // =====================================================
    // Branch
    // Example:
    // Master of Computer Applications
    // =====================================================

    @Column(
            nullable = false
    )
    private String branch;

    // =====================================================
    // Semester
    // Allowed:
    // 1 to 8
    // =====================================================

    @Column(
            nullable = false
    )
    private Integer semester;
}