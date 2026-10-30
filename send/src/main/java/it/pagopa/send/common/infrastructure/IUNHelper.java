package it.pagopa.send.common.infrastructure;

import it.pagopa.send.generated.openapi.clients.bff.model.NewNotificationResponse;

import java.util.Base64;

public class IUNHelper {
    public static String extractFromBffNewNotificationResponse(NewNotificationResponse obj) {
        return new String(Base64.getDecoder().decode(obj.getNotificationRequestId()));
    }
}
