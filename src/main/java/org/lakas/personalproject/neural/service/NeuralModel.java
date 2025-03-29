package org.lakas.personalproject.neural.service;

public enum NeuralModel {
    BYTE_DANCE_72B("bytedance-research/ui-tars-72b:free"),
    DEEP_SEEK_V3("deepseek/deepseek-chat-v3-0324:free"),
    QWEN_72B("qwen/qwen2.5-vl-72b-instruct:free"),
    GEMINI_25_PRO_EXP("google/gemini-2.5-pro-exp-03-25:free"),
    GEMMA_3_27B("google/gemma-3-27b-it:free"),
    QWERKY_72B("featherless/qwerky-72b:free");
    private final String name;

    public String getName() {
        return name;
    }

    NeuralModel(String name) {
        this.name = name;
    }
}
