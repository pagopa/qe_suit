package it.pagopa.send.common.legal_notification.domain;

import java.util.List;

/**
 * Cronologia di una notifica vista dalla PA mittente: gli stati attraversati, dal più recente al primo.
 */
public record NotificationTimeline(String iun, List<NotificationStatus> statuses) {

    public NotificationTimeline {
        statuses = List.copyOf(statuses);
    }
}
