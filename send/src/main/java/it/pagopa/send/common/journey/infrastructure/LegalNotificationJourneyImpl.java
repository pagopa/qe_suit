package it.pagopa.send.common.journey.infrastructure;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.send.common.journey.application.LegalNotificationJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.generated.openapi.clients.bff.model.BffRequestStatus;
import it.pagopa.send.legalnotification.application.LegalNotificationUseCase;
import it.pagopa.send.model.LegalNotificationType;
import it.pagopa.send.model.RecipientSpec;
import it.pagopa.send.utils.IUNHelper;
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
public class LegalNotificationJourneyImpl implements LegalNotificationJourney<LegalNotificationJourneyImpl> {

    private final LegalNotificationUseCase legalNotificationUseCase;

    private final LegalNotificationRequestFactory requestFactory;

    private final CurrentUserSession currentUserSession;

    private final List<RecipientSpec> recipients = new ArrayList<>();
    private final Map<String, String> overrides = new HashMap<>();
    private Tenant sender;
    private LegalNotificationType type = LegalNotificationType.SIMPLE;

    @Getter
    private final NotificationContext notificationContext;

    @Override
    public LegalNotificationJourneyImpl withSender(Tenant sender) {
        this.sender = sender;
        currentUserSession.setSender(sender);
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl withType(LegalNotificationType type) {
        this.type = type;
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl withRecipient(RecipientSpec recipient) {
        this.recipients.add(recipient);
        currentUserSession.setRecipients(this.recipients.stream().map(RecipientSpec::recipient).toList());
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl withOverrides(Map<String, String> overrides) {
        this.overrides.putAll(overrides);
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl sendNotification(BffNotificationStatus targetStatus) {
        BffNewNotificationRequest request = requestFactory.build(type, sender, List.copyOf(recipients), overrides);
        legalNotificationUseCase.sendNotification(request, targetStatus);
        log.info("Notifica legale inviata: {}", notificationContext.getBffNewNotificationResponse());
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl deleteNotification() {
        String iun = IUNHelper.extractFromBffNewNotificationResponse(notificationContext.getBffNewNotificationResponse());
        legalNotificationUseCase.deleteNotification(iun);
        log.info("Notifica legale con IUN {} eliminata", iun);
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl readNotification() {
//        String iun = IUNHelper.extractFromBffNewNotificationResponse(notificationContext.getBffNewNotificationResponse());
        String iun = "WTXW-MNDW-JQLK-202609-W-1";
        LegalNotificationDomain response = legalNotificationUseCase.readNotification(iun);
        log.info("Notifica legale con IUN {} letta: {}", iun, response);
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl searchNotification() {
        legalNotificationUseCase.searchNotification(overrides);
        return null;
    }
}
