package org.lakas.personalproject.service;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.exception.NeuralServiceIsNotAvailableException;
import org.lakas.personalproject.model.ChatContext;
import org.lakas.personalproject.model.GlobalContext;
import org.lakas.personalproject.neural.service.NeuralService;
import org.lakas.personalproject.neural.service.TelegramNeuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NeuralMessageProducerService implements MessageProducerService {
    private final GlobalContext globalContext;
    private final TelegramNeuralService defaultNeuralService;

    @Autowired
    public NeuralMessageProducerService(TelegramNeuralService neuralService, GlobalContext globalContext,
                                        TelegramNeuralService defaultNeuralService) {
        this.globalContext = globalContext;
        this.defaultNeuralService = defaultNeuralService;
    }

    @Override
    public String getMessage(ChatContext chatContext) {
        TelegramNeuralService neuralService = globalContext.getNeuralService();

        if (neuralService == null) {
            log.warn("No neural model was specified by user. Using default neural model");
            neuralService = defaultNeuralService;
        }

        if (!neuralService.isAvailable()) {
            throw new NeuralServiceIsNotAvailableException();
        }

        log.info("Neural Service model {} is available", neuralService.getNeuralModel());
        String neuralNetworkResponse = tryToGenerateMessage(neuralService, chatContext, 0);

        if (neuralNetworkResponse == null) {
            throw new NeuralServiceIsNotAvailableException();
        }

        neuralNetworkResponse = neuralNetworkResponse.replace("\n", "");
        return neuralNetworkResponse;
    }

    private String tryToGenerateMessage(TelegramNeuralService neuralService, ChatContext chatContext, int attempt) {
        if (attempt == 3) {
            return null;
        }

        try {
            return neuralService.generateMessage(chatContext);
        } catch (Exception ex) {
            log.info("Couldn't get message from neural network ({}), attempt №{}", ex.getClass().getSimpleName(), (attempt + 1));
            log.error(ex.getMessage(), ex);
            return tryToGenerateMessage(neuralService, chatContext, attempt + 1);
        }
    }
}
