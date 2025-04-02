package org.lakas.personalproject.configuration;

import org.lakas.personalproject.model.GlobalContext;
import org.lakas.personalproject.service.FileLoggerService;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
    public WebDriver getDriver() {
        return new ChromeDriver();
    }

    @Bean
    public CommandLineRunner clearLogs() {
        return args -> fileLoggerService.clearLogs();
    }
}
