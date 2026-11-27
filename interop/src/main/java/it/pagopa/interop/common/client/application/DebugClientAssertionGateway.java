package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface DebugClientAssertionGateway extends Plugin<Channel> {

    void executeClientAssertionValidation(String clientAssertion, String dpopProof, ClientKind clientKind, String clientId);
}
