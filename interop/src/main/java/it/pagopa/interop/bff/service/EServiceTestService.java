package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.support.EServiceSeedFactory;
import it.pagopa.interop.common.contract.service.IEServiceTestService;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import it.pagopa.interop.common.utils.DeepMerger;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceTestService extends RestService implements IEServiceTestService {

    private final EservicesApi eservicesApi;
    private final EServiceSeedFactory seedFactory;
    private final EServiceDescriptorTestService descriptorClient;

    public TestChain<CreatedEServiceDescriptor, EService> createDraftWith(EServiceSeed seed) {
        return super.create(
                () -> eservicesApi.createEServiceWithHttpInfo(seed),
                created -> descriptorClient.readDescriptorAndUpsert(
                        created.getId(),
                        created.getDescriptorId()
                )
        );
    }

    @Override
    public TestChain<?, EService> createDraftWith(Map<String, String> rawCreationSeed) {
        return createDraftWith(seedFactory.creationSeedFrom(rawCreationSeed));
    }

    @Override
    public TestChain<?, EService> createDraftWithOverride(Map<String, String> rawOverrides) {
        EServiceSeed overrides = seedFactory.creationSeedFrom(rawOverrides);
        EServiceSeed seed = DeepMerger.merge(overrides, seedFactory.fullCreationRequest());

        return createDraftWith(seed);
    }

    @Override
    public TestChain<?, EService> createAndFillDraftEservice() {
        return createDraftWith(seedFactory.fullCreationRequest());
    }

    @Override
    public TestChain<ProducerEServiceDetails, EService> read(UUID eserviceId) {
        return super.read(
                () -> eservicesApi.getProducerEServiceDetailsWithHttpInfo(eserviceId),
                es -> descriptorClient.readDescriptorAndUpsert(
                        es.getId(),
                        es.getLatestActiveDescriptorId()
                )
        );
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}