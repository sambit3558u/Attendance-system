package com.smartattendance.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.entity.Attendance;
import com.smartattendance.backend.entity.AttendanceStatus;
import com.smartattendance.backend.entity.ClassSession;
import com.smartattendance.backend.entity.ClassSessionStatus;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.AttendanceRepository;
import com.smartattendance.backend.repository.ClassSessionRepository;
import com.smartattendance.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QrAttendanceController {
    private static final long QR_SECONDS = 15;
    private static final Map<String, QrGrant> TOKENS = new ConcurrentHashMap<>();
    private final ClassSessionRepository sessions;
    private final AttendanceRepository attendance;
    private final UserRepository users;

    private record QrGrant(Long sessionId, LocalDateTime expiresAt) {}

    @PostMapping("/teacher/attendance/{sessionId}/qr")
    public Object generate(Authentication auth, @PathVariable Long sessionId) {
        User teacher = principal(auth);
        ClassSession session = ownedLiveSession(teacher, sessionId);
        TOKENS.entrySet().removeIf(entry -> entry.getValue().sessionId().equals(sessionId));
        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(QR_SECONDS);
        TOKENS.put(token, new QrGrant(session.getId(), expiresAt));
        return Map.of("token", token, "sessionId", sessionId, "expiresAt", expiresAt, "validForSeconds", QR_SECONDS);
    }

    @GetMapping("/teacher/attendance/{sessionId}/students")
    @Transactional(readOnly = true)
    public Object students(Authentication auth, @PathVariable Long sessionId) {
        User teacher = principal(auth);
        ClassSession session = ownedSession(teacher, sessionId);
            return users.findByRoleAndBranchIgnoreCaseAndSemester(Role.STUDENT, session.getBranch(), session.getSemester()).stream().map(student -> {
            Attendance row = attendance.findByStudentIdAndClassSessionId(student.getId(), sessionId).orElse(null);
            String rollNumber = student.getRollNumber();
            if (rollNumber == null || rollNumber.isBlank() || rollNumber.equalsIgnoreCase(student.getEmail())) {
                rollNumber = "—";
            }
            return Map.of("studentId", student.getId(), "name", student.getName(), "rollNumber", rollNumber, "status", row == null ? "NOT_MARKED" : row.getStatus().name());
        }).toList();
    }

    @PatchMapping("/teacher/attendance/{sessionId}/students/{studentId}/present")
    @Transactional
    public Object overridePresent(Authentication auth, @PathVariable Long sessionId, @PathVariable Long studentId) {
        User teacher = principal(auth);
        ClassSession session = ownedSession(teacher, sessionId);
        User student = users.findById(studentId).orElseThrow(() -> new ApiException("Student not found"));
        validateStudent(session, student);
        Attendance row = attendance.findByStudentIdAndClassSessionId(studentId, sessionId).orElseGet(() -> newAttendance(student, session));
        row.setStatus(AttendanceStatus.PRESENT); row.setGeoVerified(false); row.setAttendanceTime(LocalDateTime.now());
        attendance.save(row);
        return Map.of("message", "Student marked Present by teacher", "status", "PRESENT");
    }

    @PostMapping("/student/attendance/qr-claim")
    @Transactional
    public Object claim(Authentication auth, @RequestBody Map<String, String> body) {
        User student = principal(auth);
        if (student.getRole() != Role.STUDENT) throw new ApiException("Only students can claim QR attendance");
        String token = body.get("token");
        QrGrant grant = token == null ? null : TOKENS.get(token);
        if (grant == null || LocalDateTime.now().isAfter(grant.expiresAt())) { if (token != null) TOKENS.remove(token); throw new ApiException("QR expired. Scan the latest QR code."); }
        ClassSession session = sessions.findById(grant.sessionId()).orElseThrow(() -> new ApiException("Class session not found"));
        if (session.getStatus() != ClassSessionStatus.LIVE) throw new ApiException("This class is no longer live");
        validateStudent(session, student);
        Attendance row = attendance.findByStudentIdAndClassSessionId(student.getId(), session.getId()).orElseGet(() -> newAttendance(student, session));
        row.setStatus(AttendanceStatus.PRESENT); row.setGeoVerified(false); row.setAttendanceTime(LocalDateTime.now());
        attendance.save(row);
        return Map.of("message", "QR verified. Attendance marked Present.", "sessionId", session.getId(), "status", "PRESENT", "verification", "QR");
    }

    private Attendance newAttendance(User student, ClassSession session) { return Attendance.builder().student(student).classSession(session).subject(session.getSubjectName()).branch(session.getBranch()).semester(session.getSemester()).status(AttendanceStatus.PRESENT).geoVerified(false).allowedRadiusMeters(session.getAttendanceRadiusMeters()).attendanceTime(LocalDateTime.now()).build(); }
    private void validateStudent(ClassSession session, User student) { if (student.getRole() != Role.STUDENT || student.getBranch() == null || !student.getBranch().equalsIgnoreCase(session.getBranch()) || !session.getSemester().equals(student.getSemester())) throw new ApiException("Student is not eligible for this class"); }
    private ClassSession ownedLiveSession(User teacher, Long id) { ClassSession s = ownedSession(teacher,id); if (s.getStatus()!=ClassSessionStatus.LIVE) throw new ApiException("QR is available only for a live class"); return s; }
    private ClassSession ownedSession(User teacher, Long id) { ClassSession s=sessions.findById(id).orElseThrow(()->new ApiException("Class session not found")); if(teacher.getRole()!=Role.TEACHER||s.getTeacher()==null||!teacher.getId().equals(s.getTeacher().getId())) throw new ApiException("You cannot manage another teacher's class"); return s; }
    private User principal(Authentication auth) { if(auth==null||!(auth.getPrincipal() instanceof User user)) throw new ApiException("Authentication required"); return user; }
}
