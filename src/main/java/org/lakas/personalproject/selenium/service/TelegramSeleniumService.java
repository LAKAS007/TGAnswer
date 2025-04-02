package org.lakas.personalproject.selenium.service;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.ConversatorType;
import org.lakas.personalproject.model.Gender;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.neural.service.TelegramNeuralService;
import org.lakas.personalproject.selenium.MessageExtractorSelenium;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TelegramSeleniumService {

    private final WebDriver driver;
    private final MessageExtractorSelenium messageExtractor;
    private final TelegramNeuralService neuralService;

    @Autowired
    public TelegramSeleniumService(WebDriver driver, MessageExtractorSelenium messageExtractor,
                                   TelegramNeuralService neuralService) {
        this.driver = driver;
        this.messageExtractor = messageExtractor;
        this.neuralService = neuralService;
    }

    public List<Message> readAllMessages() {
        return messageExtractor.extractMessages(24);
    }

    public List<Message> readConversatorMessages() {
        return messageExtractor.extractMessages(24);
    }

    public Gender defineConversatorGender(List<Message> messages) {
        messages = messages.stream()
                .filter(x -> x.getMessageAuthor() == Message.MessageAuthor.CONVERSATOR)
                .toList();

        return neuralService.defineGender(messages);
    }

    public ConversatorType defineConversatorType(List<Message> messages, String conversatorName) {
        return neuralService.defineConversatorType(messages, conversatorName);
    }

    public String retrieveConversatorName() {
        By findParam = By.xpath("//div[@class='top']");
        return driver.findElement(findParam).getText();
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
