package it.pagopa.interop.common.journey.application;

public interface InteropJourney extends
        AgreementJourney<InteropJourney>,
        UserJourney<InteropJourney>,
        EServiceJourney<InteropJourney>,
        ClientJourney<InteropJourney>,
        ProducerKeychainJourney<InteropJourney>,
        PurposeJourney<InteropJourney>,
        ChannelJourney<InteropJourney>,
        FinalizerJourney {
}
