package it.pagopa.reporting.parser;

public class ReportParsingException extends Exception {
    public ReportParsingException(String message) {
        super(message);
    }

    public ReportParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}

