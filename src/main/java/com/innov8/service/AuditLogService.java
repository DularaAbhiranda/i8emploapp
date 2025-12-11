package com.innov8.service;

import com.innov8.model.AuditLog;
import com.innov8.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log user activity for Datadog tracking
     */
    public void logActivity(String action, String entityType, Long entityId, String username, 
                           String ipAddress, Integer responseStatus, Long executionTime, 
                           String details, String userAgent) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .username(username)
                    .ipAddress(ipAddress)
                    .responseStatus(responseStatus)
                    .executionTime(executionTime)
                    .details(details)
                    .userAgent(userAgent)
                    .build();

            auditLogRepository.save(auditLog);

            // Also log to application logs for Datadog
            MDC.put("auditAction", action);
            MDC.put("entityType", entityType);
            MDC.put("username", username);
            MDC.put("ipAddress", ipAddress);
            MDC.put("responseStatus", String.valueOf(responseStatus));
            MDC.put("executionTime", String.valueOf(executionTime));

            log.info("User Activity - {}: {} (Entity: {} ID: {}) by {} from {}", 
                    action, details != null ? details : "", entityType, entityId, username, ipAddress);

        } catch (Exception e) {
            log.error("Failed to log audit activity", e);
        }
    }

    /**
     * Get user activity logs
     */
    public List<AuditLog> getUserActivity(String username) {
        return auditLogRepository.findByUsernameOrderByTimestampDesc(username);
    }

    /**
     * Get activity by action type
     */
    public List<AuditLog> getActivityByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }

    /**
     * Get audit trail for specific entity
     */
    public List<AuditLog> getEntityAuditTrail(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    /**
     * Get activity within date range
     */
    public List<AuditLog> getActivityByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findByTimestampRange(startTime, endTime);
    }

    /**
     * Get user activity within date range
     */
    public List<AuditLog> getUserActivityByDateRange(String username, LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findUserActivityByDateRange(username, startTime, endTime);
    }
}
