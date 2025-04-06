package org.lakas.personalproject.selenium;

import org.lakas.personalproject.model.GlobalContext;
import org.lakas.personalproject.neural.service.TelegramNeuralService;
import org.lakas.personalproject.selenium.service.TelegramSeleniumService;
import org.lakas.personalproject.service.FileLoggerService;
import org.lakas.personalproject.service.MessageProducerService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SeleniumFactory {
    private final String TG_URL;
    private final MessageProducerService messageProducerService;
    private final FileLoggerService fileLoggerService;
    private final GlobalContext globalContext;
    private final TelegramNeuralService telegramNeuralService;

    public SeleniumFactory(@Value("${tg.url}") String tgUrl,
                           MessageProducerService messageProducerService, FileLoggerService fileLoggerService,
                           GlobalContext globalContext, TelegramNeuralService telegramNeuralService) {
        this.TG_URL = tgUrl;
        this.messageProducerService = messageProducerService;
        this.fileLoggerService = fileLoggerService;
        this.globalContext = globalContext;
        this.telegramNeuralService = telegramNeuralService;
    }

    public SeleniumCore getSeleniumCore(String login, WebDriver driver) {
        return new SeleniumCore(
                TG_URL,
                getAuthorizationSelenium(driver),
                getTelegramSeleniumService(driver),
                driver,
                messageProducerService,
                fileLoggerService,
                globalContext,
                login
        );
    }

    private AuthorizationSelenium getAuthorizationSelenium(WebDriver driver) {
        return new AuthorizationSelenium(driver);
    }

    private TelegramSeleniumService getTelegramSeleniumService(WebDriver driver) {
        return new TelegramSeleniumService(telegramNeuralService, getMessageExtractorSelenium(driver));
    }

    private MessageExtractorSelenium getMessageExtractorSelenium(WebDriver driver) {
        return new MessageExtractorSelenium(driver);
    }
}
