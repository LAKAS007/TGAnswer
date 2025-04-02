package org.lakas.personalproject.neural.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.model.ChatContext;
import org.lakas.personalproject.model.ConversatorType;
import org.lakas.personalproject.model.Gender;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.model.Message.MessageAuthor;
import org.openqa.selenium.InvalidArgumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class GeneralNeuralService implements TelegramNeuralService {
    private final String URL = "https://openrouter.ai/api/v1/chat/completions";

    private final NeuralModel neuralModel;

    private final RestClient restClient;

    private final String authToken;
    private final Map<String, Gender> genderMap = Map.of(
            "м", Gender.MALE,
            "ж", Gender.FEMALE,
            "н", Gender.UNDEFINED
    );

    public GeneralNeuralService(NeuralModel neuralModel, RestClient restClient, String authToken) {
        this.neuralModel = neuralModel;
        this.restClient = restClient;
        this.authToken = authToken;
    }

    @Override
    public String generateMessage(ChatContext chatContext) {
        String preform = "Ты - мой помощник для ответов в соц. сети. " +
                "Ты отвечаешь за меня людям в социальной сети. ОБЯЗАТЕЛЬНО отвечай собеседникам на том же языке, на котором они пишут. Если язык не русский, отвечай только на английском" +
                "Можно использовать ТОЛЬКО буквы/цифры/знаки препинания. ЗАПРЕЩЕНО использовать смайлики/картинки. " +
                "Я тебе даю КОНТЕКСТ разговора: мои сообщения и сообщения собеседника." +
                "ОБЯЗАТЕЛЬНО УЧИТЫВАЙ время сообщений. \n" +
                String.format("Пол человека: %s. УЧИТЫВАЙ его пол при ответах, если он определён.\n", chatContext.getConversatorGender()) +
                String.format("Собеседник это: %s. УЧИТЫВАЙ его статус при ответах, если он определён.\n", chatContext.getConversatorType()) +
                String.format("Человек подписан в соц. сети как: %s. УЧИТЫВАЙ это. Если так явно указано имя и/или фамилия, ЗАПОМНИ ЕГО ИМЯ. НЕ ИСПОЛЬЗУЙ имя в ответах слишком часто. Если видишь, что в предыдущших сообщениях уже использовалось имя собеседника, НЕ ДУБЛИРУЙ его.\n", chatContext.getConversatorName()) +
                "УЧИТЫВАЯ ВЕСЬ представленный выше контекст, ты ДОЛЖЕН ответить собеседнику. В представленном контексте, для облегчения твоего восприятия, мои сообщения идут после префикса [Я], а сообщения собеседника идут после [Собеседник]. " +
                "Тебе НЕЛЬЗЯ ставить префикс [Я] в ответе. Ответь как бы ответил нормальный, позитивный человек, общающийся в социальной сети. Вот контекст:\n";

        log.debug("Preform: {}", preform);

        String msg = extractMessageFromContext(chatContext);

        String responseText = sendRequest(preform + msg);
        log.debug("Response text: {}", responseText.trim());
        return getAnswer(responseText);
    }

    @Override
    public String request(String requestText) {
        String responseText = sendRequest(requestText);
        log.debug("Response text: {}", responseText.trim());
        return getAnswer(responseText);
    }

    @Override
    public Gender defineGender(List<Message> messagesList) {
        String preForm = "Сейчас я отправлю тебе список сообщений человека.\n" +
                "Ты должен определить его пол. Если пол определить невозможно, он считается неизвестным." +
                "В качестве ответа верни ТОЛЬКО ОДНУ БУКВУ: м, ж, или н, где: " +
                "м - мужской, ж - женский, н - неизвестно";
        String msgListStr = "[" + messagesList.stream()
                .map(Message::getText)
                .collect(Collectors.joining(", ")) + "]";

        String response = request(preForm + msgListStr).toLowerCase();

        boolean isValid = genderMap.containsKey(response);
        int cnt = 0;

        while (!isValid && cnt < 3) {
            log.error("Invalid response from neural network about gender: {}, trying again (attempt №{})", response, (cnt + 1));
            response = request(preForm + messagesList).toLowerCase();
            isValid = genderMap.containsKey(response);
            cnt++;

            if (isValid) {
                return genderMap.get(response);
            }
        }

        if (!isValid) {
            return Gender.UNDEFINED;
        }

        return genderMap.get(response);
    }

    @Override
    public ConversatorType defineConversatorType(List<Message> messagesList, String conversatorName) {
        String preForm = "Сейчас я отправлю тебе список сообщений человека и то, как он записан в социальной сети.\n" +
                "Ты должен определить его статус (кто он по отношему ко мне). Если это определить невозможно, статус считается неизвестным.\n" +
                "Учитывая мои инструкции, В качестве ответа верни ТОЛЬКО ОДИН ИЗ ВАРИНТОВ: " + Arrays.toString(ConversatorType.values()) + ".\n" +
                "Собеседник подписан как " + conversatorName + ".\n" +
                "Ниже, для облегчения твоего восприятия, список, в котором мои сообщения идут после префикса [Я], а сообщения собеседника идут после [Собеседник].\n" +
                "Список сообщений: ";

        String msgListStr = "[" + messagesList.stream()
            .map(Message::getText)
            .collect(Collectors.joining(", ")) + "]";

        String response = request(preForm + msgListStr).toUpperCase();

        boolean isValid = false;

        ConversatorType conversatorType;

        try {
            conversatorType = ConversatorType.valueOf(response);
            return conversatorType;
        } catch (InvalidArgumentException ex) {
        }

        int cnt = 0;

        while (!isValid && cnt < 3) {
            log.error("Invalid response from neural network about conversator status: {}, trying again (attempt №{})", response, (cnt + 1));
            response = request(preForm + messagesList).toLowerCase();
            isValid = List.of(Arrays.stream(ConversatorType.values()).map(Enum::name)).contains(response);

            if (isValid) {
                return ConversatorType.valueOf(response);
            }

            cnt++;
        }

        return ConversatorType.UNDEFINED;
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
                "model", neuralModel.getModelName(),
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

    private String extractMessageFromContext(ChatContext messageCtx) {
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
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public NeuralModel getNeuralModel() {
        return neuralModel;
    }
}
