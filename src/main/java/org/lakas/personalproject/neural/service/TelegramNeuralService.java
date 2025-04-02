package org.lakas.personalproject.neural.service;

import org.lakas.personalproject.model.ChatContext;
import org.lakas.personalproject.model.ConversatorType;
import org.lakas.personalproject.model.Gender;
import org.lakas.personalproject.model.Message;

import java.util.List;

public interface TelegramNeuralService extends NeuralService {
    Gender defineGender(List<Message> messagesList);
    ConversatorType defineConversatorType(List<Message> messagesList, String conversatorName);
    String generateMessage(ChatContext chatContext);
}
