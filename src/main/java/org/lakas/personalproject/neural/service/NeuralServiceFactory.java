package org.lakas.personalproject.neural.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NeuralServiceFactory {
    private final RestClient restClient;
    private final String authToken;

    public NeuralServiceFactory(RestClient restClient, @Value("${neural.api.key}") String authToken) {
        this.restClient = restClient;
        this.authToken = authToken;
    }

    public TelegramNeuralService getTelegramNeuralService(NeuralModel model) {
        return switch (model) {
            case BYTE_DANCE_72B, DEEP_SEEK_V3, QWEN_72B, GEMINI_25_PRO_EXP, GEMMA_3_27B, QWERKY_72B -> new GeneralNeuralService(model, restClient, authToken);
        };
    }
}
