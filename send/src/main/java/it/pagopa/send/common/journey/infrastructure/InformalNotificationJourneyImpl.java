package it.pagopa.send.common.journey.infrastructure;

import it.pagopa.send.common.informal_notification.application.InformalNotificationUseCase;
import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.journey.application.InformalNotificationJourney;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.Tenant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformalNotificationJourneyImpl implements InformalNotificationJourney<InformalNotificationJourneyImpl> {

    private final InformalNotificationUseCase informalNotificationUseCase;
    private final CurrentUserSession currentUserSession;

    @Override
    public InformalNotificationJourneyImpl withInformalSender(Tenant tenant) {
        currentUserSession.setSender(tenant);
        return this;
    }

    @Override
    public InformalNotificationJourneyImpl prepareInformalNotification(Map<String, String> data) {
        informalNotificationUseCase.prepareNotification(data);
        return this;
    }

    @Override
    public InformalNotificationJourneyImpl withInformalRecipient(InformalRecipientSpec recipient) {
        Tenant sender = currentUserSession.getSender();
        informalNotificationUseCase.addRecipient(sender, recipient);
        currentUserSession.setRecipients(List.of(recipient.recipient()));
        return this;
    }

    @Override
    public InformalNotificationDomain sendInformalNotification(Tenant sender) {
        currentUserSession.setSender(sender);
        InformalNotificationDomain domain = informalNotificationUseCase.sendNotification(sender);
        log.info("Comunicazione bonaria inviata con successo: {}", domain);
        return domain;
    }
}
