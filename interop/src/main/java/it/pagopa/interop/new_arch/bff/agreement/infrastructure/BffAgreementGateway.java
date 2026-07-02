package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.new_arch.bff.agreement.infrastructure.client.BffAgreementRestClient;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffActivateAgreementRequest;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffCreateAgreementRequest;
import it.pagopa.interop.new_arch.bff.agreement.infrastructure.request.BffSubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.AgreementGateway;
import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.CreateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.agreement.domain.AgreementRef;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BffAgreementGateway implements AgreementGateway {

    private final BffAgreementRestClient restClient;

    @Override
    public AgreementRef createAgreement(CreateAgreementRequest request) {
        if (!(request instanceof BffCreateAgreementRequest bffRequest))
            throw new IllegalArgumentException("Invalid request type: " + request.getClass().getName());

        return restClient.create(bffRequest.getRealPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel()
                .getRef();
    }

    @Override
    public Agreement getAgreement(AgreementRef ref) {
        return restClient.read(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel();
    }

    @Override
    public Optional<AgreementRef> submitAgreement(SubmitAgreementRequest request) {
        if (!(request instanceof BffSubmitAgreementRequest bffRequest))
            throw new IllegalArgumentException("Invalid request type: " + request.getClass().getName());

        AgreementRef ref = restClient.submit(bffRequest.getAgreementId(), bffRequest.getPayload())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel()
                .getRef();

        return Optional.of(ref);
    }

    @Override
    public Optional<AgreementRef> activateAgreement(ActivateAgreementRequest request) {
        if (!(request instanceof BffActivateAgreementRequest bffRequest))
            throw new IllegalArgumentException("Invalid request type: " + request.getClass().getName());

        AgreementRef ref = restClient.activate(
                        bffRequest.getAgreement().getId(), Optional.ofNullable(bffRequest.getDelegation()).map(DelegationRef::getId).orElse(null)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext()
                .getModel()
                .getRef();

        return Optional.of(ref);
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}