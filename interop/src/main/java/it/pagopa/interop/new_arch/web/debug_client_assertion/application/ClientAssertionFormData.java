package it.pagopa.interop.new_arch.web.debug_client_assertion.application;

import lombok.Value;

@Value
public class ClientAssertionFormData {
    String clientAssertion;
    String dpopProof;
    String clientId;
}
