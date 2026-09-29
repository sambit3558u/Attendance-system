package com.smartattendance.backend.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.smartattendance.backend.entity.*;
public interface DeviceApprovalRequestRepository extends JpaRepository<DeviceApprovalRequest, Long> {
 Optional<DeviceApprovalRequest> findFirstByStudentIdAndDeviceIdAndStatusOrderByRequestedAtDesc(Long studentId, String deviceId, DeviceApprovalStatus status);
 List<DeviceApprovalRequest> findByStatusOrderByRequestedAtDesc(DeviceApprovalStatus status);
}
