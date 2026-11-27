package it.pagopa.interop.common.producer_keychain.application;

import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.KeyRegistrationMaterial;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.common.producer_keychain.domain.KeyRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainPublicKey;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainRef;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeysPage;
import org.springframework.plugin.core.Plugin;

import java.util.List;

public interface ProducerKeychainGateway extends Plugin<Channel> {
    ProducerKeychain createProducerKeychain(ProducerKeychainCreationCommand command);
    void createProducerKey(ProducerKeychainRef ref, ProducerKeyCreationCommand command);
    void deleteProducerKeychain(ProducerKeychainRef ref);
    void deleteProducerKey(ProducerKeychainRef ref, KeyRef key);
    ProducerKeychain getProducerKeychain(ProducerKeychainRef ref);
    ProducerKeychainPublicKey getProducerKey(ProducerKeychainRef ref, KeyRef key);
    ProducerKeysPage getProducerKeys(ProducerKeychainRef ref, int offset, int limit);
    void updateProducerKeys(ProducerKeychainRef ref, List<ProducerKeychainPublicKey> keys);
    void addEService(ProducerKeychainRef ref, EServiceRef eservice);
    ProducerKeychain updateMetadata(ProducerKeychainRef ref, String name, String description);
    void tryToUpdateMetadata(ProducerKeychainRef ref, String name, String description);
    void shouldFailToUpdateMetadataAsMalformed();
    void shouldFailToUpdateMetadataAsForbidden();
    void shouldFailToUpdateMetadataAsConflict();
    void attemptEServiceAssociation(ProducerKeychainRef ref, EServiceRef eServiceRef);
    void attemptKeyRegistration(ProducerKeychain keychain, KeyRegistrationMaterial material, String name);
    void attemptProducerKeychainCreation(ProducerKeychainCreationCommand command);
}

