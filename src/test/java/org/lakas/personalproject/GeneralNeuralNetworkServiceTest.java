package org.lakas.personalproject;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lakas.personalproject.configuration.NeuralNetworkConfig;
import org.lakas.personalproject.configuration.WebConfig;
import org.lakas.personalproject.model.Message;
import org.lakas.personalproject.model.MessageContext;
import org.lakas.personalproject.neural.service.NeuralModel;
import org.lakas.personalproject.neural.service.NeuralService;
import org.lakas.personalproject.neural.service.NeuralServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootTest(classes = {WebConfig.class, NeuralServiceFactory.class, NeuralNetworkConfig.class})
@TestPropertySource("classpath:/application-test.yml")
public class GeneralNeuralNetworkServiceTest {
    @Autowired
    NeuralServiceFactory factory;

    @DisplayName("Should generate message")
    @Test
    public void testShouldGenerateMessage() {
        NeuralService service = factory.getNeuralService(NeuralModel.BYTE_DANCE72B);
        NeuralService service2 = factory.getNeuralService(NeuralModel.DEEP_SEEK_V3);
        NeuralService service3 = factory.getNeuralService(NeuralModel.QWEN_72B);

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

        MessageContext msgCtx = new MessageContext(messages, MessageContext.Gender.MALE);

        String msg = service.generateMessage(msgCtx);
        String msg2 = service2.generateMessage(msgCtx);
        String msg3 = service3.generateMessage(msgCtx);
        System.out.println("Byte dance: \n" + msg);
        System.out.println("Deep seek: \n" + msg2);
        System.out.println("Qwen: \n" + msg3);
    }
}
