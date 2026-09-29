package com.smartattendance.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(
            String email
    );

    boolean existsByEmail(
            String email
    );

    boolean existsByPhone(
            String phone
    );

    boolean existsByRollNumber(
            String rollNumber
    );

    // =====================================================
    // CLASS ELIGIBLE STUDENTS
    // -----------------------------------------------------
    // Teacher End Attendance ke time:
    // same branch + semester ke students load honge.
    // =====================================================

    List<User>
    findByRoleAndBranchIgnoreCaseAndSemester(
            Role role,
            String branch,
            Integer semester
    );
}