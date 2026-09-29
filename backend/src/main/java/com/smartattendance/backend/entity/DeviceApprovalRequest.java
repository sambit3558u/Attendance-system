package com.smartattendance.backend.entity;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="device_approval_requests") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeviceApprovalRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="student_id", nullable=false) private User student;
 @Column(nullable=false, length=255) private String deviceId;
 @Column(length=255) private String userAgent;
 @Column(length=64) private String ipAddress;
 @Column(nullable=false) private LocalDateTime requestedAt;
 @Column(nullable=false) private LocalDateTime expiresAt;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private DeviceApprovalStatus status;
 private LocalDateTime decidedAt;
}
