package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.journey.application.TenantJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantJourneyImpl implements TenantJourney<TenantJourneyImpl> {

    private final TenantUseCase tenantUseCase;
    private final EntityStore entityStore;

    @Override
    public TenantJourneyImpl assignCertifiedAttribute(Tenant tenant) {
        Attribute attribute = entityStore.getLastOrThrow(Attribute.class);
        tenantUseCase.assignCertifiedAttribute(attribute.getId(), tenant);
        return this;
    }
}
