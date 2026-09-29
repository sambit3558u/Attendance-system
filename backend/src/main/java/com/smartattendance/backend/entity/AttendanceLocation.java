package com.smartattendance.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "attendance_locations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_location_department_branch_place",
                        columnNames = {
                                "department",
                                "branch",
                                "place_name"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLocation {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    // =====================================================
    // PLACE NAME
    // Example:
    // Computer Lab
    // MCA Block
    // Main Academic Building
    // =====================================================

    @Column(
            name = "place_name",
            nullable = false,
            length = 150
    )
    private String placeName;

    // =====================================================
    // DEPARTMENT
    // =====================================================

    @Column(
            name = "department",
            nullable = false,
            length = 200
    )
    private String department;

    // =====================================================
    // BRANCH
    // =====================================================

    @Column(
            name = "branch",
            nullable = false,
            length = 200
    )
    private String branch;

    // =====================================================
    // GEO LOCATION
    // =====================================================

    @Column(
            name = "latitude",
            nullable = false
    )
    private Double latitude;

    @Column(
            name = "longitude",
            nullable = false
    )
    private Double longitude;

    @Column(
            name = "radius_meters",
            nullable = false
    )
    private Double radiusMeters;

    // =====================================================
    // CREATED / UPDATED
    // =====================================================

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    // =====================================================
    // CREATE TIME
    // =====================================================

    @PrePersist
    public void prePersist() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt =
                now;

        updatedAt =
                now;
    }

    // =====================================================
    // UPDATE TIME
    // =====================================================

    @PreUpdate
    public void preUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}