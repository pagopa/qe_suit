package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.PurposeJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class PurposeJourneyImpl implements PurposeJourney<PurposeJourneyImpl> {

    private static final String REJECTION_REASON = "Rifiutata nell'ambito del test";

    private final PurposeUseCase purposeUseCase;
    private final EntityStore entityStore;
    private final AuthSession authSession;
    private final CurrentChannel<Channel> currentChannel;

    @Override
    public PurposeJourneyImpl createPurpose(PurposeVersionState state) {
        EService lastEService = entityStore.getLastOrThrow(EService.class);
        Purpose draftPurpose;

        // Se si richiede la finalità in attesa di approvazione, le chiamate giornaliere per fruitore
        // saranno impostate con un limite maggiore di quello base dell'ultimo descrittore e-service
        // è necessario anche per rifiutare la finalità
        if (state.equals(PurposeVersionState.WAITING_FOR_APPROVAL) || state.equals(PurposeVersionState.REJECTED)) {
            draftPurpose = purposeUseCase.createPurpose(lastEService,
                    command -> command.eserviceId(lastEService.getId()).dailyCalls(
                            lastEService.getActiveDescriptor().getDailyCallsPerConsumer() + 1
                    )
            );
        } else {
            draftPurpose = purposeUseCase.createPurpose(lastEService);
        }

        return processLifecycle(lastEService, draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createPurposeAndLinkToEService(EService eService, PurposeVersionState state) {
        Purpose draftPurpose = purposeUseCase.createPurpose(eService);
        return processLifecycle(eService, draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createReversePurposeAndLinkToEService(EService eService, EServiceRiskAnalysis riskAnalysis, PurposeVersionState state) {
        Purpose draftPurpose = purposeUseCase.createReversePurpose(eService, riskAnalysis);
        return processLifecycle(eService, draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createPurposeFromTemplateAndLinkToEService(PurposeTemplate purposeTemplate, EService eService, PurposeVersionState state) {
        Purpose draftPurpose;
        // Se si richiede la finalità in attesa di approvazione, le chiamate giornaliere per fruitore
        // saranno impostate con un limite maggiore di quello base dell'ultimo descrittore e-service
        // è necessario anche per rifiutare la finalità
        if (state.equals(PurposeVersionState.WAITING_FOR_APPROVAL) || state.equals(PurposeVersionState.REJECTED)) {
            draftPurpose = purposeUseCase.createPurposeFromTemplate(purposeTemplate, eService,
                    command -> command.eserviceId(eService.getId()).dailyCalls(
                            eService.getLatestDescriptor().getDailyCallsPerConsumer() + 1
                    )
            );
        } else {
            draftPurpose = purposeUseCase.createPurposeFromTemplate(purposeTemplate, eService);
        }
        return processLifecycle(eService, draftPurpose, state);
    }

    private PurposeJourneyImpl processLifecycle(EService eService, Purpose purpose, PurposeVersionState targetState) {
       switch (targetState) {
            case DRAFT -> {
                // Nothing to do, the purpose is already in DRAFT state
            }

            case WAITING_FOR_APPROVAL -> purposeUseCase.askForApprovalPurpose(purpose, purpose.getLastDraftVersion(), null);

            case ACTIVE -> activatePipeline(purpose);

            case SUSPENDED -> {
                Purpose activatedPurpose = activatePipeline(purpose);
                suspendPipeline(activatedPurpose);
            }

            case ARCHIVED -> archivePipeline(purpose);

            case REJECTED -> rejectPipeline(eService, purpose);

            default -> throw new UnsupportedOperationException(
                    String.format("La transizione allo stato %s) non è ancora supportata nel Journey.", targetState)
            );
        }

        return this;
    }

    private Purpose activatePipeline(Purpose purpose) {
        return purposeUseCase.activatePurpose(purpose, purpose.getLastDraftVersion(), null);
    }

    private Purpose suspendPipeline(Purpose purpose) {
        return purposeUseCase.suspendPurpose(purpose, purpose.getLastActiveVersion(), null);
    }

    private Purpose archivePipeline(Purpose purpose) {
        Purpose activatedPurpose = activatePipeline(purpose);
        PurposeVersion activeVersion = activatedPurpose.getLastActiveVersion();
        if (activeVersion == null) {
            throw new IllegalStateException(
                    "No active version found for purpose " + activatedPurpose.getId()
            );
        }
        return purposeUseCase.archivePurpose(activatedPurpose, activeVersion);
    }

    private Purpose rejectPipeline(EService eService, Purpose purpose) {
        Purpose waitingPurpose = purposeUseCase.askForApprovalPurpose(
                purpose, purpose.getLastDraftVersion(), null
        );
        PurposeVersion waitingVersion = waitingPurpose.getLastWaitingForApprovalVersion();
        if (waitingVersion == null) {
            throw new IllegalStateException(
                    "No purpose version waiting for approval was found for purpose " + purpose.getId()
            );
        }
        // Il rifiuto è un'operazione dell'erogatore: va eseguita con la sessione del producer dell'e-service
        return withProducerSession(
                eService,
                () -> purposeUseCase.rejectPurpose(waitingPurpose, waitingVersion, REJECTION_REASON)
        );
    }

    private Purpose withProducerSession(EService eService, Supplier<Purpose> action) {
        InteropActor originalUser = authSession.getActor();
        Tenant originalTenant = authSession.getTenant();
        Channel originalChannel = currentChannel.getCurrentChannel();
        Throwable primaryFailure = null;

        try {
            Tenant producer = Tenant.fromOrganizationId(UUID.fromString(eService.getProducerId()));
            currentChannel.setCurrentChannel(Channel.BFF);
            authSession.set(User.getTenantAdmin(producer), producer);
            return action.get();
        } catch (RuntimeException | Error failure) {
            primaryFailure = failure;
            throw failure;
        } finally {
            Throwable restorationFailure = null;
            try {
                authSession.set(originalUser, originalTenant);
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
}
