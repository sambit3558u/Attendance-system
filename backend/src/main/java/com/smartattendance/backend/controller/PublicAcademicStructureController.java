package com.smartattendance.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.BranchResponse;
import com.smartattendance.backend.entity.Department;
import com.smartattendance.backend.service.AcademicStructureService;

import lombok.RequiredArgsConstructor;

/**
 * Read-only academic catalog used by the public registration form. Admin-only
 * create, update and delete operations remain under /api/admin/academic.
 */
@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
public class PublicAcademicStructureController {

    private final AcademicStructureService service;

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(service.getDepartments());
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BranchResponse>> getBranches(
            @RequestParam(required = false) Long departmentId
    ) {
        return ResponseEntity.ok(
                departmentId == null
                        ? service.getBranches()
                        : service.getBranches(departmentId)
        );
    }
}
