package org.lakas_vaka.auto_answer.selenium;

import org.lakas_vaka.auto_answer.model.chat.Message;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface SeleniumChat {
    void writeMessage(String message);
    void openChat();
    WebElement getInputLine();
    Message extractLastMessage();
    Message extractLastConversatorMessage();
    List<Message> extractMessages();
    List<Message> extractConversatorMessages();
    String extractConversatorName();
    void waitForNewMessages(Message lastMessage);
}
