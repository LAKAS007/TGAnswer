package org.lakas.personalproject.selenium;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.exception.NeuralServiceIsNotAvailableException;
import org.lakas.personalproject.model.*;
import org.lakas.personalproject.selenium.service.TelegramSeleniumService;
import org.lakas.personalproject.service.FileLoggerService;
import org.lakas.personalproject.service.MessageProducerService;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Builder
public class SeleniumCore {
    private final String TG_URL;
    private final AuthorizationSelenium authorizationSelenium;
    private final TelegramSeleniumService tgService;
    private final WebDriver driver;
    private final MessageProducerService messageService;
    private final FileLoggerService loggerService;
    private final GlobalContext globalContext;
    private final String login;
    private final int MILLIS_SLEEP_TIME = 1000;

    public SeleniumCore(String tgUrl, AuthorizationSelenium authorizationSelenium,
                        TelegramSeleniumService tgService, WebDriver driver,
                        MessageProducerService messageService, FileLoggerService loggerService,
                        GlobalContext globalContext, String login) {
        TG_URL = tgUrl;
        this.authorizationSelenium = authorizationSelenium;
        this.tgService = tgService;
        this.driver = driver;
        this.messageService = messageService;
        this.loggerService = loggerService;
        this.globalContext = globalContext;
        this.login = login;
    }

    @Async
    public void start(String login) {
        Optional<Message> lastMsg = Optional.empty();

        setupOnDialog(login);
        sleep(1000);

        ChatContext chatCtx = setupChatContext(login);
        globalContext.registerChatContext(login, chatCtx);
        log.info("Chat context: {}", chatCtx);

        runLoop(login, lastMsg, chatCtx);

    }

    private void runLoop(String login, Optional<Message> lastMsg, ChatContext chatCtx) {
        while (true) {
            waitForNewMessages(lastMsg);
            chatCtx = updateChatContext(chatCtx);
            globalContext.registerChatContext(login, chatCtx);
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
        loggerService.writeLog(login, "Waiting for new message(-s)");

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
        loggerService.writeLog(login, "Waiting for any messages");

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
        loggerService.writeLog(login, "Waiting neural network to response... ");
        String msg;

        try {
            msg = messageService.getMessage(chatContext);
        } catch (NeuralServiceIsNotAvailableException ex) {
            loggerService.writeLog(login, "Unfortunately, there was an error with neural network response :(");
            return;
        }

        log.info("Got message from neural network: {}", msg);
        loggerService.writeLog(login, "Got neural network response");

        try {
            tgService.writeMessage(msg);
        } catch (WebDriverException ex) {
            log.error("Exception while writing message", ex); // TODO Fix problem with infinite loop
            return;
        }

        log.info("Sent generated message to {}", login);
        loggerService.writeLog(login, "Sent generated message to " + login);
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
        loggerService.writeLog(login, "Automatic web browser has been started");
        log.info("Waiting for authorization");
        loggerService.writeLog(login, "Please, authorize in Telegram. Waiting for it ;)");
        authorizationSelenium.waitForAuthorization();

        log.info("Authorization successful. Reading messages from {}", login);
        loggerService.writeLog(login, "Authorization successful. Reading messages from " + login);
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
        loggerService.writeLog(login, "Defining conversator gender, because it wasn't specified...");
        Gender conversatorGender = tgService.defineConversatorGender(messages);
        List<String> additionalInfo = new ArrayList<>();
        log.info("Defining conversator status...");
        loggerService.writeLog(login, "Defining conversator status, because it wasn't specified...");
        ConversatorType conversatorType = tgService.defineConversatorType(messages, conversatorName);
        log.info("Chat context setup completed");
        loggerService.writeLog(login, "Initial setup has been completed successfully!");

        return ChatContext.builder()
                .webDriver(driver)
                .telegramLogin(telegramLogin)
                .messages(messages)
                .conversatorGender(conversatorGender)
                .conversatorName(conversatorName)
                .additionalInformation(additionalInfo)
                .conversatorType(conversatorType)
                .isEnabled(true)
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
                .webDriver(chatContext.getWebDriver())
                .telegramLogin(chatContext.getTelegramLogin())
                .messages(messages)
                .conversatorGender(chatContext.getConversatorGender())
                .conversatorName(chatContext.getConversatorName())
                .additionalInformation(chatContext.getAdditionalInformation())
                .conversatorType(chatContext.getConversatorType())
                .isEnabled(chatContext.isEnabled())
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
