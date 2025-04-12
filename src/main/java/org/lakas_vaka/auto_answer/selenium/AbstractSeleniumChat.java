package org.lakas_vaka.auto_answer.selenium;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.model.chat.Message;
import org.lakas_vaka.auto_answer.selenium.mapper.MessageSeleniumMapper;
import org.lakas_vaka.auto_answer.log.FileLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractSeleniumChat implements SeleniumChat {
    private static MessageSeleniumMapper mapper;
    private final FileLogger fileLogger;
    private final int MILLIS_SLEEP_TIME = 100;

    public AbstractSeleniumChat(FileLogger fileLogger) {
        this.fileLogger = fileLogger;
    }

    @Override
    public void writeMessage(String message) {
        var inputLine = getInputLine();
        inputLine.click();
        inputLine.sendKeys(message, Keys.ENTER);
    }

    @Override
    public void openChat() {
        var driver = getDriver();
        driver.get(getChatUrl(getLogin()));
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                        .equals("complete"));
        log.info("Started Selenium Core");
        fileLogger.writeLog(getLogin(), "Automatic web browser has been started");
        log.info("Waiting for authorization");
        fileLogger.writeLog(getLogin(), "Please, authorize in social network. Waiting for it ;)");
        waitForAuthorization();

        log.info("Authorization successful. Reading messages from {}", getLogin());
        fileLogger.writeLog(getLogin(), "Authorization successful. Reading messages from " + getLogin());
        hideWindow();
    }

    @Override
    public void waitForNewMessages(Message lastMessage) {
        LocalDateTime startTime = LocalDateTime.now();
        if (lastMessage == null) {
            log.info("Waiting for any messages");
            fileLogger.writeLog(getLogin(), "Waiting for any message");

            while (true) {
                Message lastMsg = extractLastMessage();
                if (lastMsg != null && lastMsg.getSentAt().isAfter(startTime)) {
                    break;
                }
            }

            log.info("Received new messages");
            fileLogger.writeLog(getLogin(), "Received new message(-s)");
            return;
        }

        log.info("Waiting for new messages");
        Message currentLastMessage = extractLastMessage();
        fileLogger.writeLog(getLogin(), "Waiting for new message(-s)");

        while (currentLastMessage == null || currentLastMessage.getMessageAuthor().equals(Message.MessageAuthor.CLIENT) || lastMessage.equals(currentLastMessage)) {
            sleep(MILLIS_SLEEP_TIME);
            currentLastMessage = extractLastMessage();
        }
        log.info("Received new messages");
        fileLogger.writeLog(getLogin(), "Received new message(-s)");
    }

    @Override
    public Message extractLastMessage() {
        try {
            return extractMessages().getLast();
        } catch (java.util.NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public List<Message> extractMessages() {
        List<WebElement> elements = getDriver().findElements(getMessageFilter());

        if (elements.isEmpty()) {
            return new ArrayList<>();
        }

        return mapToMessages(elements);
    }

    @Override
    public List<Message> extractConversatorMessages() {
        List<WebElement> elements = getDriver().findElements(getConversatorMessageFilter());
        return mapToMessages(elements);
    }

    private List<Message> mapToMessages(List<WebElement> webElements) {
        if (mapper == null) {
            mapper = new MessageSeleniumMapper(getDriver());
        }

        mapper.setDriver(getDriver());

        return webElements.stream().map(mapper::mapToMessage).toList();
    }

    @Override
    public Message extractLastConversatorMessage() {
        try {
            return extractConversatorMessages().getLast();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public String extractConversatorName() {
        return getDriver().findElement(getConversatorNameFilter()).getText();
    }

    @Override
    public WebElement getInputLine() {
        return getDriver().findElement(getInputLineFilter());
    }

    private void waitForAuthorization() {
        WebElement enterLine = null;

        while (enterLine == null) {
            enterLine = tryGetEnterLine();
        }
    }

    private void hideWindow() {
        getDriver().manage().window().setPosition(new Point(-10000, 0));
    }

    private WebElement tryGetEnterLine() {
        try {
            return getInputLine();
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract WebDriver getDriver();

    public abstract String getChatUrl(String login);

    public abstract By getMessageFilter();

    public abstract By getConversatorMessageFilter();

    public abstract By getConversatorNameFilter();

    public abstract By getInputLineFilter();

    public abstract String getLogin();
}
