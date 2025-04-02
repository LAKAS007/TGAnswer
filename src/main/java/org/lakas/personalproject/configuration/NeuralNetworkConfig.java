package org.lakas.personalproject.configuration;

import lombok.extern.slf4j.Slf4j;
import org.lakas.personalproject.neural.service.NeuralModel;
import org.lakas.personalproject.neural.service.NeuralModelService;
import org.lakas.personalproject.neural.service.NeuralServiceFactory;
import org.lakas.personalproject.neural.service.TelegramNeuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class NeuralNetworkConfig {
    @Value("${neural.model}")
    private String modelName;

    private final NeuralServiceFactory neuralServiceFactory;
    private final NeuralModelService neuralModelService;

    @Autowired
    public NeuralNetworkConfig(NeuralServiceFactory neuralServiceFactory, NeuralModelService neuralModelService) {
        this.neuralServiceFactory = neuralServiceFactory;
        this.neuralModelService = neuralModelService;
    }

    @Bean
    public TelegramNeuralService defaultNeuralService() {
        return neuralServiceFactory.getTelegramNeuralService(defaultNeuralModel());
    }

    @Bean
    public NeuralModel defaultNeuralModel() {
        NeuralModel neuralModel = neuralModelService.getNeuralModelByName(modelName);
        log.info("Default neural model: {}", neuralModel);
        return neuralModel;
    }
}
