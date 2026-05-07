package it.pagopa.interop.domain.services.client;

import it.pagopa.interop.domain.dto.Client;
import it.pagopa.interop.domain.dto.requests.ClientCreationRequest;

public interface ClientService {
    Client createClient(ClientCreationRequest request);
}
