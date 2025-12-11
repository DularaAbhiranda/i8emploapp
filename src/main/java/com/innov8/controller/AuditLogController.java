package com.innov8.controller;

import com.innov8.dto.AuditLogDTO;
import com.innov8.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Slf4j
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getAllAuditLogs() {
        MDC.put("endpoint", "GET /audit-logs");
        MDC.put("action", "RETRIEVE_AUDIT_LOGS");
        
        try {
            // Get recent 100 logs - can be paginated later
            List<AuditLogDTO> logs = auditLogService.getActivityByDateRange(
                    LocalDateTime.now().minusDays(7), 
                    LocalDateTime.now())
                    .stream()
                    .limit(100)
                    .map(AuditLogDTO::from)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} audit logs", logs.size());
            return ResponseEntity.ok(logs);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<AuditLogDTO>> getUserActivity(@PathVariable String username) {
        MDC.put("endpoint", "GET /audit-logs/user/{username}");
        MDC.put("action", "RETRIEVE_USER_AUDIT");
        MDC.put("username", username);
        
        try {
            List<AuditLogDTO> logs = auditLogService.getUserActivity(username)
                    .stream()
                    .limit(100)
                    .map(AuditLogDTO::from)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} audit logs for user: {}", logs.size(), username);
            return ResponseEntity.ok(logs);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLogDTO>> getActivityByAction(@PathVariable String action) {
        MDC.put("endpoint", "GET /audit-logs/action/{action}");
        MDC.put("action", "RETRIEVE_ACTION_AUDIT");
        MDC.put("actionType", action);
        
        try {
            List<AuditLogDTO> logs = auditLogService.getActivityByAction(action)
                    .stream()
                    .limit(100)
                    .map(AuditLogDTO::from)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} audit logs for action: {}", logs.size(), action);
            return ResponseEntity.ok(logs);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogDTO>> getEntityAuditTrail(
            @PathVariable String entityType, 
            @PathVariable Long entityId) {
        MDC.put("endpoint", "GET /audit-logs/entity/{entityType}/{entityId}");
        MDC.put("action", "RETRIEVE_ENTITY_AUDIT_TRAIL");
        MDC.put("entityType", entityType);
        MDC.put("entityId", String.valueOf(entityId));
        
        try {
            List<AuditLogDTO> logs = auditLogService.getEntityAuditTrail(entityType, entityId)
                    .stream()
                    .map(AuditLogDTO::from)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} audit logs for entity: {} ID: {}", logs.size(), entityType, entityId);
            return ResponseEntity.ok(logs);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/range")
    public ResponseEntity<List<AuditLogDTO>> getActivityByDateRange(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        MDC.put("endpoint", "GET /audit-logs/range");
        MDC.put("action", "RETRIEVE_RANGE_AUDIT");
        MDC.put("startTime", startTime.toString());
        MDC.put("endTime", endTime.toString());
        
        try {
            List<AuditLogDTO> logs = auditLogService.getActivityByDateRange(startTime, endTime)
                    .stream()
                    .limit(100)
                    .map(AuditLogDTO::from)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} audit logs for date range: {} to {}", logs.size(), startTime, endTime);
            return ResponseEntity.ok(logs);
        } finally {
            MDC.clear();
        }
    }
}
