package it.pagopa.interop.bff.tenant.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedDiscreteTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RevokeVerifiedAttributeRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;
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

    public TestChain<Void> addCertifiedAttribute(
            @Nonnull UUID tenantId,
            @Nonnull CertifiedTenantAttributeSeed seed
    ) {
        return execute(
                () -> tenantsApi.addCertifiedAttribute()
                        .tenantIdPath(tenantId)
                        .body(seed)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> addCertifiedDiscreteAttribute(
            @Nonnull UUID tenantId,
            @Nonnull CertifiedDiscreteTenantAttributeSeed seed
    ) {
        return execute(
                () -> tenantsApi.addCertifiedDiscreteAttribute()
                        .tenantIdPath(tenantId)
                        .body(seed)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> addDeclaredAttribute(@Nonnull DeclaredTenantAttributeSeed seed) {
        return execute(
                () -> tenantsApi.addDeclaredAttribute()
                        .body(seed)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> verifyVerifiedAttribute(
            @Nonnull UUID tenantId,
            @Nonnull VerifiedTenantAttributeSeed seed
    ) {
        return execute(
                () -> tenantsApi.verifyVerifiedAttribute()
                        .tenantIdPath(tenantId)
                        .body(seed)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> revokeDeclaredAttribute(@Nonnull UUID attributeId) {
        return execute(
                () -> tenantsApi.revokeDeclaredAttribute()
                        .attributeIdPath(attributeId)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> revokeCertifiedAttribute(
            @Nonnull UUID tenantId,
            @Nonnull UUID attributeId
    ) {
        return execute(
                () -> tenantsApi.revokeCertifiedAttribute()
                        .tenantIdPath(tenantId)
                        .attributeIdPath(attributeId)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> revokeCertifiedDiscreteAttribute(
            @Nonnull UUID tenantId,
            @Nonnull UUID attributeId
    ) {
        return execute(
                () -> tenantsApi.revokeCertifiedDiscreteAttribute()
                        .tenantIdPath(tenantId)
                        .attributeIdPath(attributeId)
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Void> revokeVerifiedAttribute(
            @Nonnull UUID tenantId,
            @Nonnull UUID attributeId,
            @Nonnull RevokeVerifiedAttributeRequest request
    ) {
        return execute(
                () -> tenantsApi.revokeVerifiedAttribute()
                        .tenantIdPath(tenantId)
                        .attributeIdPath(attributeId)
                        .body(request)
                        .execute(Function.identity()),
                Void.class
        );
    }
}

