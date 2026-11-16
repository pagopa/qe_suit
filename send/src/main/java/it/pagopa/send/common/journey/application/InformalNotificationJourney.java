package it.pagopa.send.common.journey.application;

import it.pagopa.send.common.informal_notification.domain.InformalNotificationDomain;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.informal.model.FullSentInformalNotificationV1;

import java.util.Map;

public interface InformalNotificationJourney<SELF extends InformalNotificationJourney<SELF>> extends JourneyModule {
    SELF withInformalSender(User tenant);
    SELF prepareInformalNotification(Map<String, String> data);
    SELF withInformalRecipient(InformalRecipientSpec recipient);
    InformalNotificationDomain sendInformalNotification(User sender);
    FullSentInformalNotificationV1 getInformalNotification(String iun);
    FullSentInformalNotificationV1 getInformalNotification(String iun,int interval);
    FullSentInformalNotificationV1 getLastInformalNotification();
}
