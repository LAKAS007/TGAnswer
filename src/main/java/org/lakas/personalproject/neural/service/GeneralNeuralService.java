package org.lakas.personalproject.neural.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.model.MessageContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.stream.Collectors;

public class GeneralNeuralService implements NeuralService {
    private final String URL = "https://openrouter.ai/api/v1/chat/completions";

    private final NeuralModel neuralModel;

    private final RestClient restClient;

    private final String authToken;

    public GeneralNeuralService(NeuralModel neuralModel, RestClient restClient, String authToken) {
        this.neuralModel = neuralModel;
        this.restClient = restClient;
        this.authToken = authToken;
    }

    @Override
    public String generateMessage(MessageContext messageContext) {
        String msg = messageContext.getList().stream()
                .map(Message::getText)
                .collect(Collectors.joining(" "));

        String responseText = sendRequest(msg);
        return getAnswer(responseText);
    }

    @Override
    public boolean isAvailable() {
        ResponseEntity<Void> responseEntity = restClient.options()
                .uri(URL)
                .header("Authorization", "Bearer " + authToken)
                .retrieve()
                .toBodilessEntity();

        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    private String sendRequest(String requestMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", neuralModel.getName(),
                "messages", new Object[]{
                        Map.of("role", "user", "content", requestMessage)
                }
        );

        return restClient.post()
                .uri(URL)
                .header("Authorization", "Bearer " + authToken)
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }

    private String getAnswer(String responseText) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseText);
            JsonNode msgNode = rootNode.path("choices").get(0).path("message").path("content");
            String messageJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgNode);
            return messageJson.substring(1, messageJson.length() - 1);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NeuralModel getNeuralModel() {
        return neuralModel;
    }
}
