package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.ProducerKeychainJourney;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainUseCase;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.common.producer_keychain.domain.KeyRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProducerKeychainJourneyImpl implements ProducerKeychainJourney<ProducerKeychainJourneyImpl> {
    private final ProducerKeychainUseCase useCase;
    private final EntityStore entityStore;

    @Override
    public ProducerKeychainJourneyImpl createProducerKeychain(ProducerKeychainCreationCommand command) {
        useCase.createProducerKeychain(command);
        return this;
    }

    @Override
    public ProducerKeychainJourneyImpl createProducerKey(ProducerKeyCreationCommand command) {
        ProducerKeychain keychain = entityStore.getLastOrThrow(ProducerKeychain.class);
        useCase.createProducerKey(keychain.getRef(), command);
        return this;
    }

    @Override
    public ProducerKeychainJourneyImpl deleteProducerKeychain() {
        ProducerKeychain keychain = entityStore.getLastOrThrow(ProducerKeychain.class);
        useCase.deleteProducerKeychain(keychain.getRef());
        return this;
    }

    @Override
    public ProducerKeychainJourneyImpl deleteProducerKey(KeyRef key) {
        ProducerKeychain keychain = entityStore.getLastOrThrow(ProducerKeychain.class);
        useCase.deleteProducerKey(keychain.getRef(), key);
        getAllProducerKeys();
        return this;
    }

    @Override
    public ProducerKeychainJourneyImpl getProducerKeychain() {
        ProducerKeychain keychain = entityStore.getLastOrThrow(ProducerKeychain.class);
        useCase.getProducerKeychain(keychain.getRef());
        return this;
    }

    @Override
    public ProducerKeychainJourneyImpl getProducerKey(KeyRef key) {
        ProducerKeychain keychain = entityStore.getLastOrThrow(ProducerKeychain.class);
        useCase.getProducerKey(keychain.getRef(), key);
        return this;
    }

    @Override
    public ProducerKeychainJourneyImpl getProducerKeys(int offset, int limit) {
        ProducerKeychain keychain = entityStore.getLastOrThrow(ProducerKeychain.class);
        useCase.getProducerKeys(keychain.getRef(), offset, limit);
        return this;
    }

    @Override
    public ProducerKeychainJourneyImpl getAllProducerKeys() {
        ProducerKeychain keychain = entityStore.getLastOrThrow(ProducerKeychain.class);
        useCase.getAllProducerKeys(keychain.getRef());
        return this;
    }
}


