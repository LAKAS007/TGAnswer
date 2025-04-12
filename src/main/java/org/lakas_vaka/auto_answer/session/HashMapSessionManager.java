package org.lakas_vaka.auto_answer.session;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HashMapSessionManager implements SessionManager {
    private final Map<String, ChatSession> sessions = new HashMap<>();

    @Override
    public boolean isActive(String login) {
        ChatSession session = sessions.get(login);

        if (session != null) {
            return session.isEnabled();
        }

        return false;
    }

    @Override
    public boolean exists(String login) {
        return sessions.containsKey(login);
    }

    @Override
    public void saveSession(String login, ConversatorContext conversatorContext, WebDriver webDriver) {
        ChatSession session = ChatSession.builder()
                .login(login)
                .webDriver(webDriver)
                .conversatorContext(conversatorContext)
                .isEnabled(true)
                .messages(new ArrayList<>())
                .build();

        sessions.put(login, session);
    }

    @Override
    public void removeSession(String login) {
        if (sessions.containsKey(login)) {
            ChatSession session = sessions.get(login);
            session.getWebDriver().quit();
            sessions.remove(login);
        }
    }

    @Override
    public List<ChatSession> getAll() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public ChatSession getSession(String login) {
        return sessions.get(login);
    }

    @PreDestroy
    private void destroy() {
        log.info("Closing all active sessions and associated web drivers");
        for (ChatSession session : sessions.values()) {
            session.getWebDriver().quit();
        }
    }
}
