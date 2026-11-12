package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface EServiceJourney<SELF extends EServiceJourney<SELF>> extends JourneyModule {
    SELF createEService(EServiceCreationCommand command, EServiceDescriptorState state);

    SELF createEService(Consumer<EServiceCreationCommand> commandConfig, EServiceDescriptorState state);

    default SELF createEService(EServiceCreationCommand command) {
        return createEService(command, EServiceDescriptorState.DRAFT);
    }

    default SELF createEService(Consumer<EServiceCreationCommand> commandConfig) {
        return createEService(commandConfig, EServiceDescriptorState.DRAFT);
    }

    default SELF createEService(EServiceDescriptorState state) {
        return createEService(command -> {
        }, state);
    }

    SELF shouldFailToImportSOAPEService(
            String eserviceName,
            String wsdlResourcePath,
            SoapInterfaceUploadFailureReason reason
    );

    SELF assertEServiceNotPresentInProducerCatalog(String eserviceName);

    default SELF createEService() {
        return createEService(EServiceDescriptorState.DRAFT);
    }

    SELF addRiskAnalysis();

    SELF archiveEService(GracePeriodDays gracePeriodDays);

    SELF waitUntilEService(Predicate<EService> predicate);
}
