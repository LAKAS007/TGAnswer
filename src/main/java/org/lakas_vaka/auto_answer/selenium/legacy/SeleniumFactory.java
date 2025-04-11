package org.lakas_vaka.auto_answer.selenium.legacy;

import org.lakas_vaka.auto_answer.session.GlobalContext;
import org.lakas_vaka.auto_answer.neural.service.TelegramNeuralService;
import org.lakas_vaka.auto_answer.log.FileLogger;
import org.lakas_vaka.auto_answer.service.message.MessageProducerService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SeleniumFactory {
    private final String TG_URL;
    private final MessageProducerService messageProducerService;
    private final FileLogger fileLogger;
    private final GlobalContext globalContext;
    private final TelegramNeuralService telegramNeuralService;

    public SeleniumFactory(@Value("${tg.url}") String tgUrl,
                           MessageProducerService messageProducerService, FileLogger fileLogger,
                           GlobalContext globalContext, TelegramNeuralService telegramNeuralService) {
        this.TG_URL = tgUrl;
        this.messageProducerService = messageProducerService;
        this.fileLogger = fileLogger;
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
                fileLogger,
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
