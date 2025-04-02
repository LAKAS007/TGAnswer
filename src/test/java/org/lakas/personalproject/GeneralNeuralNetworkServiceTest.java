package org.lakas.personalproject;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lakas.personalproject.configuration.NeuralNetworkConfig;
import org.lakas.personalproject.configuration.WebConfig;
import org.lakas.personalproject.model.Gender;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.model.ChatContext;
import org.lakas.personalproject.neural.service.NeuralModel;
import org.lakas.personalproject.neural.service.NeuralServiceFactory;
import org.lakas.personalproject.neural.service.TelegramNeuralService;
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
        List<TelegramNeuralService> services = new ArrayList<>();

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

        ChatContext msgCtx = ChatContext.builder()
                .messages(messages)
                .conversatorName("Test Boy")
                .telegramLogin("login")
                .conversatorGender(Gender.MALE)
                .build();

        for (var service : services) {
            String msg = service.generateMessage(msgCtx);
            System.out.println(service.getClass().getSimpleName() + ": \n" + msg + "\n-----");
        }
    }

    @DisplayName("Scum problem")
    @Test
    public void testShouldGenerateMessageInScumProblem() {
        NeuralModel[] values = NeuralModel.values();
        List<TelegramNeuralService> services = new ArrayList<>();

        for (NeuralModel model : values) {
            services.add(factory.getTelegramNeuralService(model));
        }

        List<Message> messages = List.of(
                new Message("Здравствуйте! Вы выиграли миллион долларов! Пожалуйста, перейдите по ссылке http://getprice-fdasd54-asd.com чтобы забрать его",
                        Message.MessageAuthor.CONVERSATOR, LocalDateTime.now())
        );

        ChatContext msgCtx = ChatContext.builder()
                .messages(messages)
                .conversatorName("Test Boy")
                .telegramLogin("login")
                .conversatorGender(Gender.MALE)
                .build();

        for (var service : services) {
            String msg = service.generateMessage(msgCtx);
            System.out.println(service.getNeuralModel().getModelName() + ": \n" + msg + "\n-----");
        }
    }
}
