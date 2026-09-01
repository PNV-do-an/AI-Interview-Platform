package com.aiinterview.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
public class AiInterviewPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiInterviewPlatformApplication.class, args);
    }
}
