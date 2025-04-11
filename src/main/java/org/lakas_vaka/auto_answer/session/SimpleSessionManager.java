package org.lakas_vaka.auto_answer.session;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SimpleSessionManager implements SessionManager {
    private final Map<String, ChatSession> sessions = new HashMap<>();

    @Override
    public boolean isActive(String login) {
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
            session.getWebDriver().close();
            sessions.remove(login);
        }
    }

    @Override
    public ChatSession getSession(String login) {
        return sessions.get(login);
    }

    @PreDestroy
    private void destroy() {
        log.info("Closing all active sessions and associated web drivers");
        for (ChatSession session : sessions.values()) {
            session.getWebDriver().close();
        }
    }
}
