package it.pagopa.interop.common.debug_client_assertion.application;

import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.debug_client_assertion.domain.DebugClientAssertionValidation;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface DebugClientAssertionGateway extends Plugin<Channel> {

    DebugClientAssertionValidation executeClientAssertionValidation(String clientAssertion, String dpopProof, ClientKind clientKind, String clientId);
}
