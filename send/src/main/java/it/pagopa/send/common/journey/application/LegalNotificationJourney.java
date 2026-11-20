package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;

public interface LegalNotificationJourney<SELF extends LegalNotificationJourney<SELF>> extends JourneyModule {
    SELF withSender(User tenant);
    SELF withRecipient(RecipientSpec recipient);
    SELF prepareNotification(LegalNotificationCreationCommand command);
    SELF sendNotification(User sender, NotificationStatus targetStatus);
    SELF waitForNotificationStatus(NotificationStatus targetStatus);
    SELF deleteNotification();
    SELF readNotification();
    SELF searchNotification();
}
