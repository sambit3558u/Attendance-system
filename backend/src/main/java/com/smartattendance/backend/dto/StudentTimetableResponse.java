package com.smartattendance.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Student timetable page ka complete response.
 *
 * Student ki current information users table se aayegi.
 * Timetable current branch + current semester ke according aayega.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTimetableResponse {

    private Long userId;

    private String name;

    private String rollNumber;

    private String branch;

    private Integer semester;

    private List<TimetableResponse> timetable;
}