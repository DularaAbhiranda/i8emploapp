package com.innov8.job;

import com.innov8.service.PersonnelService;
import com.innov8.service.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthCheckJob {

    private final PersonnelService personnelService;
    private final SecurityAuditService securityAuditService;

    /**
     * Runs every 60 seconds to check for inactive personnel and log summary.
     * This creates regular background tasks visible in APM.
     */
    @Scheduled(fixedRate = 60000)
    public void healthCheckAndAudit() {
        long startTime = System.currentTimeMillis();
        String jobId = java.util.UUID.randomUUID().toString();

        MDC.put("jobId", jobId);
        MDC.put("action", "HEALTH_CHECK");

        try {
            // Check for inactive personnel
            var inactivePersonnel = personnelService.getInactivePersonnel();
            
            MDC.put("status", "COMPLETED");
            MDC.put("inactiveCount", String.valueOf(inactivePersonnel.size()));
            MDC.put("executionTime", String.valueOf(System.currentTimeMillis() - startTime));

            log.info("Health check completed. Inactive personnel count: {}", inactivePersonnel.size());

            if (!inactivePersonnel.isEmpty()) {
                log.warn("Found {} inactive personnel members during health check", inactivePersonnel.size());
                inactivePersonnel.forEach(p -> log.debug("Inactive: {} - {}", p.getName(), p.getDepartment()));
            }

            // Trigger deep security scan asynchronously
            securityAuditService.runDeepScan();

        } catch (Exception e) {
            MDC.put("status", "FAILED");
            log.error("Health check failed: {}", jobId, e);
        } finally {
            MDC.clear();
        }
    }

}
