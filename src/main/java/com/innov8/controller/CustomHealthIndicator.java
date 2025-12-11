package com.innov8.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for Datadog monitoring.
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "innov8-observability-lab")
                .withDetail("environment", "production")
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
    }

}
