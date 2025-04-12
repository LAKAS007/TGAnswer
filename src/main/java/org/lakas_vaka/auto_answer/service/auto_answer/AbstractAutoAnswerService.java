package org.lakas_vaka.auto_answer.service.auto_answer;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.log.FileLogger;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.lakas_vaka.auto_answer.model.chat.ChatAgent;
import org.lakas_vaka.auto_answer.model.chat.Message;
import org.lakas_vaka.auto_answer.service.message.MessageProducerService;
import org.lakas_vaka.auto_answer.session.ChatSession;
import org.lakas_vaka.auto_answer.session.SessionManager;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

@Slf4j
public abstract class AbstractAutoAnswerService implements AutoAnswerService {
    private static final FileLogger fileLogger = new FileLogger();

    @Override
    public void start(String login, ConversatorContext conversatorContext) {
        SessionManager sessionManager = getSessionManager();

        if (sessionManager.exists(login)) {
            if (sessionManager.isActive(login)) {
                log.error("Session {} already active", login);
                return;
            } else {
                sessionManager.getSession(login).setEnabled(true);
            }
        } else {
            fileLogger.clearLogs(login);
        }

        if (conversatorContext.getNeuralModel() != null) {
            log.info("Selected {} neural model", conversatorContext.getNeuralModel());
            fileLogger.writeLog(login,
                    "Selected [" + conversatorContext.getNeuralModel().getModelName() + "] neural model");
        }

        sessionManager.saveSession(login, conversatorContext, null);

        WebDriver driver = new ChromeDriver();

        log.info("Created new web driver");

        sessionManager.saveSession(login, conversatorContext, driver);

        ChatAgent chatAgent = getChatAgent(login);
        chatAgent.openChat();

        setupConversatorContext(login, conversatorContext);
        sessionManager.saveSession(login, conversatorContext, driver);

        runAutoAnswer(chatAgent);
    }

    private void runAutoAnswer(ChatAgent chatAgent) {
        Message lastMessage = null;
        while (true) {
            chatAgent.waitForNewMessages(lastMessage);
            List<Message> messages = chatAgent.readActualMessages();
            lastMessage = getLastConversatorMessage(messages);

            MessageProducerService messageProducerService = getMessageProducerService();
            ChatSession session = getSessionManager().getSession(chatAgent.getLogin());
            session.getMessages().addAll(messages);

            String answer = messageProducerService.getMessage(session);
            chatAgent.writeText(answer);
        }
    }

    private void setupConversatorContext(String login, ConversatorContext conversatorContext) {
        getChatAgent(login).setupChatContext(conversatorContext);
    }

    @Override
    public void stop(String login) {
        SessionManager sessionManager = getSessionManager();
        sessionManager.getSession(login).getWebDriver().quit();
    }

    private Message getLastConversatorMessage(List<Message> messages) {
        try {
            return messages.stream()
                    .filter(x -> x.getMessageAuthor().equals(Message.MessageAuthor.CONVERSATOR))
                    .toList()
                    .getLast();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    public abstract ChatAgent getChatAgent(String login);

    public abstract SessionManager getSessionManager();

    public abstract MessageProducerService getMessageProducerService();
}
