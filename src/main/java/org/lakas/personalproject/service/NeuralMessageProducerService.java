package org.lakas.personalproject.service;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.exception.NeuralServiceIsNotAvailableException;
import org.lakas.personalproject.model.MessageContext;
import org.lakas.personalproject.neural.service.NeuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NeuralMessageProducerService implements MessageProducerService {
    private final NeuralService neuralService;

    @Autowired
    public NeuralMessageProducerService(NeuralService neuralService) {
        this.neuralService = neuralService;
    }

    @Override
    public String getMessage(MessageContext messageContext) {
        if (!neuralService.isAvailable()) {
            throw new NeuralServiceIsNotAvailableException();
        }

        log.info("Neural Service model {} is available", neuralService.getNeuralModel());
        return neuralService.generateMessage(messageContext);
    }
}
