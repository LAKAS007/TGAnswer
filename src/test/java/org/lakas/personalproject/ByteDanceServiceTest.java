package org.lakas.personalproject;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.lakas.personalproject.neural.service.ByteDanceNeuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class ByteDanceServiceTest {
    @Autowired
    ByteDanceNeuralService service;


    @Test
    public void test() {
        String s = service.generateMessage(null);
        log.info(s);
    }
}
