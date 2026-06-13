package it.pagopa.interop.bff.eservice;

import it.pagopa.interop.bff.risk_analysis.RiskAnalysisDataPreparationService;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.domain.model.EService;
import it.pagopa.interop.common.domain.model.RiskAnalysis;
import it.pagopa.interop.common.utils.PollingUtils;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceDataPreparationService {

    private final EservicesApi eservicesApi;
    private final RiskAnalysisDataPreparationService riskAnalysisService;
    private final ScenarioContext context;


    public EService createEservice(EServiceSeed request) {
        CreatedEServiceDescriptor createdEservice = eservicesApi.createEService(request);
        return getEservice(createdEservice.getId(), createdEservice.getDescriptorId());
    }

    public EService createEservice() {
        return createEservice(buildDefaultRequest());
    }

    public EService createEservice(Consumer<EServiceSeed> overrides) {
        EServiceSeed seed = buildDefaultRequest();
        if (overrides != null) {
            overrides.accept(seed);
        }
        return createEservice(seed);
    }

    public EService publishEservice(EService eservice) {
        UUID eserviceId = eservice.getEserviceId();
        UUID descriptorId = eservice.getLastDraftDescriptorId();

        ensureRiskAnalysisIfReceive(eservice);
        prepareDraftDescriptorForPublication(eserviceId, descriptorId);
        addInterfaceToDraftDescriptor(eserviceId, descriptorId);

        eservicesApi.publishDescriptor(eserviceId, descriptorId);

        EService published = pollEservice(
                () -> new EService(eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId)),
                resp -> resp != null
                        && Objects.equals(descriptorId, resp.getId())
                        && resp.getState() == EServiceDescriptorState.PUBLISHED,
                Duration.ofSeconds(20)
        );

        context.upsert(published);
        return published;
    }

    public EService getEservice(UUID eserviceId, UUID descriptorId) {
        EService eservice = pollEservice(
                () -> new EService(eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId)),
                resp -> resp != null
                        && Objects.equals(eserviceId, resp.getEservice().getId())
                        && Objects.equals(descriptorId, resp.getId()),
                Duration.ofSeconds(15)
        );

        context.upsert(eservice);
        return eservice;
    }

    private EServiceSeed buildDefaultRequest() {
        return new EServiceSeed()
                .name("Default EService - " + UUID.randomUUID().toString().substring(0, 8))
                .description("Default EService description")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .personalData(false)
                .isSignalHubEnabled(false)
                .isConsumerDelegable(false)
                .isClientAccessDelegable(false);
    }

    private void prepareDraftDescriptorForPublication(UUID eserviceId, UUID descriptorId) {
        DescriptorAttributesSeed attributes = new DescriptorAttributesSeed()
                .certified(java.util.List.of())
                .declared(java.util.List.of())
                .verified(java.util.List.of());

        UpdateEServiceDescriptorSeed seed = new UpdateEServiceDescriptorSeed()
                .attributes(attributes)
                .audience(java.util.List.of("pagopa.it"))
                .dailyCallsPerConsumer(1)
                .dailyCallsTotal(10)
                .voucherLifespan(60)
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);

        eservicesApi.updateDraftDescriptor(eserviceId, descriptorId, seed);
    }

    private void ensureRiskAnalysisIfReceive(EService eservice) {
        if (eservice.getEservice().getMode() != EServiceMode.RECEIVE) return;

        RiskAnalysis riskAnalysis = riskAnalysisService.createRiskAnalysis(true);
        EServiceRiskAnalysisSeed raSeed = new EServiceRiskAnalysisSeed()
                .name(riskAnalysis.getTitle())
                .riskAnalysisForm(riskAnalysis.getForm());

        eservicesApi.addRiskAnalysisToEService(eservice.getEserviceId(), raSeed);
    }

    private void addInterfaceToDraftDescriptor(UUID eserviceId, UUID descriptorId) {
        // 1) assicura descriptor disponibile/stabile
        pollEservice(
                () -> eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId),
                resp -> resp != null && resp.getState() == EServiceDescriptorState.DRAFT,
                Duration.ofSeconds(20)
        );

        ClassPathResource resource = new ClassPathResource("assets/origin-interface.yaml");
        ResponseEntity<CreatedResource> createResponse = pollResponse(
                () -> eservicesApi.createEServiceDocumentWithHttpInfo(
                        eserviceId,
                        descriptorId,
                        "INTERFACE",
                        "Interfaccia",
                        resource
                ),
                resp -> resp != null && resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null,
                Duration.ofSeconds(20)
        );

        CreatedResource created = createResponse.getBody();

        // 3) verifica interfaccia presente
        pollEservice(
                () -> eservicesApi.getProducerEServiceDescriptor(eserviceId, descriptorId),
                resp -> resp != null
                        && resp.getInterface() != null
                        && Objects.equals(resp.getInterface().getId(), created.getId()),
                Duration.ofSeconds(20)
        );
    }

    private <T> T pollEservice(java.util.function.Supplier<T> supplier, java.util.function.Predicate<T> predicate, Duration timeout) {
        return PollingUtils.pollUntil(
                supplier,
                predicate,
                timeout,
                Duration.ofSeconds(2)
        );
    }

    private <T> T pollResponse(java.util.function.Supplier<T> supplier, java.util.function.Predicate<T> predicate, Duration timeout) {
        return PollingUtils.pollUntil(
                supplier,
                predicate,
                timeout,
                Duration.ofSeconds(2)
        );
    }
}