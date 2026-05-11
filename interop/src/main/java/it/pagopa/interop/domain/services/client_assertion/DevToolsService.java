package it.pagopa.interop.domain.services.client_assertion;

import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.model.DPoPProof;

public interface DevToolsService {
    ClientAssertionValidationResult validate(ClientAssertion clientAssertion, Client client);
    ClientAssertionValidationResult validate(ClientAssertion clientAssertion, Client client, DPoPProof proof);
    ClientAssertionValidationResult validate(String clientAssertion, InteropClientType clientType, String client, String proof);
}
