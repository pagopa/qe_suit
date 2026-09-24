package it.pagopa.reporting.dto;

public enum ExecutionStatus {
    PASSED,
    FAILED,
    ERROR,
    SKIPPED,
    UNKNOWN;

    public String cssClass() {
        return name().toLowerCase();
    }
}

