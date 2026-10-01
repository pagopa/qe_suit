package it.pagopa.interop.bff.producer_keychain.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKey;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffProducerKeychainRestClient extends RestClient {
    private final ProducerKeychainApi api;

    public BffProducerKeychainRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.api = apiClient.producerKeychain();
    }

    public TestChain<CreatedResource> createProducerKeychain(ProducerKeychainSeed seed) {
        return execute(() -> api.createProducerKeychain().body(seed).execute(Function.identity()), CreatedResource.class);
    }

    public TestChain<Void> createProducerKey(UUID keychainId, KeySeed seed) {
        return execute(() -> api.createProducerKey().producerKeychainIdPath(keychainId)
                .body(seed).execute(Function.identity()), Void.class);
    }

    public TestChain<Void> deleteProducerKeychain(UUID keychainId) {
        return execute(() -> api.deleteProducerKeychain().producerKeychainIdPath(keychainId)
                .execute(Function.identity()), Void.class);
    }

    public TestChain<Void> deleteProducerKey(UUID keychainId, String keyId) {
        return execute(() -> api.deleteProducerKeyById().producerKeychainIdPath(keychainId)
                .keyIdPath(keyId).execute(Function.identity()), Void.class);
    }

    public TestChain<ProducerKeychain> getProducerKeychain(UUID keychainId) {
        return execute(() -> api.getProducerKeychain().producerKeychainIdPath(keychainId)
                .execute(Function.identity()), ProducerKeychain.class);
    }

    public TestChain<PublicKey> getProducerKey(UUID keychainId, UUID keyId) {
        return execute(() -> api.getProducerKeyById().producerKeychainIdPath(keychainId)
                .keyIdPath(keyId.toString()).execute(Function.identity()), PublicKey.class);
    }

    public TestChain<PublicKeys> getProducerKeys(UUID keychainId, int offset, int limit) {
        return execute(() -> api.getProducerKeys().producerKeychainIdPath(keychainId)
                .offsetQuery(offset).limitQuery(limit).execute(Function.identity()), PublicKeys.class);
    }
}
