package org.lakas.personalproject.service;

import org.lakas.personalproject.model.MessageContext;

public interface MessageProducerService {
    String getMessage(MessageContext messageContext);
}
