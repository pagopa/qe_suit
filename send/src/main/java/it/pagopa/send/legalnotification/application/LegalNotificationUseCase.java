package it.pagopa.send.legalnotification.application;

import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
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
}
