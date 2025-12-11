package com.innov8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Innov8ObservabilityLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(Innov8ObservabilityLabApplication.class, args);
    }

}
