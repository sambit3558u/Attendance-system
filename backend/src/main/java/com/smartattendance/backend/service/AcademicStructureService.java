package com.smartattendance.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartattendance.backend.dto.AcademicStructureRequest;
import com.smartattendance.backend.dto.BranchResponse;
import com.smartattendance.backend.entity.Branch;
import com.smartattendance.backend.entity.Department;
import com.smartattendance.backend.exception.ApiException;
import com.smartattendance.backend.repository.BranchRepository;
import com.smartattendance.backend.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AcademicStructureService {

    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;

    /* =========================
       DEPARTMENTS
    ========================= */

    public List<Department> getDepartments() {
        return departmentRepository
                .findAllByOrderByNameAsc();
    }

    @Transactional
    public Department createDepartment(
            AcademicStructureRequest request
    ) {

        String name =
                cleanName(request.getName());

        if (
            departmentRepository
                .existsByNameIgnoreCase(name)
        ) {
            throw new ApiException(
                "Department already exists"
            );
        }

        Department department =
                Department.builder()
                        .name(name)
                        .build();

        return departmentRepository.save(
                department
        );
    }

    @Transactional
    public Department updateDepartment(
            Long id,
            AcademicStructureRequest request
    ) {

        Department department =
                departmentRepository
                        .findById(id)
                        .orElseThrow(
                            () -> new ApiException(
                                "Department not found"
                            )
                        );

        String name =
                cleanName(request.getName());

        if (
            departmentRepository
                .existsByNameIgnoreCaseAndIdNot(
                    name,
                    id
                )
        ) {
            throw new ApiException(
                "Department already exists"
            );
        }

        department.setName(name);

        return departmentRepository.save(
                department
        );
    }

    @Transactional
    public void deleteDepartment(Long id) {

        Department department =
                departmentRepository
                        .findById(id)
                        .orElseThrow(
                            () -> new ApiException(
                                "Department not found"
                            )
                        );

        List<Branch> branches =
                branchRepository
                        .findByDepartmentIdOrderByNameAsc(
                            id
                        );

        if (!branches.isEmpty()) {
            throw new ApiException(
                "Delete all branches of this department first"
            );
        }

        departmentRepository.delete(
                department
        );
    }

    /* =========================
       BRANCHES
    ========================= */

    @Transactional(readOnly = true)
    public List<BranchResponse> getBranches() {

        return branchRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(this::toBranchResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getBranches(
            Long departmentId
    ) {

        return branchRepository
                .findByDepartmentIdOrderByNameAsc(
                    departmentId
                )
                .stream()
                .map(this::toBranchResponse)
                .toList();
    }

    @Transactional
    public BranchResponse createBranch(
            AcademicStructureRequest request
    ) {

        if (request.getDepartmentId() == null) {
            throw new ApiException(
                "Department is required"
            );
        }

        Department department =
                departmentRepository
                        .findById(
                            request.getDepartmentId()
                        )
                        .orElseThrow(
                            () -> new ApiException(
                                "Department not found"
                            )
                        );

        String name =
                cleanName(request.getName());

        if (
            branchRepository
                .existsByDepartmentIdAndNameIgnoreCase(
                    department.getId(),
                    name
                )
        ) {
            throw new ApiException(
                "Branch already exists in this department"
            );
        }

        Branch branch =
                Branch.builder()
                        .name(name)
                        .department(department)
                        .build();

        Branch saved =
                branchRepository.save(branch);

        return toBranchResponse(saved);
    }

    @Transactional
    public BranchResponse updateBranch(
            Long id,
            AcademicStructureRequest request
    ) {

        Branch branch =
                branchRepository
                        .findById(id)
                        .orElseThrow(
                            () -> new ApiException(
                                "Branch not found"
                            )
                        );

        if (request.getDepartmentId() == null) {
            throw new ApiException(
                "Department is required"
            );
        }

        Department department =
                departmentRepository
                        .findById(
                            request.getDepartmentId()
                        )
                        .orElseThrow(
                            () -> new ApiException(
                                "Department not found"
                            )
                        );

        String name =
                cleanName(request.getName());

        if (
            branchRepository
                .existsByDepartmentIdAndNameIgnoreCaseAndIdNot(
                    department.getId(),
                    name,
                    id
                )
        ) {
            throw new ApiException(
                "Branch already exists in this department"
            );
        }

        branch.setName(name);
        branch.setDepartment(department);

        Branch saved =
                branchRepository.save(branch);

        return toBranchResponse(saved);
    }

    @Transactional
    public void deleteBranch(Long id) {

        if (!branchRepository.existsById(id)) {
            throw new ApiException(
                "Branch not found"
            );
        }

        branchRepository.deleteById(id);
    }

    /* =========================
       RESPONSE MAPPER
    ========================= */

    private BranchResponse toBranchResponse(
            Branch branch
    ) {

        Department department =
                branch.getDepartment();

        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .departmentId(
                    department.getId()
                )
                .departmentName(
                    department.getName()
                )
                .build();
    }

    /* =========================
       VALIDATION
    ========================= */

    private String cleanName(String name) {

        if (
            name == null ||
            name.trim().isEmpty()
        ) {
            throw new ApiException(
                "Name is required"
            );
        }

        return name.trim();
    }
}