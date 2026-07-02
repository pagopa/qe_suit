package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.GetAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface AgreementGateway extends Plugin<Channel> {

    TestChain<?, Agreement> createAgreement(CreateAgreementRequest request);

    TestChain<?, Agreement> getAgreement(GetAgreementRequest request);

    TestChain<?, Agreement> submitAgreement(SubmitAgreementRequest request);

    TestChain<?, Agreement> activateAgreement(ActivateAgreementRequest request);
}
