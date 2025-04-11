package org.lakas_vaka.auto_answer.neural.service;

import org.lakas_vaka.auto_answer.model.chat.ConversatorType;
import org.lakas_vaka.auto_answer.model.chat.Gender;
import org.lakas_vaka.auto_answer.model.chat.Message;
import org.lakas_vaka.auto_answer.session.ChatSession;

import java.util.List;

public interface SocialNetworkNeuralService extends NeuralService {
    Gender defineGender(List<Message> messagesList);
    ConversatorType defineConversatorType(List<Message> messagesList, String conversatorName);
    String generateMessage(ChatSession chatSession);
}
