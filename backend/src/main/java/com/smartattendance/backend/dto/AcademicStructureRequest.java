package com.smartattendance.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcademicStructureRequest {

    private String name;

    private Long departmentId;
}