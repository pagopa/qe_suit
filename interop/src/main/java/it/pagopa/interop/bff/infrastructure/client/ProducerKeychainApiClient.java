package it.pagopa.interop.bff.infrastructure.client;

import it.pagopa.interop.common.contract.model.producer_keychain.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.rest.RestService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProducerKeychainApiClient extends RestService {

    private final ProducerKeychainApi api;
    private final ProducerKeychainMapper mapper;

    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource, ProducerKeychain> createProducerKeychain(it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed producerKeychainSeed) {
        return super.create(
            () -> api.createProducerKeychainWithHttpInfo(producerKeychainSeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.CompactProducerKeychains, ProducerKeychain> getProducerKeychains(Integer offset, Integer limit, String q, List<UUID> userIds, UUID eserviceId) {
        return super.readAll(
            () -> api.getProducerKeychainsWithHttpInfo(offset, limit, q, userIds, eserviceId),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain, ProducerKeychain> getProducerKeychain(UUID producerKeychainId) {
        return super.read(
            () -> api.getProducerKeychainWithHttpInfo(producerKeychainId),
            mapper::toDomain
        );
    }
    public TestChain<Void, ProducerKeychain> deleteProducerKeychain(UUID producerKeychainId) {
        return super.read(
            () -> api.deleteProducerKeychainWithHttpInfo(producerKeychainId),
            mapper::toDomain
        );
    }
    public TestChain<List<it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser>, ProducerKeychain> getProducerKeychainUsers(UUID producerKeychainId) {
        return super.readAll(
            () -> api.getProducerKeychainUsersWithHttpInfo(producerKeychainId),
            mapper::toDomainList
        );
    }
    public TestChain<Void, ProducerKeychain> addProducerKeychainUsers(UUID producerKeychainId, it.pagopa.interop.generated.openapi.clients.bff.model.AddUsersToClientRequest addUsersToClientRequest) {
        return super.update(
            () -> api.addProducerKeychainUsersWithHttpInfo(producerKeychainId, addUsersToClientRequest),
            mapper::toDomain
        );
    }
    public TestChain<Void, ProducerKeychain> removeProducerKeychainUser(UUID producerKeychainId, UUID userId) {
        return super.read(
            () -> api.removeProducerKeychainUserWithHttpInfo(producerKeychainId, userId),
            mapper::toDomain
        );
    }
    public TestChain<Void, ProducerKeychain> createProducerKey(UUID producerKeychainId, it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed keySeed) {
        return super.update(
            () -> api.createProducerKeyWithHttpInfo(producerKeychainId, keySeed),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys, ProducerKeychain> getProducerKeys(UUID producerKeychainId, Integer offset, Integer limit, List<UUID> userIds) {
        return super.readAll(
            () -> api.getProducerKeysWithHttpInfo(producerKeychainId, offset, limit, userIds),
            mapper::toDomainList
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.PublicKey, ProducerKeychain> getProducerKeyById(UUID producerKeychainId, String keyId) {
        return super.read(
            () -> api.getProducerKeyByIdWithHttpInfo(producerKeychainId, keyId),
            mapper::toDomain
        );
    }
    public TestChain<Void, ProducerKeychain> deleteProducerKeyById(UUID producerKeychainId, String keyId) {
        return super.read(
            () -> api.deleteProducerKeyByIdWithHttpInfo(producerKeychainId, keyId),
            mapper::toDomain
        );
    }
    public TestChain<Void, ProducerKeychain> addProducerKeychainEService(UUID producerKeychainId, it.pagopa.interop.generated.openapi.clients.bff.model.EServiceAdditionDetailsSeed eserviceAdditionDetailsSeed) {
        return super.update(
            () -> api.addProducerKeychainEServiceWithHttpInfo(producerKeychainId, eserviceAdditionDetailsSeed),
            mapper::toDomain
        );
    }
    public TestChain<Void, ProducerKeychain> removeProducerKeychainEService(UUID producerKeychainId, UUID eserviceId) {
        return super.read(
            () -> api.removeProducerKeychainEServiceWithHttpInfo(producerKeychainId, eserviceId),
            mapper::toDomain
        );
    }
    public TestChain<it.pagopa.interop.generated.openapi.clients.bff.model.EncodedClientKey, ProducerKeychain> getEncodedProducerKeychainKeyById(UUID producerKeychainId, String keyId) {
        return super.read(
            () -> api.getEncodedProducerKeychainKeyByIdWithHttpInfo(producerKeychainId, keyId),
            mapper::toDomain
        );
    }
}