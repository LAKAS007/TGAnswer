package org.lakas.personalproject.selenium;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.exception.NeuralServiceIsNotAvailableException;
import org.lakas.personalproject.model.*;
import org.lakas.personalproject.selenium.service.TelegramSeleniumService;
import org.lakas.personalproject.service.MessageProducerService;
import org.lakas.personalproject.service.FileLoggerService;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SeleniumCore {
    private final String TG_URL;
    private final AuthorizationSelenium authorizationSelenium;
    private final TelegramSeleniumService tgService;
    private final WebDriver driver;
    private final MessageProducerService messageService;
    private final FileLoggerService loggerService;
    private final GlobalContext globalContext;
    private final int MILLIS_SLEEP_TIME = 1000;

    @Autowired
    public SeleniumCore(@Value("${tg.url}") String tgUrl, AuthorizationSelenium authorizationSelenium,
                        TelegramSeleniumService tgService, WebDriver driver,
                        MessageProducerService messageService, FileLoggerService loggerService,
                        GlobalContext globalContext) {
        TG_URL = tgUrl;
        this.authorizationSelenium = authorizationSelenium;
        this.tgService = tgService;
        this.driver = driver;
        this.messageService = messageService;
        this.loggerService = loggerService;
        this.globalContext = globalContext;
    }

    @Async
    public void start(String login) {
        loggerService.clearLogs();
        Optional<Message> lastMsg = Optional.empty();
        setupOnDialog(login);
        sleep(1000);
        ChatContext chatCtx = setupChatContext(login);
        globalContext.setCurrentChatContext(chatCtx);
        log.info("Chat context: {}", chatCtx);

        while (true) {
            waitForNewMessages(lastMsg);
            chatCtx = updateChatContext(chatCtx);
            sendAnswerTo(chatCtx);
            lastMsg = getLastMsgFromChatCtx(chatCtx);
        }
    }

    private void waitForNewMessages(Optional<Message> optionalOldLastMsg) {
        if (optionalOldLastMsg.isEmpty()) {
            waitUntilAnyMessages();
            return;
        }

        Message oldLastMsg = optionalOldLastMsg.get();
        Optional<Message> optionalLastMsg = getLastMessage();

        if (optionalLastMsg.isEmpty()) {
            waitUntilAnyMessages();
            return;
        }

        Message lastMsg = optionalLastMsg.get();

        log.info("Waiting for new messages");
        loggerService.writeLog("Waiting for new message(-s)");

        while (lastMsg.equals(oldLastMsg)) {
            sleep(MILLIS_SLEEP_TIME);
            optionalLastMsg = getLastMessage();

            if (optionalLastMsg.isEmpty()) {
                waitUntilAnyMessages();
                return;
            }

            lastMsg = optionalLastMsg.get();
        }
    }

    private void waitUntilAnyMessages() {
        log.info("Waiting for any messages");
        loggerService.writeLog("Waiting for any messages");

        while (getLastMessage().isEmpty()) {
            sleep(MILLIS_SLEEP_TIME);
        }
    }

    private void sendAnswerTo(ChatContext chatContext) {
        String login = chatContext.getTelegramLogin();

        if (!isDriverOnDialog(login)) {
            setupOnDialog(login);
        }

        if (chatContext.isEmpty()) {
            return;
        }

        log.info("Waiting neural network to response");
        loggerService.writeLog("Waiting neural network to response... ");
        String msg;

        try {
            msg = messageService.getMessage(chatContext);
        } catch (NeuralServiceIsNotAvailableException ex) {
            loggerService.writeLog("Unfortunately, there was an error with neural network response :(");
            return;
        }

        log.info("Got message from neural network: {}", msg);
        loggerService.writeLog("Got neural network response");

        try {
            tgService.writeMessage(msg);
        } catch (WebDriverException ex) {
            log.error("Exception while writing message", ex); // TODO Fix problem with infinite loop
            return;
        }

        log.info("Sent generated message to {}", login);
        loggerService.writeLog("Sent generated message to " + login);
    }

    private boolean isDriverOnDialog(String login) {
        return driver.getCurrentUrl() != null || driver.getCurrentUrl().contains(login);
    }

    private void setupOnDialog(String login) {
        driver.get(TG_URL + login);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                        .equals("complete"));
        log.info("Started Selenium Core");
        loggerService.writeLog("Automatic web browser has been started");
        log.info("Waiting for authorization");
        loggerService.writeLog("Please, authorize in Telegram. Waiting for it ;)");
        authorizationSelenium.waitForAuthorization();

        log.info("Authorization successful. Reading messages from {}", login);
        loggerService.writeLog("Authorization successful. Reading messages from " + login);
        hideWindow();
    }

    private void hideWindow() {
        driver.manage().window().setPosition(new Point(-10000, 0));
    }

    private ChatContext setupChatContext(String telegramLogin) {
        List<Message> messages = tgService.readAllMessages();

        if (messages.isEmpty()) {
            return ChatContext.EMPTY;
        }

        String conversatorName = tgService.retrieveConversatorName();
        log.info("Defining conversator gender...");
        loggerService.writeLog("Defining conversator gender, because it wasn't specified...");
        Gender conversatorGender = tgService.defineConversatorGender(messages);
        List<String> additionalInfo = new ArrayList<>();
        log.info("Defining conversator status...");
        loggerService.writeLog("Defining conversator status, because it wasn't specified...");
        ConversatorType conversatorType = tgService.defineConversatorType(messages, conversatorName);
        log.info("Chat context setup completed");
        loggerService.writeLog("Initial setup has been completed successfully!");

        return ChatContext.builder()
                .telegramLogin(telegramLogin)
                .messages(messages)
                .conversatorGender(conversatorGender)
                .conversatorName(conversatorName)
                .additionalInformation(additionalInfo)
                .conversatorType(conversatorType)
                .build();
    }

    private ChatContext updateChatContext(ChatContext chatContext) {
        List<Message> messages = tgService.readAllMessages();
        log.info("Read {} messages", messages.size());

        if (messages.isEmpty()) {
            log.info("No messages found");
            return chatContext;
        }

        return ChatContext.builder()
                .telegramLogin(chatContext.getTelegramLogin())
                .messages(messages)
                .conversatorGender(chatContext.getConversatorGender())
                .conversatorName(chatContext.getConversatorName())
                .additionalInformation(chatContext.getAdditionalInformation())
                .conversatorType(chatContext.getConversatorType())
                .build();
    }

    private Optional<Message> getLastMessage() {
        return tgService.readLastMessage();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Message> getLastMsgFromChatCtx(ChatContext msgCtx) {
        if (msgCtx.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(msgCtx.getMessages().stream()
                    .filter(x -> x.getMessageAuthor().equals(Message.MessageAuthor.CONVERSATOR))
                    .sorted(Comparator.comparing(Message::getSentAt).reversed())
                    .toList()
                    .getFirst());
        } catch (NoSuchElementException ex) {
            return Optional.empty();
        }
    }
}
