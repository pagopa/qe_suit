package it.pagopa.interop.bff.tenant.infrastructure;

import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.tenant.application.TenantGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BffTenantGateway implements TenantGateway {
    private final BffTenantRestClient restClient;

    @Override
    public void assignCertifiedAttribute(UUID attributeId, Tenant tenant) {
        restClient.assignCertifiedAttribute(attributeId, tenant.getOrganizationId());
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

}
