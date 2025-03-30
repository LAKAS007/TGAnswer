package org.lakas.personalproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class Message {
    public enum MessageAuthor {
        CLIENT, CONVERSATOR
    }
    private final String text;
    private final MessageAuthor messageAuthor;
    private final LocalDateTime sentAt;
}
