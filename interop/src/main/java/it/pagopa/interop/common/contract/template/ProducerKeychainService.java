package it.pagopa.interop.common.contract.template;

import it.pagopa.interop.common.contract.model.ProducerKeychain;
import it.pagopa.interop.common.contract.enums.Channel;
import it.pagopa.interop.common.contract.model.request.ProducerKeychainReadAllRequest;
import it.pagopa.interop.common.contract.template.action.TestChain;
import org.springframework.plugin.core.Plugin;

public interface ProducerKeychainService extends Plugin<Channel> {

    TestChain<?, ProducerKeychain> create();

    TestChain<?, ProducerKeychain> readAll(ProducerKeychainReadAllRequest request);

    void deleteAll();
}
