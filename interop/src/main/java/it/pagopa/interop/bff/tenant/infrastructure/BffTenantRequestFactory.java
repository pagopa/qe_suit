package it.pagopa.interop.bff.tenant.infrastructure;

import it.pagopa.interop.bff.tenant.application.*;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.tenant.application.TenantRequestFactory;
import it.pagopa.interop.common.tenant.application.command.*;
import it.pagopa.interop.generated.openapi.clients.bff.model.MailKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.MailSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantDelegatedFeaturesFlagsUpdateSeed;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BffTenantRequestFactory implements TenantRequestFactory {

    @Override
    public AssignCertifiedAttributeCommand assignCertifiedAttributeCommand(AttributeRef attributeRef) {
        return new BffAssignCertifiedAttributeCommand().attribute(attributeRef);
    }

    @Override
    public AssignCertifiedDiscreteAttributeCommand assignCertifiedDiscreteAttributeCommand(AttributeRef attributeRef, int value) {
        return new BffAssignCertifiedDiscreteAttributeCommand().attribute(attributeRef).value(value);
    }

    @Override
    public AssignDeclaredAttributeCommand assignDeclaredAttributeCommand(AttributeRef attributeRef) {
        return new BffAssignDeclaredAttributeCommand().attribute(attributeRef);
    }

    @Override
    public AssignVerifiedAttributeCommand assignVerifiedAttributeCommand(AttributeRef attributeRef, AgreementRef agreementRef) {
        return new BffAssignVerifiedAttributeCommand().attribute(attributeRef).agreement(agreementRef);
    }

    @Override
    public RevokeVerifiedAttributeCommand revokeVerifiedAttributeCommand(AgreementRef agreementRef) {
        return new BffRevokeVerifiedAttributeCommand().agreement(agreementRef);
    }

    @Override
    public boolean supports(@jakarta.annotation.Nonnull Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    public TenantDelegatedFeaturesFlagsUpdateSeed enableProducerDelegationPayload(boolean consumerEnabled) {
        return new TenantDelegatedFeaturesFlagsUpdateSeed()
                .isDelegatedProducerFeatureEnabled(true)
                .isDelegatedConsumerFeatureEnabled(consumerEnabled);
    }

    public MailSeed contactEmailWithHyperlinkInNotes() {
        return new MailSeed()
                .kind(MailKind.CONTACT_EMAIL)
                .address("contact-" + UUID.randomUUID() + "@example.test")
                .description("Contatto https://example.org");
    }
}
