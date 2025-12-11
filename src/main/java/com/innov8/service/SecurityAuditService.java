package com.innov8.service;

import com.innov8.model.PersonnelStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;
import com.innov8.repository.PersonnelRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditService {

    private final PersonnelRepository personnelRepository;

    /**
     * Simulates a deep security scan that runs asynchronously.
     * This will be visible in APM traces with longer execution times.
     */
    @Async("taskExecutor")
    public void runDeepScan() {
        long startTime = System.currentTimeMillis();
        String scanId = java.util.UUID.randomUUID().toString();

        MDC.put("scanId", scanId);
        MDC.put("action", "SECURITY_SCAN");
        MDC.put("status", "IN_PROGRESS");

        log.info("Starting deep security scan: {}", scanId);

        try {
            // Simulate CPU-intensive work: random sleep between 2-5 seconds
            long sleepDuration = 2000 + (long) (Math.random() * 3000);
            Thread.sleep(sleepDuration);

            log.debug("Deep scan processing took {}ms", sleepDuration);

            // Perform some operations
            int inactiveCount = personnelRepository.findByStatus(PersonnelStatus.INACTIVE).size();
            
            long executionTime = System.currentTimeMillis() - startTime;
            MDC.put("status", "COMPLETED");
            MDC.put("executionTime", String.valueOf(executionTime));
            MDC.put("inactiveCount", String.valueOf(inactiveCount));

            log.info("Deep security scan completed: {} - Found {} inactive personnel", scanId, inactiveCount);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            MDC.put("status", "INTERRUPTED");
            log.error("Deep scan interrupted: {}", scanId, e);
        } finally {
            MDC.clear();
        }
    }

}
