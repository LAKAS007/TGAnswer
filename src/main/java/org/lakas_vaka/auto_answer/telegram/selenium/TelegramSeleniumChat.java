package org.lakas_vaka.auto_answer.telegram.selenium;

import org.lakas_vaka.auto_answer.log.FileLogger;
import org.lakas_vaka.auto_answer.selenium.AbstractSeleniumChat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TelegramSeleniumChat extends AbstractSeleniumChat {
    private final WebDriver webDriver;
    private final String login;
    private final static String TG_URL = "https://web.telegram.org/k/#";

    public TelegramSeleniumChat(String login, FileLogger fileLogger, WebDriver webDriver) {
        super(fileLogger);
        this.webDriver = webDriver;
        this.login = login;
    }

    @Override
    public WebDriver getDriver() {
        return webDriver;
    }

    @Override
    public String getChatUrl(String login) {
        return TG_URL + login;
    }

    @Override
    public By getMessageFilter() {
        return By.xpath("//div[contains(@class, 'message') and contains(@class, 'spoilers-container')]");
    }

    @Override
    public By getConversatorMessageFilter() {
        return By.xpath("//div[contains(@class, 'message') and contains(@class, 'spoilers-container') and ./*[contains(@class, 'translatable-message')]]");
    }

    @Override
    public By getConversatorNameFilter() {
        return By.xpath("//div[@class='top']");
    }

    @Override
    public By getInputLineFilter() {
        return By.xpath("//div[contains(@class, 'input-message-input') and @data-peer-id]");
    }

    @Override
    public String getLogin() {
        return login;
    }
}
