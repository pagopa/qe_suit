package it.pagopa.send.common.informal_notification.application;

import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationTimeline;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.informal.model.FullSentInformalNotificationV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class InformalNotificationUseCase {

    private final InformalNotificationGateway informalNotificationGateway;

    public void prepareNotification(Map<String, String> data) {
        informalNotificationGateway.prepareNotification(data);
    }

    public void addRecipient(User user, InformalRecipientSpec recipient) {
        informalNotificationGateway.addRecipient(user, recipient);
    }

    public InformalNotificationDomain sendNotification(User sender) {
        return informalNotificationGateway.sendNotification(sender);
    }

    public FullSentInformalNotificationV1 getInformalNotification(String iun) {
        return informalNotificationGateway.getInformalNotification(iun);
    }

    public FullSentInformalNotificationV1 getInformalNotification(String iun,int interval) {
        return informalNotificationGateway.getInformalNotification(iun,interval);
    }

    public FullSentInformalNotificationV1 getLastInformalNotification() {
        return informalNotificationGateway.getInformalNotification();
    }

    public int getSenderDetailStatus(String iun) {
        return informalNotificationGateway.getSenderDetailStatus(iun);
    }

    public InformalNotificationTimeline getLastSenderTimeline() {
        return informalNotificationGateway.getSenderTimeline();
    }
}
