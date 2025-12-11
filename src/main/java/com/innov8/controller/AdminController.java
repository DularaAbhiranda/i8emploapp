package com.innov8.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.MDC;

/**
 * Admin controller for testing and simulation purposes.
 * IMPORTANT: This should ONLY be exposed in development/staging environments!
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    /**
     * Hidden endpoint to simulate critical errors for Datadog Error Tracking testing.
     * This intentionally throws a RuntimeException to test alerting capabilities.
     * 
     * SECURITY WARNING: This endpoint should be protected in production!
     */
    @GetMapping("/simulate-error")
    public void simulateError() {
        MDC.put("endpoint", "GET /admin/simulate-error");
        MDC.put("action", "SIMULATE_ERROR");
        MDC.put("status", "CRITICAL");

        log.error("SIMULATED CRITICAL FAILURE - Testing Datadog Error Tracking");
        
        throw new RuntimeException("Simulated Critical Failure: This is a test error for Datadog alerting and error tracking verification.");
    }

}
