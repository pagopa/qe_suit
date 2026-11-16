package it.pagopa.interop.m2m.producer_keychain.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainGateway;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.common.producer_keychain.domain.KeyRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainPublicKey;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeysPage;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ProducerKeychainAddEService;
import it.pagopa.interop.m2m.producer_keychain.application.M2MProducerKeyCreationCommand;
import it.pagopa.interop.m2m.producer_keychain.application.M2MProducerKeychainCreationCommand;
import it.pagopa.kernel.security.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class M2MProducerKeychainGateway implements ProducerKeychainGateway {
    private final M2MProducerKeychainRestClient restClient;
    private final EntityStore entityStore;

    @Override
    public ProducerKeychain createProducerKeychain(ProducerKeychainCreationCommand command) {
        if (!(command instanceof M2MProducerKeychainCreationCommand m2mCommand)) {
            throw new IllegalArgumentException("Expected M2M producer keychain creation command");
        }
        return restClient.createProducerKeychain(m2mCommand.getSeed())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(created -> getProducerKeychain(ProducerKeychainRef.of(created.getId()))).get();
    }

    @Override
    public void createProducerKey(ProducerKeychainRef ref, ProducerKeyCreationCommand command) {
        if (!(command instanceof M2MProducerKeyCreationCommand m2mCommand)) {
            throw new IllegalArgumentException("Expected M2M producer key creation command");
        }
        restClient.createProducerKey(ref.id(), m2mCommand.getKeySeed())
                .withPolling(PollingStrategy.UNTIL_SUCCESS).get();
        ProducerKeychain keychain = getProducerKeychain(ref);
        var keys = keychain.getKeys() == null ? new ArrayList<Key>() : new ArrayList<>(keychain.getKeys());
        keys.add(m2mCommand.getKey());
        entityStore.upsert(keychain.toBuilder().keys(List.copyOf(keys)).build());
    }

    @Override
    public ProducerKeychain getProducerKeychain(ProducerKeychainRef ref) {
        return restClient.getProducerKeychain(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(response -> entityStore.getById(ref.id(), ProducerKeychain.class)
                        .map(ProducerKeychain::toBuilder).orElseGet(ProducerKeychain::builder)
                        .id(response.getId()).name(response.getName()).description(response.getDescription()).build())
                .updateContext().get();
    }

    @Override
    public void deleteProducerKeychain(ProducerKeychainRef ref) {
        restClient.deleteProducerKeychain(ref.id()).withPolling(PollingStrategy.UNTIL_SUCCESS).get();
    }

    @Override
    public void deleteProducerKey(ProducerKeychainRef ref, KeyRef key) {
        restClient.deleteProducerKey(ref.id(), key.id()).withPolling(PollingStrategy.UNTIL_SUCCESS).get();
    }

    @Override
    public ProducerKeychainPublicKey getProducerKey(ProducerKeychainRef ref, KeyRef key) {
        throw new UnsupportedOperationException("Reading a single producer key is not supported on M2M v3");
    }

    @Override
    public ProducerKeysPage getProducerKeys(ProducerKeychainRef ref, int offset, int limit) {
        throw new UnsupportedOperationException("Reading producer key metadata is not supported on M2M v3");
    }

    @Override
    public void updateProducerKeys(ProducerKeychainRef ref, List<ProducerKeychainPublicKey> keys) {
        ProducerKeychain keychain = entityStore.getById(ref.id(), ProducerKeychain.class)
                .orElseGet(() -> getProducerKeychain(ref));
        entityStore.upsert(keychain.toBuilder().publicKeys(List.copyOf(keys)).build());
    }

    @Override
    public void addEService(ProducerKeychainRef ref, EServiceRef eservice) {
        restClient.addProducerKeychainEService(ref.id(), new ProducerKeychainAddEService().eserviceId(eservice.id()))
                .withPolling(PollingStrategy.UNTIL_SUCCESS).get();
        ProducerKeychain keychain = getProducerKeychain(ref);
        Set<EServiceRef> eservices = keychain.getEservices() == null
                ? new LinkedHashSet<>() : new LinkedHashSet<>(keychain.getEservices());
        eservices.add(eservice);
        entityStore.upsert(keychain.toBuilder().eservices(Set.copyOf(eservices)).build());
    }

    @Override
    public ProducerKeychain updateMetadata(ProducerKeychainRef ref, String name, String description) {
        throw new UnsupportedOperationException("Updating producer keychain metadata is not supported on M2M v3");
    }

    @Override
    public void tryToUpdateMetadata(ProducerKeychainRef ref, String name, String description) {
        throw new UnsupportedOperationException("Updating producer keychain metadata is not supported on M2M v3");
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.M2M_V3;
    }
}
