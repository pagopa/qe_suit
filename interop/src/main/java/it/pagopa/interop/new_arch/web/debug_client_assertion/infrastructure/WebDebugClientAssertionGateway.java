package it.pagopa.interop.new_arch.web.debug_client_assertion.infrastructure;

import it.pagopa.interop.new_arch.common.client.domain.ClientKind;
import it.pagopa.interop.new_arch.common.debug_client_assertion.application.DebugClientAssertionGateway;
import it.pagopa.interop.new_arch.common.debug_client_assertion.domain.DebugClientAssertionValidation;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebDebugClientAssertionGateway implements DebugClientAssertionGateway {

    private final BrowserDebugClientAssertionClient client;

    @Override
    public DebugClientAssertionValidation executeClientAssertionValidation(String clientAssertion, String dpopProof, ClientKind clientKind, String clientId) {
        return client.submitClientAssertionValidationRequest(clientAssertion, clientKind, clientId, dpopProof)
                .withoutPolling()
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}
