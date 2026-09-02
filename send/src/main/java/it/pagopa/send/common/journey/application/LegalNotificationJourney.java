package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.notification.domain.NotificationStatus;
import it.pagopa.send.model.RecipientSpec;

import java.util.Map;

public interface LegalNotificationJourney<SELF extends LegalNotificationJourney<SELF>> extends JourneyModule {
    SELF withSender(Tenant tenant);
    SELF withRecipient(RecipientSpec recipient);
    SELF prepareNotification(Map<String, String> data);
    SELF sendNotification(Tenant sender, NotificationStatus targetStatus);
    SELF deleteNotification();
    SELF readNotification();
    SELF searchNotification();
}
