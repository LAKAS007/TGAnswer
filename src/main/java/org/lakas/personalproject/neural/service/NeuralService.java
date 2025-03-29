package org.lakas.personalproject.neural.service;

import org.lakas.personalproject.model.MessageContext;

public interface NeuralService {
    String generateMessage(MessageContext messageContext);
    boolean isAvailable();
    NeuralModel getNeuralModel();
}
