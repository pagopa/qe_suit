package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.*;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.journey.application.EServiceJourney;
import it.pagopa.utils.async.PollingUtils;
import jakarta.annotation.Nullable;
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
    private final EServiceRiskAnalysisUseCase eServiceRiskAnalysisUseCase;
    private final CurrentUserSession currentUserSession;
    private final EServiceRequestFactory requestFactory;

    @Override
    public EServiceJourneyImpl createEService(EServiceCreationCommand command, EServiceDescriptorState targetState) {
        EService draftEService = eServiceUseCase.createEService(command);
        return processLifecycle(draftEService, draftEService.getLastDraftDescriptor(), targetState);
    }

    @Override
    public EServiceJourneyImpl createEService(Consumer<EServiceCreationCommand> commandConfig, EServiceDescriptorState targetState) {
        EService draftEService = eServiceUseCase.createEService(commandConfig);
        return processLifecycle(draftEService, draftEService.getLastDraftDescriptor(), targetState);
    }

    @Override
    public EServiceJourneyImpl createEService(EServiceDescriptorState targetState) {
        return createEService(cmd -> {
        }, targetState);
    }

    @Override
    public EServiceJourneyImpl linkOpenApiInterface() {
        return linkOpenApiInterface("assets/origin-interface.yaml", null);
    }

    @Override
    public EServiceJourneyImpl linkOpenApiInterface(String path, @Nullable String name) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor descriptor = eService.getLastDraftDescriptor();
        EServiceDescriptor updatedDescriptor = eServiceDescriptorUseCase.linkOpenApiInterface(eService, descriptor, path, name);
        eService.addDescriptor(updatedDescriptor);
        entityStore.upsert(eService);
        return this;
    }

    @Override
    public EServiceJourneyImpl createEServiceWithCertifiedAttribute(EServiceDescriptorState state) {
        Attribute attribute = entityStore.getLastOrThrow(Attribute.class);
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        command.attributes(Attributes.builder().certified(attribute).build());
        createEService(EServiceDescriptorState.DRAFT);
        return updateDescriptor(command, state);
    }

    @Override
    public EServiceJourneyImpl addDescriptor(EServiceDescriptorState state) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor eServiceDescriptor = eServiceDescriptorUseCase.addDescriptor(eService);
        EService updatedEService = entityStore.getLastOrThrow(EService.class);
        return processLifecycle(updatedEService, eServiceDescriptor, state);
    }

    @Override
    public EServiceJourneyImpl addDescriptor(EService eService, EServiceDescriptorState state) {
        EServiceDescriptor eServiceDescriptor = eServiceDescriptorUseCase.addDescriptor(eService);
        EService updatedEService = entityStore.getLastOrThrow(EService.class);
        return processLifecycle(updatedEService, eServiceDescriptor, state);
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
    public EServiceJourneyImpl addRiskAnalysis() {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceRiskAnalysis riskAnalysis = eServiceRiskAnalysisUseCase.addLatestRiskAnalysis(currentUserSession.getTenant(), eService, true);
        entityStore.upsert(riskAnalysis);
        return this;
    }

    @Override
    public EServiceJourneyImpl updateDescriptor(Consumer<UpdateEServiceDescriptorCommand> commandConfig, EServiceDescriptorState state) {
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        commandConfig.accept(command);
        return updateDescriptor(command, state);
    }

    @Override
    public EServiceJourneyImpl updateDescriptorWithContextAttributes(UpdateEServiceDescriptorCommand command, int dailyCallsPerConsumer, EServiceDescriptorState state) {
        command.attributes(attributes -> prepareContextAttributes(attributes, dailyCallsPerConsumer));
        return updateDescriptor(command, state);
    }

    @Override
    public EServiceJourneyImpl updateDescriptorWithContextAttributes(Consumer<UpdateEServiceDescriptorCommand> commandConfig, int dailyCallsPerConsumer, EServiceDescriptorState state) {
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
        PollingUtils.pollUntil(() -> {
            eService.getDescriptors().forEach(descriptor -> eServiceDescriptorUseCase.getDescriptor(eService, descriptor));
            return eServiceUseCase.getEService(eService);
        }, predicate);
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
            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState)
            );
        };
    }

    private EServiceJourneyImpl publishPipeline(EService eService, EServiceDescriptor eServiceDescriptor, UpdateEServiceDescriptorCommand command) {
        EServiceDescriptor updatedDescriptor;
        if (command != null) {
            updatedDescriptor = eServiceDescriptorUseCase.prepareDescriptorForPublication(eService, eServiceDescriptor, command);
        } else {
            updatedDescriptor = eServiceDescriptorUseCase.prepareDescriptorForPublication(
                    eService, eServiceDescriptor,
                    cmd -> cmd.dailyCallsTotal(eServiceDescriptor.getDailyCallsTotal())
                            .dailyCallsPerConsumer(eServiceDescriptor.getDailyCallsPerConsumer())
                            .attributes(Attributes.builder()
                                    .certified(eServiceDescriptor.getAttributes().getCertified())
                                    .declared(eServiceDescriptor.getAttributes().getDeclared())
                                    .verified(eServiceDescriptor.getAttributes().getVerified())
                                    .build())
            );
        }
        eServiceDescriptorUseCase.linkOpenApiInterface(eService, updatedDescriptor, "assets/origin-interface.yaml", null);
        eServiceDescriptorUseCase.publishDescriptor(eService, eServiceDescriptor);
        return this;
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