package org.lakas.personalproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class ChatContext {
    public static final ChatContext EMPTY = new ChatContext("", new ArrayList<>(), Gender.MALE, "", new ArrayList<>(), ConversatorType.STRANGER);

    private final String telegramLogin;
    private final List<Message> messages;
    private final Gender conversatorGender;
    private final String conversatorName;
    private final List<String> additionalInformation;
    private final ConversatorType conversatorType;

    public void clearMessages() {
        if (messages != null) {
            messages.clear();
        }
    }

    public boolean isEmpty() {
        return messages != null && messages.isEmpty();
    }

    @Override
    public String toString() {
        return "ChatContext{" +
                "telegramLogin='" + telegramLogin + '\'' +
                ", conversatorGender=" + conversatorGender +
                ", conversatorName='" + conversatorName + '\'' +
                ", additionalInformation=" + additionalInformation +
                ", conversatorType=" + conversatorType +
                '}';
    }
}
