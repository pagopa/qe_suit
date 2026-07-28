package it.pagopa.send.legalnotification.application;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.common.kernel.domain.Tenant;
import it.pagopa.send.model.LegalNotificationType;
import it.pagopa.send.model.RecipientSpec;
import it.pagopa.send.utils.factory.LegalNotificationRequestFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestratore fluente per la creazione di una notifica legale, sul modello dei Journey di
 * interop: accumula mittente/destinatari/override attraverso gli step di uno stesso scenario e
 * delega la costruzione della request a {@link LegalNotificationRequestFactory}, mantenendo
 * request/response dell'ultimo invio per gli step di asserzione successivi.
 */
@Slf4j
@Component
@ScenarioScope
@RequiredArgsConstructor
public class LegalNotificationJourney {

    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationUseCase legalNotificationUseCase;

    private final List<RecipientSpec> recipients = new ArrayList<>();
    private final Map<String, String> overrides = new HashMap<>();
    private Tenant sender;
    private LegalNotificationType type = LegalNotificationType.SIMPLE;

    @Getter
    private BffNewNotificationRequest lastRequest;
    @Getter
    private BffNewNotificationResponse lastResponse;

    public LegalNotificationJourney withSender(Tenant sender) {
        this.sender = sender;
        return this;
    }

    public LegalNotificationJourney withType(LegalNotificationType type) {
        this.type = type;
        return this;
    }

    public LegalNotificationJourney withRecipient(RecipientSpec recipient) {
        this.recipients.add(recipient);
        return this;
    }

    public LegalNotificationJourney withOverrides(Map<String, String> overrides) {
        this.overrides.putAll(overrides);
        return this;
    }

    public LegalNotificationJourney send() {
        lastRequest = requestFactory.build(type, sender, List.copyOf(recipients), overrides);
        lastResponse = legalNotificationUseCase.sendNotification(lastRequest);
        log.info("Notifica legale inviata: {}", lastResponse);
        return this;
    }

}
