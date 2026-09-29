package com.smartattendance.backend.entity;

import java.time.LocalDate;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "teacher_attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_teacher_attendance_teacher_date",
                        columnNames = {
                                "teacher_id",
                                "attendance_date"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "teacher_id",
            nullable = false
    )
    private User teacher;

    @Column(
            name = "attendance_date",
            nullable = false
    )
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private AttendanceStatus status;

    @Column(
            name = "teacher_latitude",
            nullable = false
    )
    private Double teacherLatitude;

    @Column(
            name = "teacher_longitude",
            nullable = false
    )
    private Double teacherLongitude;

    @Column(
            name = "gps_accuracy"
    )
    private Double gpsAccuracy;

    @Column(
            name = "registered_latitude",
            nullable = false
    )
    private Double registeredLatitude;

    @Column(
            name = "registered_longitude",
            nullable = false
    )
    private Double registeredLongitude;

    @Column(
            name = "distance_meters",
            nullable = false
    )
    private Double distanceMeters;

    @Column(
            name = "allowed_radius_meters",
            nullable = false
    )
    private Double allowedRadiusMeters;

    @Column(
            name = "place_name",
            length = 150
    )
    private String placeName;

    @Column(
            name = "department",
            length = 200
    )
    private String department;

    @Column(
            name = "marked_at",
            nullable = false
    )
    private LocalDateTime markedAt;

    @PrePersist
    public void prePersist() {
        if (attendanceDate == null) {
            attendanceDate =
                    LocalDate.now();
        }

        if (markedAt == null) {
            markedAt =
                    LocalDateTime.now();
        }
    }
}