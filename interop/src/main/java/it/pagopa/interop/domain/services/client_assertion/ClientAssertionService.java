package it.pagopa.interop.domain.services.client_assertion;

import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;

public interface ClientAssertionService {
    ClientAssertionValidationResult validateClientAssertion(ClientAssertion clientAssertion, Client client);
}
