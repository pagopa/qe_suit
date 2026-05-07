package it.pagopa.interop.domain.services.client;

import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.requests.ClientCreationRequest;

public interface ClientService {
    Client createClient(ClientCreationRequest request);
}
