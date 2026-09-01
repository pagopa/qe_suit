package it.pagopa.send.legalnotification.application;

import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.generated.openapi.clients.bff.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LegalNotificationUseCase {
    private final LegalNotificationGateway legalNotificationGateway;

    public void sendNotification(BffNewNotificationRequest request, BffNotificationStatus targetStatus) {
        legalNotificationGateway.sendNotification(request, targetStatus);
    }

    public void deleteNotification(String iun) {
        legalNotificationGateway.deleteNotification(iun);
    }

    public LegalNotificationDomain readNotification(String iun) {
        return legalNotificationGateway.readNotification(iun);
    }

    public LegalNotificationDomain searchNotification(Map<String, String> overrides) {
        return legalNotificationGateway.searchNotification(overrides);
    }

}
