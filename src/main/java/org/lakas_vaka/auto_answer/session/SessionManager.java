package org.lakas_vaka.auto_answer.session;

import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.openqa.selenium.WebDriver;

import java.util.List;

public interface SessionManager {
    boolean isActive(String login);
    boolean exists(String login);
    void saveSession(String login, ConversatorContext conversatorContext, WebDriver webDriver);
    void removeSession(String login);
    List<ChatSession> getAll();
    ChatSession getSession(String login);
}
