package it.pagopa.interop.service.client_assertion;

import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.model.DPoPProof;

public interface DevToolsService {
    ClientAssertionValidationResult performValidation(ClientAssertion clientAssertion, Client client);
    ClientAssertionValidationResult performValidation(ClientAssertion clientAssertion, Client client, DPoPProof proof);
    ClientAssertionValidationResult performValidation(String clientAssertion, InteropClientType clientType, String clientId, String dPoPProof);
    void submitValidationRequest(String clientAssertion, String clientId, String dPoPProof);
}
