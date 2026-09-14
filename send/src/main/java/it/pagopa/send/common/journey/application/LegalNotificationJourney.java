package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;

import java.util.Map;

public interface LegalNotificationJourney<SELF extends LegalNotificationJourney<SELF>> extends JourneyModule {
    SELF withSender(Tenant tenant);
    SELF withRecipient(RecipientSpec recipient);
    SELF prepareNotification(Map<String, String> data);
    SELF sendNotification(Tenant sender, NotificationStatus targetStatus);
    SELF waitForNotificationStatus(NotificationStatus targetStatus);
    SELF deleteNotification();
    SELF readNotification();
    SELF searchNotification();
}
