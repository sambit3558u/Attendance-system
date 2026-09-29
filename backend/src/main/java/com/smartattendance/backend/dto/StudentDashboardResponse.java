package com.smartattendance.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse {

    // =====================================================
    // STUDENT
    // =====================================================

    private Long userId;

    private String name;

    private String email;

    private String rollNumber;

    private String branch;

    private Integer semester;

    // =====================================================
    // CURRENT SEMESTER ATTENDANCE
    // =====================================================

    private long totalClasses;

    private long presentClasses;

    private long absentClasses;

    private double attendancePercentage;

    // =====================================================
    // LIVE CLASS
    // =====================================================

    private LiveClassResponse liveClass;

    // =====================================================
    // TODAY'S TIMETABLE
    // =====================================================

    private List<StudentDashboardTimetableItem>
            todayTimetable;

    // =====================================================
    // RECENT ATTENDANCE HISTORY
    // =====================================================

    private List<StudentDashboardHistoryItem>
            recentAttendance;

    // =====================================================
    // NOTIFICATIONS
    // =====================================================

    private long unreadNotificationCount;
}