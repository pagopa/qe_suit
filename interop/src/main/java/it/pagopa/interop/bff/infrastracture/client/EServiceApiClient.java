package it.pagopa.interop.bff.infrastracture.client;

import it.pagopa.interop.common.contract.model.EService;
import it.pagopa.interop.common.contract.template.rest.*;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceApiClient extends RestService implements
        CanCreate<EServiceSeed, CreatedEServiceDescriptor, EService>,
        CanRead<UUID, ProducerEServiceDetails, EService>,
        CanUpdate<EServiceApiClient.EServiceUpdateSeed, CreatedResource, EService>,
        CanDelete<UUID, Void, EService> {

    public record EServiceUpdateSeed(UUID eServiceId, UpdateEServiceSeed updateEServiceSeed) { }

    private final EservicesApi eservicesApi;

    @Override
    public ResponseEntity<ProducerEServiceDetails> doRead(UUID uuid) {
        return eservicesApi.getProducerEServiceDetailsWithHttpInfo(uuid);
    }

    @Override
    public EService updateModelAfterRead(ProducerEServiceDetails producerEServiceDetails) {
        //TODO map struct
        return null;
    }

    @Override
    public EServiceSeed doDefaultCreationRequest() {
        return new EServiceSeed()
                .name("Default EService - " + UUID.randomUUID().toString().substring(0, 8))
                .description("Default EService description")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .personalData(false)
                .isSignalHubEnabled(false)
                .isConsumerDelegable(false)
                .asyncExchange(false)
                .isClientAccessDelegable(false);
    }

    @Override
    public ResponseEntity<CreatedResource> doUpdate(EServiceUpdateSeed eServiceUpdateSeed) {
        return eservicesApi.updateEServiceByIdWithHttpInfo(
                eServiceUpdateSeed.eServiceId(),
                eServiceUpdateSeed.updateEServiceSeed()
        );
    }

    @Override
    public EService updateModelAfterModify(CreatedResource createdResource) {
        //TODO: devo leggere eservice + descrittore, conviene aspettare qualche secondo per essere sicuro di non andare in errori di eventual consistency
        return null;
    }

    @Override
    public ResponseEntity<Void> doDelete(UUID uuid) {
        return eservicesApi.deleteEServiceWithHttpInfo(uuid);
    }

    @Override
    public ResponseEntity<CreatedEServiceDescriptor> doCreate(EServiceSeed seed) {
        return eservicesApi.createEServiceWithHttpInfo(seed);
    }

    @Override
    public EService updateModelAfterCreate(CreatedEServiceDescriptor createdEServiceDescriptor) {
        //TODO: devo leggere eservice + descrittore
        return null;
    }
}
