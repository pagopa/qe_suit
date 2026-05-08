package it.pagopa.interop.domain.services.client_assertion;

import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertionValidation;

public interface ClientAssertionService {
    ClientAssertionValidation validateClientAssertion(String clientAssertion, Client client);
}
