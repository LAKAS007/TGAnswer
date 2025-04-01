package org.lakas.personalproject.selenium;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.exception.NeuralServiceIsNotAvailableException;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.model.MessageContext;
import org.lakas.personalproject.service.MessageProducerService;
import org.lakas.personalproject.service.SeleniumLoggerService;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SeleniumCore {
    private final String TG_URL;
    private final AuthorizationSelenium authorizationSelenium;
    private final TgSelenium tgSelenium;
    private final WebDriver driver;
    private final MessageProducerService messageService;
    private final SeleniumLoggerService loggerService;
    private final int MILLIS_SLEEP_TIME = 1000;

    @Autowired
    public SeleniumCore(@Value("${tg.url}") String tgUrl, AuthorizationSelenium authorizationSelenium,
                        TgSelenium tgSelenium, WebDriver driver,
                        MessageProducerService messageService, SeleniumLoggerService loggerService) {
        TG_URL = tgUrl;
        this.authorizationSelenium = authorizationSelenium;
        this.tgSelenium = tgSelenium;
        this.driver = driver;
        this.messageService = messageService;
        this.loggerService = loggerService;
    }

    @Async
    public void start(String login) {
        while (true) {
            MessageContext lastMsgCtx = replyTo(login);
            Optional<Message> lastMsg = getLastMessageFromMsgCtx(lastMsgCtx);
            waitUntilNewMessages(lastMsg);
            log.info("New messages received");
            loggerService.writeLog("New message(-s) were received");
        }
    }

    private void waitUntilNewMessages(Optional<Message> optionalOldLastMsg) {
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

    private MessageContext replyTo(String login) {
        if (driver.getCurrentUrl() == null || !driver.getCurrentUrl().contains(login)) {
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
        }

        MessageContext msgCtx = getCurrentMessageContext();

        if (msgCtx.isEmpty()) {
            return MessageContext.EMPTY;
        }

        log.info("Waiting neural network to response");
        loggerService.writeLog("Waiting neural network to response... ");
        String msg;

        try {
            msg = messageService.getMessage(msgCtx);
        } catch (NeuralServiceIsNotAvailableException ex) {
            loggerService.writeLog("Unfortunately, there was an error with neural network response :(");
            return MessageContext.EMPTY;
        }

        log.info("Got message from neural network: {}", msg);
        loggerService.writeLog("Got neural network response");

        try {
            tgSelenium.writeMessage(msg);
        } catch (WebDriverException ex) {
            log.error("Exception while writing message", ex);
            return MessageContext.EMPTY;
        }

        log.info("Sent generated message to {}", login);
        loggerService.writeLog("Sent generated message to " + login);

        return msgCtx;
    }

    private MessageContext getCurrentMessageContext() {
        List<Message> messages = tgSelenium.readMessages();
        log.info("Read {} messages", messages.size());

        if (messages.isEmpty()) {
            log.info("No messages found. No work required");
            return MessageContext.EMPTY;
        }

        return new MessageContext(messages, MessageContext.Gender.MALE);
    }

    private Optional<Message> getLastMessage() {
        return tgSelenium.readLastMessage();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Message> getLastMessageFromMsgCtx(MessageContext msgCtx) {
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
