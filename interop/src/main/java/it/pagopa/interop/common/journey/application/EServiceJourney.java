package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.journey.infrastructure.EServiceJourneyImpl;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface EServiceJourney<SELF extends EServiceJourney<SELF>> extends JourneyModule {
    SELF createEService(EServiceCreationCommand command, EServiceDescriptorState state);

    default SELF createEService(EServiceCreationCommand command) {
        return createEService(command, EServiceDescriptorState.DRAFT);
    }

    SELF createEService(EServiceDescriptorState state);

    SELF addRiskAnalysis();

    SELF updateDescriptor(Consumer<UpdateEServiceDescriptorCommand> commandConfig, EServiceDescriptorState state) ;

    SELF updateDescriptorWithContextAttributes(
            UpdateEServiceDescriptorCommand command,
            int dailyCallsPerConsumer,
            EServiceDescriptorState state
    );

    SELF updateDescriptorWithContextAttributes(
            Consumer<UpdateEServiceDescriptorCommand> commandConfig,
            int dailyCallsPerConsumer,
            EServiceDescriptorState state
    );

    SELF archiveEService(GracePeriodDays gracePeriodDays);

    SELF waitUntilEService(Predicate<EService> predicate);

    default SELF createEService() {
        return createEService(EServiceDescriptorState.DRAFT);
    }
}
