package org.lakas_vaka.auto_answer.service.auto_answer;

import org.lakas_vaka.auto_answer.model.ConversatorContext;

public interface AutoAnswerService {
    void start(String login, ConversatorContext conversatorContext);
    void stop(String login);
}
