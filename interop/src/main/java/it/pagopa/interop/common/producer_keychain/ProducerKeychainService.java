package it.pagopa.interop.common.producer_keychain;

import it.pagopa.interop.common.enums.Channel;
import it.pagopa.interop.common.producer_keychain.request.BaseReadAllProducerKeychainRequest;
import it.pagopa.interop.common.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

public interface ProducerKeychainService extends Plugin<Channel> {

    TestChain<?, ProducerKeychain> create();

    TestChain<?, ProducerKeychain> readAll(BaseReadAllProducerKeychainRequest request);

    void deleteAll();
}
