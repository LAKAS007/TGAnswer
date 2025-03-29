package org.lakas.personalproject.neural.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.model.MessageContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import retrofit2.http.Body;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ByteDanceNeuralService implements NeuralService {
    private final RestClient restClient;

    private final String authToken;

    @Autowired
    public ByteDanceNeuralService(RestClient restClient, @Value("${neural.api.key}") String authToken) {
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

    private String sendRequest(String requestMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", "bytedance-research/ui-tars-72b:free",
                "messages", new Object[]{
                        Map.of("role", "user", "content", requestMessage)
                }
        );

        return restClient.post()
                .uri("https://openrouter.ai/api/v1/chat/completions")
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
}
