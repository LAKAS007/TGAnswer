package org.lakas_vaka.auto_answer.selenium.legacy;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.model.chat.ConversatorType;
import org.lakas_vaka.auto_answer.model.chat.Gender;
import org.lakas_vaka.auto_answer.model.chat.Message;
import org.lakas_vaka.auto_answer.neural.service.TelegramNeuralService;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Optional;

@Slf4j
public class TelegramSeleniumService {
    private final TelegramNeuralService neuralService;
    private final MessageExtractorSelenium messageExtractor;

    public TelegramSeleniumService(TelegramNeuralService neuralService, MessageExtractorSelenium messageExtractor) {
        this.neuralService = neuralService;
        this.messageExtractor = messageExtractor;
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
       return messageExtractor.retrieveConversatorName();
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
        return messageExtractor.getEnterLine();
    }
}
