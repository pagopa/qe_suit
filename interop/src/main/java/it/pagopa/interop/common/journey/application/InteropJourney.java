package it.pagopa.interop.common.journey.application;

public interface InteropJourney extends
        AttributeJourney<InteropJourney>,
        AgreementJourney<InteropJourney>,
        UserJourney<InteropJourney>,
        EServiceJourney<InteropJourney>,
        ClientJourney<InteropJourney>,
        PurposeJourney<InteropJourney>,
        TenantJourney<InteropJourney>,
        FinalizerJourney {
}
