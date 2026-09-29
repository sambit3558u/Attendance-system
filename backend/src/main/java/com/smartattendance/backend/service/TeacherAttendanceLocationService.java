package com.smartattendance.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.TeacherAttendanceLocationRequest;
import com.smartattendance.backend.dto.TeacherAttendanceLocationResponse;
import com.smartattendance.backend.entity.TeacherAttendanceLocation;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.TeacherAttendanceLocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherAttendanceLocationService {

    private final TeacherAttendanceLocationRepository
            teacherAttendanceLocationRepository;

    // =====================================================
    // GET ALL
    // =====================================================

    @Transactional(readOnly = true)
    public List<TeacherAttendanceLocationResponse>
    getAllLocations() {

        return teacherAttendanceLocationRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =====================================================
    // CREATE
    // =====================================================

    @Transactional
    public TeacherAttendanceLocationResponse create(
            TeacherAttendanceLocationRequest request
    ) {

        if (request == null) {
            throw new ApiException(
                    "Teacher attendance location request is required"
            );
        }

        String placeName =
                request
                        .getPlaceName()
                        .trim();

        String department =
                request
                        .getDepartment()
                        .trim();

        // One teacher attendance location per department
        if (
                teacherAttendanceLocationRepository
                        .existsByDepartmentIgnoreCase(
                                department
                        )
        ) {

            throw new ApiException(
                    "Teacher attendance location already configured for department: "
                            + department
            );
        }

        TeacherAttendanceLocation location =
                TeacherAttendanceLocation
                        .builder()
                        .placeName(
                                placeName
                        )
                        .department(
                                department
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

        TeacherAttendanceLocation saved =
                teacherAttendanceLocationRepository
                        .save(location);

        return toResponse(saved);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @Transactional
    public TeacherAttendanceLocationResponse update(
            Long id,
            TeacherAttendanceLocationRequest request
    ) {

        if (id == null) {
            throw new ApiException(
                    "Teacher attendance location ID is required"
            );
        }

        if (request == null) {
            throw new ApiException(
                    "Teacher attendance location request is required"
            );
        }

        TeacherAttendanceLocation location =
                teacherAttendanceLocationRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ApiException(
                                                "Teacher attendance location not found with ID: "
                                                        + id
                                        )
                        );

        String placeName =
                request
                        .getPlaceName()
                        .trim();

        String department =
                request
                        .getDepartment()
                        .trim();

        // Make sure another row is not already
        // configured for this department.
        teacherAttendanceLocationRepository
                .findByDepartmentIgnoreCase(
                        department
                )
                .ifPresent(
                        existing -> {

                            if (
                                    !existing
                                            .getId()
                                            .equals(id)
                            ) {

                                throw new ApiException(
                                        "Teacher attendance location already configured for department: "
                                                + department
                                );
                            }
                        }
                );

        location.setPlaceName(
                placeName
        );

        location.setDepartment(
                department
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

        TeacherAttendanceLocation updated =
                teacherAttendanceLocationRepository
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

        TeacherAttendanceLocation location =
                teacherAttendanceLocationRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ApiException(
                                                "Teacher attendance location not found with ID: "
                                                        + id
                                        )
                        );

        teacherAttendanceLocationRepository
                .delete(location);
    }

    // =====================================================
    // ENTITY -> DTO
    // =====================================================

    private TeacherAttendanceLocationResponse toResponse(
            TeacherAttendanceLocation location
    ) {

        return TeacherAttendanceLocationResponse
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