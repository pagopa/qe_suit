package it.pagopa.send.model;

import java.util.Map;

/**
 * Vocabolario del solo step Cucumber "tutto-in-uno" mono-destinatario esistente (es. "una notifica
 * di tipo semplice creata dalla PA..."): non guida più la costruzione della notifica (nessun
 * template YAML per tipo, nessuna logica nel gateway), viene tradotto in override di conteggio
 * pagoPA/F24 sul singolo destinatario tramite {@link #recipientOverrides()}, con lo stesso
 * meccanismo del flusso multi-destinatario.
 */
public enum LegalNotificationType {
    SIMPLE,
    SINGLE_RECIPIENT_WITH_PAGOPA_PAYMENT,
    SINGLE_RECIPIENT_WITH_F24_PAYMENT;

    public Map<String, String> recipientOverrides() {
        return switch (this) {
            case SIMPLE -> Map.of("pagoPA_number", "0", "F24_number", "0");
            case SINGLE_RECIPIENT_WITH_PAGOPA_PAYMENT -> Map.of("pagoPA_number", "1", "F24_number", "0");
            case SINGLE_RECIPIENT_WITH_F24_PAYMENT -> Map.of("pagoPA_number", "0", "F24_number", "1");
        };
    }
}
