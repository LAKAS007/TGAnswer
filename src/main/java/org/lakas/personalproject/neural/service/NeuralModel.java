package org.lakas.personalproject.neural.service;

public enum NeuralModel {
    BYTE_DANCE72B("bytedance-research/ui-tars-72b:free"),
    DEEP_SEEK_V3("deepseek/deepseek-chat-v3-0324:free"),;
    private final String name;

    public String getName() {
        return name;
    }

    NeuralModel(String name) {
        this.name = name;
    }
}
