package org.lakas_vaka.auto_answer.session;

import lombok.*;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.lakas_vaka.auto_answer.model.chat.ConversatorType;
import org.lakas_vaka.auto_answer.model.chat.Gender;
import org.lakas_vaka.auto_answer.model.chat.Message;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class ChatSession {
    public static final ChatSession EMPTY = new ChatSession(null, "", new ArrayList<>(), ConversatorContext.EMPTY, true);

    private final WebDriver webDriver;
    private final String login;
    @NonNull
    private final List<Message> messages;
    @NonNull
    private final ConversatorContext conversatorContext;
    private boolean isEnabled;

    public void clearMessages() {
        messages.clear();
    }

    public void addMessages(Message... messages) {
        this.messages.addAll(List.of(messages));
    }

    public void addMessages(List<Message> messages) {
        this.messages.addAll(messages);
    }

    public boolean isEmpty() {
        return this == EMPTY ||
                (webDriver == null && login.isEmpty() && messages.isEmpty() && conversatorContext.isEmpty() && !isEnabled);
    }

    @Override
    public String toString() {
        return "ChatSession{" +
                "telegramLogin='" + login + '\'' +
                ", conversatorGender=" + conversatorContext.getConversatorGender() +
                ", conversatorName='" + conversatorContext.getConversatorName() + '\'' +
                ", additionalInformation=" + conversatorContext.getAdditionalInformation() +
                ", conversatorType=" + conversatorContext.getConversatorType() +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
