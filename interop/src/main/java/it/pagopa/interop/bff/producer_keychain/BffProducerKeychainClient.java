package it.pagopa.interop.bff.producer_keychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.domain.model.ProducerKeychain;
import it.pagopa.interop.common.service.producer_keychain.request.BaseReadAllProducerKeychainRequest;
import it.pagopa.interop.common.service.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.service.template.rest.*;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactProducerKeychains;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BffProducerKeychainClient extends RestService implements
        CanCreate<ProducerKeychainSeed, CreatedResource, ProducerKeychain>,
        CanRead<UUID, it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain, ProducerKeychain>,
        CanReadAll<BaseReadAllProducerKeychainRequest, CompactProducerKeychains, ProducerKeychain>,
        CanDelete<UUID, Void, ProducerKeychain> {

    private final ProducerKeychainApi keychainApi;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain> doRead(UUID id) {
        return keychainApi.getProducerKeychainWithHttpInfo(id);
    }

    @Override
    public it.pagopa.interop.common.domain.model.ProducerKeychain updateModelAfterRead(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain producerKeychain) {
        return objectMapper.convertValue(producerKeychain, it.pagopa.interop.common.domain.model.ProducerKeychain.class);
    }

    @Override
    public ResponseEntity<CreatedResource> doCreate(ProducerKeychainSeed producerKeychainSeed) {
        return keychainApi.createProducerKeychainWithHttpInfo(producerKeychainSeed);
    }

    @Override
    public it.pagopa.interop.common.domain.model.ProducerKeychain updateModelAfterCreate(CreatedResource createdResource) {
        return this.read(createdResource.getId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .getModel();
    }

    @Override
    public ProducerKeychainSeed doDefaultCreationRequest() {
        // Genera un nome univoco (es: "test-keychain-a1b2c3d4")
        String uniqueName = "test-keychain-" + UUID.randomUUID().toString().substring(0, 8);

        return new ProducerKeychainSeed()
                .name(uniqueName)
                .description("Portachiavi di test generato automaticamente")
                .members(List.of());
    }

    @Override
    public ResponseEntity<CompactProducerKeychains> doReadAll(BaseReadAllProducerKeychainRequest getAllRequest) {
        return keychainApi.getProducerKeychainsWithHttpInfo(
                getAllRequest.getOffset(),
                getAllRequest.getLimit(),
                getAllRequest.getQ(),
                getAllRequest.getUserIds(),
                getAllRequest.getEserviceId()
        );
    }

    @Override
    public List<it.pagopa.interop.common.domain.model.ProducerKeychain> updateModelsAfterRead(CompactProducerKeychains compactProducerKeychains) {
        List<it.pagopa.interop.common.domain.model.ProducerKeychain> producerKeychains = new ArrayList<>();

        for(CompactProducerKeychain keychain : compactProducerKeychains.getResults()){
            var item = objectMapper.convertValue(keychain, it.pagopa.interop.common.domain.model.ProducerKeychain.class);
            producerKeychains.add(item);
        }

        return producerKeychains;
    }

    @Override
    public ResponseEntity<Void> doDelete(UUID id) {
        return keychainApi.deleteProducerKeychainWithHttpInfo(id);
    }
}
