package it.pagopa.interop.new_arch.common.journey.application;

import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptorState;

import java.util.function.Consumer;

public interface EServiceJourney<SELF extends EServiceJourney<SELF>> extends JourneyModule {
    SELF createEService(EServiceCreationCommand command, EServiceDescriptorState state);

    default SELF createEService(EServiceCreationCommand command) {
        return createEService(command, EServiceDescriptorState.DRAFT);
    }

    SELF createEService(Consumer<EServiceCreationCommand> command, EServiceDescriptorState state);

    default SELF createEService(Consumer<EServiceCreationCommand> command) {
        return createEService(command, EServiceDescriptorState.DRAFT);
    }

    SELF createEService(EServiceDescriptorState state);

    default SELF createEService() {
        return createEService(EServiceDescriptorState.DRAFT);
    }
}
