package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.client.domain.DebugClientAssertionValidation;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface DebugClientAssertionGateway extends Plugin<Channel> {

    DebugClientAssertionValidation executeClientAssertionValidation(String clientAssertion, String dpopProof, ClientKind clientKind, String clientId);
}
