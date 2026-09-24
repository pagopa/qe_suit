package it.pagopa.reporting.writer;

public class ReportWritingException extends Exception {
    public ReportWritingException(String message) {
        super(message);
    }

    public ReportWritingException(String message, Throwable cause) {
        super(message, cause);
    }
}

