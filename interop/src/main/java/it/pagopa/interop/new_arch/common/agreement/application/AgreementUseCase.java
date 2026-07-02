package it.pagopa.interop.new_arch.common.agreement.application;

import it.pagopa.interop.common.contract.template.action.TestChain;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AgreementUseCase {
    private final AgreementGateway agreementGateway;
    private final AgreementRequestFactory requestFactory;

    public Agreement createAgreement(Consumer<?> creationConfig) {
        return executeSuccessfully(agreementGateway.createAgreement(requestFactory.creationRequest(creationConfig)));
    }

    public Agreement getAgreement(Consumer<?> creationConfig) {
        return executeSuccessfully(agreementGateway.getAgreement(requestFactory.getRequest(creationConfig)));
    }

    public Agreement activateAgreement(Consumer<?> creationConfig) {
        return executeSuccessfully(agreementGateway.activateAgreement(requestFactory.activateRequest(creationConfig)));
    }

    public Agreement submitAgreement(Consumer<?> creationConfig) {
        return executeSuccessfully(agreementGateway.submitAgreement(requestFactory.submitRequest(creationConfig)));
    }

    private Agreement executeSuccessfully(TestChain<?, Agreement> chain) {
        return chain.withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel();
    }
}