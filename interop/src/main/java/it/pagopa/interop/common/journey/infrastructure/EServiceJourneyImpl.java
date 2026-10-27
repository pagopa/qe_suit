package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorLifecycleUseCase;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.journey.application.EServiceJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
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
    public EServiceJourneyImpl createEService(EServiceDescriptorState targetState) {
        rejectUnattendedApprovalSetup(targetState);
        lifecycle.validateCanReachState(EServiceDescriptorState.DRAFT, targetState);
        EService eService = eServiceUseCase.createEService(command -> {
        });
        return bringDescriptorToState(eService, targetState);
    }

    @Override
    public EServiceJourneyImpl addRiskAnalysis() {
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceRiskAnalysis riskAnalysis =
                eServiceRiskAnalysisUseCase.addLatestRiskAnalysis(currentUserSession.getTenant(), eService, true);
        entityStore.upsert(riskAnalysis);
        return this;
    }

    @Override
    public EServiceJourneyImpl archiveEService(GracePeriodDays gracePeriodDays) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        eServiceUseCase.archiveEService(eService, gracePeriodDays);
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
    }
}
