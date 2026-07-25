package it.pagopa.interop.common.debug_client_assertion.application;

import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.kernel.security.DPoPProof;
import it.pagopa.interop.common.kernel.security.ClientAssertion;
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
