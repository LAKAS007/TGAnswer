package org.lakas.personalproject.service;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.ChatContext;
import org.lakas.personalproject.model.GlobalContext;
import org.lakas.personalproject.selenium.SeleniumCore;
import org.lakas.personalproject.selenium.SeleniumFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SeleniumService {
    private final GlobalContext globalContext;
    private final SeleniumFactory seleniumFactory;
    private final FileLoggerService loggerService;

    public SeleniumService(@Value("${tg.url}") String tgUrl, GlobalContext globalContext,
                           SeleniumFactory seleniumFactory, FileLoggerService loggerService) {
        this.globalContext = globalContext;
        this.seleniumFactory = seleniumFactory;
        this.loggerService = loggerService;
    }

    @Async
    public void startReplyingTo(String login) {
        ChatContext chatContext = globalContext.getChatContext(login);

        if (chatContext == null) {
            log.info("Setting up selenium core");
            loggerService.clearLogs(login);
            globalContext.registerChatContext(login, ChatContext.EMPTY);
            WebDriver driver = new ChromeDriver();
            SeleniumCore seleniumCore = seleniumFactory.getSeleniumCore(login, driver);
            seleniumCore.start(login);
            return;
        }

        if (!chatContext.isEnabled()) {
            loggerService.clearLogs(login);
            log.info("Reenabling selenium core");
            loggerService.writeLog(login, "Setting up again. Please, wait...");
            WebDriver driver = new ChromeDriver();
            SeleniumCore seleniumCore = seleniumFactory.getSeleniumCore(login, driver);
            seleniumCore.start(login);
            return;
        }

        log.warn("Chat Context [{}] already registered", login);
    }

    public void stopReplyingTo(String login) {
        ChatContext chatCtx = globalContext.getChatContext(login);

        if (chatCtx == null) {
            log.warn("Chat Context [{}] does not exist", login);
            return;
        }

        WebDriver driver = chatCtx.getWebDriver();
        driver.close();
        chatCtx.setEnabled(false);
        globalContext.registerChatContext(login, chatCtx);
        loggerService.writeLog(login, "Automatic web browser has been closed");
    }
}
