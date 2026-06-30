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
//    private void addInterfaceToDraftDescriptor(UUID eserviceId, UUID descriptorId) {
//        // 1) assicura descriptor disponibile/stabile
//        pollEservice(
//                () -> eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId),
//                resp -> resp != null && resp.getState() == EServiceDescriptorState.DRAFT,
//                Duration.ofSeconds(20)
//        );
//
//        ClassPathResource resource = new ClassPathResource("assets/origin-interface.yaml");
//        ResponseEntity<CreatedResource> createResponse = pollResponse(
//                () -> eservicesApi.createEServiceDocumentWithHttpInfo(
//                        eserviceId,
//                        descriptorId,
//                        "INTERFACE",
//                        "Interfaccia",
//                        resource
//                ),
//                resp -> resp != null && resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null,
//                Duration.ofSeconds(20)
//        );
//
//        CreatedResource created = createResponse.getBody();
//
//        // 3) verifica interfaccia presente
//        pollEservice(
//                () -> eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId),
//                resp -> resp != null
//                        && resp.getInterface() != null
//                        && Objects.equals(resp.getInterface().getId(), created.getId()),
//                Duration.ofSeconds(20)
//        );
//    }
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
