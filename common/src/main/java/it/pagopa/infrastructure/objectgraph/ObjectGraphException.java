package it.pagopa.infrastructure.objectgraph;

public class ObjectGraphException extends RuntimeException {

    public ObjectGraphException(String message) {
        super(message);
    }

    public ObjectGraphException(String message, Throwable cause) {
        super(message, cause);
    }
}
