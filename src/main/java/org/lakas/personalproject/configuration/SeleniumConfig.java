package org.lakas.personalproject.configuration;

import org.lakas.personalproject.service.FileLoggerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {
    private final FileLoggerService fileLoggerService;

    public SeleniumConfig(FileLoggerService fileLoggerService) {
        this.fileLoggerService = fileLoggerService;
    }

    @Bean
    public CommandLineRunner clearLogs() {
        return args -> fileLoggerService.clearAllLogs();
    }
}
