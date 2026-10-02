package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementRef;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeRef;
import it.pagopa.interop.common.journey.application.TenantJourney;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantJourneyImpl implements TenantJourney<TenantJourneyImpl> {

    private final TenantUseCase tenantUseCase;
    private final EntityStore entityStore;

    @Override
    public TenantJourneyImpl assignCertifiedAttribute(TenantRef tenant) {
        AttributeRef attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        tenantUseCase.assignCertifiedAttribute(tenant, attribute);
        return this;
    }

    @Override
    public TenantJourneyImpl assignCertifiedDiscreteAttribute(TenantRef tenantRef, int value) {
        AttributeRef attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        tenantUseCase.assignCertifiedDiscreteAttribute(tenantRef, attribute, value);
        return this;
    }

    @Override
    public TenantJourneyImpl assignDeclaredAttribute() {
        AttributeRef attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        tenantUseCase.assignDeclaredAttribute(attribute);
        return this;
    }

    @Override
    public TenantJourneyImpl assignVerifiedAttribute(TenantRef tenantRef) {
        AttributeRef attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        AgreementRef agreement = entityStore.getLastOrThrow(Agreement.class).getRef();
        tenantUseCase.assignVerifiedAttribute(tenantRef, attribute, agreement);
        return this;
    }

    @Override
    public TenantJourneyImpl revokeDeclaredAttribute() {
        AttributeRef attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        tenantUseCase.revokeDeclaredAttribute(attribute);
        return this;
    }

    @Override
    public TenantJourneyImpl revokeCertifiedAttribute(TenantRef tenantRef) {
        AttributeRef attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        tenantUseCase.revokeCertifiedAttribute(tenantRef, attribute);
        return this;
    }

    @Override
    public TenantJourneyImpl revokeCertifiedDiscreteAttribute(TenantRef tenantRef) {
        AttributeRef attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        tenantUseCase.revokeCertifiedDiscreteAttribute(tenantRef, attribute);
        return this;
    }

    @Override
    public TenantJourneyImpl revokeVerifiedAttribute(TenantRef tenantRef) {
        AttributeRef attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        AgreementRef agreement = entityStore.getLastOrThrow(Agreement.class).getRef();
        tenantUseCase.revokeVerifiedAttribute(tenantRef, attribute, agreement);
        return this;
    }
}
