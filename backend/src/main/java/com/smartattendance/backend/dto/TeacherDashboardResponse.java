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
public class TeacherDashboardResponse {

    private TeacherInfo teacher;

    private long totalStudents;

    /*
     * Full academic year scheduled class count.
     */
    private long totalClasses;

    /*
     * Current date/day ki classes.
     */
    private long classesToday;

    private double averageAttendance;

    /*
     * Teacher's own attendance today.
     */
    private String teacherAttendanceStatus;

    private Double teacherAttendanceDistance;

    /*
     * Today's scheduled classes.
     */
    private List<ScheduleItem> todaySchedule;

    /*
     * Teacher's own monthly attendance.
     */
    private AttendanceOverview attendanceOverview;

    private List<RecentClassItem> recentClasses;

    /*
     * Completed classes.
     */
    private List<MyClassItem> myClasses;

    private List<TopAttendanceItem> topAttendance;


    // =====================================================
    // TEACHER
    // =====================================================

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherInfo {

        private String name;

        private String department;
    }


    // =====================================================
    // TODAY SCHEDULE
    // =====================================================

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleItem {

        /*
         * Timetable ID.
         */
        private Long id;

        /*
         * Existing class session ID if teacher
         * already started this class today.
         */
        private Long sessionId;

        /*
         * Example:
         * 10:00 AM - 11:00 AM
         */
        private String time;

        private String subject;

        private String branch;

        private Integer semester;

        /*
         * SCHEDULED
         * LIVE
         * COMPLETED
         */
        private String status;
    }


    // =====================================================
    // TEACHER OWN ATTENDANCE
    // =====================================================

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceOverview {

        private double presentPercentage;

        private double absentPercentage;

        private long presentCount;

        private long absentCount;

        private long totalMarkedDays;
    }


    // =====================================================
    // RECENT CLASS
    // =====================================================

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentClassItem {

        private Long id;

        private String subject;

        private String course;

        private String time;

        private String status;
    }


    // =====================================================
    // COMPLETED / MY CLASSES
    // =====================================================

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyClassItem {

        private Long id;

        private String className;

        private String course;

        private long studentCount;

        private String schedule;
    }


    // =====================================================
    // TOP ATTENDANCE
    // =====================================================

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopAttendanceItem {

        private Long id;

        private String subject;

        private String course;

        private double percentage;
    }
}