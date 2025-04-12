package org.lakas_vaka.auto_answer.configuration;

import lombok.extern.slf4j.Slf4j;
import org.lakas_vaka.auto_answer.neural.service.NeuralModel;
import org.lakas_vaka.auto_answer.neural.service.NeuralModelService;
import org.lakas_vaka.auto_answer.neural.service.NeuralServiceFactory;
import org.lakas_vaka.auto_answer.neural.service.SocialNetworkNeuralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public SocialNetworkNeuralService defaultNeuralService() {
        return neuralServiceFactory.getSocialMediaNeuralService(defaultNeuralModel());
    }

    @Bean
    public NeuralModel defaultNeuralModel() {
        NeuralModel neuralModel = neuralModelService.getNeuralModelByName(modelName);
        log.info("Default neural model: {}", neuralModel);
        return neuralModel;
    }
}
