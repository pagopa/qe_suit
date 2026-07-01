package it.pagopa.interop.common.contract.service;

import it.pagopa.interop.common.contract.model.client.Client;
import it.pagopa.interop.common.contract.model.client.ClientKind;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;
import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.utils.KeyPairUtils;
import org.springframework.plugin.core.Plugin;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface IClientTestService extends Plugin<Channel> {

    TestChain<?, Client> read(UUID clientId);

    TestChain<?, Client> create(ClientKind kind, List<UUID> members);

    default TestChain<?, Client> createClientIncludingUsers(ClientKind kind, Tenant tenant, UserRole... roles){
        List<UUID> members = new ArrayList<>();

        for(UserRole role : roles)
            members.add(User.getTenantUser(tenant, role).getUserId());

        return create(kind, members);
    }

    TestChain<?, Client> addKey(UUID clientId, KeyPair key);

    default TestChain<?, Client> addKey(UUID clientId) {
        KeyPair key = KeyPairUtils.generate(KeyPairUtils.KeyAlgorithm.RSA);
        return addKey(clientId, key);
    }

    TestChain<?, Client> addPurpose(UUID clientId, UUID purposeId);

    default TestChain<?, Client> create(ClientKind kind) {
        return create(kind, null);
    }
}
