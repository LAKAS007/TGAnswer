package org.lakas.personalproject.selenium;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.Message;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MessageExtractorSelenium {
    private final WebDriver driver;

    @Autowired
    public MessageExtractorSelenium(WebDriver driver) {
        this.driver = driver;
    }

    public List<Message> extractMessages(int n) {
        List<WebElement> messageElements = getMessageElements();
        int messageElementsSize = messageElements.size();

        if (messageElementsSize == 0) {
            return new ArrayList<>();
        }

        List<Message> messages = new ArrayList<>();

        if (messageElements.size() < n) {
            log.warn("Number of read messages ({}) is less than then desired size of reading ({})", messageElementsSize, n);
            n = messageElementsSize;
        }

        for (int i = messageElementsSize - n; i < messageElementsSize; i++) {
            WebElement element = messageElements.get(i);

            Message msg = convertToMessage(element);
            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    public Optional<Message> extractLastMessage() {
        By findParameter = By.xpath("//div[contains(@class, 'message') and contains(@class, 'spoilers-container')]");
        WebElement lastElement;

        try {
            lastElement = driver.findElements(findParameter).getLast();
        } catch (Exception ex) {
            return Optional.empty();
        }

        Message message = convertToMessage(lastElement);

        if (message == null || message.getMessageAuthor().equals(Message.MessageAuthor.CLIENT)) {
            return Optional.empty();
        }

        return Optional.of(message);
    }

    private List<WebElement> getMessageElements() {
        By findParameter = By.xpath("//div[contains(@class, 'message') and contains(@class, 'spoilers-container')]");
        return driver.findElements(findParameter);
    }

    private Message convertToMessage(WebElement messageElement) {
        String text;
        Message.MessageAuthor msgAuthor;

        if (isOutMessage(messageElement)) {
            text = extractTextFromOutMessage(messageElement);
            msgAuthor = Message.MessageAuthor.CLIENT;
        } else {
            text = extractTextFromInMessage(messageElement);
            msgAuthor = Message.MessageAuthor.CONVERSATOR;
        }

        if (text.isBlank()) {
            return null;
        }
        LocalDateTime sentAt;

        try {
            sentAt = extractSendingDateTimeFromMessage(messageElement);
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


}
