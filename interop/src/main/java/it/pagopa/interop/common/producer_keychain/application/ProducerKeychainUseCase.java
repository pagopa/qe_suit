package it.pagopa.interop.common.producer_keychain.application;

import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.KeyRegistrationMaterial;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.common.producer_keychain.domain.KeyRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainPublicKey;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeysPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProducerKeychainUseCase {
    private static final int PAGE_SIZE = 100;

    private final ProducerKeychainGateway gateway;

    public ProducerKeychain createProducerKeychain(ProducerKeychainCreationCommand command) {
        return gateway.createProducerKeychain(Objects.requireNonNull(command, "command"));
    }

    public void createProducerKey(ProducerKeychainRef ref, ProducerKeyCreationCommand command) {
        gateway.createProducerKey(ref, Objects.requireNonNull(command, "command"));
    }

    public void deleteProducerKeychain(ProducerKeychainRef ref) {
        gateway.deleteProducerKeychain(ref);
    }

    public void deleteProducerKey(ProducerKeychainRef ref, KeyRef key) {
        gateway.deleteProducerKey(ref, key);
    }

    public void addEService(ProducerKeychainRef ref, EServiceRef eservice) {
        gateway.addEService(ref, Objects.requireNonNull(eservice, "eservice"));
    }

    public ProducerKeychain updateMetadata(ProducerKeychainRef ref, String name, String description) {
        return gateway.updateMetadata(ref, name, description);
    }

    public void tryToUpdateMetadata(ProducerKeychainRef ref, String name, String description) {
        gateway.tryToUpdateMetadata(ref, name, description);
    }

    public void shouldFailToUpdateMetadataAsMalformed() {
        gateway.shouldFailToUpdateMetadataAsMalformed();
    }

    public void shouldFailToUpdateMetadataAsForbidden() {
        gateway.shouldFailToUpdateMetadataAsForbidden();
    }

    public void shouldFailToUpdateMetadataAsConflict() {
        gateway.shouldFailToUpdateMetadataAsConflict();
    }

    public ProducerKeychain getProducerKeychain(ProducerKeychainRef ref) {
        return gateway.getProducerKeychain(ref);
    }

    public ProducerKeychainPublicKey getProducerKey(ProducerKeychainRef ref, KeyRef key) {
        return gateway.getProducerKey(ref, key);
    }

    public ProducerKeysPage getProducerKeys(ProducerKeychainRef ref, int offset, int limit) {
        if (offset < 0 || limit <= 0) {
            throw new IllegalArgumentException("offset must be nonnegative and limit must be positive");
        }
        ProducerKeysPage page = gateway.getProducerKeys(ref, offset, limit);
        gateway.updateProducerKeys(ref, page.keys());
        return page;
    }

    public List<ProducerKeychainPublicKey> getAllProducerKeys(ProducerKeychainRef ref) {
        List<ProducerKeychainPublicKey> all = new ArrayList<>();
        int offset = 0;
        while (true) {
            ProducerKeysPage page = gateway.getProducerKeys(ref, offset, PAGE_SIZE);
            all.addAll(page.keys());
            if (page.keys().isEmpty() || (page.totalCount() != null && all.size() >= page.totalCount())
                    || (page.totalCount() == null && page.keys().size() < PAGE_SIZE)) {
                List<ProducerKeychainPublicKey> keys = List.copyOf(all);
                gateway.updateProducerKeys(ref, keys);
                return keys;
            }
            offset = Math.addExact(offset, page.keys().size());
        }
    }

    public void attemptEServiceAssociation(ProducerKeychainRef ref, EServiceRef eServiceRef) {
        gateway.attemptEServiceAssociation(ref, eServiceRef);
    }

    public void attemptKeyRegistration(ProducerKeychain keychain, KeyRegistrationMaterial material, String name) {
        gateway.attemptKeyRegistration(keychain, material, name);
    }

    public void attemptProducerKeychainCreation(ProducerKeychainCreationCommand command) {
        gateway.attemptProducerKeychainCreation(Objects.requireNonNull(command, "command"));
    }
}
