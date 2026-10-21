package it.pagopa.interop.common.journey.application;

public interface InteropJourney extends
        AttributeJourney<InteropJourney>,
        AgreementJourney<InteropJourney>,
        UserJourney<InteropJourney>,
        DelegationJourney<InteropJourney>,
        EServiceJourney<InteropJourney>,
        EServiceDescriptorJourney<InteropJourney>,
        EServiceTemplateJourney<InteropJourney>,
        EServiceTemplateInstanceJourney<InteropJourney>,
        ClientJourney<InteropJourney>,
        ProducerKeychainJourney<InteropJourney>,
        PurposeJourney<InteropJourney>,
        PurposeTemplateJourney<InteropJourney>,
        TenantJourney<InteropJourney>,
        ChannelJourney<InteropJourney>,
        FinalizerJourney {
}
