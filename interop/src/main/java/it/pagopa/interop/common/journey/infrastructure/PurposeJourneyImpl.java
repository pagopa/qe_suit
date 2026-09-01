package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.PurposeJourney;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurposeJourneyImpl implements PurposeJourney<PurposeJourneyImpl> {

    private final PurposeUseCase purposeUseCase;
    private final EntityStore entityStore;

    @Override
    public PurposeJourneyImpl linkPurpose(PurposeVersionState state) {
        EService lastEService = entityStore.getLastOrThrow(EService.class);
        Purpose draftPurpose = purposeUseCase.addDraftPurpose(lastEService);
        return processLifecycle(draftPurpose, state);
    }

    @Override
    public PurposeJourneyImpl linkPurpose(EService eService, PurposeVersionState state) {
        Purpose draftPurpose = purposeUseCase.addDraftPurpose(eService);
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
