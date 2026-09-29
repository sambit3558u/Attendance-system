package com.smartattendance.backend.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_class_session",
                        columnNames = {
                                "student_id",
                                "class_session_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    // =====================================================
    // PRIMARY KEY
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // STUDENT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private User student;

    // =====================================================
    // EXACT CLASS SESSION
    // -----------------------------------------------------
    // Old attendance rows ke liye nullable rakha hai.
    // New attendance records me session mandatory hoga.
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "class_session_id"
    )
    private ClassSession classSession;

    // =====================================================
    // SNAPSHOT DATA
    // =====================================================

    @Column(nullable = false)
    private String subject;

    private String branch;

    private Integer semester;

    // =====================================================
    // ATTENDANCE STATUS
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    // =====================================================
    // GPS VERIFICATION
    // =====================================================

    @Column(nullable = false)
    private boolean geoVerified;

    @Column(name = "student_latitude")
    private Double studentLatitude;

    @Column(name = "student_longitude")
    private Double studentLongitude;

    @Column(name = "gps_accuracy")
    private Double gpsAccuracy;

    @Column(name = "distance_meters")
    private Double distanceMeters;

    @Column(name = "allowed_radius_meters")
    private Double allowedRadiusMeters;

    // =====================================================
    // TIME
    // =====================================================

    @Column(nullable = false)
    private LocalDateTime attendanceTime;
}