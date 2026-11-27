package it.pagopa.interop.bff.debug_client_assertion.infrastructure;

import it.pagopa.interop.common.client.application.DebugClientAssertionGateway;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffDebugClientAssertionGateway implements DebugClientAssertionGateway {

    private final BffDebugClientAssertionRestClient restClient;

    @Override
    public void executeClientAssertionValidation(
            String clientAssertion, String dpopProof, ClientKind clientKind, String clientId
    ) {
        restClient.submitClientAssertionValidationRequest(clientAssertion, dpopProof, clientId)
                .withoutPolling();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}
