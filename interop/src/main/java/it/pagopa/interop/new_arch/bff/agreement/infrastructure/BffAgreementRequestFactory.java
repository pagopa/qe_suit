package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffActivateAgreementRequest;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffCreateAgreementRequest;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffSubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.AgreementRequestFactory;
import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import org.springframework.stereotype.Component;

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

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}