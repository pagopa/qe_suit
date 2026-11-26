package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.send.common.legal_notification.domain.NotificationStatus;

import java.util.Arrays;
import java.util.Optional;

/**
 * Etichette con cui il portale della PA mostra lo stato di una notifica (chip del dettaglio), tradotte nello stato di
 * dominio: solo quelle già viste nel portale, un'etichetta diversa è un errore esplicito.
 */
public enum NotificationStatusLabel {
    ACCEPTED("Depositata", NotificationStatus.ACCEPTED),
    DELIVERING("Invio in corso", NotificationStatus.DELIVERING),
    DELIVERED("Consegnata", NotificationStatus.DELIVERED),
    EFFECTIVE_DATE("Perfezionata per decorrenza termini", NotificationStatus.EFFECTIVE_DATE),
    CANCELLATION_IN_PROGRESS("Annullamento in corso", NotificationStatus.CANCELLATION_IN_PROGRESS),
    CANCELLED("Annullata", NotificationStatus.CANCELLED);

    private final String label;
    private final NotificationStatus status;

    NotificationStatusLabel(String label, NotificationStatus status) {
        this.label = label;
        this.status = status;
    }

    public static NotificationStatus toStatus(String label) {
        return find(label)
                .orElseThrow(() -> new IllegalStateException("Stato della notifica non riconosciuto nel portale: " + label));
    }

    public static Optional<NotificationStatus> find(String label) {
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .map(value -> value.status);
    }
}
