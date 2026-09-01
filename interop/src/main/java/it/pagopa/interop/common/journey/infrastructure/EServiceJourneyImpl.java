package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.context.EntityStore;
import it.pagopa.interop.common.journey.application.EServiceJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EServiceJourneyImpl implements EServiceJourney<EServiceJourneyImpl> {

    private final EServiceUseCase eServiceUseCase;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EntityStore entityStore;

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
        entityStore.upsert(eService);

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
        EServiceDescriptor descriptor = eServiceDescriptorUseCase.prepareDescriptorForPublication(eService, eService.getLastDraftDescriptor());
        EServiceDescriptor published = eServiceDescriptorUseCase.publishDescriptor(eService, descriptor);
        eService.addDescriptor(published);
        entityStore.upsert(eService);
        return this;
    }
}