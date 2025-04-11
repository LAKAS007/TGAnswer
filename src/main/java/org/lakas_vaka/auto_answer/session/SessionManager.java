package org.lakas_vaka.auto_answer.session;

import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.openqa.selenium.WebDriver;

public interface SessionManager {
    boolean isActive(String login);
    void saveSession(String login, ConversatorContext conversatorContext, WebDriver webDriver);
    void removeSession(String login);
    ChatSession getSession(String login);
}
