package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.AgreementJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AgreementJourneyImpl implements AgreementJourney<AgreementJourneyImpl> {
    private final AgreementUseCase agreementUseCase;
    private final EntityStore entityStore;
    private final CurrentUserSession currentUserSession;
    private final CurrentChannel<Channel> currentChannel;

    @Override
    public AgreementJourneyImpl linkAgreement(
            EService eService,
            AgreementState agreementState,
            @org.jspecify.annotations.Nullable Delegation delegation
    ) {
        Agreement agreement = agreementUseCase.createAgreement(eService, eService.getActiveDescriptor(), delegation);

        switch (agreementState) {
            case DRAFT -> requireRequestedState(agreement, AgreementState.DRAFT);
            case PENDING -> {
                Agreement submitted = submitAndResolveState(agreement);
                requireRequestedState(submitted, AgreementState.PENDING);
            }
            case ACTIVE -> activateIfPending(submitAndResolveState(agreement), delegation);
            case SUSPENDED -> {
                Agreement active = activateIfPending(submitAndResolveState(agreement), delegation);
                Agreement suspended = agreementUseCase.suspendAgreement(active, delegation);
                requireRequestedState(
                        agreementUseCase.awaitState(suspended, Set.of(AgreementState.SUSPENDED)),
                        AgreementState.SUSPENDED
                );
            }
            case ARCHIVED -> {
                Agreement active = activateIfPending(submitAndResolveState(agreement), delegation);
                Agreement archived = agreementUseCase.archiveAgreement(active);
                requireRequestedState(
                        agreementUseCase.awaitState(archived, Set.of(AgreementState.ARCHIVED)),
                        AgreementState.ARCHIVED
                );
            }
            default -> throw new UnsupportedOperationException("Not implemented yet: " + agreementState);
        }

        return this;
    }

    @Override
    public AgreementJourneyImpl linkAgreement(AgreementState agreementState, @Nullable Delegation delegation) {
        EService eService = entityStore.getLastOrThrow(EService.class);
        return linkAgreement(eService, agreementState, delegation);
    }

    private Agreement submitAndResolveState(Agreement agreement) {
        Agreement submitted = agreementUseCase.submitAgreement(agreement);
        return agreementUseCase.awaitState(
                submitted, Set.of(AgreementState.PENDING, AgreementState.ACTIVE)
        );
    }

    private Agreement activateIfPending(Agreement agreement, @Nullable Delegation delegation) {
        if (agreement.getState() == AgreementState.ACTIVE) {
            return agreement;
        }
        if (agreement.getState() != AgreementState.PENDING) {
            throw new IllegalStateException("Submitted agreement reached unsupported state " + agreement.getState());
        }
        Agreement activated = withProducerSession(
                agreement,
                () -> agreementUseCase.activateAgreement(agreement, delegation)
        );
        Agreement resolved = agreementUseCase.awaitState(activated, Set.of(AgreementState.ACTIVE));
        requireRequestedState(resolved, AgreementState.ACTIVE);
        return resolved;
    }

    private Agreement withProducerSession(Agreement agreement, java.util.function.Supplier<Agreement> action) {
        User originalUser = currentUserSession.getUser();
        Tenant originalTenant = currentUserSession.getTenant();
        Channel originalChannel = currentChannel.getCurrentChannel();
        Throwable primaryFailure = null;

        try {
            Tenant producer = Tenant.fromOrganizationId(agreement.getProducerId());
            currentChannel.setCurrentChannel(Channel.BFF);
            currentUserSession.set(User.getTenantAdmin(producer), producer);
            return action.get();
        } catch (RuntimeException | Error failure) {
            primaryFailure = failure;
            throw failure;
        } finally {
            Throwable restorationFailure = null;
            try {
                currentUserSession.set(originalUser, originalTenant);
            } catch (RuntimeException | Error failure) {
                restorationFailure = failure;
            }
            try {
                currentChannel.setCurrentChannel(originalChannel);
            } catch (RuntimeException | Error failure) {
                if (restorationFailure == null) {
                    restorationFailure = failure;
                } else {
                    restorationFailure.addSuppressed(failure);
                }
            }
            if (restorationFailure != null) {
                if (primaryFailure != null) {
                    primaryFailure.addSuppressed(restorationFailure);
                } else if (restorationFailure instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                } else {
                    throw (Error) restorationFailure;
                }
            }
        }
    }

    private static void requireRequestedState(Agreement agreement, AgreementState expectedState) {
        if (agreement.getState() != expectedState) {
            throw new IllegalStateException(
                    "Agreement reached state " + agreement.getState() + " instead of " + expectedState
            );
        }
    }
}
