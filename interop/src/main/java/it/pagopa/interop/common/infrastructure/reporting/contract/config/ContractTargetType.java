package it.pagopa.interop.common.infrastructure.reporting.contract.config;

import java.util.Locale;

public enum ContractTargetType {
    OPENAPI,
    PAGE;

    public static ContractTargetType fromRaw(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("contract-report target-type is blank");
        }
        try {
            return ContractTargetType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported contract-report target-type: " + raw, ex);
        }
    }
}
