package org.lakas_vaka.auto_answer.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.log.FileLogger;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.lakas_vaka.auto_answer.model.chat.ChatAgent;
import org.lakas_vaka.auto_answer.neural.service.SocialNetworkNeuralService;
import org.lakas_vaka.auto_answer.service.auto_answer.AbstractAutoAnswerService;
import org.lakas_vaka.auto_answer.service.message.MessageProducerService;
import org.lakas_vaka.auto_answer.service.message.NeuralMessageProducerService;
import org.lakas_vaka.auto_answer.session.SessionManager;
import org.lakas_vaka.auto_answer.session.SimpleSessionManager;
import org.lakas_vaka.auto_answer.telegram.TelegramChatAgent;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TelegramAutoAnswerService extends AbstractAutoAnswerService {
    private final NeuralMessageProducerService neuralMessageProducerService;
    private final SimpleSessionManager sessionManager;
    private final SocialNetworkNeuralService neuralService;

    @Autowired
    public TelegramAutoAnswerService(NeuralMessageProducerService neuralMessageProducerService,
                                     SimpleSessionManager sessionManager, SocialNetworkNeuralService neuralService) {
        this.neuralMessageProducerService = neuralMessageProducerService;
        this.sessionManager = sessionManager;
        this.neuralService = neuralService;
    }

    @Async
    @Override
    public void start(String login, ConversatorContext conversatorContext) {
        log.debug("Started telegram auto answer service");
        super.start(login, conversatorContext);
    }

    @Override
    public ChatAgent getChatAgent(String login) {
        WebDriver driver = sessionManager.getSession(login).getWebDriver();
        return new TelegramChatAgent(login, driver, new FileLogger(), neuralService);
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public MessageProducerService getMessageProducerService() {
        return neuralMessageProducerService;
    }
}
