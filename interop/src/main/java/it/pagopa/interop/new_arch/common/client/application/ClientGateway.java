package it.pagopa.interop.new_arch.common.client.application;

import it.pagopa.interop.new_arch.common.client.domain.Client;
import it.pagopa.interop.new_arch.common.client.domain.ClientKind;
import it.pagopa.interop.new_arch.common.client.domain.ClientRef;
import it.pagopa.interop.new_arch.common.infrastructure.security.Key;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import it.pagopa.interop.new_arch.common.purpose.domain.Purpose;

import java.util.List;

public interface ClientGateway {
    Client getClient(ClientRef ref);

    // CreationRequest per rendere configurabile la creazione con le chiavi o gli utenti o la purpose
    Client createClient(ClientKind kind, List<User> members);

    Client addKey(Client client, Key key);

    Client addPurpose(Client client, Purpose purpose);
}
