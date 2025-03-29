package org.lakas.personalproject.neural.service;

import org.lakas.personalproject.model.MessageContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import retrofit2.http.Body;

@Service
public class ByteDanceNeuralService implements NeuralService {
    private final RestClient restClient;

    private final String authToken;

    @Autowired
    public ByteDanceNeuralService(RestClient restClient, @Value("") String authToken) {
        this.restClient = restClient;
        this.authToken = authToken;
    }

    @Override
    public String generateMessage(MessageContext messageContext) {
        restClient.post()
                .uri("https://openrouter.ai/api/v1/chat/completions")
                .header("Authorization", "Bearer ");
    }
}
