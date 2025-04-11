package org.lakas_vaka.auto_answer.neural.service;

public interface NeuralService {
    String request(String requestText);

    boolean isAvailable();

    NeuralModel getNeuralModel();
}
