package it.pagopa.interop.m2m.v3.infrastructure.security;

import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class M2MApiClientProvider {

    private final InteropJourney interopJourney;

    @Cacheable(
            cacheNames = "m2mApiClient",
            key = "@m2MApiClientProvider.cacheKey(#tenant, #role)",
            sync = true
    )
    public Client getOrCreate(Tenant tenant, M2MRole role) {
        return createClient(tenant, role);
    }

    public String cacheKey(Tenant tenant, M2MRole role) {
        return tenant.getOrganizationId()
                + "|"
                + role.name();
    }

    private Client createClient(Tenant tenant, M2MRole role) {
        User tenantAdmin = User.getTenantUser(tenant, UserRole.ADMIN);
        UserRef tenantAdminRef = UserRef.of(tenantAdmin, tenant);

        return interopJourney
                .withProducer(tenant, tenantAdmin)
                .switchChannel(Channel.BFF)
                .createClient(clientConfig -> {
                    clientConfig
                            .name(RandomUtils.randomAlphanumericName("client"))
                            .kind(ClientKind.API)
                            .users(tenantAdminRef)
                            .keys(List.of(
                                    ClientKeyCreationCommand::randomClientKey
                            ));

                    if (role == M2MRole.M2M_ADMIN) clientConfig.admin(tenantAdminRef);
                })
                .switchChannel(Channel.M2M_V3)
                .get(Client.class);
    }
}
