package com.smartattendance.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.smartattendance.backend.entity.Attendance;
import com.smartattendance.backend.entity.ClassSession;
import com.smartattendance.backend.entity.ClassSessionStatus;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceRepository;
import com.smartattendance.backend.repository.ClassSessionRepository;
import com.smartattendance.backend.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class QrAttendanceControllerTest {
    @Mock ClassSessionRepository sessions;
    @Mock AttendanceRepository attendance;
    @Mock UserRepository users;
    private QrAttendanceController controller;
    private User teacher;
    private User student;
    private ClassSession session;

    @BeforeEach
    void setup() {
        controller = new QrAttendanceController(sessions, attendance, users);
        teacher = User.builder().id(1L).role(Role.TEACHER).email("teacher@example.com").build();
        student = User.builder().id(2L).role(Role.STUDENT).email("student@example.com").branch("MCA").semester(3).build();
        session = ClassSession.builder().id(11L).teacher(teacher).branch("MCA").semester(3)
                .subjectName("Python").status(ClassSessionStatus.LIVE).build();
    }

    @Test
    void generatesSessionBoundQrToken() {
        when(sessions.findById(11L)).thenReturn(Optional.of(session));
        Map<?, ?> response = (Map<?, ?>) controller.generate(auth(teacher), 11L);
        assertEquals(11L, response.get("sessionId"));
        org.junit.jupiter.api.Assertions.assertNotNull(response.get("token"));
        assertEquals(15L, response.get("validForSeconds"));
    }

    @Test
    void rejectsNonLiveSessionForQr() {
        session.setStatus(ClassSessionStatus.COMPLETED);
        when(sessions.findById(11L)).thenReturn(Optional.of(session));
        assertEquals("QR is available only for a live class", assertThrows(ApiException.class,
                () -> controller.generate(auth(teacher), 11L)).getMessage());
    }

    @Test
    void rejectsStudentFromDifferentClass() {
        when(sessions.findById(11L)).thenReturn(Optional.of(session));
        when(attendance.findByStudentIdAndClassSessionId(2L, 11L)).thenReturn(Optional.empty());
        student.setBranch("BCOM");
        String token = (String) ((Map<?, ?>) controller.generate(auth(teacher), 11L)).get("token");
        assertThrows(ApiException.class, () -> controller.claim(auth(student), Map.of("token", token)));
    }

    private UsernamePasswordAuthenticationToken auth(User user) {
        return new UsernamePasswordAuthenticationToken(user, null);
    }
}
