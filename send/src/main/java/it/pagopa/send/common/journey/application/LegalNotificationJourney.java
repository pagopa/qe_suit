package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.model.LegalNotificationType;
import it.pagopa.send.model.RecipientSpec;

import java.util.Map;

public interface LegalNotificationJourney<SELF extends LegalNotificationJourney<SELF>> extends JourneyModule {
    SELF withSender(Tenant sender);
    SELF withType(LegalNotificationType type);
    SELF withRecipient(RecipientSpec recipient);
    SELF withOverrides(Map<String, String> overrides);
    SELF sendNotification(BffNotificationStatus targetStatus);
    SELF deleteNotification();
    SELF readNotification();
    SELF searchNotification();
}
