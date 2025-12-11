package com.innov8.dto;

import com.innov8.model.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {
    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private String username;
    private String ipAddress;
    private String details;
    private Integer responseStatus;
    private Long executionTime;
    private LocalDateTime timestamp;
    private String userAgent;

    public static AuditLogDTO from(AuditLog auditLog) {
        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .username(auditLog.getUsername())
                .ipAddress(auditLog.getIpAddress())
                .details(auditLog.getDetails())
                .responseStatus(auditLog.getResponseStatus())
                .executionTime(auditLog.getExecutionTime())
                .timestamp(auditLog.getTimestamp())
                .userAgent(auditLog.getUserAgent())
                .build();
    }
}
