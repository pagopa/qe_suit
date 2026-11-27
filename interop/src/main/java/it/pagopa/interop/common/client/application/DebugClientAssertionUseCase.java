package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.client.application.command.DebugClientAssertionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebugClientAssertionUseCase {

    private final DebugClientAssertionGateway debugClientAssertionGateway;

    public void executeClientAssertionValidation(DebugClientAssertionCommand formData) {
        debugClientAssertionGateway.executeClientAssertionValidation(
                formData.getClientAssertion() != null ? formData.getClientAssertion().getClientAssertion() : null,
                formData.getDpopProof() != null ? formData.getDpopProof().getJwt() : null,
                formData.getClient() != null ? formData.getClient().getKind() : null,
                formData.getClient() != null ? formData.getClient().getId().toString() : null
        );
    }
}
