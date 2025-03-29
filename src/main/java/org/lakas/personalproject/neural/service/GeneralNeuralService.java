package org.lakas.personalproject.neural.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.model.Message.MessageAuthor;
import org.lakas.personalproject.model.MessageContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
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
        String preform = "Ты - мой помощник, когда меня я не могу отвечать. " +
                "Ты отвечаешь за меня людям в социальной сети. Общайся ТОЛЬКО на русском языке. " +
                "Можно использовать ТОЛЬКО буквы/цифры/знаки препинания. ЗАПРЕЩЕНО использовать смайлики/картинки. " +
                "Я тебе даю КОНТЕКСТ разговора: мои сообщения и сообщения собеседника. " +
                "ОБЯЗАТЕЛЬНО учитывай время сообщений. " +
                "Учитывая контекст, ты ДОЛЖЕН ответить собеседнику. В представленном контексте, для облегчения твоего восприятия, мои сообщения идут после префикса [Я], а сообщения собеседника идут после [Собеседник]. " +
                "Тебе НЕЛЬЗЯ ставить префикс [Я] в ответе. Ответь как бы ответил нормальный, позитивный человек, общающийся в социальной сети. Вот контекст:\n";

        String msg = extractMessageFromContext(messageContext);

        String responseText = sendRequest(preform + msg);
        log.debug("Response text: {}", responseText);
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
        log.debug("Request message: {}", requestMessage);
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

    private String extractMessageFromContext(MessageContext messageCtx) {
        StringBuilder stringBuilder = new StringBuilder();

        List<Message> messages = messageCtx.getMessages();

        for (Message message : messages) {
            String text = message.getText();
            MessageAuthor messageAuthor = message.getMessageAuthor();

            String prefix = "[" + message.getSentAt().toString() + "] ";
            prefix += messageAuthor.equals(MessageAuthor.CLIENT) ? "[Я]: " : "[Собеседник]: ";
            stringBuilder.append(prefix).append(text).append("\n");
        }

        return stringBuilder.toString();
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
