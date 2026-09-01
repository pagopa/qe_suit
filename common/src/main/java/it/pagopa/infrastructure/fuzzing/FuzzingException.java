package it.pagopa.infrastructure.fuzzing;

public class FuzzingException extends RuntimeException {

    public FuzzingException(String message) {
        super(message);
    }

    public FuzzingException(String message, Throwable cause) {
        super(message, cause);
    }
}
