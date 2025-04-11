package org.lakas_vaka.auto_answer.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileLogger {
    private static final Path LOGS_FILE_PATH = Path.of("data/");

    public List<String> getLogs(String login) {
        createFileIfNotExists(login);
        try {
            return Files.readAllLines(Path.of(LOGS_FILE_PATH + "/" + login));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLog(String login, String log) {
        LocalDateTime currentTime = LocalDateTime.now();
        String timeStr = currentTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        createFileIfNotExists(login);

        try {
            Files.writeString(Path.of(LOGS_FILE_PATH + "/" + login), String.format("[%s] %s\n", timeStr, log),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearLogs(String login) {
        try {
            Files.writeString(Path.of(LOGS_FILE_PATH + "/" + login), "", StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
        }
    }

    public void clearAllLogs() {
        File folder = new File(LOGS_FILE_PATH.toString());
        File[] files = folder.listFiles();

        for (File file : files) {
            file.delete();
        }
    }

    private void createFileIfNotExists(String fileName) {
        File file = new File(LOGS_FILE_PATH + "/" + fileName);

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
