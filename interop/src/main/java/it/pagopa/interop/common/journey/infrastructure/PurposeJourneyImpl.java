package it.pagopa.interop.common.journey.infrastructure;

import static org.instancio.internal.util.StringUtils.isBlank;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.PurposeJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisSigningState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.instancio.internal.util.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurposeJourneyImpl implements PurposeJourney<PurposeJourneyImpl> {

    private final PurposeUseCase purposeUseCase;
    private final EntityStore entityStore;
    private final AuthSession authSession;

    @Override
    public PurposeJourneyImpl createPurpose(PurposeVersionState state) {
        EService lastEService = entityStore.getLastOrThrow(EService.class);
        Purpose draftPurpose;

        // Se si richiede la finalità in attesa di approvazione, le chiamate giornaliere per fruitore
        // saranno impostate con un limite maggiore di quello base dell'ultimo descrittore e-service
        if (state.equals(PurposeVersionState.WAITING_FOR_APPROVAL)) {
            draftPurpose = purposeUseCase.createPurpose(lastEService,
                    command -> command.eserviceId(lastEService.getId()).dailyCalls(
                            lastEService.getActiveDescriptor().getDailyCallsPerConsumer() + 1
                    )
            );
        } else {
            draftPurpose = purposeUseCase.createPurpose(lastEService);
        }

        return processLifecycle(draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createPurposeAndLinkToEService(EService eService, PurposeVersionState state) {
        return createPurposeAndLinkToEService(eService, state, null);
    }

    @Override
    public PurposeJourneyImpl createPurposeAndLinkToEService(EService eService, PurposeVersionState state, @Nullable String alias) {
        Purpose draftPurpose = purposeUseCase.createPurpose(eService);
        registerAliasIfPresent(alias, draftPurpose);
        return processLifecycle(draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createReversePurposeAndLinkToEService(EService eService, EServiceRiskAnalysis riskAnalysis, PurposeVersionState state) {
        return createReversePurposeAndLinkToEService(eService, riskAnalysis, state, null);
    }

    @Override
    public PurposeJourneyImpl createReversePurposeAndLinkToEService(
            EService eService,
            EServiceRiskAnalysis riskAnalysis,
            PurposeVersionState state,
            @Nullable String alias
    ) {
        Purpose draftPurpose = purposeUseCase.createReversePurpose(eService, riskAnalysis);
        registerAliasIfPresent(alias, draftPurpose);
        return processLifecycle(draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createPurposeFromTemplateAndLinkToEService(PurposeTemplate purposeTemplate, EService eService, PurposeVersionState state) {
        return createPurposeFromTemplateAndLinkToEService(purposeTemplate, eService, state, null);
    }

    @Override
    public PurposeJourneyImpl createPurposeFromTemplateAndLinkToEService(
            PurposeTemplate purposeTemplate,
            EService eService,
            PurposeVersionState state,
            @Nullable String alias
    ) {
        Purpose draftPurpose;
        // Se si richiede la finalità in attesa di approvazione, le chiamate giornaliere per fruitore
        // saranno impostate con un limite maggiore di quello base dell'ultimo descrittore e-service
        if (state.equals(PurposeVersionState.WAITING_FOR_APPROVAL)) {
            draftPurpose = purposeUseCase.createPurposeFromTemplate(purposeTemplate, eService,
                    command -> command.eserviceId(eService.getId()).dailyCalls(
                            eService.getLatestDescriptor().getDailyCallsPerConsumer() + 1
                    )
            );
        } else {
            draftPurpose = purposeUseCase.createPurposeFromTemplate(purposeTemplate, eService);
        }
        registerAliasIfPresent(alias, draftPurpose);
        return processLifecycle(draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl clonePurpose(Purpose purpose, EService eService) {
        Purpose activePurpose = purpose.getLastActiveVersion() != null
                ? purpose
                : activatePipeline(purpose);
        purposeUseCase.clonePurpose(activePurpose, eService);
        return this;
    }

    @Override
    public PurposeJourneyImpl bringPurposeRiskAnalysisToStateForReviewer(
            Purpose purpose,
            Tenant consumer,
            RiskAnalysisSigningState targetState
    ) {
        User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);

        switch (targetState) {
            case ASSIGNED -> assignRiskAnalysisPipeline(purpose, reviewer);
            case SUBMITTED -> {
                Purpose assigned = assignRiskAnalysisPipeline(purpose, reviewer);
                submitRiskAnalysisPipeline(assigned);
            }
            case SIGNED -> {
                Purpose assigned = assignRiskAnalysisPipeline(purpose, reviewer);
                Purpose submitted = submitRiskAnalysisPipeline(assigned);
                authSession.set(reviewer, consumer);
                purposeUseCase.signRiskAnalysis(submitted);
            }
            case REJECTED -> {
                Purpose assigned = assignRiskAnalysisPipeline(purpose, reviewer);
                Purpose submitted = submitRiskAnalysisPipeline(assigned);
                authSession.set(reviewer, consumer);
                purposeUseCase.rejectRiskAnalysis(submitted, "Rifiutata nell'ambito del test");
            }
            default -> throw new UnsupportedOperationException(
                    String.format("Stato di valutazione analisi del rischio %s non supportato nel Journey.", targetState)
            );
        }

        return this;
    }

    private Purpose assignRiskAnalysisPipeline(Purpose purpose, User reviewer) {
        return purposeUseCase.assignRiskAnalysisReviewer(
                purpose,
                RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS,
                reviewer
        );
    }

    private Purpose submitRiskAnalysisPipeline(Purpose purpose) {
        return purposeUseCase.submitRiskAnalysis(purpose);
    }

    private void registerAliasIfPresent(@Nullable String alias, Purpose purpose) {
        if (isBlank(alias)) {
            return;
        }
        Purpose storedPurpose = entityStore.getById(purpose.getId(), Purpose.class)
                .orElse(purpose);
        entityStore.upsert(alias, storedPurpose);
    }

    private PurposeJourneyImpl processLifecycle(Purpose purpose, PurposeVersionState targetState) {
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
}
