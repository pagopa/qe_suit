package it.pagopa.send.common.delegation.domain;

import java.util.Arrays;

/**
 * Stato di una delega tra destinatari: creata dal delegante è {@code PENDING}, accettata dal delegato diventa
 * {@code ACTIVE}.
 */
public enum DelegationStatus {
    PENDING("pending"),
    ACTIVE("active"),
    REVOKED("revoked"),
    REJECTED("rejected"),
    EXPIRED("expired"),
    UNKNOWN("unknown");

    private final String value;

    DelegationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DelegationStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
