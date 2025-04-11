package org.lakas_vaka.auto_answer.model.chat;

import org.lakas_vaka.auto_answer.model.ConversatorContext;

import java.util.List;

public interface ChatAgent {
    List<Message> readActualMessages();
    void openChat();
    void writeText(String text);
    void waitForNewMessages(Message lastMessage);
    void setupChatContext(ConversatorContext conversatorContext);
    String getLogin();
}
