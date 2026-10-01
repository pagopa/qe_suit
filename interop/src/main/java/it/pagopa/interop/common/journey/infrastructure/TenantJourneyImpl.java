package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.agreement.domain.AgreementRef;
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

    @Override
    public TenantJourneyImpl assignCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef) {
        tenantUseCase.assignCertifiedAttribute(tenantRef, attributeRef);
        return this;
    }

    @Override
    public TenantJourneyImpl assignCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef, int value) {
        tenantUseCase.assignCertifiedDiscreteAttribute(tenantRef, attributeRef, value);
        return this;
    }

    @Override
    public TenantJourneyImpl assignDeclaredAttribute(AttributeRef attributeRef) {
        tenantUseCase.assignDeclaredAttribute(attributeRef);
        return this;
    }

    @Override
    public TenantJourneyImpl assignVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef) {
        tenantUseCase.assignVerifiedAttribute(tenantRef, attributeRef, agreementRef);
        return this;
    }

    @Override
    public TenantJourneyImpl revokeDeclaredAttribute(AttributeRef attributeRef) {
        tenantUseCase.revokeDeclaredAttribute(attributeRef);
        return this;
    }

    @Override
    public TenantJourneyImpl revokeCertifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef) {
        tenantUseCase.revokeCertifiedAttribute(tenantRef, attributeRef);
        return this;
    }

    @Override
    public TenantJourneyImpl revokeCertifiedDiscreteAttribute(TenantRef tenantRef, AttributeRef attributeRef) {
        tenantUseCase.revokeCertifiedDiscreteAttribute(tenantRef, attributeRef);
        return this;
    }

    @Override
    public TenantJourneyImpl revokeVerifiedAttribute(TenantRef tenantRef, AttributeRef attributeRef, AgreementRef agreementRef) {
        tenantUseCase.revokeVerifiedAttribute(tenantRef, attributeRef, agreementRef);
        return this;
    }
}
