package org.lakas_vaka.auto_answer.selenium.mapper;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.model.chat.Message;
import org.openqa.selenium.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class MessageSeleniumMapper {
    private WebDriver driver;

    public MessageSeleniumMapper(WebDriver driver) {
        this.driver = driver;
    }

    public Message mapToMessage(WebElement element) {
        String text;
        Message.MessageAuthor msgAuthor;

        if (isOutMessage(element)) {
            text = extractTextFromOutMessage(element);
            msgAuthor = Message.MessageAuthor.CLIENT;
        } else {
            text = extractTextFromInMessage(element);
            msgAuthor = Message.MessageAuthor.CONVERSATOR;
        }

        if (text.isBlank()) {
            return null;
        }
        LocalDateTime sentAt;

        try {
            sentAt = extractSendingDateTimeFromMessage(element);
        } catch (DateTimeParseException ex) {
            log.error("Invalid datetime format", ex);
            return null;
        }

        return new Message(text, msgAuthor, sentAt);
    }

    private String extractTextFromOutMessage(WebElement element) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(arguments[0].childNodes)" +
                        ".filter(node => node.nodeType === Node.TEXT_NODE)" +
                        ".map(node => node.textContent.trim())" +
                        ".join(' ');", element);
    }

    private String extractTextFromInMessage(WebElement element) {
        return element.findElement(By.xpath(".//span[contains(@class, 'translatable-message')]")).getText();
    }

    private LocalDateTime extractSendingDateTimeFromMessage(WebElement element) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm:ss");
        return LocalDateTime.parse(element.findElement(By.className("time-inner")).getDomAttribute("title"), formatter);
    }

    private boolean isOutMessage(WebElement element) {
        try {
            element.findElement(By.xpath(".//span[contains(@class, 'translatable-message')]"));
            return false;
        } catch (NoSuchElementException ex) {
            return true;
        }
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
