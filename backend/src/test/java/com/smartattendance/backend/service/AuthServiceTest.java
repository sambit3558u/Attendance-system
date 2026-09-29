package com.smartattendance.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.smartattendance.backend.dto.LoginRequest;
import com.smartattendance.backend.dto.RegisterRequest;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.UserRepository;
import com.smartattendance.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository users;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwt;
    @InjectMocks AuthService service;

    @BeforeEach
    void setup() {
        org.springframework.test.util.ReflectionTestUtils.setField(service, "loginCooldown", 0L);
    }

    @Test
    void rejectsDuplicateEmailOnRegister() {
        when(users.existsByEmail("student@example.com")).thenReturn(true);
        RegisterRequest request = new RegisterRequest();
        request.setName("Student"); request.setEmail("student@example.com");
        request.setPhone("9876543210"); request.setPassword("Secret1!");
        request.setConfirmPassword("Secret1!"); request.setRole(Role.STUDENT);
        request.setRollNumber("R1"); request.setBranch("MCA"); request.setSemester(3);
        assertEquals("Email is already registered", assertThrows(ApiException.class, () -> service.register(request)).getMessage());
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        User user = User.builder().id(7L).name("Teacher").email("teacher@example.com")
                .phone("9876543210").password("encoded").role(Role.TEACHER).department("SOPS").build();
        LoginRequest request = new LoginRequest();
        request.setEmail("teacher@example.com"); request.setPassword("Secret1!"); request.setRole(Role.TEACHER);
        when(users.findByEmail("teacher@example.com")).thenReturn(java.util.Optional.of(user));
        when(encoder.matches("Secret1!", "encoded")).thenReturn(true);
        when(jwt.generateToken(user)).thenReturn("jwt-token");
        when(jwt.getExpirationTime()).thenReturn(3600000L);
        assertEquals("jwt-token", service.login(request).getAccessToken());
    }
}
