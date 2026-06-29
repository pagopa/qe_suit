package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.infrastructure.client.EServiceMapper;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.request.RequestOverride;
import it.pagopa.interop.common.contract.service.EServiceService;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.common.utils.DeepMerger;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.instancio.Select.field;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceBffService extends RestService implements EServiceService {

    private final EservicesApi eservicesApi;
    private final EServiceMapper mapper;

    @Override
    public TestChain<CreatedEServiceDescriptor, EService> create() {
        return createWith(buildFullCreationRequest());
    }

    @Override
    public TestChain<ProducerEServiceDetails, EService> read(UUID eserviceId) {
        return super.read(
                () -> eservicesApi.getProducerEServiceDetailsWithHttpInfo(eserviceId),
                mapper::toDomain
        );
    }

    @Override
    public TestChain<ProducerEServiceDescriptor, EService> read(UUID eserviceId, UUID descriptorId) {
        return super.read(
                () -> eservicesApi.getProducerEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
                mapper::toDomain
        );
    }

    public TestChain<CreatedEServiceDescriptor, EService> createWith(EServiceSeed seed) {
        return super.create(
                () -> eservicesApi.createEServiceWithHttpInfo(seed),
                (e) -> e.g
        );
    }

    public TestChain<CreatedEServiceDescriptor, EService> createWithOverride(EServiceSeed override){
        EServiceSeed seed = buildFullCreationRequest();
        return createWith(DeepMerger.merge(override, seed));
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private EServiceSeed buildFullCreationRequest(){
        return Instancio.of(EServiceSeed.class)
                .set(field(EServiceSeed::getName), "eservice-" + UUID.randomUUID())
                .set(field(EServiceSeed::getDescription), "descrizione test")
                .set(field(EServiceSeed::getTechnology), EServiceTechnology.REST)
                .set(field(EServiceSeed::getMode), EServiceMode.RECEIVE)
                .create();
    }
}