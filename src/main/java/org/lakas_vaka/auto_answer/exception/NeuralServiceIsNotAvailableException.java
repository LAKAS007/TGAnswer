package org.lakas_vaka.auto_answer.exception;

public class NeuralServiceIsNotAvailableException extends RuntimeException {
    public NeuralServiceIsNotAvailableException() {
        super("Neural Service is not available");
    }
}
