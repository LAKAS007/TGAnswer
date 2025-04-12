package org.lakas_vaka.auto_answer.service.message;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.exception.NeuralServiceIsNotAvailableException;
import org.lakas_vaka.auto_answer.log.FileLogger;
import org.lakas_vaka.auto_answer.neural.service.NeuralModel;
import org.lakas_vaka.auto_answer.neural.service.NeuralServiceFactory;
import org.lakas_vaka.auto_answer.session.ChatSession;
import org.lakas_vaka.auto_answer.neural.service.SocialNetworkNeuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NeuralMessageProducerService implements MessageProducerService {
    private final NeuralModel defaulNeuralModel;
    private final FileLogger fileLogger;
    private final NeuralServiceFactory neuralServiceFactory;

    @Autowired
    public NeuralMessageProducerService(NeuralModel defaulNeuralModel, FileLogger fileLogger,
                                        NeuralServiceFactory neuralServiceFactory) {
        this.defaulNeuralModel = defaulNeuralModel;
        this.fileLogger = fileLogger;
        this.neuralServiceFactory = neuralServiceFactory;
    }

    @Override
    public String getMessage(ChatSession chatSession) {
        NeuralModel desiredNeuralModel = chatSession.getConversatorContext().getNeuralModel();

        if (desiredNeuralModel == null) {
            log.warn("No neural model was specified by user. Using default neural model");
            desiredNeuralModel = defaulNeuralModel;
        }

        var neuralService = neuralServiceFactory.getSocialMediaNeuralService(desiredNeuralModel);

        if (!neuralService.isAvailable()) {
            throw new NeuralServiceIsNotAvailableException();
        }

        log.info("Neural model {} is available", desiredNeuralModel);
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
