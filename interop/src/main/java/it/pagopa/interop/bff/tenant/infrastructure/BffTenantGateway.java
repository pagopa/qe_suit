package it.pagopa.interop.bff.tenant.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.tenant.application.TenantGateway;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BffTenantGateway implements TenantGateway {
    private final BffTenantRestClient restClient;

    @Override
    public void assignCertifiedAttribute(UUID attributeId, Tenant tenant) {
        CertifiedTenantAttributeSeed seed = new CertifiedTenantAttributeSeed();
        seed.setId(attributeId);
        restClient.assignCertifiedAttribute(seed, tenant.getOrganizationId())
                .withPolling(PollingStrategy.UNTIL_SUCCESS);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

}
