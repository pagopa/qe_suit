package it.pagopa.interop.new_arch.common.journey.infrastructure;

import it.pagopa.interop.new_arch.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.journey.application.EServiceJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EServiceJourneyImpl implements EServiceJourney<EServiceJourneyImpl> {

    private final EServiceUseCase eServiceUseCase;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final DomainContext domainContext;

    @Override
    public EServiceJourneyImpl createEService(EServiceCreationCommand command, EServiceDescriptorState targetState) {
        EService draftEService = eServiceUseCase.createEService(command);
        return processLifecycle(draftEService, targetState);
    }

    @Override
    public EServiceJourneyImpl createEService(EServiceDescriptorState targetState) {
        EService draftEService = eServiceUseCase.createEService(cmd -> {});
        return processLifecycle(draftEService, targetState);
    }

    private EServiceJourneyImpl processLifecycle(EService eService, EServiceDescriptorState targetState) {
        domainContext.upsert(eService);

        return switch (targetState) {
            case DRAFT -> this;

            case PUBLISHED -> publishPipeline(eService);

            // Facilmente estensibile in futuro senza toccare i metodi pubblici:
            // case SUSPENDED -> publishPipeline(eService).suspendPipeline(eService);

            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState)
            );
        };
    }

    private EServiceJourneyImpl publishPipeline(EService eService) {
        eServiceDescriptorUseCase.publishDescriptor(eService, eService.getLastDraftDescriptor());
        return this;
    }
}