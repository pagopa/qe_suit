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

    /**
     * Il notificationRequestId restituito dalla creazione è il base64 dello IUN, riutilizzabile
     * per interrogare la GET della notifica.
     */
    public String extractIun(BffNewNotificationResponse response) {
        return new String(Base64.getDecoder().decode(response.getNotificationRequestId()));
    }

    public BffFullNotificationV1 waitForStatus(String iun, BffNotificationStatus targetStatus) {
        return gateway.waitForStatus(iun, targetStatus);
    }

//    public boolean assertNotificationStatus() {
//        return gateway.ass
//    }
}
