package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EServiceDescriptorLifecycleUseCase {
    private final EServiceDescriptorUseCase descriptors;
    private final EServiceDescriptorLifecyclePlanner planner;
    private final AgreementUseCase agreements;

    public void validateCanReachState(
            EServiceDescriptorState source, EServiceDescriptorState target
    ) {
        planner.planToReachState(source, target);
    }

    public EServiceDescriptor bringToState(
            EService eService, EServiceDescriptor descriptor, EServiceDescriptorState targetState
    ) {
        return bringToState(eService, descriptor, targetState, null);
    }

    public EServiceDescriptor bringToState(
            EService eService,
            EServiceDescriptor descriptor,
            EServiceDescriptorState targetState,
            UpdateEServiceDescriptorCommand command
    ) {
        EServiceDescriptor current = descriptors.getDescriptor(eService, descriptor);
        List<EServiceDescriptorLifecycleStep> plan = planner.planToReachState(current.getState(), targetState);

        if (command != null && current.getState() != EServiceDescriptorState.DRAFT)
            throw new IllegalArgumentException("Cannot apply draft configuration in state " + current.getState());

        if (plan.isEmpty() && command != null)
            current = descriptors.updateDescriptor(eService, current, command);

        for (EServiceDescriptorLifecycleStep step : plan) {
            current = switch (step.action()) {
                case PUBLISH -> publishDraft(eService, current, command);
                case PUBLISH_FOR_APPROVAL -> {
                    EServiceDescriptor prepared = descriptors.prepareDescriptorForPublication(eService, current, command);
                    yield descriptors.publishDescriptorAwaitingApproval(eService, prepared);
                }
                case PUBLISH_NEXT_VERSION -> {
                    agreements.ensureActiveAgreement(eService, current);
                    publishNextVersion(eService);
                    yield descriptors.awaitState(eService, current, step.expectedState());
                }
                case SUSPEND -> descriptors.suspendDescriptor(eService, current);
                case ARCHIVE -> {
                    descriptors.archiveDescriptor(eService, current, GracePeriodDays.NUMBER_60);
                    yield descriptors.getDescriptor(eService, current);
                }
            };
            requireState(current, step.expectedState());
        }
        return requireState(current, targetState);
    }

    public EServiceDescriptor prepareForPublication(EService eService, EServiceDescriptor descriptor) {
        EServiceDescriptor current = descriptors.getDescriptor(eService, descriptor);
        return descriptors.prepareDescriptorForPublication(
                eService, current, (UpdateEServiceDescriptorCommand) null
        );
    }

    private EServiceDescriptor publishDraft(
            EService eService, EServiceDescriptor descriptor, UpdateEServiceDescriptorCommand command
    ) {
        EServiceDescriptor prepared = descriptors.prepareDescriptorForPublication(eService, descriptor, command);
        return descriptors.publishDescriptor(eService, prepared);
    }

    private void publishNextVersion(EService eService) {
        EServiceDescriptor next = eService.findDraftDescriptor()
                .map(draft -> descriptors.getDescriptor(eService, draft))
                .filter(draft -> draft.getState() == EServiceDescriptorState.DRAFT)
                .orElseGet(() -> descriptors.addDescriptor(eService));
        EServiceDescriptor prepared = descriptors.prepareDescriptorForPublication(
                eService, next, (UpdateEServiceDescriptorCommand) null
        );
        descriptors.publishDescriptor(eService, prepared);
    }

    private static EServiceDescriptor requireState(
            EServiceDescriptor descriptor, EServiceDescriptorState expected
    ) {
        if (descriptor.getState() != expected) {
            throw new IllegalStateException("Expected descriptor state " + expected + " but was " + descriptor.getState());
        }
        return descriptor;
    }
}
