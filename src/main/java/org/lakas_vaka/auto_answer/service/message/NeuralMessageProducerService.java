package org.lakas_vaka.auto_answer.service.message;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.exception.NeuralServiceIsNotAvailableException;
import org.lakas_vaka.auto_answer.log.FileLogger;
import org.lakas_vaka.auto_answer.session.ChatSession;
import org.lakas_vaka.auto_answer.neural.service.SocialNetworkNeuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NeuralMessageProducerService implements MessageProducerService {
    private final SocialNetworkNeuralService defaultNeuralService;
    private final FileLogger fileLogger;

    @Autowired
    public NeuralMessageProducerService(SocialNetworkNeuralService defaultNeuralService, FileLogger fileLogger) {
        this.defaultNeuralService = defaultNeuralService;
        this.fileLogger = fileLogger;
    }

    @Override
    public String getMessage(ChatSession chatSession) {
//        if (neuralService == null) {
//            log.warn("No neural model was specified by user. Using default neural model");
//            neuralService = defaultNeuralService;
//        }

        var neuralService = defaultNeuralService;

        if (!neuralService.isAvailable()) {
            throw new NeuralServiceIsNotAvailableException();
        }

        log.info("Neural Service model {} is available", neuralService.getNeuralModel());
        log.info("Waiting neural network to response");
        fileLogger.writeLog(chatSession.getLogin(), "Waiting neural network to response...");

        String neuralNetworkResponse = tryToGenerateMessage(neuralService, chatSession, 0);

        if (neuralNetworkResponse == null) {
            throw new NeuralServiceIsNotAvailableException();
        }

        log.info("Got response from neural network: {}", neuralNetworkResponse);
        fileLogger.writeLog(chatSession.getLogin(), "Got response from neural network");

        neuralNetworkResponse = neuralNetworkResponse.replace("\n", "");
        return neuralNetworkResponse;
    }

    private String tryToGenerateMessage(SocialNetworkNeuralService neuralService, ChatSession chatSession, int attempt) {
        if (attempt == 3) {
            return null;
        }

        try {
            return neuralService.generateMessage(chatSession);
        } catch (Exception ex) {
            log.info("Couldn't get message from neural network ({}), attempt №{}", ex.getClass().getSimpleName(), (attempt + 1));
            log.error(ex.getMessage(), ex);
            return tryToGenerateMessage(neuralService, chatSession, attempt + 1);
        }
    }
}
