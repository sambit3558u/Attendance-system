package com.smartattendance.backend.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.StudentTimetableResponse;
import com.smartattendance.backend.dto.TimetableRequest;
import com.smartattendance.backend.dto.TimetableResponse;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.Subject;
import com.smartattendance.backend.entity.Timetable;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.SubjectRepository;
import com.smartattendance.backend.repository.TimetableRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Timetable Service
 * ----------------------------------------------------------
 * Timetable related business logic handle karta hai.
 *
 * Main Responsibilities:
 *
 * 1. Timetable create karna.
 * 2. Timetable update karna.
 * 3. Timetable delete karna.
 * 4. Student ke current Branch + Semester ka timetable load.
 * 5. Teacher ka timetable load karna.
 * ==========================================================
 */

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;

    private final SubjectRepository subjectRepository;

    private final UserRepository userRepository;

    /**
     * ======================================================
     * Create Timetable
     * ======================================================
     */
    @Transactional
    public TimetableResponse createTimetable(
            TimetableRequest request
    ) {

        validateRequest(request);

        /*
         * Subject database se find karo.
         */
        Subject subject =
                subjectRepository
                        .findById(
                                request.getSubjectId()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Subject not found"
                                )
                        );

        /*
         * Assigned Teacher find karo.
         */
        User teacher =
                userRepository
                        .findByEmail(
                                request
                                        .getTeacherEmail()
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Teacher not found"
                                )
                        );

        /*
         * Teacher role check.
         */
        if (teacher.getRole() != Role.TEACHER) {
            throw new ApiException(
                    "Selected user is not a Teacher"
            );
        }

        /*
         * Subject branch must match timetable branch.
         */
        if (!subject
                .getBranch()
                .equalsIgnoreCase(
                        request
                                .getBranch()
                                .trim()
                )) {

            throw new ApiException(
                    "Subject does not belong to selected branch"
            );
        }

        /*
         * Subject semester must match timetable semester.
         */
        if (!subject
                .getSemester()
                .equals(
                        request.getSemester()
                )) {

            throw new ApiException(
                    "Subject does not belong to selected semester"
            );
        }

        /*
         * Same exact start-time slot duplicate check.
         */
        boolean duplicateSlot =
                timetableRepository
                        .existsByBranchAndSemesterAndDayAndStartTime(
                                request
                                        .getBranch()
                                        .trim(),
                                request.getSemester(),
                                request.getDay(),
                                request.getStartTime()
                        );

        if (duplicateSlot) {
            throw new ApiException(
                    "A class already exists at this time"
            );
        }

        /*
         * Timetable Entity build karo.
         */
        Timetable timetable =
                Timetable.builder()
                        .subject(
                                subject
                        )
                        .teacher(
                                teacher
                        )
                        .branch(
                                request
                                        .getBranch()
                                        .trim()
                        )
                        .semester(
                                request.getSemester()
                        )
                        .day(
                                request.getDay()
                        )
                        .startTime(
                                request.getStartTime()
                        )
                        .endTime(
                                request.getEndTime()
                        )
                        .build();

        Timetable savedTimetable =
                timetableRepository.save(
                        timetable
                );

        return convertToResponse(
                savedTimetable
        );
    }

    /**
     * ======================================================
     * Update Timetable
     * ======================================================
     */
    @Transactional
    public TimetableResponse updateTimetable(
            Long timetableId,
            TimetableRequest request
    ) {

        validateRequest(request);

        Timetable timetable =
                timetableRepository
                        .findById(
                                timetableId
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Timetable not found"
                                )
                        );

        Subject subject =
                subjectRepository
                        .findById(
                                request.getSubjectId()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Subject not found"
                                )
                        );

        User teacher =
                userRepository
                        .findByEmail(
                                request
                                        .getTeacherEmail()
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Teacher not found"
                                )
                        );

        if (teacher.getRole() != Role.TEACHER) {
            throw new ApiException(
                    "Selected user is not a Teacher"
            );
        }

        if (!subject
                .getBranch()
                .equalsIgnoreCase(
                        request
                                .getBranch()
                                .trim()
                )) {

            throw new ApiException(
                    "Subject does not belong to selected branch"
            );
        }

        if (!subject
                .getSemester()
                .equals(
                        request.getSemester()
                )) {

            throw new ApiException(
                    "Subject does not belong to selected semester"
            );
        }

        /*
         * Existing timetable update.
         */
        timetable.setSubject(
                subject
        );

        timetable.setTeacher(
                teacher
        );

        timetable.setBranch(
                request
                        .getBranch()
                        .trim()
        );

        timetable.setSemester(
                request.getSemester()
        );

        timetable.setDay(
                request.getDay()
        );

        timetable.setStartTime(
                request.getStartTime()
        );

        timetable.setEndTime(
                request.getEndTime()
        );

        Timetable updatedTimetable =
                timetableRepository.save(
                        timetable
                );

        return convertToResponse(
                updatedTimetable
        );
    }

    /**
     * ======================================================
     * Delete Timetable
     * ======================================================
     */
    @Transactional
    public void deleteTimetable(
            Long timetableId
    ) {

        Timetable timetable =
                timetableRepository
                        .findById(
                                timetableId
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Timetable not found"
                                )
                        );

        timetableRepository.delete(
                timetable
        );
    }

    /**
     * ======================================================
     * Student Timetable
     * ------------------------------------------------------
     * Important:
     *
     * Student ka Branch aur Semester frontend/localStorage
     * se nahi liya ja raha.
     *
     * Har API call par users table se latest Student record
     * read kiya jayega.
     *
     * Agar student ka semester database me 3 se 4 update
     * hota hai, next request automatically Semester 4 ka
     * timetable return karegi.
     * ======================================================
     */
    @Transactional(readOnly = true)
    public StudentTimetableResponse getStudentTimetable(
            String email
    ) {

        /*
         * Email validation.
         */
        if (email == null
                || email.isBlank()) {

            throw new ApiException(
                    "Email is required"
            );
        }

        /*
         * IMPORTANT:
         * Student ko users table se fresh load karo.
         */
        User student =
                userRepository
                        .findByEmail(
                                email
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Student not found"
                                )
                        );

        /*
         * Student role validation.
         */
        if (student.getRole()
                != Role.STUDENT) {

            throw new ApiException(
                    "Only students can access student timetable"
            );
        }

        /*
         * Current Branch database se.
         */
        String currentBranch =
                student.getBranch();

        /*
         * Current Semester database se.
         */
        Integer currentSemester =
                student.getSemester();

        if (currentBranch == null
                || currentBranch.isBlank()) {

            throw new ApiException(
                    "Student branch is not configured"
            );
        }

        if (currentSemester == null) {

            throw new ApiException(
                    "Student semester is not configured"
            );
        }

        /*
         * Latest Branch + Semester ke according
         * timetable database se load karo.
         */
        List<Timetable> timetableList =
                timetableRepository
                        .findByBranchAndSemesterOrderByDayAscStartTimeAsc(
                                currentBranch,
                                currentSemester
                        );

        /*
         * Entity list → TimetableResponse list.
         */
        List<TimetableResponse> timetableResponses =
                timetableList
                        .stream()
                        .map(
                                this::convertToResponse
                        )
                        .toList();

        /*
         * Timetable empty hone par bhi Student Information
         * frontend ko milegi.
         */
        return StudentTimetableResponse
                .builder()
                .userId(
                        student.getId()
                )
                .name(
                        student.getName()
                )
                .rollNumber(
                        student.getRollNumber()
                )
                .branch(
                        currentBranch
                )
                .semester(
                        currentSemester
                )
                .timetable(
                        timetableResponses
                )
                .build();
    }

    /**
     * ======================================================
     * Teacher Timetable
     * ======================================================
     */
    @Transactional(readOnly = true)
    public List<TimetableResponse> getTeacherTimetable(
            String teacherEmail
    ) {

        if (teacherEmail == null
                || teacherEmail.isBlank()) {

            throw new ApiException(
                    "Teacher Email is required"
            );
        }

        User teacher =
                userRepository
                        .findByEmail(
                                teacherEmail
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(
                                () -> new ApiException(
                                        "Teacher not found"
                                )
                        );

        if (teacher.getRole()
                != Role.TEACHER) {

            throw new ApiException(
                    "Only Teachers can access teacher timetable"
            );
        }

        List<Timetable> timetableList =
                timetableRepository
                        .findByTeacherIdOrderByDayAscStartTimeAsc(
                                teacher.getId()
                        );

        return timetableList
                .stream()
                .map(
                        this::convertToResponse
                )
                .toList();
    }

    /**
     * ======================================================
     * Validate Timetable Request
     * ======================================================
     */
    private void validateRequest(
            TimetableRequest request
    ) {

        if (request == null) {
            throw new ApiException(
                    "Timetable data is required"
            );
        }

        if (request.getSubjectId() == null) {
            throw new ApiException(
                    "Subject is required"
            );
        }

        if (request.getTeacherEmail() == null
                || request
                        .getTeacherEmail()
                        .isBlank()) {

            throw new ApiException(
                    "Teacher Email is required"
            );
        }

        if (request.getBranch() == null
                || request
                        .getBranch()
                        .isBlank()) {

            throw new ApiException(
                    "Branch is required"
            );
        }

        if (request.getSemester() == null
                || request.getSemester() < 1
                || request.getSemester() > 8) {

            throw new ApiException(
                    "Semester must be between 1 and 8"
            );
        }

        if (request.getDay() == null) {
            throw new ApiException(
                    "Day is required"
            );
        }

        LocalTime startTime =
                request.getStartTime();

        LocalTime endTime =
                request.getEndTime();

        if (startTime == null) {
            throw new ApiException(
                    "Start Time is required"
            );
        }

        if (endTime == null) {
            throw new ApiException(
                    "End Time is required"
            );
        }

        if (!endTime.isAfter(startTime)) {
            throw new ApiException(
                    "End Time must be after Start Time"
            );
        }
    }

    /**
     * ======================================================
     * Entity → Response DTO
     * ======================================================
     */
    private TimetableResponse convertToResponse(
            Timetable timetable
    ) {

        return TimetableResponse
                .builder()
                .id(
                        timetable.getId()
                )
                .subjectId(
                        timetable
                                .getSubject()
                                .getId()
                )
                .subjectName(
                        timetable
                                .getSubject()
                                .getSubjectName()
                )
                .subjectCode(
                        timetable
                                .getSubject()
                                .getSubjectCode()
                )
                .teacherId(
                        timetable
                                .getTeacher()
                                .getId()
                )
                .teacherName(
                        timetable
                                .getTeacher()
                                .getName()
                )
                .branch(
                        timetable.getBranch()
                )
                .semester(
                        timetable.getSemester()
                )
                .day(
                        timetable.getDay()
                )
                .startTime(
                        timetable.getStartTime()
                )
                .endTime(
                        timetable.getEndTime()
                )
                .build();
    }
}