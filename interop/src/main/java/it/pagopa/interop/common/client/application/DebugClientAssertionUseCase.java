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
        return debugClientAssertionGateway.executeClientAssertionValidation(formData.getClientAssertion().getClientAssertion(), formData.getDpopProof().getJwt(), formData.getClient().getKind(), formData.getClient().getId().toString());
    }
}
