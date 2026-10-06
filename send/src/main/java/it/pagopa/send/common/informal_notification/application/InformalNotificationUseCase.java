package it.pagopa.send.common.informal_notification.application;

import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.user.domain.Tenant;
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

    public void addRecipient(Tenant sender, InformalRecipientSpec recipient) {
        informalNotificationGateway.addRecipient(sender, recipient);
    }

    public InformalNotificationDomain sendNotification(Tenant sender) {
        return informalNotificationGateway.sendNotification(sender);
    }

    public FullSentInformalNotificationV1 getInformalNotification(String iun) {
        return informalNotificationGateway.getInformalNotification(iun);
    }

    public FullSentInformalNotificationV1 getLastInformalNotification() {
        return informalNotificationGateway.getInformalNotification();
    }
}
