package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.client.application.command.DebugClientAssertionCommand;
import it.pagopa.interop.common.client.domain.DebugClientAssertionValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebugClientAssertionUseCase {

    private final DebugClientAssertionGateway debugClientAssertionGateway;

    public DebugClientAssertionValidation executeClientAssertionValidation(DebugClientAssertionCommand formData) {
        return debugClientAssertionGateway.executeClientAssertionValidation(
                formData.getClientAssertion() != null ? formData.getClientAssertion().getClientAssertion() : null,
                formData.getDpopProof() != null ? formData.getDpopProof().getJwt() : null,
                formData.getClient() != null ? formData.getClient().getKind() : null,
                formData.getClient() != null ? formData.getClient().getId().toString() : null
        );
    }
}
