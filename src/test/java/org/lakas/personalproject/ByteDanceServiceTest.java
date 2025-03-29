package org.lakas.personalproject;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lakas.personalproject.neural.service.NeuralModel;
import org.lakas.personalproject.neural.service.NeuralService;
import org.lakas.personalproject.neural.service.NeuralServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class ByteDanceServiceTest {
    @Autowired
    NeuralServiceFactory factory;

    @DisplayName("Should generate message")
    @Test
    public void testShouldGenerateMessage() {
        NeuralService service = factory.getNeuralService(NeuralModel.BYTE_DANCE72B);
        String msg = service.generateMessage(null);
        log.info(msg);
    }
}
