package org.lakas_vaka.auto_answer.service.message;

import org.lakas_vaka.auto_answer.session.ChatSession;

public interface MessageProducerService {
    String getMessage(ChatSession chatSession);
}
