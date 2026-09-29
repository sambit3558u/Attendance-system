package com.smartattendance.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartattendance.backend.dto.AcademicStructureRequest;
import com.smartattendance.backend.dto.BranchResponse;
import com.smartattendance.backend.entity.Department;
import com.smartattendance.backend.service.AcademicStructureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/academic")
@RequiredArgsConstructor
public class AdminAcademicStructureController {

    private final AcademicStructureService service;

    /* =========================
       DEPARTMENTS
    ========================= */

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments() {

        return ResponseEntity.ok(
            service.getDepartments()
        );
    }

    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(
            @RequestBody AcademicStructureRequest request
    ) {

        return ResponseEntity.ok(
            service.createDepartment(request)
        );
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,
            @RequestBody AcademicStructureRequest request
    ) {

        return ResponseEntity.ok(
            service.updateDepartment(
                id,
                request
            )
        );
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long id
    ) {

        service.deleteDepartment(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    /* =========================
       BRANCHES
    ========================= */

    @GetMapping("/branches")
    public ResponseEntity<List<BranchResponse>> getBranches(
            @RequestParam(required = false)
            Long departmentId
    ) {

        if (departmentId != null) {

            return ResponseEntity.ok(
                service.getBranches(
                    departmentId
                )
            );
        }

        return ResponseEntity.ok(
            service.getBranches()
        );
    }

    @PostMapping("/branches")
    public ResponseEntity<BranchResponse> createBranch(
            @RequestBody AcademicStructureRequest request
    ) {

        return ResponseEntity.ok(
            service.createBranch(request)
        );
    }

    @PutMapping("/branches/{id}")
    public ResponseEntity<BranchResponse> updateBranch(
            @PathVariable Long id,
            @RequestBody AcademicStructureRequest request
    ) {

        return ResponseEntity.ok(
            service.updateBranch(
                id,
                request
            )
        );
    }

    @DeleteMapping("/branches/{id}")
    public ResponseEntity<Void> deleteBranch(
            @PathVariable Long id
    ) {

        service.deleteBranch(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}