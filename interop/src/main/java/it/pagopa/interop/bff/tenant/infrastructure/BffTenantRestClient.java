package it.pagopa.interop.bff.tenant.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffTenantRestClient extends RestClient {

    private final TenantsApi tenantsApi;

    public BffTenantRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.tenantsApi = apiClient.tenants();
    }

    public TestChain<Void> assignCertifiedAttribute(@Nonnull UUID attributeId, @Nonnull UUID organizationId) {
        CertifiedTenantAttributeSeed seed = new CertifiedTenantAttributeSeed();
        seed.setId(attributeId);
        return execute(
                () -> tenantsApi.addCertifiedAttribute()
                        .tenantIdPath(organizationId)
                        .body(seed)
                        .execute(Function.identity()),
                Void.class
        );
    }
}
