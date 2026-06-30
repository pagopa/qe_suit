package it.pagopa.interop.bff.journey;

import it.pagopa.interop.bff.client.EServiceBffClient;
import it.pagopa.interop.bff.client.EServiceDescriptorBffClient;
import it.pagopa.interop.bff.client.EServiceRiskAnalysisBffClient;
import it.pagopa.interop.common.contract.journey.EServiceJourney;
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
public class EServiceBffJourney implements EServiceJourney<EServiceBffJourney> {

    private final EServiceBffClient service;
    private final EServiceDescriptorBffClient descriptorService;
    private final EServiceRiskAnalysisBffClient riskAnalysisService;
    private final ScenarioContext context;

    @Override
    public EServiceBffJourney createEService(EServiceDescriptorState state) {
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
    public EServiceBffJourney publishEService() {
        EService current = context.getLastOrThrow(EService.class);
        EServiceDescriptor draftDescriptor = current.getLastDraftDescriptor();

        descriptorService.publish(current.getId(), draftDescriptor.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();

        return this;
    }
}
