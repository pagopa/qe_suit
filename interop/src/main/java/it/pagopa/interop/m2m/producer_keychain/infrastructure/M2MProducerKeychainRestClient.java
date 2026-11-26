package it.pagopa.interop.m2m.producer_keychain.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.ProducerKeychainsApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ProducerKey;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ProducerKeychainAddEService;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ProducerKeychainSeed;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ProducerKeychainUpdateSeed;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class M2MProducerKeychainRestClient extends RestClient {
    private final ProducerKeychainsApi api;

    public M2MProducerKeychainRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.api = apiClient.producerKeychains();
    }

    public TestChain<ProducerKeychain> createProducerKeychain(ProducerKeychainSeed seed) {
        return execute(() -> api.createProducerKeychain().body(seed).execute(Function.identity()), ProducerKeychain.class);
    }

    public TestChain<ProducerKey> createProducerKey(UUID keychainId, KeySeed seed) {
        return execute(() -> api.createProducerKeychainKey().keychainIdPath(keychainId)
                .body(seed).execute(Function.identity()), ProducerKey.class);
    }

    public TestChain<ProducerKeychain> getProducerKeychain(UUID keychainId) {
        return execute(() -> api.getProducerKeychain().keychainIdPath(keychainId)
                .execute(Function.identity()), ProducerKeychain.class);
    }

    public TestChain<ProducerKeychain> updateProducerKeychain(
            UUID keychainId, ProducerKeychainUpdateSeed updateSeed) {
        return execute(() -> {
            Map<String, String> payload = new LinkedHashMap<>();
            if (updateSeed.getName() != null) {
                payload.put(ProducerKeychainUpdateSeed.JSON_PROPERTY_NAME, updateSeed.getName());
            }
            if (updateSeed.getDescription() != null) {
                payload.put(ProducerKeychainUpdateSeed.JSON_PROPERTY_DESCRIPTION, updateSeed.getDescription());
            }
            return api.updateProducerKeychain().keychainIdPath(keychainId)
                    .reqSpec(reqSpec -> reqSpec
                            .setContentType("application/merge-patch+json; charset=UTF-8")
                            .setBody(payload))
                    .execute(Function.identity());
        }, ProducerKeychain.class);
    }

    public TestChain<Void> addProducerKeychainEService(UUID keychainId, ProducerKeychainAddEService seed) {
        return execute(() -> api.addProducerKeychainEService().keychainIdPath(keychainId)
                .body(seed).execute(Function.identity()), Void.class);
    }

    public TestChain<Void> deleteProducerKeychain(UUID keychainId) {
        return execute(() -> api.deleteProducerKeychain().keychainIdPath(keychainId)
                .execute(Function.identity()), Void.class);
    }

    public TestChain<Void> deleteProducerKey(UUID keychainId, UUID keyId) {
        return execute(() -> api.deleteProducerKeychainKeyById().keychainIdPath(keychainId)
                .keyIdPath(keyId).execute(Function.identity()), Void.class);
    }
}
