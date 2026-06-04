package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.service.template.CanCreate;
import it.pagopa.interop.bff.service.template.CanGet;
import it.pagopa.interop.bff.service.template.TestService;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainService extends TestService implements
        CanCreate<CreatedResource, ProducerKeychainSeed>, CanGet<ProducerKeychain, UUID> {

    private final ProducerKeychainApi keychainApi;

    @Override
    public ResponseEntity<ProducerKeychain> doGet(UUID id) {
        return keychainApi.getProducerKeychainWithHttpInfo(id);
    }

    @Override
    public ResponseEntity<CreatedResource> doCreate(ProducerKeychainSeed producerKeychainSeed) {
        return keychainApi.createProducerKeychainWithHttpInfo(producerKeychainSeed);
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
}
