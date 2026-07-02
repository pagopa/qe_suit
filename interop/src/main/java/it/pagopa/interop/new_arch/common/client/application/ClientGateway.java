package it.pagopa.interop.new_arch.common.client.application;

import it.pagopa.interop.new_arch.common.client.domain.Client;
import it.pagopa.interop.new_arch.common.client.domain.ClientKind;
import it.pagopa.interop.new_arch.common.infrastructure.security.Key;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRole;
import it.pagopa.interop.new_arch.common.purpose.domain.Purpose;

import java.util.List;

public interface ClientGateway {
    Client read(ClientRef ref);

    Client create(ClientKind kind, List<User> members);

    Client createClientIncludingUsers(ClientKind kind, Tenant tenant, UserRole... roles);

    Client addKey(Client client, Key key);

    Client addPurpose(Client client, Purpose purpose);
}
