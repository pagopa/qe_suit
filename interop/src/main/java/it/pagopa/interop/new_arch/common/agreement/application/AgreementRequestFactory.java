package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface AgreementRequestFactory extends Plugin<Channel> {
    CreateAgreementRequest creationRequest();

    SubmitAgreementRequest submitRequest();

    ActivateAgreementRequest activateRequest();
}