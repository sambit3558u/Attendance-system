        package com.smartattendance.backend.service;

        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import com.smartattendance.backend.dto.StudentProfileResponse;
        import com.smartattendance.backend.dto.StudentProfileUpdateRequest;
        import com.smartattendance.backend.entity.Role;
        import com.smartattendance.backend.entity.User;
        import com.smartattendance.backend.exception.ApiException;
        import com.smartattendance.backend.repository.UserRepository;

        import lombok.RequiredArgsConstructor;

        /**
         * ==========================================================
         * Student Profile Service
         * ----------------------------------------------------------
         * Student profile read aur update logic handle karta hai.
         *
         * Editable:
         * - Name
         * - Phone
         * - Roll Number
         * - Semester
         *
         * Read-only:
         * - Email
         * - Branch
         * - Role
         * - User ID
         * ==========================================================
         */

        @Service
        @RequiredArgsConstructor
        public class StudentProfileService {

        private final UserRepository userRepository;

        /**
         * ======================================================
         * Get Student Profile
         * ======================================================
         */
        @Transactional(readOnly = true)
        public StudentProfileResponse getProfile(
                String email
        ) {

                User student =
                        findStudentByEmail(email);

                return convertToResponse(
                        student
                );
        }

        /**
         * ======================================================
         * Update Student Profile
         * ------------------------------------------------------
         * Student sirf allowed fields update kar sakta hai.
         *
         * Branch, Email aur Role update nahi honge.
         * ======================================================
         */
        @Transactional
        public StudentProfileResponse updateProfile(
                String email,
                StudentProfileUpdateRequest request
        ) {

                if (request == null) {
                throw new ApiException(
                        "Profile data is required"
                );
                }

                User student =
                        findStudentByEmail(email);

                // =================================================
                // Name
                // =================================================

                String name =
                        normalizeRequiredText(
                                request.getName(),
                                "Name is required"
                        );

                // =================================================
                // Phone
                // =================================================

                String phone =
                        normalizeRequiredText(
                                request.getPhone(),
                                "Phone number is required"
                        );

                // =================================================
                // Roll Number
                // =================================================

                String rollNumber =
                        normalizeRequiredText(
                                request.getRollNumber(),
                                "Roll number is required"
                        );

                // =================================================
                // Semester
                // =================================================

                Integer semester =
                        request.getSemester();

                if (semester == null
                        || semester < 1
                        || semester > 8) {

                throw new ApiException(
                        "Semester must be between 1 and 8"
                );
                }

                // =================================================
                // Phone Duplicate Check
                // =================================================
                /*
                * Agar same student ka current phone hai
                * to allow hoga.
                *
                * Agar kisi aur user ka phone hai
                * to reject hoga.
                */

                if (!phone.equals(
                        student.getPhone()
                )
                        && userRepository.existsByPhone(
                                phone
                        )) {

                throw new ApiException(
                        "Phone number is already registered"
                );
                }

                // =================================================
                // Roll Number Duplicate Check
                // =================================================

                if (!rollNumber.equalsIgnoreCase(
                        student.getRollNumber()
                )
                        && userRepository.existsByRollNumber(
                                rollNumber
                        )) {

                throw new ApiException(
                        "Roll Number is already registered"
                );
                }

                // =================================================
                // Update Editable Fields
                // =================================================

                student.setName(
                        name
                );

                student.setPhone(
                        phone
                );

                student.setRollNumber(
                        rollNumber
                );

                student.setSemester(
                        semester
                );

                // =================================================
                // Read-only Fields
                // -------------------------------------------------
                // Intentionally NOT updating:
                //
                // student.setEmail(...)
                // student.setBranch(...)
                // student.setRole(...)
                //
                // Isliye student apni Branch change nahi kar sakta.
                // =================================================

                User updatedStudent =
                        userRepository.save(
                                student
                        );

                return convertToResponse(
                        updatedStudent
                );
        }

        /**
         * ======================================================
         * Find Student By Email
         * ======================================================
         */
        private User findStudentByEmail(
                String email
        ) {

                if (email == null
                        || email.isBlank()) {

                throw new ApiException(
                        "Email is required"
                );
                }

                String normalizedEmail =
                        email
                                .trim()
                                .toLowerCase();

                User student =
                        userRepository
                                .findByEmail(
                                        normalizedEmail
                                )
                                .orElseThrow(
                                        () -> new ApiException(
                                                "Student not found"
                                        )
                                );

                if (student.getRole()
                        != Role.STUDENT) {

                throw new ApiException(
                        "Only students can access student profile"
                );
                }

                return student;
        }

        /**
         * ======================================================
         * Convert User Entity To StudentProfileResponse
         * ======================================================
         */
        private StudentProfileResponse convertToResponse(
                User student
        ) {

                return StudentProfileResponse
                        .builder()
                        .userId(
                                student.getId()
                        )
                        .name(
                                student.getName()
                        )
                        .email(
                                student.getEmail()
                        )
                        .phone(
                                student.getPhone()
                        )
                        .rollNumber(
                                student.getRollNumber()
                        )
                        .branch(
                                student.getBranch()
                        )
                        .semester(
                                student.getSemester()
                        )
                        .role(
                                student.getRole()
                        )
                        .build();
        }

        /**
         * ======================================================
         * Normalize Required Text
         * ======================================================
         */
        private String normalizeRequiredText(
                String value,
                String errorMessage
        ) {

                if (value == null
                        || value.isBlank()) {

                throw new ApiException(
                        errorMessage
                );
                }

                return value.trim();
        }
        }