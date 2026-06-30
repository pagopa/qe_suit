package it.pagopa.interop.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.bff.infrastructure.client.EServiceMapper;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.service.EServiceService;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceBffService extends RestService implements EServiceService<EServiceSeed> {

    private final EservicesApi eservicesApi;
    private final EServiceMapper mapper;
    private final ScenarioContext context;
    private final ObjectMapper objectMapper;

    @Override
    public TestChain<CreatedEServiceDescriptor, EService> createWith(EServiceSeed seed) {
        return super.create(
                () -> eservicesApi.createEServiceWithHttpInfo(seed),
                (created) -> getUpdatedEService(created.getId(), created.getDescriptorId())
        );
    }

    @Override
    public TestChain<ProducerEServiceDetails, EService> read(UUID eserviceId) {
        return super.read(
                () -> eservicesApi.getProducerEServiceDetailsWithHttpInfo(eserviceId),
                (es) -> getUpdatedEService(es.getId(), es.getLatestActiveDescriptorId())
        );
    }

    @Override
    public TestChain<ProducerEServiceDescriptor, EService> read(UUID eserviceId, UUID descriptorId) {
        return super.read(
                () -> eservicesApi.getProducerEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
                (e) -> mapper.toDomainWithUpsert(e, context.getById(eserviceId, EService.class).orElse(null))
        );
    }

    @Override
    public EServiceSeed buildFullCreationRequest() {
        return Instancio.of(EServiceSeed.class)
                .set(field(EServiceSeed::getName), "eservice-" + UUID.randomUUID())
                .set(field(EServiceSeed::getDescription), "descrizione test")
                .set(field(EServiceSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceSeed::getMode), EServiceMode.RECEIVE)
                .create();
    }

    @Override
    public EServiceSeed mapRawCreationSeed(Map<String, String> rawSeed) {
        return objectMapper.convertValue(rawSeed, EServiceSeed.class);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private EService getUpdatedEService(UUID eserviceId, UUID descriptorId) {
        return read(eserviceId, descriptorId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .getModel();
    }
}