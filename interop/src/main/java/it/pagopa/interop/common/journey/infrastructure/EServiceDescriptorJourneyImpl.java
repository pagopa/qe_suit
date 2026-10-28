package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorLifecycleUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.journey.application.EServiceDescriptorJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class EServiceDescriptorJourneyImpl implements EServiceDescriptorJourney<EServiceDescriptorJourneyImpl> {

    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EServiceDescriptorLifecycleUseCase lifecycle;
    private final EntityStore entityStore;
    private final EServiceRequestFactory requestFactory;
    private final CurrentUserSession currentUserSession;

    @Override
    public EServiceDescriptorJourneyImpl addDescriptor(EServiceDescriptorState state) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        return addDescriptor(eService, state);
    }

    @Override
    public EServiceDescriptorJourneyImpl addDescriptor(EService eService, EServiceDescriptorState state) {
        requireDelegatedApprovalActor(eService, state);
        lifecycle.validateCanReachState(EServiceDescriptorState.DRAFT, state);
        EServiceDescriptor descriptor = eServiceDescriptorUseCase.addDescriptor(eService);
        return processLifecycle(eService, descriptor, state);
    }

    @Override
    public EServiceDescriptorJourneyImpl updateDescriptor(Consumer<UpdateEServiceDescriptorCommand> commandConfig, EServiceDescriptorState state) {
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        commandConfig.accept(command);
        return updateDescriptor(command, state);
    }

    @Override
    public EServiceDescriptorJourneyImpl updateDescriptor(
            UpdateEServiceDescriptorCommand command,
            EServiceDescriptorState state
    ) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        requireDelegatedApprovalActor(eService, state);
        return processLifecycle(eService, eService.getLastDraftDescriptor(), state, command);
    }

    @Override
    public EServiceDescriptorJourneyImpl updateDescriptorWithContextAttributes(
            Consumer<UpdateEServiceDescriptorCommand> commandConfig,
            int dailyCallsPerConsumer,
            EServiceDescriptorState state
    ) {
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        commandConfig.accept(command);
        return updateDescriptorWithContextAttributes(command, dailyCallsPerConsumer, state);
    }


    @Override
    public EServiceDescriptorJourneyImpl updateDescriptorWithContextAttributes(
            UpdateEServiceDescriptorCommand command,
            int dailyCallsPerConsumer,
            EServiceDescriptorState state
    ) {
        command.attributes(attributes -> prepareContextAttributes(attributes, dailyCallsPerConsumer));
        return updateDescriptor(command, state);
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
        requireDelegatedApprovalActor(eService, state);
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
    public EServiceDescriptorJourneyImpl bringEServiceDescriptorToState(EService eService, int version, EServiceDescriptorState state) {
        requireDelegatedApprovalActor(eService, state);
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

    private void requireDelegatedApprovalActor(EService eService, EServiceDescriptorState state) {
        if (state != EServiceDescriptorState.WAITING_FOR_APPROVAL) {
            return;
        }
        Tenant actor = currentUserSession.getTenant();
        if (actor == null || eService.getProducerId() == null
                || actor.getOrganizationId().toString().equals(eService.getProducerId())) {
            throw new IllegalStateException(
                    "WAITING_FOR_APPROVAL setup requires an active producer delegation and its delegate actor"
            );
        }
    }

    private Attributes prepareContextAttributes(Attributes attributes, int dailyCallsPerConsumer) {
        List<Attribute> certified = new ArrayList<>(attributes.getCertified());
        List<Attribute> declared = new ArrayList<>(attributes.getDeclared());
        List<Attribute> verified = new ArrayList<>(attributes.getVerified());
        for (Attribute attribute : entityStore.getAll(Attribute.class)) {
            Attribute updatedAttribute = attribute.toBuilder().dailyCallsPerConsumer(dailyCallsPerConsumer).build();
            switch (updatedAttribute.getKind()) {
                case CERTIFIED, CERTIFIED_DISCRETE -> certified.add(updatedAttribute);
                case DECLARED -> declared.add(updatedAttribute);
                case VERIFIED -> verified.add(updatedAttribute);
            }
        }
        return attributes.toBuilder().clearCertified().clearDeclared().clearVerified()
                .certified(certified).declared(declared).verified(verified)
                .build();
    }
}
