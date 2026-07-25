package it.pagopa.interop.new_arch.common.debug_client_assertion.application;

import it.pagopa.interop.new_arch.common.client.domain.Client;
import it.pagopa.interop.new_arch.common.infrastructure.security.jwt.DPoPProof;
import it.pagopa.interop.new_arch.common.kernel.security.ClientAssertion;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class DebugClientAssertionCommand {
    ClientAssertion clientAssertion;
    DPoPProof dpopProof;
    Client client;
}
