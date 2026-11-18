package it.pagopa.interop.m2m.v3.infrastructure.auth;

import it.pagopa.infrastructure.channel.CurrentChannel;
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
public class M2Mv3ApiClientProvider {

    private final InteropJourney interopJourney;
    private final CurrentChannel<Channel> currentChannel;

    @Cacheable(
            cacheNames = "m2mApiClient",
            key = "@m2Mv3ApiClientProvider.cacheKey(#tenant, #role)",
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

        Channel previousChannel = currentChannel.getCurrentChannel();
        Throwable primaryFailure = null;
        try {
            currentChannel.setCurrentChannel(Channel.BFF);
            return interopJourney
                .withProducer(tenant, tenantAdmin)
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
                .get(Client.class);
        } catch (RuntimeException | Error failure) {
            primaryFailure = failure;
            throw failure;
        } finally {
            try {
                currentChannel.setCurrentChannel(previousChannel);
            } catch (RuntimeException | Error failure) {
                if (primaryFailure == null) throw failure;
                primaryFailure.addSuppressed(failure);
            }
        }
    }
}
