package it.pagopa.send.common.legal_notification.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Stato di una notifica legale, agnostico dal DTO OpenAPI del canale (equivalente interno di
 * {@code BffNotificationStatus}). Le interfacce dei gateway espongono solo questo tipo; solo le
 * implementazioni dei gateway conoscono il DTO OpenAPI corrispondente.
 * <p>
 * Ogni stato visibile all'utente ha la sua descrizione, con cui lo mostra il portale ("Label Stato UI" della
 * documentazione SEND) e con cui lo nominano le feature al posto del valore tecnico; gli stati senza descrizione (in
 * validazione, rifiutata, ...) non sono mostrati all'utente.
 */
public enum NotificationStatus {
    IN_VALIDATION,
    ACCEPTED("Depositata"),
    DELIVERING("Invio in corso"),
    // nel portale cambia con i destinatari e la giacenza della raccomandata
    DELIVERED("Consegnata", "Consegnata o in giacenza", "In giacenza", "Giacenza scaduta", "Ritirata"),
    VIEWED("Avvenuto accesso"),
    EFFECTIVE_DATE("Perfezionata per decorrenza termini"),
    PAID,
    // "Destinatari irreperibili" con più destinatari
    UNREACHABLE("Destinatario irreperibile", "Destinatari irreperibili"),
    // "Annullata" nel portale, "Notifica annullata" nella documentazione
    CANCELLED("Annullata", "Notifica annullata"),
    REFUSED,
    // solo nel portale, la documentazione non lo descrive
    CANCELLATION_IN_PROGRESS("Annullamento in corso"),
    RETURNED_TO_SENDER("Resa al mittente"),
    NOTIFICATION_TIMELINE_REWORKED;

    private final List<String> labels;

    NotificationStatus(String... labels) {
        this.labels = List.of(labels);
    }

    public static NotificationStatus fromValue(String value) {
        return valueOf(value);
    }

    /** Lo stato con la descrizione indicata; vuoto se nessuno stato la prevede o se la descrizione manca. */
    public static Optional<NotificationStatus> fromLabel(String label) {
        if (label == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(status -> status.labels.contains(label))
                .findFirst();
    }

    /** La notifica ha superato la validazione: ogni stato tranne quello di validazione e il rifiuto. */
    public boolean isAccepted() {
        return this != IN_VALIDATION && this != REFUSED;
    }
}
