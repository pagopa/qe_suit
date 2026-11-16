package it.pagopa.send.common.informal_notification.domain;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Timeline di una comunicazione bonaria vista dal mittente: gli stati attraversati, nell'ordine restituito dal BFF,
 * ognuno con il numero di step dei canali che lo accompagnano.
 */
public record InformalNotificationTimeline(String iun, List<StatusChange> statusHistory) {

    public record StatusChange(String status, OffsetDateTime activeFrom, int channelSteps) {
    }
}
