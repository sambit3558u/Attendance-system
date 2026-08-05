package com.smartattendance.backend.entity;

// ==========================
// JPA (Hibernate) Imports
// ==========================
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// ======================================================
// @Entity
// Is class ko database table banata hai.
//
// @Table
// Database table ka naam "users" rakhega.
// ======================================================
@Entity
@Table(name = "users")

// ======================================================
// Lombok Annotations
// ======================================================

// Automatically Getter methods ban jayenge.
@Getter

// Automatically Setter methods ban jayenge.
@Setter

// Empty constructor create karega.
@NoArgsConstructor

// All fields wala constructor create karega.
@AllArgsConstructor

// Builder Pattern provide karega.
@Builder
public class User {

    // ======================================================
    // Primary Key
    // Auto Increment ID
    // ======================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ======================================================
    // Common User Details
    // ======================================================

    // User ka Full Name
    @Column(nullable = false)
    private String name;

    // Email unique hoga.
    @Column(nullable = false, unique = true)
    private String email;

    // Phone Number unique hoga.
    // Length 10 digits.
    @Column(nullable = false, unique = true, length = 10)
    private String phone;

    // Password yahan encrypted (BCrypt) form me save hoga.
    @Column(nullable = false)
    private String password;

    // ======================================================
    // Role
    // TEACHER ya STUDENT
    //
    // EnumType.STRING
    // Database me "TEACHER"
    // ya "STUDENT" save hoga.
    // ======================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ======================================================
    // Teacher Details
    // ======================================================

    // Sirf Teacher ke liye.
    // Student ke liye NULL rahega.
    private String department;

    // ======================================================
    // Student Details
    // ======================================================

    // Roll Number unique hoga.
    // Teacher ke liye NULL rahega.
    @Column(unique = true)
    private String rollNumber;

    // Student Branch
    private String branch;

    // Semester
    private Integer semester;

    // Device ID
    // First Login ke baad save hoga.
    // Ek Student sirf ek device se login kar sakega.
    private String deviceId;

    // ======================================================
    // Security Fields
    // ======================================================

    // User ne last logout kab kiya.
    private LocalDateTime lastLogoutAt;

    // Logout ke baad
    // 5 Minutes login block karna ho to use hoga.
    private LocalDateTime loginBlockedUntil;

    // ======================================================
    // Audit Fields
    // ======================================================

    // Record create hote hi
    // Automatically current Date & Time save hoga.

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Record update hote hi
    // Automatically update ho jayega.

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}