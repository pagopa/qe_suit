package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.support.EServiceDescriptorUpdater;
import it.pagopa.interop.bff.support.EServiceSeedFactory;
import it.pagopa.interop.common.contract.service.IEServiceDescriptorTestService;
import it.pagopa.interop.common.contract.model.eservice.EService;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.contract.template.rest.AbstractRestClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceDescriptorTestService extends AbstractRestClient implements IEServiceDescriptorTestService {

    private final EservicesApi eservicesApi;
    private final EServiceSeedFactory seedFactory;
    private final EServiceDescriptorUpdater descriptorUpdater;

    @Override
    public TestChain<ProducerEServiceDescriptor, EService> read(UUID eserviceId, UUID descriptorId) {
        return super.read(
                () -> eservicesApi.getProducerEServiceDescriptorWithHttpInfo(eserviceId, descriptorId),
                descriptor -> descriptorUpdater.upsert(eserviceId, descriptor)
        );
    }

    @Override
    public TestChain<?, EService> updateDraftDescriptorWith(UUID eserviceId, UUID descriptorId, Map<String, String> rawUpdateSeed) {
        UpdateEServiceDescriptorSeed seed = seedFactory.updateDescriptorSeedFrom(rawUpdateSeed);
        return updateDraftDescriptorWith(eserviceId, descriptorId, seed);
    }

    @Override
    public TestChain<?, EService> updateDraftDescriptorWithFullData(UUID eserviceId, UUID descriptorId) {
        return updateDraftDescriptorWith(eserviceId, descriptorId, seedFactory.fullUpdateDescriptorRequest());
    }

    public TestChain<?, EService> updateDraftDescriptorWith(UUID eserviceId, UUID descriptorId, UpdateEServiceDescriptorSeed seed) {
        return super.update(
                () -> eservicesApi.updateDraftDescriptorWithHttpInfo(eserviceId, descriptorId, seed),
                res -> readDescriptorAndUpsert(eserviceId, descriptorId)
        );
    }

    @Override
    public TestChain<?, EService> publish(UUID eserviceId, UUID descriptorId) {
        return super.update(
                () -> eservicesApi.publishDescriptorWithHttpInfo(eserviceId, descriptorId),
                res -> readDescriptorAndUpsert(eserviceId, descriptorId)
        );
    }

    public EService readDescriptorAndUpsert(UUID eserviceId, UUID descriptorId) {
        return read(eserviceId, descriptorId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .getModel();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}