package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.journey.application.EServiceJourney;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class EServiceJourneyImpl implements EServiceJourney<EServiceJourneyImpl> {

    private final EServiceUseCase eServiceUseCase;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;
    private final EntityStore entityStore;
    private final EServiceRequestFactory requestFactory;

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
    public EServiceJourneyImpl updateDescriptor(UpdateEServiceDescriptorCommand command, EServiceDescriptorState state) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor eServiceDescriptor = eService.getLastDraftDescriptor();
        EServiceDescriptor updatedDescriptor = eServiceDescriptorUseCase.updateDescriptor(eService, eServiceDescriptor, command);
        eService.addDescriptor(updatedDescriptor);
        return processLifecycle(eService, updatedDescriptor, state, command);
    }

    @Override
    public EServiceJourneyImpl updateDescriptor(Consumer<UpdateEServiceDescriptorCommand> commandConfig, EServiceDescriptorState state) {
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        commandConfig.accept(command);
        return updateDescriptor(command, state);
    }

    @Override
    public EServiceJourneyImpl updateDescriptorWithContextAttributes(
            UpdateEServiceDescriptorCommand command,
            int dailyCallsPerConsumer,
            EServiceDescriptorState state
    ) {
        command.attributes(attributes -> prepareContextAttributes(attributes, dailyCallsPerConsumer));
        return updateDescriptor(command, state);
    }

    @Override
    public EServiceJourneyImpl updateDescriptorWithContextAttributes(
            Consumer<UpdateEServiceDescriptorCommand> commandConfig,
            int dailyCallsPerConsumer,
            EServiceDescriptorState state
    ) {
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        commandConfig.accept(command);
        return updateDescriptorWithContextAttributes(command, dailyCallsPerConsumer, state);
    }

    @Override
    public EServiceJourneyImpl archiveEService(GracePeriodDays gracePeriodDays) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        eServiceUseCase.archiveEService(eService, gracePeriodDays);
        return this;
    }

    @Override
    public EServiceJourneyImpl archiveFirstEServiceDescriptor(GracePeriodDays gracePeriodDays) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor descriptor = eService.getLastDeprecatedDescriptor();
        eServiceDescriptorUseCase.archiveDescriptor(eService, descriptor, gracePeriodDays);
        return this;
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
        return processLifecycle(eService, eServiceDescriptor, targetState, null);
    }

    private EServiceJourneyImpl processLifecycle(EService eService, EServiceDescriptor eServiceDescriptor, EServiceDescriptorState targetState, UpdateEServiceDescriptorCommand command) {
        entityStore.upsert(eService);

        return switch (targetState) {
            case DRAFT -> this;

            case PUBLISHED -> publishPipeline(eService, eServiceDescriptor, command);

            // Facilmente estensibile in futuro senza toccare i metodi pubblici:
            // case SUSPENDED -> publishPipeline(eService).suspendPipeline(eService);

            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState)
            );
        };
    }

    private EServiceJourneyImpl publishPipeline(EService eService, EServiceDescriptor eServiceDescriptor, UpdateEServiceDescriptorCommand command) {
        eServiceDescriptorUseCase.prepareDescriptorForPublication(eService, eServiceDescriptor, command);
        eServiceDescriptorUseCase.publishDescriptor(eService, eServiceDescriptor);
        return this;
    }

    private Attributes prepareContextAttributes(Attributes attributes, int dailyCallsPerConsumer) {
        List<Attribute> certified = new ArrayList<>(attributes.getCertified());
        List<Attribute> declared = new ArrayList<>(attributes.getDeclared());
        List<Attribute> verified = new ArrayList<>(attributes.getVerified());

        for (Attribute attribute : entityStore.getAll(Attribute.class)) {
            Attribute updatedAttribute = attribute.toBuilder()
                    .dailyCallsPerConsumer(dailyCallsPerConsumer)
                    .build();

            switch (updatedAttribute.getKind()) {
                case CERTIFIED, CERTIFIED_DISCRETE -> certified.add(updatedAttribute);
                case DECLARED -> declared.add(updatedAttribute);
                case VERIFIED -> verified.add(updatedAttribute);
            }
        }

        return attributes.toBuilder()
                .clearCertified()
                .clearDeclared()
                .clearVerified()
                .certified(certified)
                .declared(declared)
                .verified(verified)
                .build();
    }
}
