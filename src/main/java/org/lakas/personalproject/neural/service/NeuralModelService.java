package org.lakas.personalproject.neural.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class NeuralModelService {
    public NeuralModel getNeuralModelByName(String modelName) {
        return Arrays.stream(NeuralModel.values())
                .filter(x -> x.getModelName().equals(modelName))
                .findFirst()
                .orElseThrow();
    }
}
