package it.pagopa.send.bff.legal_notification.infrastructure;

import java.math.BigDecimal;

/**
 * Importi in eurocent e IVA: il dominio li tiene come {@code Integer}, lo spec del BFF li dichiara
 * {@code type: number} (quindi {@link BigDecimal} nei DTO generati).
 */
final class BffAmountConverter {

    private BffAmountConverter() {
    }

    static BigDecimal toDecimal(Integer value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }
}
