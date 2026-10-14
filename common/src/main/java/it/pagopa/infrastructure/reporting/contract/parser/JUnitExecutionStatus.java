package it.pagopa.infrastructure.reporting.contract.parser;

import java.util.Locale;

public enum JUnitExecutionStatus {
    SUCCESSFUL,
    FAILED,
    SKIPPED,
    ABORTED,
    ERRORED,
    UNKNOWN;

    public static JUnitExecutionStatus fromRaw(String raw) {
        if (raw == null || raw.isBlank()) {
            return UNKNOWN;
        }
        try {
            return JUnitExecutionStatus.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return UNKNOWN;
        }
    }
}
