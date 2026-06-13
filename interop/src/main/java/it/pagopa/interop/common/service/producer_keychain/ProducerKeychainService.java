package it.pagopa.interop.common.service.producer_keychain;

import it.pagopa.interop.common.domain.enums.Channel;
import it.pagopa.interop.common.domain.model.ProducerKeychain;
import it.pagopa.interop.common.service.producer_keychain.request.BaseReadAllProducerKeychainRequest;
import it.pagopa.interop.common.service.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

public interface ProducerKeychainService extends Plugin<Channel> {

    TestChain<?, ProducerKeychain> create();

    TestChain<?, ProducerKeychain> readAll(BaseReadAllProducerKeychainRequest request);

    void deleteAll();
}
