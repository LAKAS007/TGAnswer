package org.lakas.personalproject.exception;

public class NeuralServiceIsNotAvailableException extends RuntimeException {
    public NeuralServiceIsNotAvailableException() {
        super("Neural Service is not available");
    }
}
