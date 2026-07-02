package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.GetAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

import java.util.function.Consumer;

public interface AgreementRequestFactory extends Plugin<Channel> {
    CreateAgreementRequest creationRequest(Consumer<?> creationConfig);
    GetAgreementRequest getRequest(Consumer<?> creationConfig);
    SubmitAgreementRequest submitRequest(Consumer<?> creationConfig);
    ActivateAgreementRequest activateRequest(Consumer<?> creationConfig);
}
