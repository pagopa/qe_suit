package it.pagopa.send.common.journey.infrastructure;

import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.journey.application.LegalNotificationJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.notification.domain.NotificationStatus;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.legalnotification.application.LegalNotificationUseCase;
import it.pagopa.send.model.RecipientSpec;
import it.pagopa.send.utils.IUNHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Composer fluente per la creazione di una notifica legale: raggruppa più chiamate a
 * {@link LegalNotificationUseCase} in un solo step Cucumber (es. "crea e annulla una notifica"),
 * sul modello dei Journey di interop. Bean singleton semplice, non {@code @ScenarioScope}: non ha
 * campi di stato propri, solo dipendenze iniettate ({@link LegalNotificationUseCase},
 * {@link CurrentUserSession}, {@link NotificationContext}), che sono loro a essere scoped in modo
 * diverso per Cucumber (scenario) o JUnit (singleton in-memory) — lo stesso identico pattern dei
 * Journey di interop. Grazie a questo, la stessa istanza funziona sia dentro uno scenario Cucumber
 * sia in un test JUnit puro (es. un contract test). Quando un flusso richiede più step Cucumber
 * distinti (es. il flusso multi-destinatario) gli step chiamano {@link LegalNotificationUseCase}
 * direttamente, senza passare da qui.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LegalNotificationJourneyImpl implements LegalNotificationJourney<LegalNotificationJourneyImpl> {

    private final LegalNotificationUseCase legalNotificationUseCase;
    private final CurrentUserSession currentUserSession;

    @Getter
    private final NotificationContext notificationContext;

    @Override
    public LegalNotificationJourneyImpl withSender(Tenant tenant) {
        currentUserSession.setSender(tenant);
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl withRecipient(RecipientSpec recipient) {
        legalNotificationUseCase.addRecipient(recipient);
        currentUserSession.setRecipients(List.of(recipient.recipient()));
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl prepareNotification(Map<String, String> data) {
        legalNotificationUseCase.prepareNotification(data);
        return this;
    }

    @Override
    public LegalNotificationJourneyImpl sendNotification(Tenant sender, NotificationStatus targetStatus) {
        currentUserSession.setSender(sender);
        legalNotificationUseCase.sendNotification(sender, targetStatus);
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
        legalNotificationUseCase.searchNotification(Map.of());
        return this;
    }
}
