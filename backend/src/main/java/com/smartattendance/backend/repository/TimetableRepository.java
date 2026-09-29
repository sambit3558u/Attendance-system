package com.smartattendance.backend.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.DayOfWeek;
import com.smartattendance.backend.entity.Timetable;

@Repository
public interface TimetableRepository
        extends JpaRepository<Timetable, Long> {

    List<Timetable>
    findByBranchAndSemesterOrderByDayAscStartTimeAsc(
            String branch,
            Integer semester
    );

    List<Timetable>
    findByBranchAndSemesterAndDayOrderByStartTimeAsc(
            String branch,
            Integer semester,
            DayOfWeek day
    );

    List<Timetable>
    findByTeacherIdOrderByDayAscStartTimeAsc(
            Long teacherId
    );

    /*
     * Logged-in teacher ki aaj ki classes.
     */
    List<Timetable>
    findByTeacherIdAndDayOrderByStartTimeAsc(
            Long teacherId,
            DayOfWeek day
    );

    boolean existsByBranchAndSemesterAndDayAndStartTime(
            String branch,
            Integer semester,
            DayOfWeek day,
            LocalTime startTime
    );
}