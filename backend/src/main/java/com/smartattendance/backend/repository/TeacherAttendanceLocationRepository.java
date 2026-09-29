package com.smartattendance.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.TeacherAttendanceLocation;

@Repository
public interface TeacherAttendanceLocationRepository
        extends JpaRepository<TeacherAttendanceLocation, Long> {

    Optional<TeacherAttendanceLocation>
    findByDepartmentIgnoreCase(
            String department
    );

    boolean existsByDepartmentIgnoreCase(
            String department
    );
}