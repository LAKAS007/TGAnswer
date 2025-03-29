package org.lakas.personalproject.selenium;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.model.Message.MessageAuthor;
import org.lakas.personalproject.model.MessageContext;
import org.lakas.personalproject.neural.service.NeuralService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.lakas.personalproject.model.Message.MessageAuthor.CLIENT;

@Slf4j
@Component
public class SeleniumCore {
    private final String TG_URL;
    private final AuthorizationSelenium authorizationSelenium;
    private final TgSelenium tgSelenium;
    private final WebDriver driver;
    private final NeuralService neuralService;

    @Autowired
    public SeleniumCore(@Value("${tg.url}") String tgUrl, AuthorizationSelenium authorizationSelenium, TgSelenium tgSelenium, WebDriver driver,
                        NeuralService neuralService) {
        TG_URL = tgUrl;
        this.authorizationSelenium = authorizationSelenium;
        this.tgSelenium = tgSelenium;
        this.driver = driver;
        this.neuralService = neuralService;
    }

    @Async
    public void start(String login) {
        driver.get(TG_URL + login);
        log.info("Started Selenium Core");
        log.info("Waiting for authorization");
        authorizationSelenium.waitForAuthorization();
        log.info("Authorization successful. Reading messages from {}", login);
        MessageContext msgCtx = new MessageContext(List.of(new Message("Используй только русские буквы. Представься моему другу Никите", Message.MessageAuthor.CLIENT)), MessageContext.Gender.MALE);
        log.info("Waiting neural network to response...");
        String msg = neuralService.generateMessage(msgCtx);
        tgSelenium.writeMessage(msg);
        log.info("Sent generated message to {}", login);
    }
}
