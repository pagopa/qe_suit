package it.pagopa.interop.common.journey.application;

public interface InteropJourney extends
        AgreementJourney<InteropJourney>,
        UserJourney<InteropJourney>,
        EServiceJourney<InteropJourney>,
        EServiceTemplateJourney<InteropJourney>,
        ClientJourney<InteropJourney>,
        PurposeJourney<InteropJourney>,
        AttributeJourney<InteropJourney>,
        FinalizerJourney {
}
