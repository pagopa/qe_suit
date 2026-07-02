package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.new_arch.common.agreement.application.AgreementRequestFactory;
import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;

@Component
public class BffAgreementRequestFactory implements AgreementRequestFactory {

    @Override
    public CreateAgreementRequest creationRequest() {
        return new BffCreateAgreementRequest();
    }

    @Override
    public SubmitAgreementRequest submitRequest() {
        return new BffSubmitAgreementRequest();
    }

    @Override
    public ActivateAgreementRequest activateRequest() {
        return new BffActivateAgreementRequest();
    }

    public AgreementPayload fullCreationRequest(UUID eserviceId, UUID descriptionId, Optional<UUID> delegationId) {
        return Instancio.of(AgreementPayload.class)
                .set(field(AgreementPayload::getEserviceId), eserviceId)
                .set(field(AgreementPayload::getDescriptorId), descriptionId)
                .set(field(AgreementPayload::getDelegationId), delegationId.isPresent() ? delegationId.orElse(null) : null)
                .create();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}