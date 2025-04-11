package org.lakas_vaka.auto_answer.service.auto_answer;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.lakas_vaka.auto_answer.model.chat.*;
import org.lakas_vaka.auto_answer.service.message.MessageProducerService;
import org.lakas_vaka.auto_answer.session.ChatSession;
import org.lakas_vaka.auto_answer.session.SessionManager;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractAutoAnswerService implements AutoAnswerService {
    @Override
    public void start(String login, ConversatorContext conversatorContext) {
        SessionManager sessionManager = getSessionManager();
        if (sessionManager.isActive(login)) {
            log.error("Session {} already exists", login);
            return;
        }

        WebDriver driver = new ChromeDriver();

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
        sessionManager.removeSession(login);
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
