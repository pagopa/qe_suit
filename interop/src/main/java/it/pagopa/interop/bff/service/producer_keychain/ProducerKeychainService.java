package it.pagopa.interop.bff.service.producer_keychain;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.bff.service.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.service.template.TestService;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainService extends TestService implements IProducerKeychainService {

    private final ProducerKeychainApi keychainApi;
    private final ObjectMapper objectMapper;
    private final ScenarioContext scenarioContext;

    @Override
    public ResponseEntity<ProducerKeychain> doRead(UUID id) {
        return keychainApi.getProducerKeychainWithHttpInfo(id);
    }

    @Override
    public it.pagopa.interop.common.domain.model.ProducerKeychain updateModelAfterRead(ProducerKeychain producerKeychain) {
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
    public ResponseEntity<CompactProducerKeychains> doReadAll(GetAllRequest getAllRequest) {
        return keychainApi.getProducerKeychainsWithHttpInfo(
                getAllRequest.offset(),
                getAllRequest.limit(),
                getAllRequest.q(),
                getAllRequest.userIds(),
                getAllRequest.eserviceId()
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
