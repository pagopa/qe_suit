package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.PurposeJourney;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurposeJourneyImpl implements PurposeJourney<PurposeJourneyImpl> {

    private final PurposeUseCase purposeUseCase;
    private final EntityStore entityStore;

    @Override
    public PurposeJourneyImpl createPurpose(PurposeVersionState state) {
        EService lastEService = entityStore.getLastOrThrow(EService.class);
        Purpose draftPurpose = purposeUseCase.createPurpose(lastEService);
        return processLifecycle(draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createPurposeAndLinkToEService(EService eService, PurposeVersionState state) {
        Purpose draftPurpose = purposeUseCase.createPurpose(eService);
        return processLifecycle(draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createReversePurposeAndLinkToEService(EService eService, EServiceRiskAnalysis riskAnalysis, PurposeVersionState state) {
        Purpose draftPurpose = purposeUseCase.createReversePurpose(eService, riskAnalysis);
        return processLifecycle(draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl createPurposeFromTemplateAndLinkToEService(PurposeTemplate purposeTemplate, EService eService, PurposeVersionState state) {
        Purpose draftPurpose = purposeUseCase.createPurposeFromTemplate(purposeTemplate, eService);
        return processLifecycle(draftPurpose, state);
    }

    private PurposeJourneyImpl processLifecycle(Purpose purpose, PurposeVersionState targetState) {
       switch (targetState) {
            case DRAFT -> {
                // Nothing to do, the purpose is already in DRAFT state
            }

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
