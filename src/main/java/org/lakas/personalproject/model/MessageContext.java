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
    public enum Gender {
        MALE,FEMALE
    }
    private final List<Message> list;
    private final Gender gender;

    public void clearMessages() {
        list.clear();
    }
}
