package it.pagopa.send.utils;

import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;

import java.util.Base64;

public class IUNHelper {
    public static String extractFromBffNewNotificationResponse(BffNewNotificationResponse obj) {
        return new String(Base64.getDecoder().decode(obj.getNotificationRequestId()));
    }
}
