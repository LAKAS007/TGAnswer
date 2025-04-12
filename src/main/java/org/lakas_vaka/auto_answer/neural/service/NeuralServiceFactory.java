package org.lakas_vaka.auto_answer.neural.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class NeuralServiceFactory {
    private static GeneralNeuralService generalNeuralService;

    public NeuralServiceFactory(RestClient restClient, @Value("${neural.api.key}") String authToken) {
        generalNeuralService = new GeneralNeuralService(null, restClient, authToken);
    }

    public List<NeuralModel> getAvailableNeuralModels() {
        return List.of(NeuralModel.values());
    }

    public SocialNetworkNeuralService getSocialMediaNeuralService(NeuralModel model) {
        return switch (model) {
            case BYTE_DANCE_72B, DEEP_SEEK_V3, QWEN_72B, GEMINI_25_PRO_EXP, GEMMA_3_27B, QWERKY_72B -> {
                generalNeuralService.setNeuralModel(model);
                yield generalNeuralService;
            }
        };
    }
}
