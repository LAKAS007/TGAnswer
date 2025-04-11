package org.lakas_vaka.auto_answer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lakas_vaka.auto_answer.configuration.NeuralNetworkConfig;
import org.lakas_vaka.auto_answer.configuration.WebConfig;
import org.lakas_vaka.auto_answer.model.chat.Gender;
import org.lakas_vaka.auto_answer.model.chat.Message;
import org.lakas_vaka.auto_answer.session.ChatSession;
import org.lakas_vaka.auto_answer.neural.service.NeuralModel;
import org.lakas_vaka.auto_answer.neural.service.NeuralServiceFactory;
import org.lakas_vaka.auto_answer.neural.service.SocialNetworkNeuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes = {WebConfig.class, NeuralServiceFactory.class, NeuralNetworkConfig.class})
@TestPropertySource("classpath:/application-test.yml")
public class GeneralNeuralNetworkServiceTest {
    @Autowired
    NeuralServiceFactory factory;

    @DisplayName("Math problem")
    @Test
    public void testShouldGenerateMessageInMathProblem() {
        NeuralModel[] values = NeuralModel.values();
        List<SocialNetworkNeuralService> services = new ArrayList<>();

        for (NeuralModel model : values) {
            services.add(factory.getTelegramNeuralService(model));
        }

        List<Message> messages = List.of(
                new Message("Привет!!",
                        Message.MessageAuthor.CONVERSATOR, LocalDateTime.now().minusDays(1)),
                new Message("Скинь конспект по математике",
                        Message.MessageAuthor.CONVERSATOR, LocalDateTime.now().minusDays(1)),
                new Message("Привет! Завтра скину, я уже в кровати)",
                        Message.MessageAuthor.CLIENT, LocalDateTime.now().minusDays(1)),
                new Message("Здарова",
                        Message.MessageAuthor.CONVERSATOR, LocalDateTime.now()),
                new Message("Напоминаю",
                        Message.MessageAuthor.CONVERSATOR, LocalDateTime.now())
                );

        ChatSession chatSession = ChatSession.builder()
                .messages(messages)
                .conversatorName("Test Boy")
                .login("login")
                .conversatorGender(Gender.MALE)
                .build();

        for (var service : services) {
            String msg = service.generateMessage(chatSession);
            System.out.println(service.getClass().getSimpleName() + ": \n" + msg + "\n-----");
        }
    }

    @DisplayName("Scum problem")
    @Test
    public void testShouldGenerateMessageInScumProblem() {
        NeuralModel[] values = NeuralModel.values();
        List<SocialNetworkNeuralService> services = new ArrayList<>();

        for (NeuralModel model : values) {
            services.add(factory.getTelegramNeuralService(model));
        }

        List<Message> messages = List.of(
                new Message("Здравствуйте! Вы выиграли миллион долларов! Пожалуйста, перейдите по ссылке http://getprice-fdasd54-asd.com чтобы забрать его",
                        Message.MessageAuthor.CONVERSATOR, LocalDateTime.now())
        );

        ChatSession chatSession = ChatSession.builder()
                .messages(messages)
                .conversatorName("Test Boy")
                .login("login")
                .conversatorGender(Gender.MALE)
                .build();

        for (var service : services) {
            String msg = service.generateMessage(chatSession);
            System.out.println(service.getNeuralModel().getModelName() + ": \n" + msg + "\n-----");
        }
    }
}
