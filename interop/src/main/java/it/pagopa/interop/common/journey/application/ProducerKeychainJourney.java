package it.pagopa.interop.common.journey.application;

import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.common.producer_keychain.domain.KeyRef;

public interface ProducerKeychainJourney<SELF extends ProducerKeychainJourney<SELF>> extends JourneyModule {
    SELF createProducerKeychain(ProducerKeychainCreationCommand command);
    SELF createProducerKey(ProducerKeyCreationCommand command);
    SELF deleteProducerKeychain();
    SELF deleteProducerKey(KeyRef key);
    SELF getProducerKeychain();
    SELF getProducerKey(KeyRef key);
    SELF getProducerKeys(int offset, int limit);
    SELF getAllProducerKeys();
}


