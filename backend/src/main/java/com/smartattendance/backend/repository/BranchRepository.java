package com.smartattendance.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.Branch;

@Repository
public interface BranchRepository
        extends JpaRepository<Branch, Long> {

    List<Branch> findAllByOrderByNameAsc();

    List<Branch>
    findByDepartmentIdOrderByNameAsc(
            Long departmentId
    );

    boolean
    existsByDepartmentIdAndNameIgnoreCase(
            Long departmentId,
            String name
    );

    boolean
    existsByDepartmentIdAndNameIgnoreCaseAndIdNot(
            Long departmentId,
            String name,
            Long id
    );
}