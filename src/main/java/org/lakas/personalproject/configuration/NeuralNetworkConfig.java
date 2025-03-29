package org.lakas.personalproject.configuration;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.neural.service.NeuralModel;
import org.lakas.personalproject.neural.service.NeuralService;
import org.lakas.personalproject.neural.service.NeuralServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

@Slf4j
@Configuration
public class NeuralNetworkConfig {
    @Value("${neural.model}")
    private String modelName;

    private final NeuralServiceFactory neuralServiceFactory;

    @Autowired
    public NeuralNetworkConfig(NeuralServiceFactory neuralServiceFactory) {
        this.neuralServiceFactory = neuralServiceFactory;
    }

    @Bean
    @Primary
    public NeuralService neuralService() {
        System.out.println("Model name: " + modelName);
        return neuralServiceFactory.getNeuralService(Arrays.stream(NeuralModel.values())
                .filter(x -> x.getName().equals(modelName))
                .findFirst()
                .orElseThrow());
    }
}
