package org.lakas.personalproject.model;

import lombok.Data;
import org.lakas.personalproject.neural.service.TelegramNeuralService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class GlobalContext {
    private final Map<String, ChatContext> chatContexts = new HashMap<>();
    private String clientName;
    private Gender clientGender;
    private List<String> clientInformation;
    private TelegramNeuralService neuralService;

    public void registerChatContext(String login, ChatContext chatContext) {
        chatContexts.put(login, chatContext);
    }

    public ChatContext getChatContext(String login) {
        return chatContexts.get(login);
    }

    public boolean containsChatContext(String login) {
        return chatContexts.containsKey(login);
    }

    public void deleteChatContext(String login) {
        chatContexts.remove(login);
    }
}
