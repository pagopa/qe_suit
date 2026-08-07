package it.pagopa.interop.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.infrastructure.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Delegation;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Component
@RequiredArgsConstructor
public class BffAgreementRequestFactory {

    private final InteropJourney interopJourney;
    private final EntityStore entityStore;

    public AgreementPayload creationRequest(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation) {
        return Instancio.of(AgreementPayload.class)
                .set(field(AgreementPayload::getEserviceId), eService.getId())
                .set(field(AgreementPayload::getDescriptorId), descriptor.getId())
                .set(field(AgreementPayload::getDelegationId), delegation != null ? delegation.getId() : null)
                .create();
    }

    public AgreementPayload baseCreationRequest() {
        interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService();

        final EService eService = entityStore.getLastOrThrow(EService.class);
        return creationRequest(eService, eService.getLastDraftDescriptor(), null);
    }

}