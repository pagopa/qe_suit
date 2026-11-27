package it.pagopa.send.common.legal_notification.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationRequestOutcome;
import lombok.RequiredArgsConstructor;

/**
 * Entità della notifica legale prodotte dagli step precedenti dello scenario, lette dall'{@link EntityStore} in cui le
 * registrano i gateway:
 * <ul>
 *   <li>{@code {legalNotification}}: "la notifica", l'ultima notifica creata o letta;</li>
 *   <li>{@code {legalNotificationRequest}}: "la richiesta di notifica", l'esito dell'ultima richiesta sottomessa.</li>
 * </ul>
 */
@RequiredArgsConstructor
public class LegalNotificationParameterTypes {

    private final EntityStore entityStore;

    @ParameterType("la notifica")
    public LegalNotificationDomain legalNotification(String token) {
        return entityStore.getLastOrThrow(LegalNotificationDomain.class);
    }

    @ParameterType("la richiesta di notifica")
    public LegalNotificationRequestOutcome legalNotificationRequest(String token) {
        return entityStore.getLastOrThrow(LegalNotificationRequestOutcome.class);
    }
}
