package it.pagopa.send.common.journey.application;

public interface SendJourney extends
        LegalNotificationJourney<SendJourney>,
        InformalNotificationJourney<SendJourney>,
        DelegationJourney<SendJourney>,
        FinalizerJourney {
}
