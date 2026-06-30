package it.pagopa.interop.bff.journey;

import it.pagopa.interop.bff.service.EServiceTestService;
import it.pagopa.interop.bff.service.EServiceDescriptorTestService;
import it.pagopa.interop.bff.service.EServiceRiskAnalysisTestService;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.eservice.EServiceDescriptor;
import it.pagopa.interop.common.contract.model.eservice.EServiceDescriptorState;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceJourney implements it.pagopa.interop.common.contract.journey.EServiceJourney<EServiceJourney> {

    private final EServiceTestService service;
    private final EServiceDescriptorTestService descriptorService;
    private final EServiceRiskAnalysisTestService riskAnalysisService;
    private final ScenarioContext context;

    @Override
    public EServiceJourney createEService(EServiceDescriptorState state) {
        // DRAFT
        EService eService = service.createAndFillDraftEservice()
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel();

        EServiceDescriptor draftDescriptor = eService.getLastDraftDescriptor();

        if (state == EServiceDescriptorState.DRAFT) return this;

        // PUBLISHED
        if (state == EServiceDescriptorState.PUBLISHED) {
            riskAnalysisService.addLatestRiskAnalysis(eService.getId(), true)
                    .withPolling(PollingStrategy.UNTIL_SUCCESS)
                    .andUpdateContext();

            descriptorService.updateDraftDescriptorWithFullData(eService.getId(), draftDescriptor.getId())
                    .withPolling(PollingStrategy.UNTIL_SUCCESS)
                    .andUpdateContext();

            //TODO: aggiungere interfaccia al descrittore

            descriptorService.publish(eService.getId(), draftDescriptor.getId())
                    .withPolling(PollingStrategy.UNTIL_SUCCESS)
                    .andUpdateContext();

            return this;
        }

        throw new IllegalArgumentException("Unsupported EServiceDescriptorState: " + state);
    }

    @Override
    public EServiceJourney publishEService() {
        EService current = context.getLastOrThrow(EService.class);
        EServiceDescriptor draftDescriptor = current.getLastDraftDescriptor();

        descriptorService.publish(current.getId(), draftDescriptor.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();

        return this;
    }
}
