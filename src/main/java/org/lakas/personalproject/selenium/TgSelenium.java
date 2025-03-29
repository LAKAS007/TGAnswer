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

@Slf4j
@Component
public class TgSelenium {

    private final WebDriver driver;

    @Autowired
    public TgSelenium(WebDriver driver) {
        this.driver = driver;
    }

    public List<Message> readMessages() {
        By findParameter = By.xpath("//div[contains(@class, 'bubble') and @data-mid]");
        List<WebElement> messageElements = driver.findElements(findParameter);
        List<Message> messages = new ArrayList<>();

        for (WebElement element : messageElements) {
            Message msg = convertToMessage(element);
            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    private Message convertToMessage(WebElement messageElement) {
        String[] split = messageElement.getText().replace("\uE901", "").replaceAll("\n+", "\n").trim().split("\n");
        String text = split[0];

        if (text.isBlank()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm:ss");
        LocalDateTime sentAt;

        try {
            sentAt = LocalDateTime.parse(messageElement.findElement(By.className("time-inner")).getDomAttribute("title"), formatter);
        } catch (DateTimeParseException ex) {
            return null;
        }

        String classname = messageElement.getDomAttribute("class");
        Message.MessageAuthor msgAuthor = (classname.contains("is-out") ? Message.MessageAuthor.CLIENT : Message.MessageAuthor.CONVERSATOR);
        return new Message(text, msgAuthor, sentAt);
    }

    public void writeMessage(String message) {
        WebElement enterLine = getEnterLine();
        enterLine.click();
        enterLine.sendKeys(message, Keys.ENTER);
    }

    private WebElement getEnterLine() {
        By findParameter = By.xpath("//div[contains(@class, 'input-message-input') and @data-peer-id]");
        return driver.findElement(findParameter);
    }
}
