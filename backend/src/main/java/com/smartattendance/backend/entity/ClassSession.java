package com.smartattendance.backend.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ==========================================================
 * Class Session Entity
 * ----------------------------------------------------------
 * Stores every class started by a Teacher.
 *
 * A LIVE session will be shown on Student Dashboard.
 *
 * The attendance location selected by the teacher is also
 * stored here as a snapshot so the geo-fence remains fixed
 * for this specific class session.
 * ==========================================================
 */

@Entity
@Table(name = "class_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // TEACHER
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "teacher_id",
            nullable = false
    )
    private User teacher;

    // =====================================================
    // CLASS INFO
    // =====================================================

    @Column(
            name = "subject_name",
            nullable = false
    )
    private String subjectName;

    @Column(
            name = "start_time",
            nullable = false
    )
    private LocalTime startTime;

    @Column(
            name = "end_time",
            nullable = false
    )
    private LocalTime endTime;

    @Column(
            name = "branch",
            nullable = false
    )
    private String branch;

    @Column(
            name = "semester",
            nullable = false
    )
    private Integer semester;

    // =====================================================
    // SESSION STATUS
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false
    )
    private ClassSessionStatus status;

    // =====================================================
    // SELECTED ATTENDANCE LOCATION
    //
    // Teacher selects this while starting attendance.
    //
    // We keep the original location ID for reference,
    // but geo verification uses the snapshot values below.
    // =====================================================

    @Column(
            name = "attendance_location_id"
    )
    private Long attendanceLocationId;

    // =====================================================
    // LOCATION SNAPSHOT
    // =====================================================

    @Column(
            name = "attendance_place_name",
            length = 150
    )
    private String attendancePlaceName;

    @Column(
            name = "attendance_location_department",
            length = 200
    )
    private String attendanceLocationDepartment;

    @Column(
            name = "attendance_location_branch",
            length = 200
    )
    private String attendanceLocationBranch;

    @Column(
            name = "attendance_latitude"
    )
    private Double attendanceLatitude;

    @Column(
            name = "attendance_longitude"
    )
    private Double attendanceLongitude;

    @Column(
            name = "attendance_radius_meters"
    )
    private Double attendanceRadiusMeters;

    // =====================================================
    // ACTUAL SESSION TIMES
    // =====================================================

    @Column(
            name = "actual_started_at"
    )
    private LocalDateTime actualStartedAt;

    @Column(
            name = "actual_ended_at"
    )
    private LocalDateTime actualEndedAt;

    // =====================================================
    // CREATED AT
    // =====================================================

    @CreationTimestamp
    @Column(
            name = "created_at",
            updatable = false
    )
    private LocalDateTime createdAt;
}