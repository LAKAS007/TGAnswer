package org.lakas.personalproject.neural.service;

public interface NeuralService {
    String request(String requestText);

    boolean isAvailable();

    NeuralModel getNeuralModel();
}
