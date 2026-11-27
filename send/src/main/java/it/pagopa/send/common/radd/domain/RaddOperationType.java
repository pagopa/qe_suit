package it.pagopa.send.common.radd.domain;

/**
 * Caso d'uso RADD: {@code ACT} (atti della notifica a partire dall'AAR) o {@code AOR} (AAR non recapitati per
 * irreperibilita'). {@link #pathValue()} e' il valore del path param {@code operationType} di {@code documentDownload}.
 */
public enum RaddOperationType {
    ACT, AOR;

    public String pathValue() {
        return name().toLowerCase();
    }
}
