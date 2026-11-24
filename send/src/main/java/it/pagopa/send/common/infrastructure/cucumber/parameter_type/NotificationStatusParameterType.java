package it.pagopa.send.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;

import java.util.Map;

/**
 * Stato della notifica nelle feature, tra virgolette, con la descrizione mostrata all'utente ("Label Stato UI" della
 * documentazione SEND) al posto del valore tecnico: {@code "Perfezionata per decorrenza termini"} invece di
 * {@code EFFECTIVE_DATE}. Gli stati senza descrizione per l'utente (in validazione, rifiutata) non sono previsti.
 */
public class NotificationStatusParameterType {

    // CANCELLED ha due descrizioni: "Notifica annullata" nella documentazione, "Annullata" nel portale; valgono
    // entrambe finché le etichette del portale non saranno raccolte in un'unica tabella
    private static final Map<String, NotificationStatus> STATUS_BY_LABEL = Map.of(
            "Depositata", NotificationStatus.ACCEPTED,
            "Invio in corso", NotificationStatus.DELIVERING,
            "Consegnata", NotificationStatus.DELIVERED,
            "Avvenuto accesso", NotificationStatus.VIEWED,
            "Perfezionata per decorrenza termini", NotificationStatus.EFFECTIVE_DATE,
            "Destinatario irreperibile", NotificationStatus.UNREACHABLE,
            "Resa al mittente", NotificationStatus.RETURNED_TO_SENDER,
            "Notifica annullata", NotificationStatus.CANCELLED,
            "Annullata", NotificationStatus.CANCELLED
    );

    @ParameterType("\"(Depositata|Invio in corso|Consegnata|Avvenuto accesso|Perfezionata per decorrenza termini"
            + "|Destinatario irreperibile|Resa al mittente|Notifica annullata|Annullata)\"")
    public NotificationStatus notificationStatus(String label) {
        return STATUS_BY_LABEL.get(label);
    }
}
