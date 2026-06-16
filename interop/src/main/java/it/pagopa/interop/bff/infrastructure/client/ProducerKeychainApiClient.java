package it.pagopa.interop.bff.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.contract.template.rest.*;
import it.pagopa.interop.common.contract.model.ProducerKeychain;
import it.pagopa.interop.common.contract.model.request.ProducerKeychainReadAllRequest;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
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
public class ProducerKeychainApiClient extends RestService implements
        CanCreate<ProducerKeychainSeed, CreatedResource, ProducerKeychain>,
        CanRead<UUID, it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain, ProducerKeychain>,
        CanReadAll<ProducerKeychainReadAllRequest, CompactProducerKeychains, ProducerKeychain>,
        CanDelete<UUID, Void, ProducerKeychain> {

    private final ProducerKeychainApi keychainApi;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain> doRead(UUID id) {
        return keychainApi.getProducerKeychainWithHttpInfo(id);
    }

    @Override
    public ProducerKeychain updateModelAfterRead(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain producerKeychain) {
        return objectMapper.convertValue(producerKeychain, ProducerKeychain.class);
    }

    @Override
    public ResponseEntity<CreatedResource> doCreate(ProducerKeychainSeed producerKeychainSeed) {
        return keychainApi.createProducerKeychainWithHttpInfo(producerKeychainSeed);
    }

    @Override
    public ProducerKeychain updateModelAfterCreate(CreatedResource createdResource) {
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
    public ResponseEntity<CompactProducerKeychains> doReadAll(ProducerKeychainReadAllRequest getAllRequest) {
        return keychainApi.getProducerKeychainsWithHttpInfo(
                getAllRequest.getOffset(),
                getAllRequest.getLimit(),
                getAllRequest.getQ(),
                getAllRequest.getUserIds(),
                getAllRequest.getEserviceId()
        );
    }

    @Override
    public List<ProducerKeychain> updateModelsAfterRead(CompactProducerKeychains compactProducerKeychains) {
        List<ProducerKeychain> producerKeychains = new ArrayList<>();

        for(CompactProducerKeychain keychain : compactProducerKeychains.getResults()){
            var item = objectMapper.convertValue(keychain, ProducerKeychain.class);
            producerKeychains.add(item);
        }

        return producerKeychains;
    }

    @Override
    public ResponseEntity<Void> doDelete(UUID id) {
        return keychainApi.deleteProducerKeychainWithHttpInfo(id);
    }
}
