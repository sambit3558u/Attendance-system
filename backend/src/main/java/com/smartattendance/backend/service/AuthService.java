package com.smartattendance.backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.AuthResponse;
import com.smartattendance.backend.dto.LoginRequest;
import com.smartattendance.backend.dto.RegisterRequest;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.UserRepository;
import com.smartattendance.backend.security.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * ==========================================================
 * Authentication Service
 * ----------------------------------------------------------
 * This service contains the main authentication logic.
 *
 * Responsibilities:
 *
 * 1. Register Teacher and Student accounts.
 * 2. Validate role-specific registration fields.
 * 3. Check duplicate email, phone and roll number.
 * 4. Hash passwords using BCrypt.
 * 5. Verify login credentials.
 * 6. Verify selected role.
 * 7. Apply one-device login for Students.
 * 8. Generate a 30-day JWT token.
 * 9. Apply a 5-minute login cooldown after manual logout.
 * ==========================================================
 */

@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * Performs database operations for User records.
     */
    private final UserRepository userRepository;

    /**
     * Hashes passwords during registration and verifies
     * passwords during login.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Generates JWT authentication tokens.
     */
    private final JwtService jwtService;

    /**
     * Manual logout cooldown duration.
     *
     * Current value:
     * 300000 milliseconds = 5 minutes
     */
    @Value("${auth.login-cooldown}")
    private long loginCooldown;

    /**
     * ======================================================
     * Register User
     * ------------------------------------------------------
     * Creates either a Teacher or Student account.
     * ======================================================
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String normalizedName = normalizeRequiredText(
                request.getName(),
                "Full Name is required"
        );

        String normalizedEmail = normalizeEmail(
                request.getEmail()
        );

        String normalizedPhone = normalizePhone(
                request.getPhone()
        );

        validatePasswords(
                request.getPassword(),
                request.getConfirmPassword()
        );

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ApiException(
                    "Email is already registered"
            );
        }

        if (userRepository.existsByPhone(normalizedPhone)) {
            throw new ApiException(
                    "Phone number is already registered"
            );
        }

        if (request.getRole() == null) {
            throw new ApiException(
                    "Role is required"
            );
        }

        User.UserBuilder userBuilder = User.builder()
                .name(normalizedName)
                .email(normalizedEmail)
                .phone(normalizedPhone)
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(request.getRole());

        if (request.getRole() == Role.TEACHER) {

            String department = normalizeRequiredText(
                    request.getDepartment(),
                    "Department is required for Teacher"
            );

            userBuilder
                    .department(department)
                    .rollNumber(null)
                    .branch(null)
                    .semester(null)
                    .deviceId(null);

        } else if (request.getRole() == Role.STUDENT) {

            String rollNumber = normalizeRequiredText(
                    request.getRollNumber(),
                    "Roll Number is required for Student"
            );

            String branch = normalizeRequiredText(
                    request.getBranch(),
                    "Branch is required for Student"
            );

            Integer semester = request.getSemester();

            if (semester == null
                    || semester < 1
                    || semester > 8) {

                throw new ApiException(
                        "Semester must be between 1 and 8"
                );
            }

            if (userRepository.existsByRollNumber(rollNumber)) {
                throw new ApiException(
                        "Roll Number is already registered"
                );
            }

            userBuilder
                    .rollNumber(rollNumber)
                    .branch(branch)
                    .semester(semester)

                    /*
                     * Student account register hote waqt
                     * device ID null rahega.
                     *
                     * First login ke time save hoga.
                     */
                    .deviceId(null)

                    /*
                     * Teacher field Student ke liye null.
                     */
                    .department(null);

        } else {
            throw new ApiException(
                    "Invalid role selected"
            );
        }

        User savedUser = userRepository.save(
                userBuilder.build()
        );

        return AuthResponse.builder()
                .success(true)
                .message(
                        savedUser.getRole() == Role.TEACHER
                                ? "Teacher registered successfully"
                                : "Student registered successfully"
                )
                .accessToken(null)
                .expiresIn(null)
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    /**
     * ======================================================
     * Login User
     * ------------------------------------------------------
     * Verifies:
     *
     * 1. Email
     * 2. Password
     * 3. Selected role
     * 4. Login cooldown
     * 5. Student device ID
     *
     * Generates a 30-day JWT token after successful login.
     * ======================================================
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {

        String normalizedEmail = normalizeEmail(
                request.getEmail()
        );

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(
                        () -> new ApiException(
                                "Invalid email or password"
                        )
                );

        /*
         * Manual logout ke baad 5-minute cooldown check.
         */
        validateLoginCooldown(user);

        /*
         * Frontend selected role aur database role
         * same hona chahiye.
         */
        if (request.getRole() == null
                || user.getRole() != request.getRole()) {

            throw new ApiException(
                    "Selected role does not match this account"
            );
        }

        /*
         * Raw password ko stored BCrypt hash se compare karo.
         */
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new ApiException(
                    "Invalid email or password"
            );
        }

        /*
         * ==================================================
         * One Device Login
         * --------------------------------------------------
         * Sirf Student accounts ke liye apply hoga.
         * Teacher multiple devices se login kar sakta hai.
         * ==================================================
         */
        if (user.getRole() == Role.STUDENT) {

            String currentDeviceId = request.getDeviceId();

            /*
             * Student login me frontend deviceId
             * automatically bhejega.
             */
            if (currentDeviceId == null
                    || currentDeviceId.isBlank()) {

                throw new ApiException(
                        "Device ID is required for Student login"
                );
            }

            String normalizedDeviceId =
                    currentDeviceId.trim();

            /*
             * First login:
             *
             * Agar database me deviceId null hai,
             * current browser ka deviceId save karo.
             */
            if (user.getDeviceId() == null
                    || user.getDeviceId().isBlank()) {

                user.setDeviceId(normalizedDeviceId);

                userRepository.save(user);

            /*
             * Next login:
             *
             * Agar stored deviceId aur current deviceId
             * same nahi hain, login reject karo.
             */
            } else if (!user.getDeviceId()
                    .equals(normalizedDeviceId)) {

                throw new ApiException(
                        "This student account is already linked to another device"
                );
            }
        }

        /*
         * Sab checks successful hone ke baad
         * JWT token generate hoga.
         */
        String accessToken =
                jwtService.generateToken(user);

        return AuthResponse.builder()
                .success(true)
                .message("Login successful")
                .accessToken(accessToken)
                .expiresIn(
                        jwtService.getExpirationTime()
                )
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    /**
     * ======================================================
     * Manual Logout
     * ------------------------------------------------------
     * Stores logout time and blocks login for 5 minutes.
     * ======================================================
     */
    @Transactional
    public AuthResponse logout(String email) {

        String normalizedEmail = normalizeEmail(email);

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(
                        () -> new ApiException(
                                "User not found"
                        )
                );

        LocalDateTime logoutTime = LocalDateTime.now();

        user.setLastLogoutAt(logoutTime);

        user.setLoginBlockedUntil(
                logoutTime.plusNanos(
                        loginCooldown * 1_000_000
                )
        );

        userRepository.save(user);

        return AuthResponse.builder()
                .success(true)
                .message(
                        "Logout successful. You can login again after 5 minutes."
                )
                .accessToken(null)
                .expiresIn(null)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    /**
     * ======================================================
     * Validate Login Cooldown
     * ------------------------------------------------------
     * Prevents login until the 5-minute logout cooldown ends.
     * ======================================================
     */
    private void validateLoginCooldown(User user) {

        LocalDateTime blockedUntil =
                user.getLoginBlockedUntil();

        if (blockedUntil != null
                && LocalDateTime.now().isBefore(blockedUntil)) {

            long remainingSeconds = Duration
                    .between(
                            LocalDateTime.now(),
                            blockedUntil
                    )
                    .toSeconds();

            long remainingMinutes = Math.max(
                    1,
                    (remainingSeconds + 59) / 60
            );

            throw new ApiException(
                    "Please wait "
                            + remainingMinutes
                            + " minute(s) before logging in again"
            );
        }
    }

    /**
     * ======================================================
     * Password Validation
     * ======================================================
     */
    private void validatePasswords(
            String password,
            String confirmPassword
    ) {

        if (password == null || password.isBlank()) {
            throw new ApiException(
                    "Password is required"
            );
        }

        if (password.length() < 6) {
            throw new ApiException(
                    "Password must contain at least 6 characters"
            );
        }

        if (confirmPassword == null
                || confirmPassword.isBlank()) {

            throw new ApiException(
                    "Confirm Password is required"
            );
        }

        if (!password.equals(confirmPassword)) {
            throw new ApiException(
                    "Password and Confirm Password do not match"
            );
        }
    }

    /**
     * ======================================================
     * Normalize Email
     * ======================================================
     */
    private String normalizeEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new ApiException(
                    "Email is required"
            );
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    /**
     * ======================================================
     * Normalize Phone Number
     * ======================================================
     */
    private String normalizePhone(String phone) {

        if (phone == null || phone.isBlank()) {
            throw new ApiException(
                    "Phone Number is required"
            );
        }

        String normalizedPhone = phone.trim();

        if (!normalizedPhone.matches("\\d{10}")) {
            throw new ApiException(
                    "Phone Number must contain exactly 10 digits"
            );
        }

        return normalizedPhone;
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

        if (value == null || value.isBlank()) {
            throw new ApiException(
                    errorMessage
            );
        }

        return value.trim();
    }
}