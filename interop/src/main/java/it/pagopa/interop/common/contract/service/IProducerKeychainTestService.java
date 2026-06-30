package it.pagopa.interop.common.contract.service;

import it.pagopa.interop.common.contract.model.producer_keychain.ProducerKeychain;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.request.ProducerKeychainReadAllRequest;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface IProducerKeychainTestService extends Plugin<Channel> {

    TestChain<?, ProducerKeychain> create();

    TestChain<?, ProducerKeychain> read(UUID producerKeychainId);

    TestChain<?, ProducerKeychain> readAll(ProducerKeychainReadAllRequest request);

    TestChain<?, ProducerKeychain> delete(UUID producerKeychainId);

    void deleteAll();
}
