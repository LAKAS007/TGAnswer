package org.lakas.personalproject.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FileLoggerService {
    private static final Path LOGS_FILE_PATH = Path.of("data/selenium-logs.txt");
    public List<String> getLogs() {
        try {
            return Files.readAllLines(LOGS_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLog(String log) {
        LocalDateTime currentTime = LocalDateTime.now();
        String timeStr = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        try {
            Files.writeString(LOGS_FILE_PATH, String.format("[%s] %s\n", timeStr, log), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearLogs() {
        try {
            Files.writeString(LOGS_FILE_PATH, "", StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
