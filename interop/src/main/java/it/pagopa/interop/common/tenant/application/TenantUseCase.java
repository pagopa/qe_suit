package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TenantUseCase {
    private final TenantGateway tenantGateway;

    public void assignCertifiedAttribute(UUID attributeId, Tenant tenant) {
        tenantGateway.assignCertifiedAttribute(attributeId, tenant);
    }
}
