package it.pagopa.interop.common.journey.application;

public interface InteropJourney extends
        AttributeJourney<InteropJourney>,
        AgreementJourney<InteropJourney>,
        UserJourney<InteropJourney>,
        DelegationJourney<InteropJourney>,
        EServiceJourney<InteropJourney>,
        ClientJourney<InteropJourney>,
        PurposeJourney<InteropJourney>,
        TenantJourney<InteropJourney>,
        ChannelJourney<InteropJourney>,
        FinalizerJourney {
}
