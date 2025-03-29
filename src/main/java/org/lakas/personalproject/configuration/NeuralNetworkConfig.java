package org.lakas.personalproject.configuration;

import org.lakas.personalproject.neural.service.NeuralModel;
import org.lakas.personalproject.neural.service.NeuralService;
import org.lakas.personalproject.neural.service.NeuralServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

@Configuration
public class NeuralNetworkConfig {
    private final String modelName;

    private final NeuralServiceFactory neuralServiceFactory;

    @Autowired
    public NeuralNetworkConfig(@Value("${neural.model}") String modelName, NeuralServiceFactory neuralServiceFactory) {
        this.modelName = modelName;
        this.neuralServiceFactory = neuralServiceFactory;
    }

    @Bean
    @Primary
    public NeuralService neuralService() {
        return neuralServiceFactory.getNeuralService(Arrays.stream(NeuralModel.values())
                .filter(x -> x.getName().equals(modelName))
                .findFirst()
                .orElseThrow());
    }
}
