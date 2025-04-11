package org.lakas_vaka.auto_answer.configuration;

import org.lakas_vaka.auto_answer.log.FileLogger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {
    private final FileLogger fileLogger = new FileLogger();

    @Bean
    public FileLogger fileLogger() {
        return fileLogger;
    }

    @Bean
    public CommandLineRunner clearLogs() {
        return args -> fileLogger.clearAllLogs();
    }
}
