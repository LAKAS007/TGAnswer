package org.lakas.personalproject.model;

import lombok.Data;
import org.lakas.personalproject.neural.service.TelegramNeuralService;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class GlobalContext {
    private String clientName;
    private Gender clientGender;
    private List<String> clientInformation;
    private TelegramNeuralService neuralService;
    private ChatContext currentChatContext;
}
