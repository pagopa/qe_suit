package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

import java.util.Optional;

public interface AgreementGateway extends Plugin<Channel> {

    AgreementRef createAgreement(CreateAgreementRequest request);

    Agreement getAgreement(AgreementRef ref);

    Optional<AgreementRef> submitAgreement(SubmitAgreementRequest request);

    Optional<AgreementRef> activateAgreement(ActivateAgreementRequest request);
}
