package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffActivateAgreementRequest;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffCreateAgreementRequest;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffSubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.AgreementRequestFactory;
import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import org.instancio.Instancio;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Component
public class BffAgreementRequestFactory implements AgreementRequestFactory {

    @Override
    public CreateAgreementRequest creationRequest() {
        return new BffCreateAgreementRequest();
    }

    public AgreementPayload creationRequest(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation) {
        return Instancio.of(AgreementPayload.class)
                .set(field(AgreementPayload::getEserviceId), eService.getId())
                .set(field(AgreementPayload::getDescriptorId), descriptor.getId())
                .set(field(AgreementPayload::getDelegationId), delegation != null ? delegation.getId() : null)
                .create();
    }

    @Override
    public SubmitAgreementRequest submitRequest() {
        return new BffSubmitAgreementRequest();
    }

    @Override
    public ActivateAgreementRequest activateRequest() {
        return new BffActivateAgreementRequest();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}