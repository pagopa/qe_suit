package it.pagopa.infrastructure.reporting.contract.config;

import java.util.Locale;

public enum ContractTargetType {
    /** Una factory = un'operazione: il nome del metodo e' l'{@code operationId}. */
    OPENAPI,
    /** Una factory = una pagina, ricavata dalla {@code .on(Pagina.class)} nel sorgente. */
    PAGE,
    /**
     * Una factory = piu' operazioni, quindi nessun endpoint singolo da indicare: il target e' il
     * metodo {@code @TestFactory} stesso. Usato dai contract test di autorizzazione, dove la
     * stessa factory prova un gruppo di endpoint con piu' utenze.
     */
    TEST_FACTORY;

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
