package it.pagopa.send.controller.creazione_notifica;

import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.send.model.NotificationData;
import lombok.Data;

@Component
@ScenarioScope
@Data 
public class NotificationContext {
    private NotificationData notifica;
    private BffNewNotificationResponse bffNewNotificationResponse;

    public NotificationContext() {
        // Empty constructor for Cucumber DI
    }
}
