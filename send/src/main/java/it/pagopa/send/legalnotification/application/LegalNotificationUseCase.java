package it.pagopa.send.legalnotification.application;

import it.pagopa.send.generated.openapi.clients.bff.model.*;
import it.pagopa.send.legalnotification.infrastructure.LegalNotificationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class LegalNotificationUseCase {

    private final LegalNotificationGateway gateway;

    public BffNewNotificationResponse sendNotification(BffNewNotificationRequest request) {
        return gateway.create(request);
    }

    public BffRequestStatus deleteNotification(String iun) {
        return gateway.delete(iun);
    }

    public BffFullNotificationV1 readNotification(String iun, BffNotificationStatus targetStatus) {
        return gateway.readNotification(iun, targetStatus);
    }

//    public boolean assertNotificationStatus() {
//        return gateway.ass
//    }
}
