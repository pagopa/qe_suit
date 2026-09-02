package it.pagopa.send.controller.creazione_notifica;

import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.send.model.NotificationData;
import lombok.Data;

/**
 * {@code @Profile("cucumber")}: fuori da uno scenario lo scope {@code cucumber-glue} non esiste.
 * Per i test JUnit puri (es. contract test) c'è l'equivalente non scoped in
 * {@code JunitContextConfig}.
 */
@Component
@ScenarioScope
@Profile("cucumber")
@Data
public class NotificationContext {
    private NotificationData notifica;
    private BffNewNotificationResponse bffNewNotificationResponse;

    public NotificationContext() {
        // Empty constructor for Cucumber DI
    }
}
