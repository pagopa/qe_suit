package it.pagopa.send.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.web.notification_creation.infrastructure.cucumber.NotificationContext;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationType {
    private final NotificationContext notificationContext;

    @ParameterType("precedentemente creata")
    public BffNewNotificationResponse notification(String raw) {
        return switch (raw) {
            case "precedentemente creata" -> notificationContext.getBffNewNotificationResponse();
            default -> throw new IllegalArgumentException("Notifica non trovata: " + raw);
        };
    }
}
