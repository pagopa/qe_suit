package it.pagopa.send.bff.informal_notification.infrastructure;

import it.pagopa.send.common.informal_notification.domain.InformalNotificationTimeline;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffInformalNotificationTimelineStatusHistoryV1;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class BffInformalNotificationTimelineMapper {

    public InformalNotificationTimeline toDomain(BffFullSentInformalNotificationTimelineV1 source) {
        List<InformalNotificationTimeline.StatusChange> history = source.getNotificationStatusHistory() == null
                ? List.of()
                : source.getNotificationStatusHistory().stream().map(this::toStatusChange).toList();
        return new InformalNotificationTimeline(source.getIun(), history);
    }

    private InformalNotificationTimeline.StatusChange toStatusChange(BffInformalNotificationTimelineStatusHistoryV1 source) {
        OffsetDateTime activeFrom = source.getActiveFrom() == null ? null : OffsetDateTime.parse(source.getActiveFrom());
        int channelSteps = source.getSteps() == null ? 0 : source.getSteps().size();
        return new InformalNotificationTimeline.StatusChange(String.valueOf(source.getStatus()), activeFrom, channelSteps);
    }
}
