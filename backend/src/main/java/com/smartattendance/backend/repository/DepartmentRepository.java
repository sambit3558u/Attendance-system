package com.smartattendance.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartattendance.backend.entity.Department;

@Repository
public interface DepartmentRepository
        extends JpaRepository<Department, Long> {

    List<Department> findAllByOrderByNameAsc();

    Optional<Department> findByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            Long id
    );
}