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
public class MessageContext {
    public static final MessageContext EMPTY = new MessageContext(new ArrayList<>(), Gender.MALE);

    public enum Gender {
        MALE,FEMALE
    }
    private final List<Message> messages;
    private final Gender gender;

    public void clearMessages() {
        messages.clear();
    }

    public boolean isEmpty() {
        return messages != null &&  messages.isEmpty();
    }
}
