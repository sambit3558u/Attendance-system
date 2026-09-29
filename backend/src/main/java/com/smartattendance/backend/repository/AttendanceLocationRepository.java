package com.smartattendance.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.AttendanceLocation;

@Repository
public interface AttendanceLocationRepository
        extends JpaRepository<AttendanceLocation, Long> {

    // =====================================================
    // ALL LOCATIONS FOR DEPARTMENT + BRANCH
    //
    // Example:
    // MCA + Computer Lab
    // MCA + Main Block
    // MCA + Seminar Hall
    // =====================================================

    List<AttendanceLocation>
    findByDepartmentIgnoreCaseAndBranchIgnoreCaseOrderByPlaceNameAsc(
            String department,
            String branch
    );

    // =====================================================
    // ALL LOCATIONS FOR A BRANCH
    //
    // Useful when starting a class and the class has branch.
    // =====================================================

    List<AttendanceLocation>
    findByBranchIgnoreCaseOrderByPlaceNameAsc(
            String branch
    );

    // =====================================================
    // DUPLICATE PLACE CHECK
    //
    // Same department + branch + place name
    // cannot be duplicated.
    // =====================================================

    boolean existsByDepartmentIgnoreCaseAndBranchIgnoreCaseAndPlaceNameIgnoreCase(
            String department,
            String branch,
            String placeName
    );

    // =====================================================
    // DUPLICATE PLACE CHECK DURING UPDATE
    //
    // Ignore current row ID.
    // =====================================================

    boolean existsByDepartmentIgnoreCaseAndBranchIgnoreCaseAndPlaceNameIgnoreCaseAndIdNot(
            String department,
            String branch,
            String placeName,
            Long id
    );
}