package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.journey.application.TenantJourney;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantJourneyImpl implements TenantJourney<TenantJourneyImpl> {

    private final TenantUseCase tenantUseCase;
    private final EntityStore entityStore;
    private final AuthSession session;
    private final CurrentChannel<Channel> channel;

    @Override
    public TenantJourneyImpl ensureProducerDelegationEnabled(Tenant tenant) {
        InteropActor originalActor = session.getActor();
        Tenant originalTenant = session.getTenant();
        Channel originalChannel = channel.getCurrentChannel();
        try {
            channel.setCurrentChannel(Channel.BFF);
            session.set(User.getTenantAdmin(tenant), tenant);
            tenantUseCase.ensureProducerDelegationEnabled(TenantRef.of(tenant.getOrganizationId()));
            return this;
        } finally {
            try {
                session.set(originalActor, originalTenant);
            } finally {
                channel.setCurrentChannel(originalChannel);
            }
        }
    }

    @Override
    public TenantJourneyImpl assignCertifiedAttribute(TenantRef tenant) {
        assignCertifiedAttribute(tenant, -1);
        return this;
    }

    @Override
    public TenantJourneyImpl assignCertifiedAttribute(TenantRef tenant, int nthAttribute) {
        AttributeRef attribute;
        if (nthAttribute == 1) {
            attribute = entityStore.getFirstOrThrow(Attribute.class).getRef();
        } else if (nthAttribute == -1) {
            attribute = entityStore.getLastOrThrow(Attribute.class).getRef();
        } else {
            throw new IllegalArgumentException("No method to retrieve attribute n. " + nthAttribute);
        }
        tenantUseCase.assignCertifiedAttribute(tenant, attribute);
        return this;
    }

    @Override
    public TenantJourneyImpl assignCertifiedAttribute(TenantRef tenant, AttributeRef attributeRef) {
        tenantUseCase.assignCertifiedAttribute(tenant, attributeRef);
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
