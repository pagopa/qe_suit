package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.*;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.*;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.journey.application.EServiceJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
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
    private final EServiceDescriptorLifecycleUseCase lifecycle;
    private final EntityStore entityStore;
    private final EServiceRiskAnalysisUseCase eServiceRiskAnalysisUseCase;
    private final CurrentUserSession currentUserSession;
    private final EServiceRequestFactory requestFactory;

    @Override
    public EServiceJourneyImpl createEService(EServiceCreationCommand command, EServiceDescriptorState targetState) {
        rejectUnattendedApprovalSetup(targetState);
        lifecycle.validateCanReachState(EServiceDescriptorState.DRAFT, targetState);
        EService eService = eServiceUseCase.createEService(command);
        return bringDescriptorToState(eService, targetState);
    }

    @Override
    public EServiceJourneyImpl createEService(Consumer<EServiceCreationCommand> commandConfig, EServiceDescriptorState targetState) {
        EService draftEService = eServiceUseCase.createEService(commandConfig);
        return processLifecycle(draftEService, draftEService.getLastDraftDescriptor(), targetState);
    }

    @Override
    public EServiceJourneyImpl createEService(EServiceDescriptorState targetState) {return createEService(cmd -> {}, targetState);}

    @Override
    public EServiceJourneyImpl linkOpenApiInterface() {return linkOpenApiInterface("assets/origin-interface.yaml", null);}

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
        return addDescriptor(entityStore.getLastOrThrow(EService.class), state);
    }

    @Override
    public EServiceJourneyImpl addDescriptor(EService eService, EServiceDescriptorState state) {
        EServiceDescriptor eServiceDescriptor = eServiceDescriptorUseCase.addDescriptor(eService);
        return processLifecycle(entityStore.getLastOrThrow(EService.class), eServiceDescriptor, state);
    }

    @Override
    public EServiceJourneyImpl updateDescriptor(UpdateEServiceDescriptorCommand command, EServiceDescriptorState state) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceDescriptor updatedDescriptor = eServiceDescriptorUseCase.updateDescriptor(eService, eService.getLastDraftDescriptor(), command);
        eService.addDescriptor(updatedDescriptor);
        return processLifecycle(eService, updatedDescriptor, state, command);
    }
        var defaultCommand = requestFactory.defaultCreationEServiceCommand();
        commandConfig.accept(defaultCommand);
        return createEService(defaultCommand, targetState);
    }


    @Override
    public EServiceJourneyImpl addRiskAnalysis() {
        EService eService = entityStore.getLastOrThrow(EService.class);
        entityStore.upsert(eServiceRiskAnalysisUseCase.addLatestRiskAnalysis(currentUserSession.getTenant(), eService, true));
        return this;
    }

    @Override
    public EServiceJourneyImpl archiveEService(GracePeriodDays gracePeriodDays) {
        eServiceUseCase.archiveEService(entityStore.getLastOrThrow(EService.class), gracePeriodDays);
        return this;
    }

    @Override
    public EServiceJourneyImpl archiveFirstEServiceDescriptor(GracePeriodDays gracePeriodDays) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        eServiceDescriptorUseCase.archiveDescriptor(eService, eService.getLastDeprecatedDescriptor(), gracePeriodDays);
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

    @Override
    public EServiceJourneyImpl shouldFailToImportSOAPEService(String eserviceName, String wsdlResourcePath, SoapInterfaceUploadFailureReason reason) {
        eServiceUseCase.shouldFailToImportSOAPEService(eserviceName, wsdlResourcePath, reason);
        return this;
    }

    @Override
    public EServiceJourneyImpl assertEServiceNotPresentInProducerCatalog(String eserviceName) {
        eServiceUseCase.assertEServiceNotPresentInProducerCatalog(eserviceName);
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
            default -> throw new UnsupportedOperationException(String.format("La transizione allo stato %s non è ancora supportata nel Journey.", targetState));
        };
    }

    private EServiceJourneyImpl publishPipeline(EService eService, EServiceDescriptor eServiceDescriptor, UpdateEServiceDescriptorCommand command) {
        EServiceDescriptor updatedDescriptor = (command != null)
                ? eServiceDescriptorUseCase.prepareDescriptorForPublication(eService, eServiceDescriptor, command)
                : eServiceDescriptorUseCase.prepareDescriptorForPublication(
                        eService, eServiceDescriptor,
                        cmd -> cmd.dailyCallsTotal(eServiceDescriptor.getDailyCallsTotal())
                                .dailyCallsPerConsumer(eServiceDescriptor.getDailyCallsPerConsumer())
                                .attributes(Attributes.builder()
                                        .certified(eServiceDescriptor.getAttributes().getCertified())
                                        .declared(eServiceDescriptor.getAttributes().getDeclared())
                                        .verified(eServiceDescriptor.getAttributes().getVerified()).build())
                );
        eServiceDescriptorUseCase.linkOpenApiInterface(eService, updatedDescriptor, "assets/origin-interface.yaml", null);
        eServiceDescriptorUseCase.publishDescriptor(eService, eServiceDescriptor);
    private EServiceJourneyImpl bringDescriptorToState(EService eService, EServiceDescriptorState targetState) {
        EServiceDescriptor updated = lifecycle.bringToState(
                eService, eService.getLastDraftDescriptor(), targetState
        );
        eService.upsertDescriptor(updated);
        entityStore.upsert(eService);
        return this;
    }

    private static void rejectUnattendedApprovalSetup(EServiceDescriptorState targetState) {
        if (targetState == EServiceDescriptorState.WAITING_FOR_APPROVAL) {
            throw new UnsupportedOperationException(
                    "WAITING_FOR_APPROVAL requires an active producer delegation and the delegate actor"
            );
        }
        return attributes.toBuilder().clearCertified().clearDeclared().clearVerified()
                .certified(certified).declared(declared).verified(verified).build();
    }
}