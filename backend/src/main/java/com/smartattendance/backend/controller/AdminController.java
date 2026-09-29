package com.smartattendance.backend.controller;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.smartattendance.backend.entity.*;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository users;
    private final SubjectRepository subjects;
    private final TimetableRepository timetables;
    private final AttendanceRepository attendance;
    private final ClassSessionRepository sessions;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbc;
    private final DeviceApprovalRequestRepository deviceApprovals;

    @GetMapping("/device-approvals")
    @Transactional
    public List<Map<String,Object>> deviceApprovals() {
        expireDeviceApprovals();
        return deviceApprovals.findByStatusOrderByRequestedAtDesc(DeviceApprovalStatus.PENDING).stream().map(r -> {
            Map<String,Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("studentId", r.getStudent().getId());
            item.put("studentName", r.getStudent().getName());
            item.put("email", r.getStudent().getEmail());
            item.put("deviceId", r.getDeviceId());
            item.put("requestedAt", r.getRequestedAt());
            item.put("expiresAt", r.getExpiresAt());
            item.put("status", r.getStatus());
            return item;
        }).toList();
    }

    @PatchMapping("/device-approvals/{id}/accept")
    @Transactional
    public Map<String,Object> acceptDevice(@PathVariable Long id) { expireDeviceApprovals(); DeviceApprovalRequest r = deviceApprovals.findById(id).orElseThrow(() -> new ApiException("Approval request not found")); if (r.getStatus() != DeviceApprovalStatus.PENDING) throw new ApiException("Approval request is no longer pending"); r.setStatus(DeviceApprovalStatus.APPROVED); r.setDecidedAt(java.time.LocalDateTime.now()); r.getStudent().setDeviceId(r.getDeviceId()); deviceApprovals.save(r); return Map.of("message", "Device approved", "status", r.getStatus()); }

    @PatchMapping("/device-approvals/{id}/reject")
    @Transactional
    public Map<String,Object> rejectDevice(@PathVariable Long id) { expireDeviceApprovals(); DeviceApprovalRequest r = deviceApprovals.findById(id).orElseThrow(() -> new ApiException("Approval request not found")); if (r.getStatus() != DeviceApprovalStatus.PENDING) throw new ApiException("Approval request is no longer pending"); r.setStatus(DeviceApprovalStatus.REJECTED); r.setDecidedAt(java.time.LocalDateTime.now()); deviceApprovals.save(r); return Map.of("message", "Device rejected", "status", r.getStatus()); }

    private void expireDeviceApprovals() { deviceApprovals.findByStatusOrderByRequestedAtDesc(DeviceApprovalStatus.PENDING).forEach(r -> { if (java.time.LocalDateTime.now().isAfter(r.getExpiresAt())) { r.setStatus(DeviceApprovalStatus.EXPIRED); r.setDecidedAt(java.time.LocalDateTime.now()); deviceApprovals.save(r); } }); }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public Map<String,Object> dashboard() {
        Map<String,Object> out=new LinkedHashMap<>();
        out.put("users",users.findAll().stream().map(this::userView).toList());
        out.put("subjects",subjects.findAll());
        out.put("timetables",timetables.findAll().stream().map(this::timetableView).toList());
        out.put("attendance",attendance.findAll().stream().map(this::attendanceView).toList());
        out.put("sessions",sessions.findAll().stream().map(this::sessionView).toList());
        out.put("stats",Map.of("users",users.count(),"students",users.findAll().stream().filter(u->u.getRole()==Role.STUDENT).count(),"teachers",users.findAll().stream().filter(u->u.getRole()==Role.TEACHER).count(),"subjects",subjects.count(),"attendance",attendance.count(),"sessions",sessions.count()));
        return out;
    }

    @PostMapping("/users") public Object createUser(@RequestBody Map<String,Object> r){return userView(saveUser(new User(),r,true));}
    @PutMapping("/users/{id}") public Object updateUser(@PathVariable Long id,@RequestBody Map<String,Object> r){return userView(saveUser(users.findById(id).orElseThrow(()->new ApiException("User not found")),r,false));}
    @DeleteMapping("/users/{id}") @Transactional public ResponseEntity<?> deleteUser(@PathVariable Long id){User u=users.findById(id).orElseThrow(()->new ApiException("User not found"));if(u.getRole()==Role.ADMIN&&users.findAll().stream().filter(x->x.getRole()==Role.ADMIN).count()==1)throw new ApiException("Last admin cannot be deleted");jdbc.update("delete from attendance where student_id=?",id);jdbc.update("delete from student_notifications where student_id=?",id);jdbc.update("delete from student_settings where student_id=?",id);jdbc.update("delete from timetables where teacher_id=?",id);jdbc.update("delete from class_sessions where teacher_id=?",id);users.delete(u);return ResponseEntity.noContent().build();}
    @PatchMapping("/users/{id}/reset-device") public Object resetDevice(@PathVariable Long id){User u=users.findById(id).orElseThrow(()->new ApiException("User not found"));u.setDeviceId(null);return userView(users.save(u));}

    @PostMapping("/subjects") public Subject addSubject(@RequestBody Subject s){s.setId(null);return subjects.save(s);}
    @PutMapping("/subjects/{id}") public Subject editSubject(@PathVariable Long id,@RequestBody Subject s){if(!subjects.existsById(id))throw new ApiException("Subject not found");s.setId(id);return subjects.save(s);}
    @DeleteMapping("/subjects/{id}") @Transactional public ResponseEntity<?> deleteSubject(@PathVariable Long id){jdbc.update("delete from timetables where subject_id=?",id);subjects.deleteById(id);return ResponseEntity.noContent().build();}

    @PostMapping("/timetables") public Object addTimetable(@RequestBody Map<String,Object> r){return timetableView(saveTimetable(new Timetable(),r));}
    @PutMapping("/timetables/{id}") public Object editTimetable(@PathVariable Long id,@RequestBody Map<String,Object> r){return timetableView(saveTimetable(timetables.findById(id).orElseThrow(()->new ApiException("Timetable not found")),r));}
    @DeleteMapping("/timetables/{id}") public ResponseEntity<?> deleteTimetable(@PathVariable Long id){timetables.deleteById(id);return ResponseEntity.noContent().build();}
    @DeleteMapping("/attendance/{id}") public ResponseEntity<?> deleteAttendance(@PathVariable Long id){attendance.deleteById(id);return ResponseEntity.noContent().build();}
    @DeleteMapping("/sessions/{id}") public ResponseEntity<?> deleteSession(@PathVariable Long id){sessions.deleteById(id);return ResponseEntity.noContent().build();}
    @PatchMapping("/sessions/{id}/complete") public Object complete(@PathVariable Long id){ClassSession s=sessions.findById(id).orElseThrow(()->new ApiException("Session not found"));s.setStatus(ClassSessionStatus.COMPLETED);s.setActualEndedAt(java.time.LocalDateTime.now());return sessionView(sessions.save(s));}

    private User saveUser(User u,Map<String,Object> r,boolean creating){String email=text(r,"email").toLowerCase();if(creating&&users.existsByEmail(email))throw new ApiException("Email already exists");u.setName(text(r,"name"));u.setEmail(email);u.setPhone(text(r,"phone"));u.setRole(Role.valueOf(text(r,"role").toUpperCase()));u.setDepartment(value(r,"department"));u.setRollNumber(value(r,"rollNumber"));u.setBranch(value(r,"branch"));u.setSemester(r.get("semester")==null||r.get("semester").toString().isBlank()?null:Integer.valueOf(r.get("semester").toString()));String password=value(r,"password");if(creating&&(password==null||password.length()<6))throw new ApiException("Password must contain at least 6 characters");if(password!=null&&!password.isBlank())u.setPassword(passwordEncoder.encode(password));return users.save(u);}
    private Timetable saveTimetable(Timetable t,Map<String,Object> r){t.setSubject(subjects.findById(Long.valueOf(r.get("subjectId").toString())).orElseThrow(()->new ApiException("Subject not found")));User teacher=users.findById(Long.valueOf(r.get("teacherId").toString())).orElseThrow(()->new ApiException("Teacher not found"));if(teacher.getRole()!=Role.TEACHER)throw new ApiException("Selected user is not a teacher");t.setTeacher(teacher);t.setBranch(text(r,"branch"));t.setSemester(Integer.valueOf(r.get("semester").toString()));t.setDay(DayOfWeek.valueOf(text(r,"day")));t.setStartTime(LocalTime.parse(text(r,"startTime")));t.setEndTime(LocalTime.parse(text(r,"endTime")));return timetables.save(t);}
    private Map<String,Object> userView(User u){Map<String,Object> m=new LinkedHashMap<>();m.put("id",u.getId());m.put("name",u.getName());m.put("email",u.getEmail());m.put("phone",u.getPhone());m.put("role",u.getRole());m.put("department",u.getDepartment());m.put("rollNumber",u.getRollNumber());m.put("branch",u.getBranch());m.put("semester",u.getSemester());m.put("deviceLinked",u.getDeviceId()!=null&&!u.getDeviceId().isBlank());m.put("createdAt",u.getCreatedAt());return m;}
    private Map<String,Object> timetableView(Timetable t){return Map.of("id",t.getId(),"subjectId",t.getSubject().getId(),"subjectName",t.getSubject().getSubjectName(),"teacherId",t.getTeacher().getId(),"teacherName",t.getTeacher().getName(),"branch",t.getBranch(),"semester",t.getSemester(),"day",t.getDay(),"startTime",t.getStartTime(),"endTime",t.getEndTime());}
    private Map<String,Object> attendanceView(Attendance a){return Map.of("id",a.getId(),"studentId",a.getStudent().getId(),"studentName",a.getStudent().getName(),"rollNumber",a.getStudent().getRollNumber()==null?"":a.getStudent().getRollNumber(),"subject",a.getSubject(),"status",a.getStatus(),"geoVerified",a.isGeoVerified(),"attendanceTime",a.getAttendanceTime());}
    private Map<String,Object> sessionView(ClassSession s){return Map.of("id",s.getId(),"teacherId",s.getTeacher().getId(),"teacherName",s.getTeacher().getName(),"subjectName",s.getSubjectName(),"branch",s.getBranch(),"semester",s.getSemester(),"status",s.getStatus(),"startTime",s.getStartTime(),"endTime",s.getEndTime());}
    private String text(Map<String,Object> r,String k){String v=value(r,k);if(v==null||v.isBlank())throw new ApiException(k+" is required");return v.trim();} private String value(Map<String,Object> r,String k){return r.get(k)==null?null:r.get(k).toString();}
}
