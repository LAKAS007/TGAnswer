package org.lakas.personalproject.configuration;

import org.lakas.personalproject.service.SeleniumLoggerService;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {
    private final SeleniumLoggerService seleniumLoggerService;

    public SeleniumConfig(SeleniumLoggerService seleniumLoggerService) {
        this.seleniumLoggerService = seleniumLoggerService;
    }

    @Bean
    public WebDriver getDriver() {
        return new ChromeDriver();
    }

    @Bean
    public CommandLineRunner clearLogs() {
        return args -> seleniumLoggerService.clearLogs();
    }
}
