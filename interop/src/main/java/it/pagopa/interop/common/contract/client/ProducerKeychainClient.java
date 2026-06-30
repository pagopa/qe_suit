package it.pagopa.interop.common.contract.client;

import it.pagopa.interop.common.contract.model.producer_keychain.ProducerKeychain;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.request.ProducerKeychainReadAllRequest;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

public interface ProducerKeychainClient extends Plugin<Channel> {

    TestChain<?, ProducerKeychain> create();

    TestChain<?, ProducerKeychain> readAll(ProducerKeychainReadAllRequest request);

    void deleteAll();
}
