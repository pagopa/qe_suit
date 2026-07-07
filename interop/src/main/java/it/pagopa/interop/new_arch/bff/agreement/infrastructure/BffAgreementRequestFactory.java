package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.journey.application.InteropJourney;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Component
@RequiredArgsConstructor
public class BffAgreementRequestFactory {

    private final InteropJourney interopJourney;

    public AgreementPayload creationRequest(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation) {
        return Instancio.of(AgreementPayload.class)
                .set(field(AgreementPayload::getEserviceId), eService.getId())
                .set(field(AgreementPayload::getDescriptorId), descriptor.getId())
                .set(field(AgreementPayload::getDelegationId), delegation != null ? delegation.getId() : null)
                .create();
    }

    public AgreementPayload baseCreationRequest() {
        final EService eService = interopJourney
                .withProducer(Tenant.AGID, UserRole.ADMIN)
                .createEService()
                .getEService();

        return creationRequest(eService, eService.getLastDraftDescriptor(), null);
    }

}