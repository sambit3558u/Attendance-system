package com.smartattendance.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.User;

/**
 * ==========================================================
 * User Repository
 * ----------------------------------------------------------
 * This interface is responsible for all database operations
 * related to the User entity.
 *
 * JpaRepository provides built-in CRUD operations:
 * Save
 * Update
 * Delete
 * Find By Id
 * Find All
 * Count
 * etc.
 * ==========================================================
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ======================================================
    // Find User by Email
    // Used during Login
    // ======================================================
    Optional<User> findByEmail(String email);

    // ======================================================
    // Check Email Already Exists
    // Used during Registration
    // ======================================================
    boolean existsByEmail(String email);

    // ======================================================
    // Check Phone Number Already Exists
    // ======================================================
    boolean existsByPhone(String phone);

    // ======================================================
    // Check Roll Number Already Exists
    // Only for Students
    // ======================================================
    boolean existsByRollNumber(String rollNumber);

}