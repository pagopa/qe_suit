package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorLifecycleUseCase;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.journey.application.EServiceDescriptorJourney;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EServiceDescriptorJourneyImpl implements EServiceDescriptorJourney<EServiceDescriptorJourneyImpl> {

    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EServiceDescriptorLifecycleUseCase lifecycle;
    private final EntityStore entityStore;

    @Override
    public EServiceDescriptorJourneyImpl addDescriptor(EServiceDescriptorState state) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        return addDescriptor(eService, state);
    }

    @Override
    public EServiceDescriptorJourneyImpl addDescriptor(EService eService, EServiceDescriptorState state) {
        lifecycle.validateCanReachState(EServiceDescriptorState.DRAFT, state);
        EServiceDescriptor descriptor = eServiceDescriptorUseCase.addDescriptor(eService);
        return processLifecycle(eService, descriptor, state);
    }

    @Override
    public EServiceDescriptorJourneyImpl updateDescriptor(
            UpdateEServiceDescriptorCommand command,
            EServiceDescriptorState state
    ) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        return processLifecycle(eService, eService.getLastDraftDescriptor(), state, command);
    }

    @Override
    public EServiceDescriptorJourneyImpl archiveFirstEServiceDescriptor(GracePeriodDays gracePeriodDays) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor descriptor = eService.getLastDeprecatedDescriptor();
        eServiceDescriptorUseCase.archiveDescriptor(eService, descriptor, gracePeriodDays);
        return this;
    }

    @Override
    public EServiceDescriptorJourneyImpl bringEServiceDescriptorToState(
            EService eService,
            EServiceDescriptor descriptor,
            EServiceDescriptorState state
    ) {
        return processLifecycle(eService, descriptor, state);
    }

    @Override
    public EServiceDescriptorJourneyImpl prepareEServiceDescriptorForPublication(int version) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor descriptor = eService.getDescriptorOrThrow(version);
        lifecycle.prepareForPublication(eService, descriptor);

        return this;
    }

    @Override
    public EServiceDescriptorJourneyImpl publishDescriptor(int version) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor descriptor = eService.getDescriptorOrThrow(version);
        eServiceDescriptorUseCase.publishDescriptor(eService, descriptor);
        return this;
    }

    @Override
    public EServiceDescriptorJourneyImpl bringEServiceDescriptorToState(
            EService eService, int version, EServiceDescriptorState state
    ) {
        if (eService.findDescriptor(version).isEmpty()) {
            lifecycle.validateCanReachState(EServiceDescriptorState.DRAFT, state);
        }
        EServiceDescriptor descriptor = eServiceDescriptorUseCase.getOrCreateDescriptor(eService, version);
        return processLifecycle(eService, descriptor, state);
    }

    private EServiceDescriptorJourneyImpl processLifecycle(
            EService eService,
            EServiceDescriptor descriptor,
            EServiceDescriptorState targetState
    ) {
        return processLifecycle(eService, descriptor, targetState, null);
    }

    private EServiceDescriptorJourneyImpl processLifecycle(
            EService eService,
            EServiceDescriptor descriptor,
            EServiceDescriptorState targetState,
            UpdateEServiceDescriptorCommand command
    ) {
        EServiceDescriptor updated = lifecycle.bringToState(eService, descriptor, targetState, command);
        eService.upsertDescriptor(updated);
        entityStore.upsert(eService);
        return this;
    }
}
