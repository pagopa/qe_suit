package it.pagopa.infrastructure.pst;

public final class PstConfigurationException extends IllegalArgumentException {
    public PstConfigurationException(String message) {
        super(message);
    }

    public PstConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
