package com.smartattendance.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.AttendanceLocationRequest;
import com.smartattendance.backend.dto.AttendanceLocationResponse;
import com.smartattendance.backend.entity.AttendanceLocation;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceLocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceLocationService {

    private final AttendanceLocationRepository
            attendanceLocationRepository;

    // =====================================================
    // GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<AttendanceLocationResponse>
    getAllLocations() {

        return attendanceLocationRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =====================================================
    // CREATE
    //
    // SAME DEPARTMENT + BRANCH
    // CAN HAVE MULTIPLE DIFFERENT PLACES.
    //
    // Example:
    //
    // MCA + Computer Lab
    // MCA + MCA Block
    // MCA + Seminar Hall
    //
    // But exact same:
    //
    // MCA + Computer Lab
    //
    // cannot be duplicated.
    // =====================================================

    @Transactional
    public AttendanceLocationResponse create(
            AttendanceLocationRequest request
    ) {

        validateRequest(request);

        String placeName =
                request
                        .getPlaceName()
                        .trim();

        String department =
                request
                        .getDepartment()
                        .trim();

        String branch =
                request
                        .getBranch()
                        .trim();

        // =================================================
        // DUPLICATE CHECK:
        //
        // department + branch + placeName
        // =================================================

        boolean alreadyExists =
                attendanceLocationRepository
                        .existsByDepartmentIgnoreCaseAndBranchIgnoreCaseAndPlaceNameIgnoreCase(
                                department,
                                branch,
                                placeName
                        );

        if (alreadyExists) {

            throw new ApiException(
                    "Attendance place already exists for "
                            + department
                            + " / "
                            + branch
                            + " / "
                            + placeName
            );
        }

        // =================================================
        // CREATE ENTITY
        // =================================================

        AttendanceLocation location =
                AttendanceLocation
                        .builder()

                        .placeName(
                                placeName
                        )

                        .department(
                                department
                        )

                        .branch(
                                branch
                        )

                        .latitude(
                                request.getLatitude()
                        )

                        .longitude(
                                request.getLongitude()
                        )

                        .radiusMeters(
                                request.getRadiusMeters()
                        )

                        .build();

        AttendanceLocation saved =
                attendanceLocationRepository
                        .save(location);

        return toResponse(saved);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @Transactional
    public AttendanceLocationResponse update(
            Long id,
            AttendanceLocationRequest request
    ) {

        // =================================================
        // ID CHECK
        // =================================================

        if (id == null) {

            throw new ApiException(
                    "Attendance location ID is required"
            );
        }

        validateRequest(request);

        // =================================================
        // FIND EXISTING LOCATION
        // =================================================

        AttendanceLocation location =
                attendanceLocationRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ApiException(
                                        "Attendance location not found"
                                )
                        );

        // =================================================
        // CLEAN VALUES
        // =================================================

        String placeName =
                request
                        .getPlaceName()
                        .trim();

        String department =
                request
                        .getDepartment()
                        .trim();

        String branch =
                request
                        .getBranch()
                        .trim();

        // =================================================
        // DUPLICATE CHECK
        //
        // Ignore current row ID.
        // =================================================

        boolean duplicateExists =
                attendanceLocationRepository
                        .existsByDepartmentIgnoreCaseAndBranchIgnoreCaseAndPlaceNameIgnoreCaseAndIdNot(
                                department,
                                branch,
                                placeName,
                                id
                        );

        if (duplicateExists) {

            throw new ApiException(
                    "Attendance place already exists for "
                            + department
                            + " / "
                            + branch
                            + " / "
                            + placeName
            );
        }

        // =================================================
        // UPDATE VALUES
        // =================================================

        location.setPlaceName(
                placeName
        );

        location.setDepartment(
                department
        );

        location.setBranch(
                branch
        );

        location.setLatitude(
                request.getLatitude()
        );

        location.setLongitude(
                request.getLongitude()
        );

        location.setRadiusMeters(
                request.getRadiusMeters()
        );

        // =================================================
        // SAVE
        // =================================================

        AttendanceLocation updated =
                attendanceLocationRepository
                        .save(location);

        return toResponse(updated);
    }

    // =====================================================
    // DELETE
    // =====================================================

    @Transactional
    public void delete(
            Long id
    ) {

        if (id == null) {

            throw new ApiException(
                    "Attendance location ID is required"
            );
        }

        AttendanceLocation location =
                attendanceLocationRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ApiException(
                                        "Attendance location not found"
                                )
                        );

        attendanceLocationRepository
                .delete(location);
    }

    // =====================================================
    // GET LOCATIONS FOR A BRANCH
    //
    // THIS WILL BE USED BY TEACHER DASHBOARD
    // WHEN START ATTENDANCE IS CLICKED.
    // =====================================================

    @Transactional(readOnly = true)
    public List<AttendanceLocationResponse>
    getLocationsForBranch(
            String branch
    ) {

        if (
                branch == null ||
                branch.isBlank()
        ) {

            throw new ApiException(
                    "Branch is required"
            );
        }

        return attendanceLocationRepository
                .findByBranchIgnoreCaseOrderByPlaceNameAsc(
                        branch.trim()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =====================================================
    // GET LOCATIONS FOR DEPARTMENT + BRANCH
    // =====================================================

    @Transactional(readOnly = true)
    public List<AttendanceLocationResponse>
    getLocationsForDepartmentAndBranch(
            String department,
            String branch
    ) {

        if (
                department == null ||
                department.isBlank()
        ) {

            throw new ApiException(
                    "Department is required"
            );
        }

        if (
                branch == null ||
                branch.isBlank()
        ) {

            throw new ApiException(
                    "Branch is required"
            );
        }

        return attendanceLocationRepository
                .findByDepartmentIgnoreCaseAndBranchIgnoreCaseOrderByPlaceNameAsc(
                        department.trim(),
                        branch.trim()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =====================================================
    // VALIDATION
    // =====================================================

    private void validateRequest(
            AttendanceLocationRequest request
    ) {

        if (
                request == null
        ) {

            throw new ApiException(
                    "Attendance location data is required"
            );
        }

        // =================================================
        // PLACE NAME
        // =================================================

        if (
                request.getPlaceName() == null ||
                request.getPlaceName().isBlank()
        ) {

            throw new ApiException(
                    "Place name is required"
            );
        }

        // =================================================
        // DEPARTMENT
        // =================================================

        if (
                request.getDepartment() == null ||
                request.getDepartment().isBlank()
        ) {

            throw new ApiException(
                    "Department is required"
            );
        }

        // =================================================
        // BRANCH
        // =================================================

        if (
                request.getBranch() == null ||
                request.getBranch().isBlank()
        ) {

            throw new ApiException(
                    "Branch is required"
            );
        }

        // =================================================
        // LATITUDE
        // =================================================

        if (
                request.getLatitude() == null
        ) {

            throw new ApiException(
                    "Latitude is required"
            );
        }

        if (
                request.getLatitude() < -90 ||
                request.getLatitude() > 90
        ) {

            throw new ApiException(
                    "Latitude must be between -90 and 90"
            );
        }

        // =================================================
        // LONGITUDE
        // =================================================

        if (
                request.getLongitude() == null
        ) {

            throw new ApiException(
                    "Longitude is required"
            );
        }

        if (
                request.getLongitude() < -180 ||
                request.getLongitude() > 180
        ) {

            throw new ApiException(
                    "Longitude must be between -180 and 180"
            );
        }

        // =================================================
        // RADIUS
        // =================================================

        if (
                request.getRadiusMeters() == null ||
                request.getRadiusMeters() <= 0
        ) {

            throw new ApiException(
                    "Radius must be greater than 0"
            );
        }
    }

    // =====================================================
    // ENTITY -> RESPONSE
    // =====================================================

    private AttendanceLocationResponse toResponse(
            AttendanceLocation location
    ) {

        return AttendanceLocationResponse
                .builder()

                .id(
                        location.getId()
                )

                .placeName(
                        location.getPlaceName()
                )

                .department(
                        location.getDepartment()
                )

                .branch(
                        location.getBranch()
                )

                .latitude(
                        location.getLatitude()
                )

                .longitude(
                        location.getLongitude()
                )

                .radiusMeters(
                        location.getRadiusMeters()
                )

                .build();
    }
}