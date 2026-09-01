package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.journey.application.EServiceJourney;
import it.pagopa.interop.common.kernel.context.EntityStore;
import it.pagopa.interop.common.kernel.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class EServiceJourneyImpl implements EServiceJourney<EServiceJourneyImpl> {

    private final EServiceUseCase eServiceUseCase;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EntityStore entityStore;

    @Override
    public EServiceJourneyImpl createEService(EServiceCreationCommand command, EServiceDescriptorState targetState) {
        EService draftEService = eServiceUseCase.createEService(command);
        return processLifecycle(draftEService, draftEService.getLastDraftDescriptor(), targetState);
    }

    @Override
    public EServiceJourneyImpl createEService(EServiceDescriptorState targetState) {
        EService draftEService = eServiceUseCase.createEService(cmd -> {
        });
        return processLifecycle(draftEService, draftEService.getLastDraftDescriptor(), targetState);
    }

    @Override
    public EServiceJourneyImpl addDescriptor(EServiceDescriptorState state) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor eServiceDescriptor = eServiceDescriptorUseCase.addDescriptor(eService);
        return processLifecycle(eService, eServiceDescriptor, state);
    }

    @Override
    public EServiceJourneyImpl addDescriptor(EService eService, EServiceDescriptorState state) {
        EServiceDescriptor eServiceDescriptor = eServiceDescriptorUseCase.addDescriptor(eService);
        return processLifecycle(eService, eServiceDescriptor, state);
    }

    @Override
    public EServiceJourneyImpl waitUntilEService(Predicate<EService> predicate) {
        EService eService = entityStore.getLastOrThrow(EService.class);

        PollingUtils.pollUntil(
                () -> {
                    eService.getDescriptors().forEach(
                            descriptor -> eServiceDescriptorUseCase.getDescriptor(eService, descriptor)
                    );
                    return eServiceUseCase.getEService(eService);
                },
                predicate
        );

        return this;
    }

    private EServiceJourneyImpl processLifecycle(EService eService, EServiceDescriptor eServiceDescriptor, EServiceDescriptorState targetState) {
        entityStore.upsert(eService);

        return switch (targetState) {
            case DRAFT -> this;

            case PUBLISHED -> publishPipeline(eService, eServiceDescriptor);

            // Facilmente estensibile in futuro senza toccare i metodi pubblici:
            // case SUSPENDED -> publishPipeline(eService).suspendPipeline(eService);

            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState)
            );
        };
    }

    private EServiceJourneyImpl publishPipeline(EService eService, EServiceDescriptor eServiceDescriptor) {
        eServiceDescriptorUseCase.prepareDescriptorForPublication(eService, eServiceDescriptor);
        eServiceDescriptorUseCase.publishDescriptor(eService, eServiceDescriptor);
        return this;
    }
}