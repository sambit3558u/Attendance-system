package com.smartattendance.backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.AuthResponse;
import com.smartattendance.backend.dto.LoginRequest;
import com.smartattendance.backend.dto.RegisterRequest;
import com.smartattendance.backend.dto.ForgotPasswordRequest;
import com.smartattendance.backend.dto.ResetPasswordRequest;
import com.smartattendance.backend.dto.PasswordResetResponse;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.entity.DeviceApprovalRequest;
import com.smartattendance.backend.entity.DeviceApprovalStatus;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.UserRepository;
import com.smartattendance.backend.repository.DeviceApprovalRequestRepository;
import com.smartattendance.backend.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long RESET_TOKEN_MINUTES = 10;
    private final Map<String, PasswordResetGrant> passwordResetTokens = new ConcurrentHashMap<>();

    private record PasswordResetGrant(String email, Role role, LocalDateTime expiresAt) {}

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DeviceApprovalRequestRepository deviceApprovals;

    @Transactional
    public PasswordResetResponse requestPasswordReset(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException("No account found for this email"));
        if (user.getRole() != Role.TEACHER && user.getRole() != Role.STUDENT) {
            throw new ApiException("Password reset is available only for Teacher and Student accounts");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        passwordResetTokens.put(token, new PasswordResetGrant(email, user.getRole(), LocalDateTime.now().plusMinutes(RESET_TOKEN_MINUTES)));
        return new PasswordResetResponse(true, "Reset token generated. It expires in 10 minutes.", token);
    }

    @Transactional
    public PasswordResetResponse resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) throw new ApiException("Passwords do not match");
        PasswordResetGrant grant = passwordResetTokens.get(request.getToken());
        if (grant == null || LocalDateTime.now().isAfter(grant.expiresAt())) {
            passwordResetTokens.remove(request.getToken());
            throw new ApiException("Reset token is invalid or expired");
        }
        User user = userRepository.findByEmail(grant.email()).orElseThrow(() -> new ApiException("Account not found"));
        if (user.getRole() != grant.role()) throw new ApiException("Invalid reset request");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setLoginBlockedUntil(null);
        userRepository.save(user);
        passwordResetTokens.remove(request.getToken());
        return new PasswordResetResponse(true, "Password reset successful. You can log in now.", null);
    }

    @Value("${auth.login-cooldown}")
    private long loginCooldown;

    /* =====================================================
       REGISTER
    ===================================================== */

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

        /* =========================
           TEACHER
        ========================= */

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

        /* =========================
           STUDENT
        ========================= */

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

            if (
                    semester == null ||
                    semester < 1 ||
                    semester > 8
            ) {
                throw new ApiException(
                        "Semester must be between 1 and 8"
                );
            }

            if (
                    userRepository.existsByRollNumber(
                            rollNumber
                    )
            ) {
                throw new ApiException(
                        "Roll Number is already registered"
                );
            }

            userBuilder
                    .rollNumber(rollNumber)
                    .branch(branch)
                    .semester(semester)
                    .deviceId(null)
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

    /* =====================================================
       LOGIN
    ===================================================== */

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
         * Manual logout ke baad cooldown check.
         */
        validateLoginCooldown(user);

        /*
         * Selected role database role se match hona chahiye.
         */
        // Role is derived from the email account. The client no longer
        // chooses a role, preventing mismatched-role login attempts.

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new ApiException(
                    "Invalid email or password"
            );
        }

        /* =========================
           STUDENT ONE DEVICE LOGIN
        ========================= */

        if (user.getRole() == Role.STUDENT) {

            String currentDeviceId =
                    request.getDeviceId();

            if (
                    currentDeviceId == null ||
                    currentDeviceId.isBlank()
            ) {
                throw new ApiException(
                        "Device ID is required for Student login"
                );
            }

            String normalizedDeviceId =
                    currentDeviceId.trim();

            /*
             * First login:
             * current browser/device ko account se bind karo.
             */
            if (
                    user.getDeviceId() == null ||
                    user.getDeviceId().isBlank()
            ) {

                user.setDeviceId(
                        normalizedDeviceId
                );

                userRepository.save(user);

            /*
             * Existing binding:
             * doosre device se login reject.
             */
            } else if (!user.getDeviceId().equals(normalizedDeviceId)) {
                deviceApprovals.findFirstByStudentIdAndDeviceIdAndStatusOrderByRequestedAtDesc(user.getId(), normalizedDeviceId, DeviceApprovalStatus.PENDING)
                        .ifPresentOrElse(r -> { throw new ApiException("New device approval is pending. Admin approval is required within 24 hours."); }, () -> {
                            LocalDateTime now = LocalDateTime.now();
                            deviceApprovals.save(DeviceApprovalRequest.builder().student(user).deviceId(normalizedDeviceId).requestedAt(now).expiresAt(now.plusHours(24)).status(DeviceApprovalStatus.PENDING).build());
                            throw new ApiException("New device approval request sent to Admin. Try again after approval.");
                        });
            }
        }

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

    /* =====================================================
       LOGOUT
    ===================================================== */

    @Transactional
    public AuthResponse logout(String email) {

        String normalizedEmail =
                normalizeEmail(email);

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(
                        () -> new ApiException(
                                "User not found"
                        )
                );

        LocalDateTime logoutTime =
                LocalDateTime.now();

        user.setLastLogoutAt(
                logoutTime
        );

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

    /* =====================================================
       LOGIN COOLDOWN
    ===================================================== */

    private void validateLoginCooldown(
            User user
    ) {

        LocalDateTime blockedUntil =
                user.getLoginBlockedUntil();

        if (
                blockedUntil != null &&
                LocalDateTime.now()
                        .isBefore(blockedUntil)
        ) {

            long remainingSeconds =
                    Duration
                            .between(
                                    LocalDateTime.now(),
                                    blockedUntil
                            )
                            .toSeconds();

            long remainingMinutes =
                    Math.max(
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

    /* =====================================================
       PASSWORD VALIDATION
    ===================================================== */

    private void validatePasswords(
            String password,
            String confirmPassword
    ) {

        if (
                password == null ||
                password.isBlank()
        ) {
            throw new ApiException(
                    "Password is required"
            );
        }

        if (password.length() < 6) {
            throw new ApiException(
                    "Password must contain at least 6 characters"
            );
        }

        if (
                confirmPassword == null ||
                confirmPassword.isBlank()
        ) {
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

    /* =====================================================
       EMAIL NORMALIZATION
    ===================================================== */

    private String normalizeEmail(
            String email
    ) {

        if (
                email == null ||
                email.isBlank()
        ) {
            throw new ApiException(
                    "Email is required"
            );
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    /* =====================================================
       PHONE NORMALIZATION
    ===================================================== */

    private String normalizePhone(
            String phone
    ) {

        if (
                phone == null ||
                phone.isBlank()
        ) {
            throw new ApiException(
                    "Phone Number is required"
            );
        }

        String normalizedPhone =
                phone.trim();

        if (
                !normalizedPhone.matches(
                        "\\d{10}"
                )
        ) {
            throw new ApiException(
                    "Phone Number must contain exactly 10 digits"
            );
        }

        return normalizedPhone;
    }

    /* =====================================================
       REQUIRED TEXT
    ===================================================== */

    private String normalizeRequiredText(
            String value,
            String errorMessage
    ) {

        if (
                value == null ||
                value.isBlank()
        ) {
            throw new ApiException(
                    errorMessage
            );
        }

        return value.trim();
    }
}
