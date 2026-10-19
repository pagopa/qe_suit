package it.pagopa.interop.bff.producer_keychain.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.bff.producer_keychain.application.BffProducerKeyCreationCommand;
import it.pagopa.interop.bff.producer_keychain.application.BffProducerKeychainCreationCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainGateway;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.common.producer_keychain.domain.KeyRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainPublicKey;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeysPage;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BffProducerKeychainGateway implements ProducerKeychainGateway {
    private final BffProducerKeychainRestClient restClient;
    private final BffProducerKeychainMapper mapper;
    private final EntityStore entityStore;

    @Override
    public ProducerKeychain createProducerKeychain(ProducerKeychainCreationCommand command) {
        if (!(command instanceof BffProducerKeychainCreationCommand bffCommand)) {
            throw new IllegalArgumentException("Expected BFF producer keychain creation command");
        }
        return restClient.createProducerKeychain(bffCommand.getSeed())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(created -> getProducerKeychain(ProducerKeychainRef.of(created.getId())))
                .get();
    }

    @Override
    public void createProducerKey(ProducerKeychainRef ref, ProducerKeyCreationCommand command) {
        if (!(command instanceof BffProducerKeyCreationCommand bffCommand)) {
            throw new IllegalArgumentException("Expected BFF producer key creation command");
        }
        restClient.createProducerKey(ref.id(), bffCommand.getKeySeed())
                .withPolling(PollingStrategy.UNTIL_SUCCESS).get();
    }

    @Override
    public void deleteProducerKeychain(ProducerKeychainRef ref) {
        restClient.deleteProducerKeychain(ref.id()).withPolling(PollingStrategy.UNTIL_SUCCESS).get();
    }

    @Override
    public void deleteProducerKey(ProducerKeychainRef ref, KeyRef key) {
        restClient.deleteProducerKey(ref.id(), Objects.requireNonNull(key, "key").id().toString())
                .withPolling(PollingStrategy.UNTIL_SUCCESS).get();
    }

    @Override
    public ProducerKeychain getProducerKeychain(ProducerKeychainRef ref) {
        return restClient.getProducerKeychain(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(response -> mapper.toDomain(response,
                        entityStore.getById(ref.id(), ProducerKeychain.class).orElse(null)))
                .updateContext().get();
    }

    @Override
    public ProducerKeychainPublicKey getProducerKey(ProducerKeychainRef ref, KeyRef key) {
        Objects.requireNonNull(key, "key");
        return restClient.getProducerKey(ref.id(), key.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toPublicKey)
                .assertThat(result -> key.id().equals(result.getId()))
                .updateContext()
                .get();
    }

    @Override
    public ProducerKeysPage getProducerKeys(ProducerKeychainRef ref, int offset, int limit) {
        return restClient.getProducerKeys(ref.id(), offset, limit)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(response -> mapper.toPage(response, offset, limit)).get();
    }

    @Override
    public void updateProducerKeys(ProducerKeychainRef ref, List<ProducerKeychainPublicKey> keys) {
        ProducerKeychain keychain = entityStore.getById(ref.id(), ProducerKeychain.class)
                .orElseGet(() -> getProducerKeychain(ref));
        entityStore.upsert(keychain.toBuilder().publicKeys(List.copyOf(keys)).build());
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.BFF;
    }
}

