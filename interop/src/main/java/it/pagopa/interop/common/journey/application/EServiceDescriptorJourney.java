package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;

public interface EServiceDescriptorJourney<SELF extends EServiceDescriptorJourney<SELF>> extends JourneyModule {
    SELF addDescriptor(EServiceDescriptorState state);

    SELF addDescriptor(EService eService, EServiceDescriptorState state);

    SELF updateDescriptor(UpdateEServiceDescriptorCommand command, EServiceDescriptorState state);

    SELF archiveFirstEServiceDescriptor(GracePeriodDays gracePeriodDays);

    SELF bringEServiceDescriptorToState(
            EService eService,
            EServiceDescriptor descriptor,
            EServiceDescriptorState state
    );

    SELF prepareEServiceDescriptorForPublication(int version);

    SELF publishDescriptor(int version);

    SELF bringEServiceDescriptorToState(EService eService, int version, EServiceDescriptorState state);
}
