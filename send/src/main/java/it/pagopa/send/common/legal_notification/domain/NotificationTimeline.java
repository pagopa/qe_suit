package it.pagopa.send.common.legal_notification.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Cronologia di una notifica vista dalla PA mittente: gli stati attraversati, dal più recente al primo, ognuno con la
 * data da cui è attivo e il suo contenuto (descrizione, destinatari ed eventi di invio).
 */
public record NotificationTimeline(String iun, List<StatusChange> history) {

    public NotificationTimeline {
        history = List.copyOf(history);
    }

    /**
     * Uno stato della cronologia; la data è {@code null} quando il canale non la mostra (es. "Invio in corso"), il
     * contenuto è il testo dello stato così come lo riporta il canale.
     */
    public record StatusChange(NotificationStatus status, LocalDateTime activeFrom, String content) {
    }

    public List<NotificationStatus> statuses() {
        return history.stream().map(StatusChange::status).toList();
    }

    public Optional<StatusChange> find(NotificationStatus status) {
        return history.stream().filter(change -> change.status() == status).findFirst();
    }
}
