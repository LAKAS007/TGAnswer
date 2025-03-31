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
public class TgSelenium {

    private final WebDriver driver;
    private final MessageExtractorSelenium messageExtractor;

    @Autowired
    public TgSelenium(WebDriver driver, MessageExtractorSelenium messageExtractor) {
        this.driver = driver;
        this.messageExtractor = messageExtractor;
    }

    public List<Message> readMessages() {
        return messageExtractor.extractMessages(24);
    }

    public Optional<Message> readLastMessage() {
        return messageExtractor.extractLastMessage();
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
