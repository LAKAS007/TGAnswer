package org.lakas_vaka.auto_answer.controller.rest;

import lombok.Builder;
import lombok.Data;
import org.lakas_vaka.auto_answer.model.ConversatorContext;
import org.lakas_vaka.auto_answer.model.chat.Message;

import java.util.List;

@Data
@Builder
public class SessionRestEntity {
    private boolean hasDriver;
    private String login;
    private int messagesSize;
    private ConversatorContext conversatorContext;
    private boolean isEnabled;
}
