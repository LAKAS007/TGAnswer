package org.lakas_vaka.auto_answer.telegram;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.log.FileLogger;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.lakas_vaka.auto_answer.model.chat.ChatAgent;
import org.lakas_vaka.auto_answer.model.chat.ConversatorType;
import org.lakas_vaka.auto_answer.model.chat.Gender;
import org.lakas_vaka.auto_answer.model.chat.Message;
import org.lakas_vaka.auto_answer.neural.service.SocialNetworkNeuralService;
import org.lakas_vaka.auto_answer.telegram.selenium.TelegramSeleniumChat;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TelegramChatAgent implements ChatAgent {
    private final String login;
    private final TelegramSeleniumChat seleniumChat;
    private final FileLogger fileLogger;
    private final SocialNetworkNeuralService neuralService;

    public TelegramChatAgent(String login, WebDriver webDriver, FileLogger fileLogger,
                             SocialNetworkNeuralService neuralService) {
        this.login = login;
        this.fileLogger = fileLogger;
        this.neuralService = neuralService;
        this.seleniumChat = new TelegramSeleniumChat(login, new FileLogger(), webDriver);
    }

    @Override
    public void setupChatContext(ConversatorContext conversatorContext) {
        List<Message> messages = null;

        if (conversatorContext.getConversatorName() == null || conversatorContext.getConversatorName().isEmpty()) {
            log.info("Defining conversator name...");
            fileLogger.writeLog(login, "Defining conversator name, because it wasn't specified...");
            String conversatorName = seleniumChat.extractConversatorName();
            conversatorContext.setConversatorName(conversatorName);
            fileLogger.writeLog(login, "Conversator name was defined");
        }

        if (conversatorContext.getConversatorGender() == null) {
            log.info("Defining conversator gender...");
            fileLogger.writeLog(login, "Defining conversator gender via neural network, because it wasn't specified...");
            messages = seleniumChat.extractConversatorMessages().stream()
                    .filter(Objects::nonNull)
                    .filter(x -> x.getMessageAuthor() == Message.MessageAuthor.CONVERSATOR)
                    .toList();
            Gender conversatorGender = neuralService.defineGender(messages);
            conversatorContext.setConversatorGender(conversatorGender);
            fileLogger.writeLog(login, "Conversator name was defined");
        }

        if (conversatorContext.getConversatorType() == null || conversatorContext.getConversatorType() == ConversatorType.UNDEFINED) {
            log.info("Defining conversator status...");
            fileLogger.writeLog(login, "Defining communication style via neural network, because it wasn't specified...");

            if (messages == null) {
                messages = seleniumChat.extractConversatorMessages().stream()
                        .filter(x -> x.getMessageAuthor() == Message.MessageAuthor.CONVERSATOR)
                        .toList();
            }

            ConversatorType conversatorType = neuralService.defineConversatorType(messages, conversatorContext.getConversatorName());
            conversatorContext.setConversatorType(conversatorType);
            fileLogger.writeLog(login, "Communication style was defined");
        }

        log.info("Chat context setup completed");
        fileLogger.writeLog(login, "Chat context setup completed successfully. If you want this to be faster, please, supply all information at once");
    }

    @Override
    public List<Message> readActualMessages() {
        return seleniumChat.extractMessages();
    }

    @Override
    public void openChat() {
        seleniumChat.openChat();
    }

    @Override
    public void writeText(String text) {
        seleniumChat.writeMessage(text);
    }

    @Override
    public void waitForNewMessages(Message lastMessage) {
        seleniumChat.waitForNewMessages(lastMessage);
    }

    @Override
    public String getLogin() {
        return login;
    }
}
